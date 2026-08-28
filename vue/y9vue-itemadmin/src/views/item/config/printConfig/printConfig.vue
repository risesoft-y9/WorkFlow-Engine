<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-13 09:49:46
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-02-09 14:27:00
 * @Descripttion: 打印配置
 * @FilePath: \vue\y9vue-itemAdmin\src\views\item\config\printConfig\printConfig.vue
-->
<template>
    <y9Card :title="`打印模板配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            <el-button class="global-btn-main" type="primary" @click="bindTemplate('form')">
                <i class="ri-table-line"></i>
                <span>表单模板</span>
            </el-button>
        </div>
        <y9Table :config="printBindTableConfig"></y9Table>
        <el-drawer v-model="tableDrawer" :title="title" class="eldrawer" direction="rtl" @close="closeDrawer">
            <div style="margin-bottom: 10px; text-align: left">
                <el-input
                    v-model="searchName"
                    clearable
                    placeholder="模板名称"
                    style="width: 200px; margin-right: 5px"
                ></el-input>
                <el-button type="primary" @click="search"><i class="ri-search-2-line"></i>搜索</el-button>
            </div>
            <y9Table
                v-if="formShow"
                :config="formTableConfig"
                @select="handlerGetData"
                @select-all="handlerGetData"
            ></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="saveBind"><span>保存</span></el-button>
                <el-button @click="tableDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
    </y9Card>
</template>

<script lang="ts" setup>
    import { h, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import {
        deleteBindPrintTemplate,
        getBindTemplateList,
        getPrintFormList,
        saveBindTemplate
    } from '@/api/itemAdmin/item/printConfig';
    import { getPrintTemplateList } from '@/api/itemAdmin/printTemplate';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        //当前节点信息
        searchName: '',
        title: '',
        formShow: false,
        currInfo: props.currTreeNodeInfo,
        tableDrawer: false,
        printBindTableConfig: {
            //人员列表表格配置
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '模板名称',
                    key: 'templateName'
                },
                {
                    title: '模板类型',
                    key: 'templateType',
                    render: (row) => {
                        var str = '';
                        switch (row.templateType) {
                            case '1':
                                str = 'Word模板';
                                break;
                            case '2':
                                str = '表单模板';
                                break;
                            default:
                                break;
                        }
                        return str;
                    }
                },
                {
                    title: '操作',
                    render: (row) => {
                        let button = [
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '15px',
                                        fontWeight: 600
                                    },
                                    onClick: () => {
                                        deleteBind(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-delete-bin-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    '删除'
                                ]
                            )
                        ];
                        return button;
                    }
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        formTableConfig: {
            rowKey: 'formId',
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '表单名称',
                    key: 'formName'
                }
            ],
            tableData: [],
            pageConfig: false,
            height: 'auto'
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {
                console.log('visible', visible);
                if (!visible) {
                    getTemplateBindList();
                }
            }
        },
        taskDefKey: ''
    });

    let {
        searchName,
        title,
        formShow,
        tableDrawer,
        currInfo,
        printBindTableConfig,
        formTableConfig,
        dialogConfig,
        taskDefKey
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getTemplateBindList();
        }
    );

    onMounted(() => {
        getTemplateBindList();
    });

    async function getTemplateBindList() {
        //权限配置
        printBindTableConfig.value.tableData = [];
        let res = await getBindTemplateList(props.currTreeNodeInfo.id);
        if (res.success) {
            printBindTableConfig.value.tableData = res.data;
        }
    }

    async function bindTemplate(type) {
        title.value = '绑定表单模板';
        formShow.value = true;
        formTableConfig.value.tableData = [];
        getFormList();
        tableDrawer.value = true;
    }

    function search() {
        getFormList();
    }

    function closeDrawer() {
        searchName.value = '';
        getTemplateBindList();
    }

    const selectData = ref([]);

    // 表格 选择框 选择后获取数据
    function handlerGetData(id, data) {
        selectData.value = id;
    }

    async function getFormList() {
        let res = await getPrintFormList(props.currTreeNodeInfo.id, searchName.value);
        if (res.success) {
            formTableConfig.value.tableData = res.data;
        }
    }

    async function saveBind() {
        let templateId = '';
        let templateName = '';
        let templateType = '';
        let templateUrl = '';
        if (selectData.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请勾选要绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        if (selectData.value.length > 1) {
            ElNotification({
                title: '操作提示',
                message: '只能勾选一条绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        templateId = selectData.value[0].formId;
        templateName = selectData.value[0].formName;
        templateType = '2';
        let result = { success: false, msg: '' };
        result = await saveBindTemplate(props.currTreeNodeInfo.id, templateId, templateName, templateType, templateUrl);
        ElNotification({
            title: result.success ? '成功' : '失败',
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (result.success) {
            getTemplateBindList();
            tableDrawer.value = false;
        }
    }

    function deleteBind(row) {
        ElMessageBox.confirm('你确定要删除绑定的模板吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteBindPrintTemplate(row.id);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getTemplateBindList();
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消删除',
                    offset: 65
                });
            });
    }
</script>

<style lang="scss" scoped>
    :deep(.eldrawer .el-drawer__header) {
        margin-bottom: 0;
        padding-bottom: 16px;
        border-bottom: 1px solid #eee;
    }
</style>
