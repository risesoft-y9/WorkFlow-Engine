<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-06 16:32:39
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-04 17:55:27
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\views\y9form\table\tableManage.vue
-->
<template>
    <y9Card :title="`业务表管理${currTreeNodeInfo.name ? ' - ' + currTreeNodeInfo.name : ''}`" class="y9tablecard">
        <div
            v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''"
            class="margin-bottom-20"
        >
            <el-button class="global-btn-main" type="primary" @click="selTable">
                <i class="ri-add-line"></i>
                <span>添加表</span>
            </el-button>
            <el-button class="global-btn-main" type="primary" @click="newTable()">
                <i class="ri-add-circle-line"></i>
                <span>创建表</span>
            </el-button>
        </div>
        <y9Table :config="tableListConfig"></y9Table>
        <newOrModifyTable ref="newOrModifyTableRef" :tableList="tableListConfig.tableData" :updateList="getTableList" />
    </y9Card>
    <y9Dialog v-model:config="dialogConfig">
        <selectTable ref="selectTableRef"></selectTable>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object.ts';
    import selectTable from '@/views/y9form/table/selectTable.vue';
    import newOrModifyTable from '@/views/y9form/table/newOrModifyTable.vue';
    import { addDataBaseTable, getTables, removeTable } from '@/api/itemAdmin/y9form';

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
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        tableListConfig: {
            //人员列表表格配置
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '表名称',
                    key: 'tableName',
                    width: 'auto'
                },
                {
                    title: '中文名称',
                    key: 'tableCnName',
                    width: 'auto'
                },
                {
                    title: '系统英文名称',
                    key: 'systemName',
                    width: '150'
                },
                {
                    title: '系统中文名称',
                    key: 'systemCnName',
                    width: '150'
                },
                {
                    title: '更新时间',
                    key: 'createTime',
                    width: '170'
                },
                {
                    title: '操作',
                    width: '110',
                    render: (row) => {
                        let button = [
                            h('i', {
                                class: 'ri-edit-line',
                                style: {
                                    marginRight: '10px',
                                    fontSize: '18px'
                                },
                                title: '编辑',
                                onClick: () => {
                                    editTable(row);
                                }
                            }),
                            h('i', {
                                title: '删除',
                                class: 'ri-delete-bin-line',
                                style: {
                                    fontSize: '18px'
                                },
                                onClick: () => {
                                    ElMessageBox.confirm(`是否删除【${row.tableName}】?`, '提示', {
                                        confirmButtonText: '确定',
                                        cancelButtonText: '取消',
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            let result = { success: false, msg: '' };
                                            result = await removeTable(row.id);
                                            ElNotification({
                                                title: result.success ? '成功' : '失败',
                                                message: result.msg,
                                                type: result.success ? 'success' : 'error',
                                                duration: 2000,
                                                offset: 80
                                            });
                                            if (result.success) {
                                                tableListConfig.value.tableData.forEach((item, index) => {
                                                    if (item.id == row.id) {
                                                        tableListConfig.value.tableData.splice(index, 1);
                                                    }
                                                });
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
                            })
                        ];
                        return button;
                    }
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
                return new Promise(async (resolve, reject) => {
                    let table = selectTableRef.value.currentRow;
                    if (table == null) {
                        ElNotification({
                            title: '失败',
                            message: '请选择业务表',
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }
                    let tableNames = selectTableRef.value.tableNames;
                    if (tableNames.indexOf(table.name) > -1) {
                        ElNotification({
                            title: '失败',
                            message: '表英文名称已存在记录',
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }
                    let res = await addDataBaseTable(
                        table.name,
                        props.currTreeNodeInfo.systemName,
                        props.currTreeNodeInfo.name
                    );
                    ElNotification({
                        title: res.success ? '成功' : '失败',
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (res.success) {
                        getTableList();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {
                // console.log('visible',visible)
            }
        },
        selectTableRef: '',
        newOrModifyTableRef: ''
    });

    let { currInfo, tableListConfig, dialogConfig, selectTableRef, newOrModifyTableRef } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getTableList();
        }
    );

    onMounted(() => {
        getTableList();
    });

    defineExpose({});

    async function getTableList() {
        let res = await getTables(props.currTreeNodeInfo.systemName, 1, 50);
        if (res.success) {
            tableListConfig.value.tableData = res.rows;
        }
    }

    function selTable() {
        Object.assign(dialogConfig.value, {
            show: true,
            width: '25%',
            title: '添加业务表',
            cancelText: '取消'
        });
    }

    function newTable() {
        newOrModifyTableRef.value.show(null, props.currTreeNodeInfo.name, props.currTreeNodeInfo.systemName);
    }

    function editTable(row) {
        newOrModifyTableRef.value.show(row);
    }
</script>

<style lang="scss">
    .y9tablecard .y9-table-div {
        // height: calc( 100% - 50px ) !important;
    }
</style>
