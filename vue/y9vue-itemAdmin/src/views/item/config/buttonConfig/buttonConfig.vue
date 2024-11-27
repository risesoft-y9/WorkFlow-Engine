<template>
    <y9Card :title="`按钮配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            <el-button v-if="maxVersion != 1" class="global-btn-main" type="primary" @click="formCopy">
                <i class="ri-file-copy-2-line"></i>
                <span>复制</span>
            </el-button>
        </div>
        <y9Table v-model:selectedVal="userSelectedData" :config="buttonTableConfig">
            <template #opt_button="{ row, column, index }">
                <span style="margin-right: 15px" @click="buttonBindManage(row, 1)"
                    ><i class="ri-add-line"></i>普通按钮</span
                >
                <span @click="buttonBindManage(row, 2)"><i class="ri-add-line"></i>发送按钮</span>
            </template>
        </y9Table>

        <y9Dialog v-model:config="butcDialogConfig">
            <buttonBind
                ref="buttonBindRef"
                :buttonType="buttonType"
                :currTreeNodeInfo="currTreeNodeInfo"
                :processDefinitionId="currTreeNodeInfo.processDefinitionId"
                :taskDefKey="taskDefKey"
            />
        </y9Dialog>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object.ts';
    import buttonBind from './buttonBind.vue';
    import { copyBind, getBpmList } from '@/api/itemAdmin/item/buttonConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        maxVersion: Number,
        selectVersion: Number
    });

    const data = reactive({
        userSelectedData: '',
        buttonType: 1,
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        buttonTableConfig: {
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
                    title: '普通按钮',
                    key: 'commonButtonNames',
                    width: '200'
                },
                {
                    title: '发送按钮',
                    key: 'sendButtonNames',
                    width: '200'
                },
                {
                    title: '操作',
                    width: '220',
                    slot: 'opt_button'
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        //弹窗配置
        butcDialogConfig: {
            show: false,
            width: '',
            title: '',
            showFooter: false,
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {
                console.log('visible', visible);
                if (!visible) {
                    getButtonList();
                }
            }
        },
        taskDefKey: ''
    });

    let { userSelectedData, buttonType, currInfo, buttonTableConfig, butcDialogConfig, taskDefKey } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getButtonList();
        },
        { deep: true }
    );

    onMounted(() => {
        getButtonList();
    });

    async function getButtonList() {
        //权限配置
        buttonTableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            buttonTableConfig.value.tableData = res.data;
        }
    }

    function buttonBindManage(row, type) {
        buttonType.value = type;
        let bindTitle = '普通按钮';
        if (type == 2) {
            bindTitle = '发送按钮';
        }
        taskDefKey.value = row.taskDefKey;
        Object.assign(butcDialogConfig.value, {
            show: true,
            width: '50%',
            title: bindTitle + '【' + row.taskDefName + '】',
            showFooter: false
        });
    }

    async function formCopy() {
        var tips = '确定复制当前版本绑定的配置到最新版本吗？';
        if (props.selectVersion === props.maxVersion) {
            tips = '确定复制上一个版本绑定的配置到最新版本吗？';
        }
        ElMessageBox.confirm(tips, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await copyBind(props.currTreeNodeInfo.id, props.currTreeNodeInfo.processDefinitionId);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getButtonList();
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
    .permconfig .el-dialog__body {
        padding: 5px 10px;
    }

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
