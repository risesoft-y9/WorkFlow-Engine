<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-19 15:07:15
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-02-28 11:05:06
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\processControl\index.vue
-->

<template>
    <y9Table
        :config="controlTableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #processModel>
            <el-input
                v-model="processInstanceId"
                clearable
                placeholder="流程实例ID"
                style="width: 300px; margin-right: 10px"
            ></el-input>
            <el-button type="primary" @click="reloadTable"><i class="ri-search-line"></i> 搜索</el-button>
            <el-button class="global-btn-third" @click="refreshTable"><i class="ri-refresh-line"></i> 刷新</el-button>
        </template>
        <template #key="{ row, column, index }">
            {{ row.processDefinitionId.split(':')[0] }}
        </template>
        <template #pStatus="{ row, column, index }">
            <font v-if="row.suspended" style="color: red">挂起</font>
            <font v-else style="color: #67c23a">激活</font>
        </template>
        <template #opt_button="{ row, column, index }">
            <el-button class="global-btn-second" size="small" @click="showProcessVariable(row)">流程变量</el-button>
            <el-button class="global-btn-second" size="small" @click="showTaskVariable(row)">任务变量</el-button>
            <el-button class="global-btn-second" size="small" @click="showGraphTrace(row)">流程图</el-button>
            <el-button v-if="row.suspended" class="global-btn-second" size="small" @click="active(row)">激活</el-button>
            <el-button v-else class="global-btn-second" size="small" @click="suspend(row)">挂起</el-button>
            <el-button class="global-btn-second" size="small" @click="delProcessInstance(row)">删除</el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <GraphTraceNew
            v-if="dialogConfig.type == 'graphTraceNew'"
            ref="GraphTraceChild"
            :processDefinitionId="processDefinitionId"
            :processInstanceId="pInstanceId"
        />
        <GraphTrace
            v-if="dialogConfig.type == 'graphTrace'"
            ref="GraphTraceChild"
            :processDefinitionId="processDefinitionId"
        />
        <ProcessVariable
            v-if="dialogConfig.type == 'processVariable'"
            ref="ProcessVariableChild"
            :processInstanceId="pInstanceId"
            :suspended="suspended"
        />
        <TaskVariable
            v-if="dialogConfig.type == 'taskVariable'"
            ref="TaskVariableChild"
            :processInstanceId="pInstanceId"
            :suspended="suspended"
        />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, reactive, ref } from 'vue';
    import type { ElLoading, ElMessage, ElMessageBox } from 'element-plus';
    import { deleteProcessInstance, runningList, switchSuspendOrActive } from '@/api/processAdmin/processControl';
    import GraphTrace from '@/views/processDeploy/graphTrace.vue';
    import GraphTraceNew from '@/views/processControl/graphTrace.vue';
    import ProcessVariable from '@/views/processControl/processVariable.vue';
    import TaskVariable from '@/views/processControl/taskVariable.vue';

    let total = ref(0);
    const data = reactive({
        processDefinitionId: '',
        processInstanceId: '',
        controlTableConfig: {
            //人员列表表格配置
            loading: false,
            columns: [
                { title: '序号', type: 'index', width: '55' },
                { title: '流程实例Id', key: 'processInstanceId', width: '290' },
                { title: '流程定义Key', key: 'processDefinitionKey', width: '130', slot: 'key' },
                { title: '流程定义名称', key: 'processDefinitionName', width: '140' },
                { title: '开始时间', key: 'startTime', width: '160' },
                { title: '创建人', key: 'startUserName' },
                { title: '当前节点', key: 'activityName', width: '140' },
                { title: '状态', key: 'suspended', width: '55', slot: 'pStatus' },
                { title: '操作', width: '380', slot: 'opt_button' }
            ],
            tableData: [],
            border: false,
            headerBackground: true,
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value //总条目数
            }
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 8,
                    slotName: 'processModel'
                }
            ]
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        },
        pInstanceId: '',
        suspended: false
    });

    let {
        processInstanceId,
        processDefinitionId,
        filterConfig,
        controlTableConfig,
        dialogConfig,
        suspended,
        pInstanceId
    } = toRefs(data);

    onMounted(() => {
        reloadTable();
    });

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        controlTableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        controlTableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function refreshTable() {
        processInstanceId.value = '';
        controlTableConfig.value.pageConfig.currentPage = 1;
        controlTableConfig.value.pageConfig.pageSize = 15;
        reloadTable();
    }

    async function reloadTable() {
        controlTableConfig.value.loading = true;
        let page = controlTableConfig.value.pageConfig.currentPage;
        let rows = controlTableConfig.value.pageConfig.pageSize;
        runningList(processInstanceId.value, page, rows).then((res) => {
            controlTableConfig.value.loading = false;
            if (res.success) {
                controlTableConfig.value.tableData = res.rows;
                controlTableConfig.value.pageConfig.total = res.total;
            }
        });
    }

    function showGraphTrace(row) {
        processDefinitionId.value = row.processDefinitionId;
        pInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '80%',
            type: 'graphTraceNew',
            title: '流程图【' + row.processDefinitionName + '】',
            showFooter: false
        });
    }

    function showProcessVariable(row) {
        pInstanceId.value = row.processInstanceId;
        suspended.value = row.suspended;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '70%',
            title: '流程变量',
            type: 'processVariable',
            showFooter: false
        });
    }

    function showTaskVariable(row) {
        pInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '70%',
            title: '任务变量',
            type: 'taskVariable',
            showFooter: false
        });
    }

    function delProcessInstance(row) {
        ElMessageBox.confirm('确定删除流程实例吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                deleteProcessInstance(row.processInstanceId).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        reloadTable();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }

    function suspend(row) {
        ElMessageBox.confirm('是否挂起流程实例?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                switchSuspendOrActive('suspend', row.processInstanceId).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        reloadTable();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }

    function active(row) {
        ElMessageBox.confirm('是否激活流程实例?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                switchSuspendOrActive('active', row.processInstanceId).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        reloadTable();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }
</script>

<style lang="scss">
    @import '@/theme/global.scss';
</style>
