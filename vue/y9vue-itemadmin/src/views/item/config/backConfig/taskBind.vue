<!--

 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-12 09:42:08
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2026-06-01 14:34:44
 * @Descripttion: 按钮绑定配置
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\config\buttonConfig\buttonBind.vue
-->
<template>
    <div class="organWordBindDiv">
        <div style="margin-bottom: 16px">
            <el-button-group>
                <el-button type="primary" @click="addTask"><i class="ri-add-line"></i>退回任务</el-button>
                <el-button type="primary" @click="delTask"><i class="ri-delete-bin-line"></i>删除</el-button>
            </el-button-group>
        </div>
        <y9Table
            :config="bindTableConfig"
            highlight-current-row
            @select="handlerSelectData"
            @select-all="handlerSelectData"
        ></y9Table>
        <el-drawer v-model="tableDrawer" :title="title" direction="rtl">
            <y9Table :config="tableConfig" @select="handlerSelectData1" @select-all="handlerSelectData1" class="myTableDiv9"></y9Table>
            <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
                <el-button type="primary" @click="saveBind"><span>保存</span></el-button>
                <el-button @click="tableDrawer = false"><span>取消</span></el-button>
            </div>
        </el-drawer>
    </div>
</template>

<script lang="ts" setup>
    import {
        getBindList,
        removeBind,
        saveBindTask
    } from '@/api/itemAdmin/item/backConfig';
    import { onMounted, reactive, toRefs } from 'vue';

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
        taskDefName: String,
        bpmnList: Array,
    });

    const selectable = (row, index) => {
        console.log(row, index);
        console.log(bindTableConfig.value.tableData);
        let bind = bindTableConfig.value.tableData.filter(item => item.taskDefKey == row.taskDefKey);
        if(bind.length > 0){
            return false;
        }
        return true;
    }

    const data = reactive({
        showHeader: true,
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        bindTableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60' },
                { title: '序号', type: 'index', width: '60' },
                { title: '任务名称', key: 'taskDefName' },
                { title: '任务key', key: 'taskDefKey' },
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        tableConfig: {
            columns: [
                { title: '', type: 'selection', fixed: 'left', width: '60',selectable:selectable},
                { title: '序号', type: 'index', width: '60' },
                { title: '任务名称', key: 'taskDefName' },
                { title: '任务key', key: 'taskDefKey', width: '200' }
            ],
            tableData: [],
            pageConfig: false,
            height: '100%'
        },
        bindList: [],
        tableDrawer: false,
        title: '',
        selectData:[],
        selectData1:[],
    });

    let {
        bindTableConfig,
        tableConfig,
        tableDrawer,
        title,
        selectData,
        selectData1,
    } = toRefs(data);



    onMounted(() => {
        reloadBindList();
    });

    async function reloadBindList() {
        let res = await getBindList(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
        );
        if (res.success) {
            bindTableConfig.value.tableData = res.data;
        }
    }

    function handlerSelectData(rows) {
        selectData.value = rows;
    }

    function handlerSelectData1(rows) {
        selectData1.value = rows;
    }

   

    function addTask() {
        title.value = '添加退回任务';
        tableDrawer.value = true;
        selectData1.value = [];
        tableConfig.value.tableData = [];
        let newbpmnList = [];
        if(props.taskDefName?.indexOf('[子]') > -1){
            newbpmnList = props.bpmnList.filter(item => item.taskDefName.indexOf('[子]') != -1);
        }
        if(props.taskDefName?.indexOf('[子]') == -1){
            newbpmnList = props.bpmnList.filter(item => item.taskDefName.indexOf('[子]') == -1 && item.taskDefName.indexOf('【子】') == -1);
        }
        for (let task of newbpmnList) {
            task.id = task.taskDefKey;
            tableConfig.value.tableData.push(task);
        }
        
    }

 
    function delTask() {
        if (selectData.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请勾选要删除的数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        let taskKeyArr = [];
        for (let task of selectData.value) {
            taskKeyArr.push(task.taskDefKey);
        }
        ElMessageBox.confirm('你确定要删除绑定的退回任务吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await removeBind( props.currTreeNodeInfo.id,props.processDefinitionId,props.taskDefKey,taskKeyArr.toString());
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

    async function saveBind() {
        if (selectData1.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请选择一条数据', type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let selectTaskKey = [];
        for (let task of selectData1.value) {
            selectTaskKey.push(task.taskDefKey);
        }
        let res = await saveBindTask(
            props.currTreeNodeInfo.id,
            props.processDefinitionId,
            props.taskDefKey,
            selectTaskKey.toString()
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
</script>

<style lang="scss" scoped>
    .organWordBindDiv {
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
    :deep(.y9-table-div){
        .el-table {
            overflow-y: auto;
        }
    }
</style>
