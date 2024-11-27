<template>
    <div class="organWordBindDiv">
        <div style="margin-bottom: 16px">
            <el-button-group>
                <el-button type="primary" @click="addButton"><i class="ri-add-line"></i>按钮</el-button>
                <el-button type="primary" @click="delButton"><i class="ri-delete-bin-line"></i>删除</el-button>
                <el-button type="primary" @click="addRole"><i class="ri-user-add-line"></i>角色</el-button>
                <el-button type="primary" @click="delRole"><i class="ri-delete-bin-line"></i>删除角色</el-button>
            </el-button-group>
        </div>
        <y9Table
            :config="buttonBindTableConfig"
            highlight-current-row
            @select="handlerSelectData"
            @select-all="handlerSelectData"
        ></y9Table>
        <el-drawer v-model="tableDrawer" :title="title" direction="rtl">
            <y9Table :config="buttonTableConfig" @select="handlerGetData" @select-all="handlerGetData"></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="saveBind"><span>保存</span></el-button>
                <el-button @click="tableDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
        <el-drawer v-model="treeDrawer" :title="title" direction="rtl">
            <permTree
                ref="permTreeRef"
                :selectField="selectField"
                :showHeader="showHeader"
                :treeApiObj="treeApiObj"
                @onCheckChange="onCheckChange"
            />
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="saveRoleBind"><span>保存</span></el-button>
                <el-button @click="treeDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
        <el-drawer v-model="roleDrawer" :title="title" direction="rtl" @close="closeRoleTable">
            <y9Table :config="roleTableConfig" @select="handlerData" @select-all="handlerData"></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="delRoleBind"><span>删除</span></el-button>
                <el-button @click="roleDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
    import {
        bindRole,
        getBindList,
        getButtonList,
        getRoleList,
        removeButton,
        removeRole,
        saveBindButton
    } from '@/api/itemAdmin/item/buttonConfig';
    import { getRole, getRoleById } from '@/api/itemAdmin/item/permConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        processDefinitionId: String,
        taskDefKey: String,
        buttonType: Number
    });

    let total = ref(0);
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
                value: ['role']
            }
        ],
        treeSelectedData: [], //tree选择的数据
        showHeader: true,
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        buttonBindTableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                { title: '序号', type: 'index', width: '60' },
                { title: '按钮名称', key: 'buttonName' },
                { title: '按钮标识', key: 'buttonCustomId' },
                { title: '角色名称', key: 'roleNames', width: '150' },
                { title: '操作人', key: 'userName', width: '100' },
                { title: '绑定时间', key: 'updateTime', width: '180' }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        buttonTableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                { title: '序号', type: 'index', width: '60' },
                { title: '按钮名称', key: 'name' },
                { title: '唯一标示', key: 'customId', width: '200' }
            ],
            tableData: [],
            pageConfig: false,
            height: 'auto'
        },
        roleTableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                { title: '序号', type: 'index', width: '60' },
                { title: '角色名称', key: 'roleName' }
            ],
            tableData: [],
            pageConfig: false,
            height: 'auto'
        },
        //弹窗配置
        butDialogConfig: {
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
        tableDrawer: false,
        treeDrawer: false,
        roleDrawer: false,
        roleType: '',
        title: '',
        buttonArr: [],
        buttonBindArr: [],
        roleBindArr: [],
        roleIdArr: []
    });

    let {
        permTreeRef,
        currInfo,
        buttonBindTableConfig,
        buttonTableConfig,
        roleTableConfig,
        butDialogConfig,
        bindList,
        treeDrawer,
        tableDrawer,
        roleDrawer,
        treeApiObj,
        title,
        showHeader,
        roleType,
        treeSelectedData,
        buttonArr,
        buttonBindArr,
        roleBindArr,
        roleIdArr,
        selectField
    } = toRefs(data);

    watch(
        () => props.buttonType,
        (newVal) => {
            reloadBindList();
        }
    );

    onMounted(() => {
        reloadBindList();
    });

    async function reloadBindList() {
        buttonBindArr.value = [];
        roleBindArr.value = [];
        roleIdArr.value = [];
        buttonArr.value = [];
        let res = await getBindList(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
            props.buttonType
        );
        if (res.success) {
            buttonBindTableConfig.value.tableData = res.data;
        }
    }

    // 表格 选择框 选择后获取数据
    function handlerGetData(rows) {
        buttonArr.value = rows;
    }

    function handlerSelectData(rows) {
        buttonBindArr.value = [];
        for (let obj of rows) {
            buttonBindArr.value.push(obj.id);
        }
        roleBindArr.value = rows;
    }

    function handlerData(rows) {
        roleIdArr.value = [];
        for (let obj of rows) {
            roleIdArr.value.push(obj.id);
        }
    }

    function getButtonTable() {
        buttonArr.value = [];
        getButtonList(props.currTreeNodeInfo.id, props.processDefinitionId, props.taskDefKey, props.buttonType).then(
            (res) => {
                if (res.success) {
                    buttonTableConfig.value.tableData = res.data.rows;
                }
            }
        );
    }

    function addButton() {
        title.value = '添加按钮';
        tableDrawer.value = true;
        getButtonTable();
    }

    function addRole() {
        if (buttonBindArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        if (buttonBindArr.value.length > 1) {
            ElNotification({
                title: '操作提示',
                message: '只能选择一条绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        treeSelectedData.value = [];
        treeApiObj.value.topLevel = getRole;
        treeApiObj.value.childLevel.api = getRoleById;
        treeApiObj.value.childLevel.params.treeType = 'Role';
        title.value = '绑定角色';
        showHeader.value = false;

        treeDrawer.value = true;
        setTimeout(() => {
            permTreeRef.value.onRefreshTree();
        }, 500);
    }

    function delButton() {
        if (buttonBindArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请勾选要删除的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        ElMessageBox.confirm('你确定要删除绑定的按钮吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await removeButton(buttonBindArr.value.toString());
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

    async function delRole() {
        if (roleBindArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        if (roleBindArr.value.length > 1) {
            ElNotification({
                title: '操作提示',
                message: '只能选择一条绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        if (roleBindArr.value[0].roleIds.length == 1) {
            ElMessageBox.confirm('你确定要删除绑定的角色吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            })
                .then(async () => {
                    let result = { success: false, msg: '' };
                    result = await removeRole(roleBindArr.value[0].roleIds.toString());
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
        } else {
            title.value = '删除角色';
            roleDrawer.value = true;
            getRoleTable();
        }
    }

    async function getRoleTable() {
        roleIdArr.value = [];
        let res = await getRoleList(buttonBindArr.value[0]);
        if (res.success) {
            roleTableConfig.value.tableData = res.data;
        }
    }

    async function saveBind() {
        if (buttonArr.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请勾选一条数据', type: 'error', duration: 2000, offset: 80 });
            return;
        }
        if (buttonArr.value.length > 1) {
            ElNotification({
                title: '操作提示',
                message: '只能勾选一条数据' + title.value,
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let res = await saveBindButton(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
            buttonArr.value[0].id,
            props.buttonType
        );
        ElNotification({
            title: '操作提示',
            message: res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        tableDrawer.value = false;
        reloadBindList();
    }

    //tree点击选择框时触发
    const onCheckChange = (node, isChecked) => {
        //已经选择的节点
        treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
    };

    async function saveRoleBind() {
        if (treeSelectedData.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择' + title.value,
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        let rIds = [];
        for (let i = 0; i < treeSelectedData.value.length; i++) {
            rIds.push(treeSelectedData.value[i].id);
        }

        let result = await bindRole(rIds.join(';'), buttonBindArr.value[0]);
        ElNotification({
            title: '操作提示',
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        treeDrawer.value = false;
        reloadBindList();
    }

    async function delRoleBind() {
        if (roleIdArr.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请选择角色', type: 'error', duration: 2000, offset: 80 });
            return;
        }
        ElMessageBox.confirm('你确定要删除绑定的角色吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await removeRole(roleIdArr.value.toString());
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getRoleTable();
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

    function closeRoleTable() {
        reloadBindList();
    }
</script>

<style>
    .organWordBindDiv .el-drawer__header {
        margin-bottom: 0;
        padding-bottom: 16px;
        border-bottom: 1px solid #eee;
    }

    .organWordBindDiv .y9-card {
        box-shadow: none;
    }

    .organWordBindDiv .optBtn {
        border-radius: 50%;
        background: #586cb1;
        border-color: #586cb1;
        color: #fff;
        border: 1px solid #dcdfe6;
        padding: 9px;
    }

    .organWordBindDiv .optBtnFalse {
        border-radius: 50%;
        background: #a6a9ad;
        border-color: #a6a9ad;
        color: #fff;
        border: 1px solid #dcdfe6;
        padding: 9px;
    }
</style>
