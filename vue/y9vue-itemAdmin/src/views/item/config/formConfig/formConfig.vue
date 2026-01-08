<!--

 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-12 09:42:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 13:51:03
 * @Descripttion: 表单配置
 * @FilePath: \vue\y9vue-itemAdmin\src\views\item\config\formConfig\formConfig.vue
-->
<template>
    <y9Card :title="`表单配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table :config="formListTableConfig" @expand-change="expandChange">
            <template #childContent="props">
                <el-tabs v-model="activeName" type="card" class="demo-tabs">
                    <el-tab-pane :label="formBindType == 'PC' ? 'PC端绑定' : '手机端绑定'" name="first">
                        <div v-if="formListShow" style="padding: 0 50px">
                            <el-row :gutter="40">
                                <el-col :span="18">
                                    <el-button style="margin-bottom: 5px" type="primary" @click="addForm"
                                        ><i class="ri-add-line"></i>表单（{{ props.row.taskDefName }}）
                                    </el-button>
                                </el-col>
                                <el-col :span="6">
                                    <el-button-group v-if="formBindType == 'PC'">
                                        <el-button type="primary" @click="moveUp"
                                            ><i class="ri-arrow-up-line"></i>上移
                                        </el-button>
                                        <el-button type="primary" @click="moveDown"
                                            ><i class="ri-arrow-down-line"></i>下移
                                        </el-button>
                                        <el-button type="primary" @click="saveFormOrder"><span>保存</span></el-button>
                                    </el-button-group>
                                </el-col>
                            </el-row>
                            <y9Table :config="formBindTableConfig" @on-current-change="handleCurrentChange">
                                <template #opt_bind="{ row, column, index }">
                                    <span style="margin-right: 15px; font-weight: 600" @click="deleteForm(row)"
                                        ><i class="ri-delete-bin-line"></i>删除</span
                                    >
                                </template>
                            </y9Table>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane v-if="formBindType == 'PC'" label="页签设置" name="secord">
                        <div style="text-align: center; padding: 10px">
                            <el-checkbox v-model="bindForm.showFileTab" style="margin-left: 8px">附件</el-checkbox>
                            <el-checkbox v-model="bindForm.showHistoryTab">关联文件</el-checkbox>
                        </div>
                        <div style="text-align: center; margin: 15px 5px 0px">
                            <span slot="footer" class="dialog-footer">
                                <el-button type="primary" @click="saveSetting">保存设置</el-button>
                                <!-- <el-button @click="cancelBind">取消</el-button> -->
                            </span>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </template>
            <template #mobileCell="{ row, column, index }">
                {{ row.mobileFormName }}
            </template>
            <template #opt_button="{ row, column, index }">
                <span style="margin-right: 15px; font-weight: 600" @click="formBind(row, 'PC')">
                    <i class="ri-computer-line"></i>PC端绑定
                </span>
                <span style="font-weight: 600" @click="formBind(row, 'mobile')">
                    <i class="ri-cellphone-line"></i>手机端绑定
                </span>
            </template>
        </y9Table>
        <y9Dialog v-model:config="formDialogConfig">
            <y9Table v-model:selectedVal="selectedCheck" :config="y9FormTableConfig"></y9Table>
        </y9Dialog>
    </y9Card>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import {
        copyForm,
        deleteBind,
        deleteMobileBind,
        getBindList,
        getBpmList,
        getFormList,
        getMobileBindList,
        getY9FormList,
        saveFormBind,
        saveOrder,
        saveTabSetting
    } from '@/api/itemAdmin/item/formConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        selVersion: Function,
        processDefinitionList: {
            //流程定义版本信息
            type: Array,
            default: () => {
                return [];
            }
        },
        selectVersion: {
            type: Number,
            default: () => {
                return 1;
            }
        },
        maxVersion: {
            type: Number,
            default: () => {
                return 1;
            }
        }
    });
    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        formListTableConfig: {
            //人员列表表格配置
            height: 'auto',
            maxHeight: 'none',
            columns: [
                {
                    type: 'expand',
                    slot: 'childContent',
                    width: 60
                },
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '流程节点名称',
                    key: 'taskDefName'
                },
                {
                    title: 'PC表单',
                    key: 'eformNames'
                },
                {
                    title: '手机端表单',
                    key: 'mobileFormName',
                    slot: 'mobileCell'
                },
                {
                    title: '操作',
                    slot: 'opt_button'
                }
            ],
            rowKey: 'taskDefKey',
            tableData: [],
            pageConfig: false, //取消分页

            expandRowKeys: [] as any
        },
        y9FormTableConfig: {
            //y9表单列表表格配置
            rowKey: 'formId',
            height: 'auto',
            maxHeight: 'none',
            columns: [
                {
                    type: 'selection',
                    width: '60'
                },
                {
                    title: '表单名称',
                    key: 'formName'
                }
            ],
            tableData: [],
            pageConfig: false //取消分页
        },
        formBindTableConfig: {
            //y9表单列表表格配置
            height: 'auto',
            maxHeight: 'none',
            columns: [
                {
                    title: '表单名称',
                    key: 'formName'
                },
                {
                    title: '操作',
                    key: 'opt',
                    slot: 'opt_bind'
                }
            ],
            tableData: [],
            pageConfig: false //取消分页
        },
        //弹窗配置
        formDialogConfig: {
            show: false,
            title: '',
            loading: false,
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    if (selectedCheck.value.length == 0) {
                        ElNotification({
                            title: '提示',
                            message: '请选择表单',
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }
                    let formInfos = [];
                    selectedCheck.value.forEach((element) => {
                        formInfos.push(element.formId + ':' + element.formName);
                    });
                    let res = await saveFormBind(
                        formBindType.value,
                        formInfos.toString(),
                        props.currTreeNodeInfo.id,
                        props.currTreeNodeInfo.processDefinitionId,
                        taskDefKey.value
                    );
                    if (res.success) {
                        if (formBindType.value == 'mobile') {
                            reloadMobileBindList();
                        } else {
                            reloadBindList();
                        }
                        ElNotification({
                            title: '提示',
                            message: '绑定成功',
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        getFormConfig();
                        resolve();
                    }
                });
            },
            visibleChange: (visible) => {
                // console.log('visible',visible)
            }
        },
        rules: {
            //formName: { required: true, trigger: 'blur', message: '请选择表单' }
        },
        bindFormRef: '',
        pcBindFormRef: '',
        taskDefKey: '',
        formList: [],
        bindForm: {
            showFileTab: true,
            showDocumentTab: false,
            showHistoryTab: true
        },
        title: '',
        bindList: [],
        innerVisible: false,
        pVersion: '',
        formListShow: true,
        formBindType: '',
        activeName: 'first',
        formNames: [],
        selectedCheck: [],
        currentRow: [],
        isExpand: false
    });

    let {
        currInfo,
        formListTableConfig,
        y9FormTableConfig,
        formDialogConfig,
        formBindTableConfig,
        rules,
        bindFormRef,
        pcBindFormRef,
        taskDefKey,
        formList,
        bindList,
        title,
        bindForm,
        innerVisible,
        pVersion,
        formListShow,
        formBindType,
        activeName,
        formNames,
        selectedCheck,
        currentRow,
        isExpand
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getFormConfig();
            formListTableConfig.value.expandRowKeys = [];
        },
        { deep: true }
    );

    watch(
        () => props.selectVersion,
        (newVal) => {
            pVersion.value = props.selectVersion;
        },
        { deep: true }
    );

    onMounted(() => {
        pVersion.value = props.selectVersion;
        getFormConfig();
    });

    async function delMobileBind(id) {
        let res = await deleteMobileBind(id);
        ElNotification({
            title: res.success ? '成功' : '失败',
            message: res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (res.success) {
            await reloadMobileBindList();
            await getFormConfig();
        }
    }

    async function getFormConfig() {
        formListTableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            formListTableConfig.value.tableData = res.data;
        }
    }

    async function formBind(row, type) {
        formListTableConfig.value.expandRowKeys = [row.taskDefKey];
        formBindType.value = type;
        formListShow.value = true;
        bindForm.value = {
            showFileTab: true,
            showDocumentTab: false,
            showHistoryTab: true
        };
        formList.value = [];
        bindList.value = [];
        taskDefKey.value = row.taskDefKey;
        if (type == 'mobile') {
            activeName.value = 'first';
            await reloadMobileBindList();
        } else {
            await reloadBindList();
        }

        return;
    }

    function cancelBind() {
        formListShow.value = true;
    }

    function expandChange(row, expandedRows) {
        formBindType.value = 'PC';

        // if (!expanded) {
        //     isExpand.value = false;
        // }

        if (row.taskDefKey) {
            taskDefKey.value = row.taskDefKey;
        } else {
            taskDefKey.value = '';
        }

        let expandKeyId = '';
        isExpand.value = true;
        if (expandedRows.length == 1) {
            if (!expandKeyId) {
                expandKeyId = row.taskDefKey;
            }
        } else if (expandedRows.length >= 2) {
            //已经展开一行
            expandKeyId = expandedRows[expandedRows.length - 1].taskDefKey; //获取最后一个点开的rowID
        } else {
            //关闭
            isExpand.value = false;
        }
        if (isExpand.value) {
            formListTableConfig.value.expandRowKeys = [expandKeyId];
        } else {
            formListTableConfig.value.expandRowKeys = [];
        }

        console.log('expandKeyId', expandKeyId);

        reloadBindList();
    }

    async function saveSetting() {
        bindForm.value.itemId = props.currTreeNodeInfo.id;
        bindForm.value.processDefinitionId = props.currTreeNodeInfo.processDefinitionId;
        bindForm.value.taskDefKey = taskDefKey.value;
        let res = await saveTabSetting(bindForm.value);
        if (res.success) {
            ElNotification({
                title: '提示',
                message: '保存成功',
                type: 'success',
                duration: 2000,
                offset: 80
            });
            closeExpand();
            activeName.value = 'first';
            getFormConfig();
        }
    }

    function closeExpand() {
        formListTableConfig.value.expandRowKeys = [];
    }

    defineExpose({
        closeExpand
    });

    async function reloadBindList() {
        //获取表单绑定列表
        let res = await getBindList(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value
        );
        if (res.success) {
            formBindTableConfig.value.tableData = res.data;
            if (res.data.length > 0) {
                bindForm.value = res.data[0];
            }
        }
    }

    async function reloadMobileBindList() {
        //获取表单绑定列表
        let res = await getMobileBindList(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value
        );
        if (res.success) {
            formBindTableConfig.value.tableData = res.data;
        }
    }

    async function pIdchange(val) {
        let pId = '';
        for (let pd of props.processDefinitionList) {
            if (pd.version == val) {
                pId = pd.id;
                break;
            }
        }
        props.selVersion(pId, val);
    }

    async function bindFormInfo(id) {
        let res1 = await getFormList(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value,
            props.currTreeNodeInfo.systemName
        );
        if (res1.success) {
            y9FormTableConfig.value.tableData = res1.data;
        }
    }

    async function addForm() {
        Object.assign(formDialogConfig.value, {
            show: true,
            width: '30%',
            title: '表单选择',
            okText: '绑定',
            showFooter: true
        });
        if (formBindType.value == 'mobile') {
            let res = await getY9FormList(props.currTreeNodeInfo.systemName);
            if (res.success) {
                y9FormTableConfig.value.tableData = res.data;
            }
        } else {
            bindForm.value = {};
            bindFormInfo('');
        }
    }

    async function editForm(row) {
        title.value = '编辑表单';
        formListShow.value = false;
        bindForm.value = {};
        bindFormInfo(row.id);
    }

    async function deleteForm(row) {
        if (formBindType.value == 'mobile') {
            delMobileBind(row.id);
        } else {
            let res = await deleteBind(row.id);
            ElNotification({
                title: res.success ? '成功' : '失败',
                message: res.msg,
                type: res.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            if (res.success) {
                reloadBindList();
                innerVisible.value = false;
                await getFormConfig();
            }
        }
    }

    async function formCopy() {
        var tips = '确定复制当前版本绑定的表单到最新版本吗？';
        if (props.selectVersion === props.maxVersion) {
            tips = '确定复制上一个版本绑定的表单到最新版本吗？';
        }
        ElMessageBox.confirm(tips, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await copyForm(props.currTreeNodeInfo.id, props.currTreeNodeInfo.processDefinitionId);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getFormConfig();
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消复制',
                    offset: 65
                });
            });
    }

    function handleCurrentChange(val) {
        currentRow.value = val;
    }

    const moveUp = () => {
        //上移
        if (currentRow.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请点击选中一条数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let index = 0;
        for (let i = 0; i < formBindTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == formBindTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = formBindTableConfig.value.tableData[index - 1];
            let currRow = formBindTableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            formBindTableConfig.value.tableData.splice(index - 1, 1);
            formBindTableConfig.value.tableData.splice(index, 0, upRow);
        } else {
            ElNotification({
                title: '操作提示',
                message: '已经是第一条，不可上移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        }
    };

    const moveDown = () => {
        //下移
        if (currentRow.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请选择数据', type: 'error', duration: 2000, offset: 80 });
            return;
        }

        let index = 0;
        for (let i = 0; i < formBindTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == formBindTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == formBindTableConfig.value.tableData.length) {
            ElNotification({
                title: '操作提示',
                message: '已经是最后一条，不可下移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        } else {
            let downRow = formBindTableConfig.value.tableData[index + 1];
            let currRow = formBindTableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            formBindTableConfig.value.tableData.splice(index + 1, 1);
            formBindTableConfig.value.tableData.splice(index, 0, downRow);
        }
    };

    function saveFormOrder() {
        let ids = [];
        for (let item of formBindTableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            loading.close();
            if (res.success) {
                ElNotification({ title: '操作提示', message: res.msg, type: 'success', duration: 2000, offset: 80 });
                reloadBindList();
            } else {
                ElNotification({ title: '操作提示', message: res.msg, type: 'error', duration: 2000, offset: 80 });
            }
        });
    }
</script>
<style lang="scss" scoped>
    :deep(.tablenameclass) {
        margin-bottom: 0 !important;
    }
</style>
<style lang="scss" scoped></style>
