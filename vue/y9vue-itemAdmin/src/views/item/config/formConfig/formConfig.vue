<template>
    <y9Card :title="`表单配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            流程定义版本
            <el-select
                v-model="pVersion"
                style="width: 70px; margin-right: 15px; margin-left: 15px"
                @change="pIdchange"
            >
                <el-option v-for="pd in processDefinitionList" :key="pd.id" :label="pd.version" :value="pd.version">
                </el-option>
            </el-select>
            <el-button v-if="maxVersion != 1" class="global-btn-main" type="primary" @click="formCopy">
                <i class="ri-file-copy-2-line"></i>
                <span>复制</span>
            </el-button>
        </div>
        <y9Table :config="formListTableConfig">
            <template #mobileCell="{ row, column, index }">
                {{ row.mobileFormName }}
                <i v-if="row.mobileFormName != ''" class="ri-delete-bin-line" @click="delMobileBind(row)"></i>
            </template>
            <template #opt_button="{ row, column, index }">
                <span style="margin-right: 15px" @click="formBind(row, 'PC')"
                    ><i class="ri-computer-line"></i>PC端绑定</span
                >
                <span @click="formBind(row, 'mobile')"><i class="ri-cellphone-line"></i>手机端绑定</span>
            </template>
        </y9Table>

        <y9Dialog v-model:config="formDialogConfig">
            <div v-if="formDialogConfig.type == 'PC'">
                <el-button style="margin-bottom: 16px" type="primary" @click="addForm"
                    ><i class="ri-add-line"></i>表单
                </el-button>
                <el-table :data="bindList" border height="450px" style="width: 100%">
                    <el-table-column align="center" label="表单名称" prop="formName" width="auto"></el-table-column>
                    <el-table-column align="center" label="操作" prop="opt" width="200">
                        <template #default="opt_cell">
                            <i
                                class="ri-edit-line"
                                style="margin-right: 15px; font-size: 18px"
                                title="编辑"
                                @click="editForm(opt_cell.row)"
                            ></i>
                            <i
                                class="ri-delete-bin-line"
                                style="font-size: 18px"
                                title="删除"
                                @click="deleteForm(opt_cell.row)"
                            ></i>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div v-else-if="formDialogConfig.type == 'mobile'" class="mobileDiv">
                <el-form ref="bindFormRef" :inline-message="true" :model="bindForm" :rules="rules" :status-icon="true">
                    <table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
                        <tbody>
                            <tr>
                                <td class="lefttd" style="width: 20%">表单名称</td>
                                <td class="rigthtd">
                                    <el-form-item prop="formName">
                                        <el-select
                                            v-model="bindForm.formName"
                                            placeholder="请选择"
                                            @change="formchange"
                                        >
                                            <el-option
                                                v-for="item in formList"
                                                :key="item.formId"
                                                :label="item.formName"
                                                :value="item.formId"
                                            >
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </el-form>
            </div>

            <el-dialog
                v-if="formDialogConfig.type == 'PC'"
                v-model="innerVisible"
                :close-on-click-modal="false"
                :close-on-press-escape="false"
                :title="title"
                append-to-body
                custom-class="bindFormdialog"
                width="25%"
            >
                <el-form
                    ref="pcBindFormRef"
                    :inline-message="true"
                    :model="bindForm"
                    :rules="rules"
                    :status-icon="true"
                >
                    <table class="layui-table">
                        <tr>
                            <td class="lefttd" style="width: 25%">当前事项</td>
                            <td class="rigthtd">
                                <span>{{ bindForm.itemName }}</span>
                            </td>
                        </tr>
                        <tr>
                            <td class="lefttd">当前流程</td>
                            <td class="rigthtd">
                                <span>{{ bindForm.procDefName }}</span>
                            </td>
                        </tr>
                        <tr>
                            <td class="lefttd">表单名称</td>
                            <td class="rigthtd">
                                <el-form-item prop="formName">
                                    <el-select v-model="bindForm.formName" placeholder="请选择" @change="formchange">
                                        <el-option
                                            v-for="item in formList"
                                            :key="item.formId"
                                            :label="item.formName"
                                            :value="item.formId"
                                        >
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                            </td>
                        </tr>
                        <tr>
                            <td class="lefttd">显示顺序</td>
                            <td class="rigthtd">
                                <el-input v-model="bindForm.tabIndex"></el-input>
                            </td>
                        </tr>
                        <tr>
                            <td class="lefttd">选择其它</td>
                            <td class="rigthtd">
                                <el-checkbox v-model="bindForm.showFileTab" style="margin-left: 8px">附件</el-checkbox>
                                <el-checkbox v-model="bindForm.showHistoryTab">关联流程</el-checkbox>
                            </td>
                        </tr>
                    </table>
                </el-form>
                <div style="text-align: right; margin: 15px 5px 0px">
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="saveBind">保存</el-button>
                        <el-button @click="innerVisible = false">取消</el-button>
                    </span>
                </div>
            </el-dialog>
        </y9Dialog>
    </y9Card>
</template>

