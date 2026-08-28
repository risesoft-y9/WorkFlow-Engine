<!--

 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-12 09:42:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-30 14:29:18
 * @Descripttion: 数据迁移
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\config\dataTransfer\dataTransfer.vue
-->
<template>
    <y9Card :title="`流程数据迁移${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table
            :config="dataTableConfig"
            @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange"
        >
            <template v-if="maxVersion != selectVersion" #opt_button="{ row, column, index }">
                <span style="font-weight: 600" @click="dataMove(row)"><i class="ri-add-line"></i>迁移</span>
            </template>
        </y9Table>
    </y9Card>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import { dataTransfer, getProcessInstanceList } from '@/api/itemAdmin/item/dataTransfer';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        maxVersion: Number,
        selectVersion: Number,
        processDefinitionId: String
    });

    let total = ref(0);
    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        dataTableConfig: {
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '文件编号',
                    key: 'number',
                    width: '180'
                },
                {
                    title: '标题',
                    key: 'title'
                },
                {
                    title: '拟稿人',
                    key: 'startorName',
                    width: '160'
                },
                {
                    title: '开始时间',
                    key: 'startTime',
                    width: '180'
                },
                {
                    title: '当前办理人',
                    key: 'assigneeNames',
                    width: '160'
                },
                {
                    title: '操作',
                    width: '80',
                    slot: 'opt_button'
                }
            ],
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                pageSizeOpts: [10, 20, 30, 50, 100],
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value //总条目数
            },
            height: 'auto'
        }
    });

    let { currInfo, dataTableConfig } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal, oldVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getProcessDataList();
        },
        { deep: true }
    );

    onMounted(() => {
        getProcessDataList();
    });

    async function getProcessDataList() {
        dataTableConfig.value.tableData = [];
        let page = dataTableConfig.value.pageConfig.currentPage;
        let rows = dataTableConfig.value.pageConfig.pageSize;
        let res = await getProcessInstanceList(
            props.currTreeNodeInfo.processDefinitionId,
            props.currTreeNodeInfo.id,
            page,
            rows
        );
        if (res.success) {
            dataTableConfig.value.tableData = res.rows;
            dataTableConfig.value.pageConfig.total = res.total;
        }
    }

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        dataTableConfig.value.pageConfig.currentPage = currPage;
        getProcessDataList();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        dataTableConfig.value.pageConfig.pageSize = pageSize;
        getProcessDataList();
    }

    function dataMove(row) {
        ElMessageBox.confirm('你确定要迁移这条数据吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let processInstanceId = '';
                if (row != undefined) {
                    processInstanceId = row.processInstanceId;
                }
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                dataTransfer(props.currTreeNodeInfo.processDefinitionId, processInstanceId).then((res) => {
                    loading.close();
                    if (res.success) {
                        ElNotification({
                            title: '操作提示',
                            message: res.msg,
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        getStartNodeList();
                    } else {
                        ElNotification({
                            title: '操作提示',
                            message: res.msg,
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                    }
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消迁移',
                    offset: 65
                });
            });
    }
</script>

<style lang="scss" scoped></style>
