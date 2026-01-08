<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-13 09:49:46
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-30 15:16:09
 * @Descripttion: 复制绑定
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\config\processVersionConfig\processVersionConfig.vue
-->
<template>
    <y9Card :title="`流程版本选择${currInfo.name ? ' - ' + currInfo.name : ''}`">
        流程定义版本
        <el-select v-model="pVersion" style="width: 70px; margin-right: 15px; margin-left: 15px" @change="pIdchange">
            <el-option v-for="pd in processDefinitionList" :key="pd.id" :label="pd.version" :value="pd.version">
            </el-option>
        </el-select>
        <el-button v-if="maxVersion != 1" class="global-btn-main" type="primary" @click="copyBind">
            <i class="ri-file-copy-2-line"></i>
            <span>复制</span>
        </el-button>
        <el-tooltip
            placement="right"
            content="复制绑定信息包括：表单绑定、权限、意见框绑定、编号绑定、正文模板绑定、签收配置绑定、路由配置、按钮配置、链接节点配置、任务时间配置"
            effect="customized"
        >
            <el-button><i class="ri-questionnaire-line"></i>说明</el-button>
        </el-tooltip>
    </y9Card>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import { copyForm, getBindList, getBpmList } from '@/api/itemAdmin/item/formConfig';
    import { copyAllBind } from '@/api/itemAdmin/item/processVersionConfig';

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
                return new Promise(async (resolve, reject) => {});
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
    });

    async function getFormConfig() {
        formListTableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            formListTableConfig.value.tableData = res.data;
        }
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
    async function copyBind() {
        var tips = '确定复制当前版本的绑定到最新版本吗？';
        if (props.selectVersion === props.maxVersion) {
            tips = '确定复制上一个版本的绑定到最新版本吗？';
        }
        ElMessageBox.confirm(tips, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await copyAllBind(props.currTreeNodeInfo.id, props.currTreeNodeInfo.processDefinitionId);
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
<style lang="scss">
    .el-popper.is-customized {
        /* Set padding to ensure the height is 32px */
        padding: 6px 12px;
        background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
    }

    .el-popper.is-customized .el-popper__arrow::before {
        background: linear-gradient(45deg, #b2e68d, #bce689);
        right: 0;
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
