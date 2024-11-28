<template>
    <y9Card :title="`启动路由配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            <el-row :gutter="40">
                <el-col :span="2">
                    <el-button v-if="maxVersion != 1" class="global-btn-main" type="primary" @click="formCopy">
                        <i class="ri-file-copy-2-line"></i>
                        <span>复制</span>
                    </el-button>
                    <!-- <el-button @click="nodeOrder"
                        class="global-btn-main">
                        <i class="ri-file-copy-2-line"></i>
                        <span>排序</span>
                    </el-button> -->
                </el-col>
                <el-col :span="17" style="padding-left: 0">
                    <el-tooltip
                        content="优先级越高，用户启动流程的时候就会优先判断是否有从该节点启动流程的权限。如果所有节点都没有权限，则以优先级最低的节点启动流程"
                        effect="customized"
                        placement="right"
                    >
                        <el-button size="small"><i class="ri-questionnaire-line"></i>优先级说明</el-button>
                    </el-tooltip>
                </el-col>
                <el-col :span="5">
                    <el-button-group>
                        <el-button size="small" type="primary" @click="moveUp"
                            ><i class="ri-arrow-up-line"></i>上移
                        </el-button>
                        <el-button size="small" type="primary" @click="moveDown"
                            ><i class="ri-arrow-down-line"></i>下移
                        </el-button>
                        <el-button type="primary" @click="saveNodeOrder"><span>保存</span></el-button>
                    </el-button-group>
                </el-col>
            </el-row>
        </div>
        <y9Table :config="startNodeTableConfig" @on-current-change="handleCurrentChange">
            <template #opt_button="{ row, column, index }">
                <span style="margin-right: 15px" @click="addRole(row)"><i class="ri-add-line"></i>绑定角色</span>
                <span v-if="row.roleIds.length > 0" @click="delRole(row)"
                    ><i class="ri-delete-bin-line"></i>删除角色</span
                >
            </template>
        </y9Table>
        <el-drawer v-model="treeDrawer" :title="title" class="eldrawer" direction="rtl">
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
        <el-drawer v-model="roleDrawer" :title="title" class="eldrawer" direction="rtl" @close="closeRoleTable">
            <y9Table :config="roleTableConfig" @select="handlerData" @select-all="handlerData"></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="delRoleBind"><span>确定</span></el-button>
                <el-button @click="roleDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object.ts';
    import {
        bindRole,
        copyBind,
        getBpmList,
        getRoleList,
        removeRole,
        saveOrder
    } from '@/api/itemAdmin/item/startNodeConfig';
    import { getRole, getRoleById } from '@/api/itemAdmin/item/permConfig';

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
        title: '',
        treeDrawer: false,
        roleDrawer: false,
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
        currentRow: [],
        showHeader: true,
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        startNodeTableConfig: {
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
                    title: '优先级',
                    key: 'tabIndex',
                    width: '100'
                },
                {
                    title: '角色',
                    key: 'roleNames'
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
        roleTableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                { title: '序号', type: 'index', width: '60' },
                { title: '角色名称', key: 'name' }
            ],
            tableData: [],
            pageConfig: false
        },
        taskDefKey: ''
    });

    let {
        currentRow,
        permTreeRef,
        treeDrawer,
        roleDrawer,
        treeApiObj,
        title,
        showHeader,
        treeSelectedData,
        currInfo,
        startNodeTableConfig,
        roleTableConfig,
        taskDefKey,
        selectField
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getStartNodeList();
        },
        { deep: true }
    );

    onMounted(() => {
        getStartNodeList();
    });

    async function getStartNodeList() {
        //权限配置
        startNodeTableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            startNodeTableConfig.value.tableData = res.data;
        }
    }

    function addRole(row) {
        treeSelectedData.value = [];
        treeApiObj.value.topLevel = getRole;
        treeApiObj.value.childLevel.api = getRoleById;
        treeApiObj.value.childLevel.params.treeType = 'Role';
        title.value = '绑定角色';
        showHeader.value = false;
        treeDrawer.value = true;
        taskDefKey.value = row.taskDefKey;
        setTimeout(() => {
            permTreeRef.value.onRefreshTree();
        }, 500);
    }

    const handleCurrentChange = (val) => {
        currentRow.value = val;
    };

    const moveUp = () => {
        //上移
        if (currentRow.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请点击选择一条数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        let index = 0;
        for (let i = 0; i < startNodeTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == startNodeTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = startNodeTableConfig.value.tableData[index - 1];
            let currRow = startNodeTableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            startNodeTableConfig.value.tableData.splice(index - 1, 1);
            startNodeTableConfig.value.tableData.splice(index, 0, upRow);
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
        for (let i = 0; i < startNodeTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == startNodeTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == startNodeTableConfig.value.tableData.length) {
            ElNotification({
                title: '操作提示',
                message: '已经是最后一条，不可下移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        } else {
            let downRow = startNodeTableConfig.value.tableData[index + 1];
            let currRow = startNodeTableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            startNodeTableConfig.value.tableData.splice(index + 1, 1);
            startNodeTableConfig.value.tableData.splice(index, 0, downRow);
        }
    };

    function saveNodeOrder() {
        let ids = [];
        for (let item of startNodeTableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            loading.close();
            if (res.success) {
                ElNotification({ title: '操作提示', message: res.msg, type: 'success', duration: 2000, offset: 80 });
                getStartNodeList();
            } else {
                ElNotification({ title: '操作提示', message: res.msg, type: 'error', duration: 2000, offset: 80 });
            }
        });
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
        let result = await bindRole(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value,
            rIds.join(';')
        );
        ElNotification({
            title: '操作提示',
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        treeDrawer.value = false;
        getStartNodeList();
    }

    function closeRoleTable() {
        getStartNodeList();
    }

    async function getRoleTable() {
        let res = await getRoleList(
            props.currTreeNodeInfo.id,
            props.currTreeNodeInfo.processDefinitionId,
            taskDefKey.value
        );
        if (res.success) {
            roleTableConfig.value.tableData = res.data;
        }
    }

    const roleIdArr = ref([]);

    function handlerData(rows) {
        for (let obj of rows) {
            roleIdArr.value.push(obj.id);
        }
    }

    function delRole(row) {
        taskDefKey.value = row.taskDefKey;
        if (row.roleIds.length == 1) {
            ElMessageBox.confirm('你确定要删除绑定的角色吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            })
                .then(async () => {
                    let result = { success: false, msg: '' };
                    result = await removeRole(
                        props.currTreeNodeInfo.id,
                        props.currTreeNodeInfo.processDefinitionId,
                        taskDefKey.value,
                        row.roleIds.toString()
                    );
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        getStartNodeList();
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
            roleIdArr.value = [];
            getRoleTable();
        }
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
                result = await removeRole(
                    props.currTreeNodeInfo.id,
                    props.currTreeNodeInfo.processDefinitionId,
                    taskDefKey.value,
                    roleIdArr.value.toString()
                );
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    roleDrawer.value = false;
                    getStartNodeList();
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
                    getStartNodeList();
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
