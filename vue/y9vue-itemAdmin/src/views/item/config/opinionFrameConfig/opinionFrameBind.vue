<!--

 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-12 09:42:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-30 15:22:17
 * @Descripttion: 意见框配置
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\config\opinionFrameConfig\opinionFrameBind.vue
-->
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
        <y9Table :config="oFBListTableConfig" @select="handlerSelectData" @select-all="handlerSelectData">
            <template #roleNames="{ row, columns, index }">
                <a @click="openRoles(row)">{{ row.roleNames }}</a>
            </template>
        </y9Table>
        <el-drawer v-model="tableDrawer" :title="title" direction="rtl">
            <div style="margin: 8px 0; text-align: left">
                <el-input
                    v-model="opinionFrameName"
                    clearable
                    placeholder="意见框名称"
                    style="width: 200px; margin-right: 5px"
                ></el-input>
                <el-button type="primary" @click="search"><i class="ri-search-2-line"></i>搜索</el-button>
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
            <template #footer>
                <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                    <el-button type="primary" @click="saveRoleBind"><span>保存</span></el-button>
                    <el-button @click="treeDrawer = false"><span>取消</span></el-button>
                </div>
            </template>
        </el-drawer>
        <el-drawer v-model="roleDrawer" :title="title" direction="rtl" @close="closeRoleTable">
            <y9Table :config="roleTableConfig" @select="handlerData" @select-all="handlerData"></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="delRoleBind"><span>删除</span></el-button>
                <el-button @click="roleDrawer = false"><span>取消</span></el-button>
            </div>
            <y9Dialog v-model:config="dialogRoleConfig">
                <permTree ref="memberTreeRef" :showHeader="showHeader" :treeApiObj="memberTreeApiObj" />
            </y9Dialog>
        </el-drawer>
        <el-drawer v-model="oneClickSetDrawer" :title="title" direction="rtl" size="45%">
            <div v-if="oneClickSetShow">
                <div style="margin: 8px 0; text-align: left">
                    <el-button type="primary" @click="addSet"><i class="ri-add-line"></i>新增设置</el-button>
                </div>
                <y9Table :config="oneClickSetConfig"></y9Table>
            </div>
            <!-- <y9Dialog v-model:config="dialogConfig"> -->
            <div v-else>
                <el-form
                    ref="oneSetFormRef"
                    :inline-message="true"
                    :model="oneSetData"
                    :rules="rules"
                    :status-icon="true"
                >
                    <div style="margin: 8px">
                        <table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
                            <tbody>
                                <tr>
                                    <td class="lefttd" style="width: 20%">一键设置类型<font color="red">*</font></td>
                                    <td class="rigthtd">
                                        <el-form-item prop="oneSetTypeName">
                                            <el-select
                                                v-model="oneSetData.oneSetTypeName"
                                                placeholder="请选择一键设置类型"
                                                @change="selectOneSetType"
                                            >
                                                <el-option
                                                    v-for="item in setTypeData"
                                                    :key="item.value"
                                                    :label="item.label"
                                                    :value="item.label + '-' + item.value"
                                                ></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lefttd">对应执行动作<font color="red">*</font></td>
                                    <td class="rigthtd">
                                        <el-form-item prop="executeActionName">
                                            <el-select
                                                v-model="oneSetData.executeActionName"
                                                placeholder="请选择对应执行动作"
                                                @change="selectAction"
                                            >
                                                <el-option
                                                    v-for="item in actionData"
                                                    :key="item.value"
                                                    :label="item.label"
                                                    :value="item.label + '-' + item.value"
                                                ></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lefttd">一键设置描述</td>
                                    <td class="rigthtd">
                                        <el-form-item prop="description">
                                            <el-input
                                                v-model="oneSetData.description"
                                                autosize
                                                placeholder="请输入一键设置描述(如：点击‘xxx’设置将自动填写意见，有执行动作的，就加上‘同时系统将执行对应的动作’)"
                                                resize="none"
                                                type="textarea"
                                            />
                                        </el-form-item>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lefttd">注意事项</td>
                                    <td class="rigthtd" style="padding: 5px; color: red">
                                        1、该设置请勿配置在流程的第一个节点上，流程还未启动很多动作方法是无法执行的。<br />
                                        2、该设置需要根据流程图来配置。比如说:流程的下个节点还没有到办结的时候，你配置动作为完成，就偏离的流程的走向,与流程不符。
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </el-form>
                <div style="text-align: right; margin: 15px">
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="saveOneSet">提交</el-button>
                        <el-button @click="closeOneClickSet">关闭</el-button>
                    </span>
                </div>
            </div>
            <!-- </y9Dialog> -->
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
    import { h, reactive, ref } from 'vue';
    import {
        bindOpinionFrame,
        bindRole,
        changeSignOpinion,
        delOneClickSet,
        getBindList,
        getOneClickSetBindList,
        getRoleList,
        removeOpinionFrame,
        removeRole,
        saveOneClickSet,
        searchOpinionFrame
    } from '@/api/itemAdmin/item/opinionFrameConfig';
    import { findRoleMember, getOrgTree, getRole, getRoleById } from '@/api/itemAdmin/item/permConfig';

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
    let params = ref({ roleId: '' });
    const data = reactive({
        opinionFrameName: '',
        permTreeRef: '', //tree实例
        oneSetFormRef: '',
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
        memberTreeApiObj: {
            //tree接口对象
            topLevel: () => {
                return findRoleMember(params.value);
            },
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
                { title: '角色名称', key: 'roleNames', width: '150', slot: 'roleNames' },
                { title: '操作人', key: 'userName', width: '150' },
                { title: '绑定时间', key: 'createDate', width: '180' },
                {
                    title: '操作',
                    key: 'opt',
                    width: '180',
                    render: (row) => {
                        let button = [
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '15px',
                                        fontWeight: 600
                                    },
                                    onClick: () => {
                                        bindOneClickSet(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-settings-3-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    '绑定一键设置'
                                ]
                            )
                        ];
                        return button;
                    }
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        oFTableConfig: {
            columns: [
                { title: '', width: '60', type: 'selection', fixed: 'left' },
                { title: '序号', type: 'index', width: '60' },
                { title: '意见框名称', key: 'name' },
                { title: '唯一标示', key: 'mark', width: '200' }
            ],
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                pageSizeOpts: [10, 20, 30, 50, 100],
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value //总条目数
            },
            height: 'auto'
        },
        roleTableConfig: {
            columns: [
                { title: '', type: 'selection', width: '60', fixed: 'left' },
                { title: '序号', type: 'index', width: '60' },
                { title: '角色名称', key: 'roleName' },
                {
                    title: '角色成员',
                    key: 'opt',
                    width: '180',
                    render: (row) => {
                        let button = [
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '15px',
                                        fontWeight: 600
                                    },
                                    title: '角色成员只显示岗位人员',
                                    onClick: () => {
                                        openMemberTree(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-settings-3-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    '成员'
                                ]
                            )
                        ];
                        return button;
                    }
                }
            ],
            tableData: [],
            pageConfig: false,
            height: 'auto'
        },
        oneClickSetConfig: {
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '一键设置名称', key: 'oneSetTypeName' },
                { title: '一键设置动作名称', key: 'executeActionName' },
                { title: '描述', key: 'description', width: '220' },
                {
                    title: '操作',
                    key: 'opt',
                    width: '180',
                    render: (row) => {
                        let button = [
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '15px',
                                        fontWeight: 600
                                    },
                                    onClick: () => {
                                        editOneClickSet(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-edit-box-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    '编辑'
                                ]
                            ),
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '15px',
                                        fontWeight: 600
                                    },
                                    onClick: () => {
                                        deleteOneClickSet(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-delete-bin-2-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    '删除'
                                ]
                            )
                        ];
                        return button;
                    }
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
        dialogRoleConfig: {
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
        oneSetData: {},
        rules: {
            oneSetTypeName: { required: true, message: '请选择一键设置类型', trigger: 'blur' },
            executeActionName: { required: true, message: '请选择对应执行动', trigger: 'blur' }
        },
        setTypeData: [
            {
                label: '一键签批',
                value: 'oneClickSign'
            },
            {
                label: '一键阅知',
                value: 'oneClickRead'
            },
            {
                label: '同意',
                value: 'oneClickAgree'
            },
            {
                label: '已阅',
                value: 'oneClickAlreadyRead'
            }
        ],
        actionData: [
            {
                label: '完成（办结）',
                value: 'complete'
            },
            {
                label: '办理完成',
                value: 'processingCompleted'
            },
            {
                label: '送发送人',
                value: 'toSender'
            },
            {
                label: '提交',
                value: 'toSubmit'
            },
            {
                label: '送下一步（下一步流程节点必须只有一个）',
                value: 'sendNextTaskDefKey'
            },
            {
                label: '无动作',
                value: 'noAction'
            }
        ],
        tableDrawer: false,
        treeDrawer: false,
        roleDrawer: false,
        oneClickSetDrawer: false,
        title: '',
        opinionFrameBindArr: [],
        roleBindArr: [],
        roleIdArr: [],
        opinionFrameArr: [],
        bindId: '',
        oneClickSetShow: true
    });

    let {
        opinionFrameName,
        permTreeRef,
        oneSetFormRef,
        oFBListTableConfig,
        oFTableConfig,
        roleTableConfig,
        oneClickSetConfig,
        dialogConfig,
        dialogRoleConfig,
        oneSetData,
        rules,
        setTypeData,
        actionData,
        treeDrawer,
        tableDrawer,
        roleDrawer,
        oneClickSetDrawer,
        treeApiObj,
        memberTreeApiObj,
        title,
        showHeader,
        treeSelectedData,
        opinionFrameBindArr,
        roleBindArr,
        roleIdArr,
        opinionFrameArr,
        selectField,
        bindId,
        oneClickSetShow
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

    function openRoles(row) {
        title.value = '角色详情';
        roleDrawer.value = true;
        opinionFrameBindArr.value.push(row.id);
        getRoleTable();
    }

    function openMemberTree(row) {
        Object.assign(dialogRoleConfig.value, {
            show: true,
            width: '40%',
            title: '角色成员',
            showFooter: false
        });
        params.value.roleId = row.roleId;
        memberTreeApiObj.value.childLevel.api = getOrgTree;
        memberTreeApiObj.value.childLevel.params.treeType = 'tree_type_position';
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

    function bindOneClickSet(row) {
        oneClickSetDrawer.value = true;
        title.value = '绑定一键设置';
        bindId.value = row.id;
        loadOneClickSetList();
    }

    async function loadOneClickSetList() {
        let res = await getOneClickSetBindList(bindId.value);
        if (res.success) {
            oneClickSetConfig.value.tableData = res.data;
        }
    }

    function addSet() {
        // Object.assign(dialogConfig.value, {
        //     show: true,
        //     width: '40%',
        //     title: '新增设置',
        //     showFooter: false
        // });
        title.value = '新增设置';
        oneClickSetShow.value = false;
        oneSetData.value = {
            bindId: bindId.value,
            oneSetType: '',
            oneSetTypeName: '',
            executeAction: '',
            executeActionName: ''
        };
    }

    function editOneClickSet(row) {
        // Object.assign(dialogConfig.value, {
        //     show: true,
        //     width: '40%',
        //     title: '修改设置',
        //     showFooter: false
        // });
        oneSetData.value = row;
        oneClickSetShow.value = false;
    }

    function selectOneSetType(val) {
        oneSetData.value.oneSetType = val.split('-')[1];
        oneSetData.value.oneSetTypeName = val.split('-')[0];
    }

    function selectAction(val) {
        oneSetData.value.executeAction = val.split('-')[1];
        oneSetData.value.executeActionName = val.split('-')[0];
    }

    function saveOneSet() {
        oneSetFormRef.value.validate(async (valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                let res = await saveOneClickSet(oneSetData.value);
                console.log(res);

                loading.close();
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    loadOneClickSetList();
                    //dialogConfig.value = false;
                    oneClickSetShow.value = true;
                } else {
                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                }
            }
        });
    }

    function closeOneClickSet() {
        oneClickSetShow.value = true;
    }

    function deleteOneClickSet(row) {
        ElMessageBox.confirm('你确定要删除绑定的设置吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await delOneClickSet(row.id);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    loadOneClickSetList();
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
    .opinionBindDiv {
        :deep(.el-drawer__header) {
            margin-bottom: 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #eee;
        }
        :deep(.y9-card) {
            box-shadow: none;
        }

        :deep(.optBtn) {
            border-radius: 50%;
            background: #586cb1;
            border-color: #586cb1;
            color: #fff;
            border: 1px solid #dcdfe6;
            padding: 9px;
        }

        :deep(.optBtnFalse) {
            border-radius: 50%;
            background: #a6a9ad;
            border-color: #a6a9ad;
            color: #fff;
            border: 1px solid #dcdfe6;
            padding: 9px;
        }
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
