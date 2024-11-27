<template>
    <y9Card :title="`视图配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <el-tabs v-model="activeName" style="height: 40px" @tab-click="tabclick">
            <el-tab-pane label="草稿箱" mark="11111" name="draft"></el-tab-pane>
            <el-tab-pane label="待办件" name="todo"></el-tab-pane>
            <el-tab-pane label="在办件" name="doing"></el-tab-pane>
            <el-tab-pane label="办结件" name="done"></el-tab-pane>
            <el-tab-pane
                v-for="viewType in viewTypeList"
                :key="viewType.mark"
                :label="viewType.name"
                :name="viewType.mark"
            ></el-tab-pane>
        </el-tabs>
        <div style="margin: 8px 0">
            <el-row :gutter="40">
                <el-col :span="19">
                    <el-button-group>
                        <el-button size="small" type="primary" @click="addView('table')"
                            ><i class="ri-add-line"></i>新增
                        </el-button>
                        <el-button size="small" type="primary" @click="delView"
                            ><i class="ri-delete-bin-line"></i>删除
                        </el-button>
                        <el-button size="small" type="primary" @click="addView('custom')"
                            ><i class="ri-quill-pen-fill"></i>自定义列
                        </el-button>
                        <el-button size="small" type="primary" @click="copyViewData"
                            ><i class="ri-copyright-line"></i>复制
                        </el-button>
                    </el-button-group>
                </el-col>
                <el-col :span="5">
                    <el-button-group>
                        <el-button size="small" type="primary" @click="moveUp"
                            ><i class="ri-arrow-up-line"></i>上移
                        </el-button>
                        <el-button size="small" type="primary" @click="moveDown"
                            ><i class="ri-arrow-down-line"></i>下移
                        </el-button>
                        <el-button type="primary" @click="saveViewOrder"><span>保存</span></el-button>
                    </el-button-group>
                </el-col>
            </el-row>
        </div>
        <y9Table
            v-model:selectedVal="userSelectedData"
            :config="viewTableConfig"
            @select="handlerSelectData"
            @on-current-change="handleCurrentChange"
            @select-all="handlerSelectData"
        >
            <template #opt_button="{ row, column, index }">
                <span @click="editView(row)"><i class="ri-edit-line"></i>编辑</span>
            </template>
        </y9Table>

        <y9Dialog v-model:config="vcDialogConfig">
            <addOrEdit
                v-if="vcDialogConfig.type == 'addOrEdit'"
                :id="viewId"
                ref="addOrEditRef"
                :currTreeNodeInfo="currTreeNodeInfo"
                :optType="optType"
                :viewType="activeName"
            />
            <copyView
                v-if="vcDialogConfig.type == 'copyView'"
                ref="copyViewRef"
                :currTreeNodeInfo="currTreeNodeInfo"
                :vcDialogConfig="vcDialogConfig"
                :viewType="activeName"
            />
        </y9Dialog>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object.ts';
    import addOrEdit from './addOrEdit.vue';
    import copyView from './copyView.vue';
    import { getViewList, getViewTypeList, removeView, saveOrder, saveView } from '@/api/itemAdmin/item/viewConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        userSelectedData: '',
        currentRow: [],
        viewTypeList: [],
        viewId: '',
        optType: '',
        activeName: 'todo',
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        viewTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '表名',
                    key: 'tableName'
                },
                {
                    title: '字段名',
                    key: 'columnName',
                    width: '130'
                },
                {
                    title: '显示名称',
                    key: 'disPlayName',
                    width: '100'
                },
                {
                    title: '显示宽度',
                    key: 'disPlayWidth',
                    width: '100'
                },
                {
                    title: '显示位置',
                    key: 'disPlayAlign',
                    width: '100',
                    render: (row) => {
                        let alignStr = '';
                        switch (row.disPlayAlign) {
                            case 'left':
                                alignStr = '靠左';
                                break;
                            case 'center':
                                alignStr = '居中';
                                break;
                            case 'right':
                                alignStr = '靠右';
                                break;
                            default:
                                alignStr = '居中';
                                break;
                        }
                        return alignStr;
                    }
                },
                {
                    title: '操作人',
                    key: 'userName',
                    width: '100'
                },
                {
                    title: '创建时间',
                    key: 'createTime',
                    width: '180'
                },
                {
                    title: '操作',
                    width: '80',
                    slot: 'opt_button'
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        //弹窗配置
        vcDialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    const viewFormInstance = addOrEditRef.value.viewFormRef;
                    viewFormInstance.validate(async (valid) => {
                        if (valid) {
                            let result = { success: false, msg: '' };
                            let formData = addOrEditRef.value.viewFormData;
                            result = await saveView(formData);
                            ElNotification({
                                title: result.success ? '成功' : '失败',
                                message: result.msg,
                                type: result.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                            resolve();
                        } else {
                            ElMessage({
                                type: 'error',
                                message: '验证不通过，请检查',
                                offset: 65
                            });
                            reject();
                        }
                    });
                });
            },
            visibleChange: (visible) => {
                if (!visible) {
                    vcDialogConfig.value.onOkLoading = false;
                    getViewConfigList();
                }
            }
        },
        addOrEditRef: '',
        viewIdArr: []
    });

    let {
        viewId,
        activeName,
        currInfo,
        viewTableConfig,
        vcDialogConfig,
        optType,
        addOrEditRef,
        viewIdArr,
        currentRow,
        viewTypeList,
        userSelectedData
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getViewConfigList();
        }
    );

    async function getViewConfigList() {
        //权限配置
        viewTableConfig.value.tableData = [];
        let res = await getViewList(props.currTreeNodeInfo.id, activeName.value);
        if (res.success) {
            viewTableConfig.value.tableData = res.data;
        }
        let result = await getViewTypeList();
        if (result.success) {
            viewTypeList.value = result.data;
        }
    }

    getViewConfigList();

    function tabclick(tab, event) {
        //页签切换
        activeName.value = tab.props.name;
        getViewConfigList();
    }

    function addView(type) {
        viewId.value = '';
        optType.value = type;
        Object.assign(vcDialogConfig.value, {
            show: true,
            width: '40%',
            title: '新增',
            type: 'addOrEdit',
            showFooter: true
        });
    }

    function editView(row) {
        optType.value = 'table';
        if (row.tableName == null || row.tableName == '') {
            optType.value = 'custom';
        }
        viewId.value = row.id;
        Object.assign(vcDialogConfig.value, {
            show: true,
            width: '40%',
            title: '编辑',
            type: 'addOrEdit',
            showFooter: true
        });
    }

    function copyViewData() {
        Object.assign(vcDialogConfig.value, {
            show: true,
            width: '40%',
            title: '复制视图',
            type: 'copyView',
            showFooter: false
        });
    }

    function handleCurrentChange(val) {
        currentRow.value = val;
    }

    function handlerSelectData(id, data) {
        viewIdArr.value = id;
    }

    function delView() {
        if (viewIdArr.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请勾选要删除的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        ElMessageBox.confirm('你确定要删除的选中的数据吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                let ids = [];
                for (let obj of viewIdArr.value) {
                    ids.push(obj.id);
                }
                result = await removeView(ids.join(','));
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getViewConfigList();
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

    const moveUp = () => {
        //上移
        if (currentRow.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请点击选中一条数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let index = 0;
        for (let i = 0; i < viewTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == viewTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = viewTableConfig.value.tableData[index - 1];
            let currRow = viewTableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            viewTableConfig.value.tableData.splice(index - 1, 1);
            viewTableConfig.value.tableData.splice(index, 0, upRow);
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
        for (let i = 0; i < viewTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == viewTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == viewTableConfig.value.tableData.length) {
            ElNotification({
                title: '操作提示',
                message: '已经是最后一条，不可下移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        } else {
            let downRow = viewTableConfig.value.tableData[index + 1];
            let currRow = viewTableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            viewTableConfig.value.tableData.splice(index + 1, 1);
            viewTableConfig.value.tableData.splice(index, 0, downRow);
        }
    };

    function saveViewOrder() {
        let ids = [];
        for (let item of viewTableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            loading.close();
            if (res.success) {
                ElNotification({ title: '操作提示', message: res.msg, type: 'success', duration: 2000, offset: 80 });
                getViewConfigList();
            } else {
                ElNotification({ title: '操作提示', message: res.msg, type: 'error', duration: 2000, offset: 80 });
            }
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
