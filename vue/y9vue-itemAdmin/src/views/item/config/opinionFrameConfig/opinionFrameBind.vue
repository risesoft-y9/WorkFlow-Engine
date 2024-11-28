<template>
    <div class="opinionBindDiv">
        <div style="margin-bottom: 16px">
            <el-button-group>
                <el-button type="primary" @click="addOpinion"><i class="ri-add-line"></i>意见框</el-button>
                <el-button type="primary" @click="delOpinion"><i class="ri-delete-bin-line"></i>删除绑定</el-button>
                <el-button type="primary" @click="addRole"><i class="ri-user-add-line"></i>角色</el-button>
                <el-button type="primary" @click="delRole"><i class="ri-delete-bin-line"></i>删除角色</el-button>
            </el-button-group>
        </div>
        <y9Table :config="oFBListTableConfig" @select="handlerSelectData" @select-all="handlerSelectData"></y9Table>
        <el-drawer v-model="tableDrawer" :title="title" direction="rtl">
            <div style="margin: 8px 0; text-align: left">
                <el-input
                    v-model="opinionFrameName"
                    clearable
                    placeholder="意见框名称"
                    style="width: 200px; margin-right: 5px"
                ></el-input>
                <el-button size="small" type="primary" @click="search"><i class="ri-search-2-line"></i>搜索</el-button>
            </div>
            <y9Table
                :config="oFTableConfig"
                @select="handlerGetData"
                @select-all="handlerGetData"
                @on-curr-page-change="onCurrPageChange"
                @on-page-size-change="onPageSizeChange"
            ></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="saveOFBind"><span>保存</span></el-button>
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
        bindOpinionFrame,
        bindRole,
        changeSignOpinion,
        getBindList,
        getRoleList,
        removeOpinionFrame,
        removeRole,
        searchOpinionFrame
    } from '@/api/itemAdmin/item/opinionFrameConfig';
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
        taskDefKey: String
    });

    let total = ref(0);
    const data = reactive({
        opinionFrameName: '',
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
        oFBListTableConfig: {
            columns: [
                { title: '', width: '60', type: 'selection', fixed: 'left' },
                { title: '序号', type: 'index', width: '60' },
                { title: '意见框标识', key: 'opinionFrameMark' },
                { title: '意见框名称', key: 'opinionFrameName' },
                {
                    title: '必签意见',
                    key: 'signOpinion',
                    width: '100',
                    render: (row) => {
                        return h(
                            'button',
                            {
                                class: row.signOpinion ? 'optBtn' : 'optBtnFalse',
                                onClick: () => {
                                    changeSignOpinion(row.id, !row.signOpinion).then((res) => {
                                        ElNotification({
                                            title: '操作提示',
                                            message: res.msg,
                                            type: res.success ? 'success' : 'error',
                                            duration: 2000,
                                            offset: 80
                                        });
                                        if (res.success) {
                                            reloadBindList();
                                        }
                                    });
                                }
                            },
                            row.signOpinion ? '是' : '否'
                        );
                    }
                },
                { title: '角色名称', key: 'roleNames', width: '150' },
                { title: '操作人', key: 'userName', width: '100' },
                { title: '绑定时间', key: 'createDate', width: '180' }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        oFTableConfig: {
            columns: [
                { title: '', width: '60', type: 'selection', fixed: 'left' },
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '意见框名称',
                    key: 'name'
                },
                {
                    title: '唯一标示',
                    key: 'mark',
                    width: '200'
                }
            ],
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value //总条目数
            },
            height: 'auto'
        },
        roleTableConfig: {
            columns: [
                { title: '', type: 'selection', width: '60', fixed: 'left' },
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '角色名称',
                    key: 'roleName'
                }
            ],
            tableData: [],
            pageConfig: false,
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
        tableDrawer: false,
        treeDrawer: false,
        roleDrawer: false,
        roleType: '',
        title: '',
        opinionFrameBindArr: [],
        roleBindArr: [],
        roleIdArr: [],
        opinionFrameArr: []
    });

    let {
        opinionFrameName,
        permTreeRef,
        currInfo,
        oFBListTableConfig,
        oFTableConfig,
        roleTableConfig,
        dialogConfig,
        bindList,
        treeDrawer,
        tableDrawer,
        roleDrawer,
        treeApiObj,
        title,
        showHeader,
        roleType,
        treeSelectedData,
        opinionFrameBindArr,
        roleBindArr,
        roleIdArr,
        opinionFrameArr,
        selectField
    } = toRefs(data);

    onMounted(() => {
        reloadBindList();
    });

    async function reloadBindList() {
        opinionFrameBindArr.value = [];
        roleBindArr.value = [];
        roleIdArr.value = [];
        opinionFrameArr.value = [];
        let res = await getBindList(props.currTreeNodeInfo.id, props.processDefinitionId, props.taskDefKey);
        if (res.success) {
            oFBListTableConfig.value.tableData = res.data;
        }
    }

    // 表格 选择框 选择后获取数据
    function handlerGetData(rows) {
        opinionFrameArr.value = rows;
    }

    function handlerSelectData(rows) {
        opinionFrameBindArr.value = [];
        for (let obj of rows) {
            opinionFrameBindArr.value.push(obj.id);
        }
        roleBindArr.value = rows;
    }

    function handlerData(rows) {
        roleIdArr.value = [];
        for (let obj of rows) {
            roleIdArr.value.push(obj.id);
        }
    }

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        oFTableConfig.value.pageConfig.currentPage = currPage;
        getOpinionFrameLists();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        oFTableConfig.value.pageConfig.pageSize = pageSize;
        getOpinionFrameLists();
    }

    function getOpinionFrameLists() {
        opinionFrameArr.value = [];
        let page = oFTableConfig.value.pageConfig.currentPage;
        let rows = oFTableConfig.value.pageConfig.pageSize;
        searchOpinionFrame(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
            opinionFrameName.value,
            page,
            rows
        ).then((res) => {
            if (res.success) {
                oFTableConfig.value.tableData = res.rows;
                oFTableConfig.value.pageConfig.total = res.total;
            }
        });
    }

    function addOpinion() {
        title.value = '绑定意见框';
        tableDrawer.value = true;
        getOpinionFrameLists();
    }

    function search() {
        getOpinionFrameLists();
    }

    function addRole() {
        if (opinionFrameBindArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        if (opinionFrameBindArr.value.length > 1) {
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

    function delOpinion() {
        if (opinionFrameBindArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择绑定的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        ElMessageBox.confirm('你确定要删除绑定的意见框吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await removeOpinionFrame(opinionFrameBindArr.value.toString());
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
        let res = await getRoleList(opinionFrameBindArr.value[0]);
        if (res.success) {
            roleTableConfig.value.tableData = res.data;
        }
    }

    async function saveOFBind() {
        if (opinionFrameArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请选择' + title.value,
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        let marks = [];
        for (let i = 0; i < opinionFrameArr.value.length; i++) {
            marks.push(opinionFrameArr.value[i].name + ':' + opinionFrameArr.value[i].mark);
        }

        let res = await bindOpinionFrame(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
            marks.join(';')
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

        let result = await bindRole(rIds.join(';'), opinionFrameBindArr.value[0]);
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
    .permconfig .el-dialog__body {
        padding: 5px 10px;
    }

    .opinionBindDiv .el-drawer__header {
        margin-bottom: 0;
        padding-bottom: 16px;
        border-bottom: 1px solid #eee;
    }

    .opinionBindDiv .y9-card {
        box-shadow: none;
    }

    .opinionBindDiv .optBtn {
        border-radius: 50%;
        background: #586cb1;
        border-color: #586cb1;
        color: #fff;
        border: 1px solid #dcdfe6;
        padding: 9px;
    }

    .opinionBindDiv .optBtnFalse {
        border-radius: 50%;
        background: #a6a9ad;
        border-color: #a6a9ad;
        color: #fff;
        border: 1px solid #dcdfe6;
        padding: 9px;
    }
</style>