<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import {
        copyForm,
        deleteBind,
        deleteMobileBind,
        getBindForm,
        getBindList,
        getBpmList,
        getFormList,
        getY9FormList,
        saveBindForm,
        saveMobileBind
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
            columns: [
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
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        //弹窗配置
        formDialogConfig: {
            show: false,
            title: '',
            loading: false,
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    if (newConfig.value.type == 'mobile') {
                        await bindFormRef.value.validate(async (valid) => {
                            if (valid) {
                                let res = await saveMobileBind(bindForm.value);
                                ElNotification({
                                    title: res.success ? '成功' : '失败',
                                    message: res.msg,
                                    type: res.success ? 'success' : 'error',
                                    duration: 2000,
                                    offset: 80
                                });
                                if (res.success) {
                                    await getFormConfig();
                                }
                                resolve();
                            } else {
                                reject();
                                return;
                            }
                        });
                    }
                });
            },
            visibleChange: (visible) => {
                // console.log('visible',visible)
            }
        },
        rules: {
            formName: { required: true, trigger: 'blur', message: '请选择表单' }
        },
        bindFormRef: '',
        pcBindFormRef: '',
        taskDefKey: '',
        formList: [],
        bindForm: {},
        title: '',
        bindList: [],
        innerVisible: false,
        pVersion: ''
    });

    let {
        currInfo,
        formListTableConfig,
        formDialogConfig,
        rules,
        bindFormRef,
        pcBindFormRef,
        taskDefKey,
        formList,
        bindList,
        title,
        bindForm,
        innerVisible,
        pVersion
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getFormConfig();
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

    async function delMobileBind(row) {
        let res = await deleteMobileBind(row.mobileBindId);
        ElNotification({
            title: res.success ? '成功' : '失败',
            message: res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (res.success) {
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
        bindForm.value = {};
        Object.assign(formDialogConfig.value, {
            show: true,
            width: '30%',
            title: 'Y9表单绑定【' + row.taskDefName + '】',
            type: type,
            showFooter: type == 'PC' ? false : true
        });
        formList.value = [];
        if (type == 'mobile') {
            bindForm.value.itemName = props.currTreeNodeInfo.name;
            bindForm.value.itemId = props.currTreeNodeInfo.id;
            bindForm.value.taskDefKey = row.taskDefKey;
            bindForm.value.processDefinitionId = props.currTreeNodeInfo.processDefinitionId;
            bindForm.value.formName = row.mobileFormName;
            bindForm.value.formId = row.mobileFormId;
            bindForm.value.id = row.mobileBindId;
            let res = await getY9FormList(props.currTreeNodeInfo.systemName);
            if (res.success) {
                formList.value = res.data;
            }
        } else {
            bindList.value = [];
            taskDefKey.value = row.taskDefKey;
            await reloadBindList();
        }
        return;
    }

    async function reloadBindList() {
        //获取表单绑定列表
        let res = await getBindList(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value
        );
        if (res.success) {
            bindList.value = res.data;
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

    async function formchange(val) {
        //表单选择
        bindForm.value.formId = val;
        for (let item of formList.value) {
            if (item.formId == val) {
                bindForm.value.formName = item.formName;
                bindFormRef.value.clearValidate();
                break;
            }
        }
    }

    async function bindFormInfo(id) {
        //获取表单绑定信息
        setTimeout(() => {
            pcBindFormRef.value.clearValidate();
        }, 200);
        bindForm.value.itemName = props.currTreeNodeInfo.name;
        bindForm.value.itemId = props.currTreeNodeInfo.id;
        let res = await getBindForm(id, props.currTreeNodeInfo.processDefinitionId);
        if (res.success) {
            bindForm.value.formName = '';
            bindForm.value = res.data;
            bindForm.value.itemName = props.currTreeNodeInfo.name;
            bindForm.value.itemId = props.currTreeNodeInfo.id;
            bindForm.value.taskDefKey = taskDefKey.value;
            let res1 = await getFormList(
                props.currTreeNodeInfo.id,
                props.currTreeNodeInfo.processDefinitionId,
                taskDefKey.value,
                props.currTreeNodeInfo.systemName
            );
            if (res1.success) {
                formList.value = res1.data;
            }
        }
    }

    async function addForm() {
        bindForm.value = {};
        innerVisible.value = true;
        bindFormInfo('');
        title.value = '添加表单';
    }

    async function editForm(row) {
        bindForm.value = {};
        innerVisible.value = true;
        bindFormInfo(row.id);
        title.value = '编辑表单';
    }

    async function deleteForm(row) {
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
        }
    }

    async function saveBind() {
        //保存表单绑定
        await pcBindFormRef.value.validate(async (valid) => {
            if (valid) {
                let res = await saveBindForm(bindForm.value);
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
                    getFormConfig();
                }
            }
        });
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
</script>
<style>
    .mobileDiv .el-form-item {
        margin-bottom: 0;
    }

    .bindFormdialog .el-form-item {
        margin-bottom: 0;
    }

    .bindFormdialog .el-dialog__body {
        padding: 16px;
        border-top: 1px solid #eee;
    }
</style>
<style lang="scss" scoped>
    .layui-table {
        width: 100%;
        border-collapse: collapse;
        border-spacing: 0;

        td {
            position: revert;
            padding: 5px 10px;
            min-height: 32px;
            line-height: 32px;
            font-size: 14px;
            border-width: 1px;
            border-style: solid;
            border-color: #e6e6e6;
            display: table-cell;
            vertical-align: inherit;
        }

        .lefttd {
            background: #f5f7fa;
            text-align: center;
            // margin-right: 4px;
            width: 14%;
        }

        .rightd {
            display: flex;
            flex-wrap: wrap;
            word-break: break-all;
            white-space: pre-wrap;
        }
    }
</style>
