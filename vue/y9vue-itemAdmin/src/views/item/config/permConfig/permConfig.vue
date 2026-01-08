<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 10:26:36
 * @Descripttion: 权限配置
 * @FilePath: \vue\y9vue-itemAdmin\src\views\item\config\permConfig\permConfig.vue
-->
<template>
    <y9Card :title="`权限配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            <el-button class="global-btn-main" type="primary" @click="deleteConfig">
                <i class="ri-delete-bin-line"></i>
                <span>清空</span>
            </el-button>
        </div>
        <y9Table :config="permListTableConfig">
            <!-- <template #>

            </template> -->
        </y9Table>

        <y9Dialog v-model:config="pcDialogConfig"> </y9Dialog>
        <permBind
            ref="permBindRef"
            :currTreeNodeInfo="currTreeNodeInfo"
            :processDefinitionId="currTreeNodeInfo.processDefinitionId"
            :taskDefKey="taskDefKey"
            @reloadTable="getPermConfig"
        />
    </y9Card>
</template>

<script lang="ts" setup>
    import { h, onMounted, reactive, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import permBind from './permBind.vue';
    import { copyPerm, getBpmList, removePerm } from '@/api/itemAdmin/item/permConfig';

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
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        permListTableConfig: {
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
                    title: '权限主体',
                    key: 'roleNames'
                },
                {
                    title: '操作',
                    render: (row) => {
                        return h(
                            'span',
                            {
                                style: {
                                    marginRight: '15px',
                                    fontWeight: 600
                                },
                                onClick: () => {
                                    taskPermBind(row);
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-user-settings-line',
                                    style: {
                                        marginRight: '4px'
                                    }
                                })
                            ],
                            '权限管理'
                        );
                    }
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        //弹窗配置
        pcDialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {
                console.log('visible', visible);
                if (!visible) {
                    getPermConfig();
                }
            }
        },
        taskDefKey: '',
        permBindRef: ''
    });

    let { currInfo, permListTableConfig, pcDialogConfig, taskDefKey, permBindRef } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getPermConfig();
        },
        { deep: true }
    );

    onMounted(() => {
        getPermConfig();
    });

    async function getPermConfig() {
        //权限配置
        permListTableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            permListTableConfig.value.tableData = res.data;
        }
    }

    async function taskPermBind(row) {
        taskDefKey.value = row.taskDefKey;
        permBindRef.value.openDrawer(
            currInfo.value,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value,
            row.taskDefName
        );
        // Object.assign(pcDialogConfig.value, {
        //     show: true,
        //     width: '40%',
        //     title: '权限管理【' + row.taskDefName + '】',
        //     showFooter: false
        // });
    }

    async function formCopy() {
        var tips = '确定复制当前版本绑定的权限到最新版本吗？';
        if (props.selectVersion === props.maxVersion) {
            tips = '确定复制上一个版本绑定的权限到最新版本吗？';
        }
        ElMessageBox.confirm(tips, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await copyPerm(props.currTreeNodeInfo.id, props.currTreeNodeInfo.processDefinitionId);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getPermConfig();
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

    function deleteConfig() {
        ElMessageBox.confirm('你确定要清空所有的权限绑定吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await removePerm(props.currTreeNodeInfo.id, props.currTreeNodeInfo.processDefinitionId);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getPermConfig();
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

<style lang="scss" scoped></style>
