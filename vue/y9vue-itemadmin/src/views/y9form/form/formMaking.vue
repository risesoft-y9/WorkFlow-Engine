<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-07-18 14:51:00
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2025-08-07 15:22:35
-->
<template>
    <fm-making-form
        ref="makingform"
        v-loading="loading"
        :custom-fields="customFields"
        :jsonTemplates="jsonTemplates"
        clearable
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
        element-loading-text="正在保存中"
        generate-code
        generate-json
        preview
        style="height: 100%"
        upload
        @ready="handleFormReady"
    >
        <template #action>
            <el-button type="text" @click="saveForm">
                <i class="fm-iconfont icon-check-box" style="font-size: 16px; font-weight: 600" />
                保存
            </el-button>
            <el-button type="text" @click="formFieldList">
                <i class="fm-iconfont icon-slider" style="font-size: 16px; font-weight: 600" />
                表单字段
            </el-button>
        </template>
    </fm-making-form>
    <y9Dialog v-model:config="dialogConfig" class="fieldDialog">
        <el-button class="global-btn-second" @click="delFieldByFormId()">
            <i class="ri-delete-bin-line"></i>
            <span>清空所有绑定</span>
        </el-button>
        <y9Table :config="tableConfig" @on-curr-page-change="onCurrPageChange" @on-page-size-change="onPageSizeChange">
            <template #opt_button="{ row, column, index }">
                <el-button class="global-btn-second" size="small" @click="delField(row)"
                    ><i class="ri-delete-bin-line"></i>删除
                </el-button>
            </template>
            <template #contentUsedFor="{ row, column, index }">
                <font v-if="row.contentUsedFor == 'title'">文件标题</font>
                <font v-else-if="row.contentUsedFor == 'number'">文件编号</font>
                <font v-else-if="row.contentUsedFor == 'level'">紧急程度</font>
            </template>
        </y9Table>
        <selectTableAndField ref="selectTableAndFieldRef" :bindField="saveCopyField" :bindType="bindType" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { reactive } from 'vue';
    import {
        deleteByFormId,
        deleteFormFieldBind,
        getForm,
        getFormBindFieldList,
        saveFormField,
        saveFormJson
    } from '@/api/itemAdmin/y9form';

    const props = defineProps({
        formInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const data = reactive({
        bindType: 'appendBind',
        selectTableAndFieldRef: '',
        makingform: '',
        loading: false,
        customFields: [
            {
                name: '意见框',
                el: 'custom-opinion',
                options: {
                    defaultValue: {},
                    customClass: '',
                    labelWidth: 0,
                    isLabelWidth: false,
                    hidden: false,
                    dataBind: true,
                    required: false,
                    minHeight: '', //最小高度
                    pattern: ''
                }
            },
            {
                name: '附件列表',
                el: 'custom-file',
                model: 'custom_file',
                options: {
                    defaultValue: {},
                    customClass: '',
                    labelWidth: 0,
                    isLabelWidth: false,
                    hidden: false,
                    dataBind: true,
                    required: false,
                    pattern: ''
                }
            },
            {
                name: '人员树',
                el: 'custom-personTree',
                model: 'custom_personTree',
                options: {
                    defaultValue: {},
                    customClass: '',
                    labelWidth: 0,
                    isLabelWidth: false,
                    hidden: false,
                    dataBind: true,
                    required: false,
                    isTableField: true, //是否关联数据库字段
                    tableField: '', //关联数据库字段
                    pattern: ''
                }
            },
            {
                name: '编号按钮',
                el: 'custom-numberButton',
                options: {
                    defaultValue: {},
                    customClass: '',
                    labelWidth: 0,
                    isLabelWidth: false,
                    hidden: false,
                    dataBind: true,
                    required: false,
                    isTableField: true, //是否关联数据库字段
                    tableField: '', //关联数据库字段
                    pattern: ''
                }
            },
            {
                name: '图片显示',
                el: 'custom-picture',
                model: 'custom_picture',
                options: {
                    defaultValue: {},
                    customClass: '',
                    labelWidth: 0,
                    isLabelWidth: false,
                    hidden: false,
                    dataBind: true,
                    required: false,
                    tableField: '', //关联数据库字段
                    pattern: ''
                }
            }
        ],
        jsonTemplates: [], //表单模板
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            loading: false,
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {
                // console.log('visible',visible)
            }
        },
        tableConfig: {
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '表名称', key: 'tableName', width: 'auto' },
                { title: '字段名称', key: 'fieldName', width: 'auto' },
                { title: '字段中文名称', key: 'fieldCnName', width: 'auto' },
                { title: '字段类型', key: 'fieldType', width: 'auto' },
                { title: '字段内容作为', key: 'contentUsedFor', width: '130', slot: 'contentUsedFor' },
                { title: '操作', width: '80', slot: 'opt_button' }
            ],
            border: false,
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: 0, //总条目数
                pageSizeOpts: [10, 20, 30, 50]
            }
        }
    });

    let {
        bindType,
        selectTableAndFieldRef,
        makingform,
        customFields,
        loading,
        jsonTemplates,
        dialogConfig,
        tableConfig
    } = toRefs(data);

    // initForm();

    async function handleFormReady() {
        let res = await getForm(props.formInfo.id);
        if (res.success) {
            let y9form = res.data.y9Form;
            let formFieldJson = res.data.formField;
            makingform.value.clear();
            if (y9form.formJson != null && y9form.formJson != '') {
                makingform.value.setJSON(y9form.formJson);
            }
            let formField = JSON.parse(formFieldJson);
            makingform.value.setDataInfo(formField, y9form.systemName, props.formInfo.id);
        }
    }

    async function saveForm() {
        let json = makingform.value.getJSON(); //表单json数据
        let fieldBind = makingform.value.getFieldBind(); //表单字段绑定数据
        // let formJson = JSON.stringify(json).toString();
        let fieldBindJson = JSON.stringify(fieldBind).toString();
        loading.value = true;
        let res = await saveFormJson(props.formInfo.id, json);
        loading.value = false;
        ElNotification({
            title: res.success ? '成功' : '失败',
            message: res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (res.success) {
            let res1 = await saveFormField(props.formInfo.id, fieldBindJson);
            if (!res1.success) {
                ElNotification({
                    title: res1.success ? '成功' : '失败',
                    message: res1.msg,
                    type: res1.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
            }
        }
    }

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

    async function formFieldList() {
        Object.assign(dialogConfig.value, {
            show: true,
            width: '65%',
            title: '字段绑定详情',
            showFooter: false
        });
        reloadTable();
    }

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        let res = await getFormBindFieldList(props.formInfo.id, page, rows);
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    async function delField(row) {
        ElMessageBox.confirm('您确定要删除绑定字段吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(async () => {
                let res = await deleteFormFieldBind(row.id);
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    makingform.value.removeFormField(row);
                    tableConfig.value.pageConfig.currentPage = 1;
                    reloadTable();
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
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

    async function delFieldByFormId() {
        ElMessageBox.confirm('您确定【清空所有绑定】吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(async () => {
                let res = await deleteByFormId(props.formInfo.id);
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    makingform.value.removeAllFormField();
                    reloadTable();
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消清空所有绑定',
                    offset: 65
                });
            });
    }
</script>

<style lang="scss">
    .formMaking .el-dialog__body {
        height: calc(100% - 55px);
        padding: 10px;
    }
</style>
<style lang="scss" scoped>
    :deep(.components-list .form-edit-widget-label) {
        border-radius: 4px 4px !important;
        border: none;
        // background-color: var(--el-color-primary-light-5);
    }

    :deep(.components-list .form-edit-widget-label a:hover) {
        color: var(--el-color-primary);
    }

    :deep(.el-button) {
        box-shadow: none !important;
    }

    :deep(.widget-form-container.pc) {
        top: 0px;
        right: 0px;
        left: 0px;
    }

    :deep(.center-container .btn-bar) {
        border-bottom: solid 1px var(--fm-border-color);
    }

    .y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-header {
        border-bottom: none;
    }

    :deep(.widget-config-container .el-header) {
        border-bottom: 1px solid var(--fm-border-color);
    }

    .fm2-container {
        border: none;
    }

    :deep(.el-tabs__nav-wrap) {
        height: 46px !important;
    }

    :deep(.el-tabs__nav-wrap::after) {
        visibility: hidden;
    }

    :deep(.widget-form-container form) {
        border: none !important;
    }

    .widget-left-panel .container-left-arrow::before {
        content: '';
        border-bottom: 1px;
        border-top: 1px;
        border-right: 1px;
    }
</style>
<style>
    .fieldDialog .el-pagination__sizes {
        width: 120px !important;
    }
</style>
