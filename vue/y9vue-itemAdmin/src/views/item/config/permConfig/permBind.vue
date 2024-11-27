<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-14 16:47:40
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-16 11:20:14
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\item\config\permConfig\permBind.vue
-->
<template>
    <div class="permBindDiv">
        <div style="margin-bottom: 16px">
            <el-button-group>
                <el-button type="primary" @click="addRole(1)"><i class="ri-group-line"></i>角色</el-button>
                <el-button type="primary" @click="addRole(4)"><i class="ri-contacts-line"></i>动态角色</el-button>
                <el-button type="primary" @click="addRole(6)"><i class="ri-user-add-line"></i>岗位</el-button>
                <el-button type="primary" @click="addRole(2)"><i class="ri-team-line"></i>部门</el-button>
            </el-button-group>
        </div>
        <y9Table :config="permBindListTableConfig">
            <template #opt="{ row, column, index }">
                <span type="danger" @click="delPermBind(row)"><i class="ri-delete-bin-line"></i>删除</span>
            </template>
        </y9Table>
        <el-drawer v-model="treeDrawer" :title="title" direction="rtl">
            <permTree
                ref="permTreeRef"
                :selectField="selectField"
                :showHeader="showHeader"
                :treeApiObj="treeApiObj"
                @onCheckChange="onCheckChange"
            />
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="savePermBind"><span>保存</span></el-button>
                <el-button @click="treeDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
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
                    width: '100',
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
        title: ''
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
        selectField
    } = toRefs(data);

    onMounted(() => {
        reloadBindList();
    });

    async function reloadBindList() {
        //获取权限列表
        let res = await getBindList(props.currTreeNodeInfo.id, props.processDefinitionId, props.taskDefKey);
        if (res.success) {
            permBindListTableConfig.value.tableData = res.data;
        }
    }

    function addRole(type) {
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
            treeApiObj.value.childLevel.params.treeType = 'Department';
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
            props.taskDefKey,
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
        treeDrawer.value = false;
        reloadBindList();
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

<style>
    .permconfig .el-dialog__body {
        padding: 5px 10px;
    }

    .permBindDiv .el-drawer__header {
        margin-bottom: 0;
        padding-bottom: 16px;
        border-bottom: 1px solid #eee;
    }

    .permBindDiv .y9-card {
        box-shadow: none;
    }
</style>
