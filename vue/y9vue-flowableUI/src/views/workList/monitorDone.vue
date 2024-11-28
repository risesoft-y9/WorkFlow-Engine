<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:53:31
 * @LastEditors: mengjuhua
 * @Description:  监控办结件
-->
<template>
    <y9Table
        ref="filterRef"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #update>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                @click="refreshTable"
            >
                <i class="ri-refresh-line"></i>
                <span>{{ $t('刷新') }}</span>
            </el-button>
        </template>
        <template #documentTitle="{ row, column, index }">
            <el-link
                :style="{ color: 'blue', fontSize: fontSizeObj.baseFontSize }"
                :underline="false"
                @click="openDoc(row)"
            >
                {{ row.documentTitle == '' ? $t('未定义标题') : row.documentTitle }}
            </el-link>
        </template>
        <template #opt="{ row, column, index }">
            <el-button
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="openHistoryList(row)"
            >
                <i class="ri-sound-module-fill"></i>{{ $t('历程') }}
            </el-button>
            <el-button
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="handleDelete(row)"
            >
                <i class="ri-delete-bin-line"></i>{{ $t('删除') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <historyList ref="historyListRef" :processInstanceId="processInstanceId" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, watch } from 'vue';
    import historyList from '@/views/process/historyList.vue';
    import { getmonitorDoneList, removeProcess } from '@/api/flowableUI/monitor';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    const emits = defineEmits(['refreshCount']);

    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

    // window.onresize = () => {
    //     return (() => {
    //         tableHeight.value = useSettingStore().getWindowHeight - 280 - 20;
    //     })();
    // };

    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        itemId: '',
        processInstanceId: '',
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('文件编号')), key: 'number', width: '190' },
                // { title: "类别", key: "itemName", width: '100',},
                {
                    title: computed(() => t('标题')),
                    key: 'documentTitle',
                    slot: 'documentTitle',
                    align: 'left',
                    minWidth: '200'
                },
                { title: computed(() => t('发起人')), key: 'creatUserName', width: '180' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '150' },
                { title: computed(() => t('办结时间')), key: 'endTime', width: '150' },
                { title: computed(() => t('办结人')), key: 'user4Complete', width: '180' },
                { title: computed(() => t('操作')), key: 'opt', width: '180', slot: 'opt' }
            ],
            tableData: [],
            //height: tableHeight,
            openAutoComputedHeight: true,
            pageConfig: {
                currentPage: 1,
                pageSize: 20,
                total: 0,
                pageSizeOpts: [10, 20, 30, 50, 100]
            }
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'search',
                    key: 'name',
                    props: {
                        placeholder: computed(() => t('请输入标题,文号,发起人搜索'))
                    },
                    span: settingStore.device === 'mobile' ? 12 : 6
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 10,
                    slotName: 'update'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        }
    });

    let { filterRef, currFilters, filterConfig, itemId, processInstanceId, tableConfig, dialogConfig } = toRefs(data);

    onMounted(() => {
        itemId.value = flowableStore.getItemId;
        if (flowableStore.currentPage.indexOf('_back') > -1) {
            //返回列表获取当前页
            tableConfig.value.pageConfig.currentPage = flowableStore.currentPage.split('_')[0];
        }
        flowableStore.$patch({
            //重新设置
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        reloadTable();
    });

    //监听过滤条件改变时，获取列表数据
    watch(
        () => currFilters.value,
        (newVal) => {
            if (newVal.name) {
                reloadTable();
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function refreshTable() {
        currFilters.value.name = '';
        tableConfig.value.pageConfig.currentPage = 1;
        tableConfig.value.pageConfig.pageSize = 20;
        filterRef.value.elTableFilterRef.onReset();
        reloadTable();
    }

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        tableConfig.value.loading = true;
        let res = await getmonitorDoneList(itemId.value, currFilters.value.name, page, rows);
        tableConfig.value.loading = false;
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    function openDoc(row) {
        let query = {
            itemId: itemId.value,
            processSerialNumber: row.processSerialNumber,
            itembox: 'monitorDone',
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'monitorDone'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: '/index/edit', query: query });
    }

    function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            title: t('历程') + '【' + row.documentTitle + '】',
            type: 'history',
            showFooter: false
        });
    }

    function handleDelete(row) {
        ElMessageBox.confirm(
            t('即将删除') + '【' + row.documentTitle + `】<br>${t('删除后无法恢复！确定删除?')}'`,
            t('提示'),
            {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                dangerouslyUseHTMLString: true,
                type: 'info',
                appendTo: '.y9-table-div'
            }
        )
            .then(async () => {
                let res = await removeProcess(row.processInstanceId);
                ElMessage({
                    message: res.msg,
                    type: res.success ? 'success' : 'error',
                    offset: 65,
                    appendTo: '.y9-table-div'
                });
                if (res.success) {
                    //emit("refreshCount");
                    reloadTable();
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65,
                    appendTo: '.y9-table-div'
                });
            });
    }
</script>

<style scoped>
    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    /*messageBox */
    :global(.el-message-box .el-message-box__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    :global(.el-message-box .el-message-box__title) {
        font-size: v-bind('fontSizeObj.largeFontSize');
    }

    :global(.el-message-box .el-message-box__btns button) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
