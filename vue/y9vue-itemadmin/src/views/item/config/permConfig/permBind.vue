<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-14 16:47:40
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 10:26:25
 * @FilePath: \vue\y9vue-itemAdmin\src\views\item\config\permConfig\permBind.vue
-->
<template>
    <div class="permBindDiv">
        <el-drawer v-model="treeDrawer" :title="title" direction="rtl" @closed="closeDrawer">
            <div v-if="drawerShow" style="margin-bottom: 16px">
                <el-button-group>
                    <el-button type="primary" @click="addRole(1)"><i class="ri-group-line"></i>角色</el-button>
                    <el-button type="primary" @click="addRole(4)"><i class="ri-contacts-line"></i>动态角色</el-button>
                    <el-button type="primary" @click="addRole(6)"><i class="ri-user-add-line"></i>岗位</el-button>
                    <el-button type="primary" @click="addRole(2)"><i class="ri-team-line"></i>部门</el-button>
                </el-button-group>
                <y9Table :config="permBindListTableConfig">
                    <template #opt="{ row, column, index }">
                        <span @click="delPermBind(row)"><i class="ri-delete-bin-line"></i>删除</span>
                    </template>
                </y9Table>
            </div>
            <permTree
                v-else
                ref="permTreeRef"
                :selectField="selectField"
                :showHeader="showHeader"
                :treeApiObj="treeApiObj"
                @onCheckChange="onCheckChange"
            />
            <template #footer>
                <div class="drawer-footer">
                    <el-button v-if="!drawerShow" type="primary" @click="savePermBind"><span>保存</span></el-button>
                    <el-button v-if="!drawerShow" @click="closeRoleForm"><span>取消</span></el-button>
                    <el-button v-if="drawerShow" @click="treeDrawer = false"><span>关闭</span></el-button>
                </div>
            </template>
            <!-- <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="savePermBind"><span>保存</span></el-button>
                <el-button @click="treeDrawer = false"><span>取消</span></el-button>
            </div> -->
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
    import { reactive, toRefs } from 'vue';
    import {
        deleteBind,
        dynamicRole,
        getBindList,
        getOrgList,
        getOrgTree,
        getRole,
        getRoleById,
        saveBind,
        treeSearch
    } from '@/api/itemAdmin/item/permConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        processDefinitionId: String,
        taskDefKey: String
    });

    const data = reactive({
        permTreeRef: '', //tree实例
        treeApiObj: {
            //tree接口对象
            topLevel: '',
            childLevel: {
                //子级（二级及二级以上）tree接口
                api: '',
                params: { treeType: '' }
            },
            search: {
                api: '',
                params: {
                    key: '',
                    treeType: ''
                }
            }
        },
        selectField: [
            //设置需要选择的字段
            {
                fieldName: 'orgType',
                value: []
            }
        ],
        treeSelectedData: [], //tree选择的数据
        showHeader: true,
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        permBindListTableConfig: {
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '类型',
                    key: 'roleType',
                    width: '100',
                    render: (row) => {
                        let typeStr = '';
                        switch (row.roleType) {
                            case 1:
                                typeStr = '角色';
                                break;
                            case 2:
                                typeStr = '部门';
                                break;
                            case 6:
                                typeStr = '岗位';
                                break;
                            case 4:
                                typeStr = '动态角色';
                                break;
                            default:
                                typeStr = '';
                                break;
                        }
                        return typeStr;
                    }
                },
                {
                    title: '名称',
                    key: 'roleName'
                },
                {
                    title: '操作',
                    width: '150',
                    slot: 'opt'
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
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
                // console.log('visible',visible)
            }
        },
        bindList: [],
        treeDrawer: false,
        roleType: '',
        title: '',
        drawerShow: true,
        taskDefKey: '',
        taskDefName: ''
    });

    let {
        permTreeRef,
        currInfo,
        permBindListTableConfig,
        dialogConfig,
        bindList,
        treeDrawer,
        treeApiObj,
        title,
        showHeader,
        roleType,
        treeSelectedData,
        selectField,
        drawerShow,
        taskDefKey,
        taskDefName
    } = toRefs(data);

    function openDrawer(currTreeNodeInfo, processDefinitionId, taskDefKeyVal, taskDefNameVal) {
        console.log('openDrawer-currTreeNodeInfo', props.currTreeNodeInfo, props.processDefinitionId, taskDefKey.value);
        console.log('openDrawer-taskDefKey', taskDefKeyVal);
        taskDefKey.value = taskDefKeyVal;
        taskDefName.value = taskDefNameVal;
        title.value = '权限管理【' + taskDefNameVal + '】';
        treeDrawer.value = true;
        drawerShow.value = true;
        reloadBindList();
    }

    defineExpose({
        openDrawer
    });

    function closeRoleForm() {
        drawerShow.value = true;
        title.value = taskDefName.value;
    }

    const emits = defineEmits(['reloadTable']);
    function closeDrawer() {
        treeDrawer.value = false;
        emits('reloadTable');
    }
    async function reloadBindList() {
        //获取权限列表
        let res = await getBindList(props.currTreeNodeInfo.id, props.processDefinitionId, taskDefKey.value);
        if (res.success) {
            permBindListTableConfig.value.tableData = res.data;
        }
    }

    function addRole(type) {
        drawerShow.value = false;
        treeSelectedData.value = [];
        if (type == 1) {
            treeApiObj.value.topLevel = getRole;
            treeApiObj.value.childLevel.api = getRoleById;
            treeApiObj.value.childLevel.params.treeType = 'Role';
            title.value = '添加角色';
            showHeader.value = false;
            selectField.value[0].value = ['role'];
        } else if (type == 2) {
            treeApiObj.value.topLevel = getOrgList;
            treeApiObj.value.childLevel.api = getOrgTree;
            treeApiObj.value.childLevel.params.treeType = 'tree_type_dept';
            treeApiObj.value.search.api = treeSearch;
            treeApiObj.value.search.params.treeType = 'tree_type_dept';
            title.value = '添加部门';
            showHeader.value = true;
            selectField.value[0].value = ['Department'];
        } else if (type == 6) {
            treeApiObj.value.topLevel = getOrgList;
            treeApiObj.value.childLevel.api = getOrgTree;
            treeApiObj.value.childLevel.params.treeType = 'tree_type_position';
            treeApiObj.value.search.api = treeSearch;
            treeApiObj.value.search.params.treeType = 'tree_type_position';
            title.value = '添加岗位';
            showHeader.value = true;
            selectField.value[0].value = ['Position'];
        } else if (type == 4) {
            treeApiObj.value.topLevel = dynamicRole;
            treeApiObj.value.childLevel.params.treeType = 'dynamicRole';
            title.value = '添加动态角色';
            showHeader.value = false;
            selectField.value[0].value = ['dynamicRole'];
        }
        treeDrawer.value = true;
        setTimeout(() => {
            permTreeRef.value.onRefreshTree();
        }, 500);

        roleType.value = type;
    }

    //tree点击选择框时触发
    const onCheckChange = (node, isChecked) => {
        //已经选择的节点
        treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
    };

    async function savePermBind() {
        if (treeSelectedData.value.length == 0) {
            ElNotification({
                title: '提示',
                message: '请选择' + title.value,
                type: 'info',
                duration: 2000,
                offset: 80
            });
            reject();
            return;
        }
        let roleIds = [];
        for (let i = 0; i < treeSelectedData.value.length; i++) {
            roleIds.push(treeSelectedData.value[i].id);
        }
        let res = await saveBind(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            taskDefKey.value,
            roleIds.join(';'),
            roleType.value
        );
        ElNotification({
            title: '操作提示',
            message: res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        //treeDrawer.value = false;
        drawerShow.value = true;
        reloadBindList();
        emits('reloadTable');
    }

    function delPermBind(row) {
        ElMessageBox.confirm('你确定要删除绑定的数据', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteBind(row.id);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    reloadBindList();
                    emits('reloadTable');
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
    .permBindDiv {
        :deep(.el-dialog__body) {
            padding: 5px 10px;
        }

        :deep(.el-drawer__header) {
            margin-bottom: 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #eee;
        }

        :deep(.y9-card) {
            box-shadow: none;
        }
        :deep(.el-drawer__body) {
            margin-bottom: 15px;
        }
        :deep(.drawer-footer) {
            position: fixed;
            width: 30%;
            bottom: 0;
            right: 0;
            padding: 15px;
            text-align: center;
            background: white;
            box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
        }
    }
</style>
