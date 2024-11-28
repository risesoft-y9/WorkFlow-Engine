<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-06-15 15:14:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-16 11:19:01
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\processVariable\taskVariable.vue
-->
<template>
    <el-form ref="tVariableForm" :model="formData" :rules="rules" class="tVariable">
        <y9Table :config="varTableConfig" :filterConfig="filterConfig">
            <template #processModel>
                <label style="line-height: 32px">选择任务</label>
                <el-select v-model="taskId" style="margin-left: 8px" @change="taskChange">
                    <el-option v-for="item in taskList" :key="item.taskId" :label="item.userName" :value="item.taskId">
                    </el-option>
                </el-select>
                <el-button :disabled="disabled" style="margin-left: 10px" type="primary" @click="addVariable"
                    ><i class="ri-add-line" />新增
                </el-button>
            </template>
            <template #varKey="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="key">
                    <el-input v-model="formData.key" :disabled="editReadonly"></el-input>
                </el-form-item>
                <div v-else>{{ row.key }}</div>
            </template>
            <template #renderVal="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="value">
                    <el-input v-model="formData.value"></el-input>
                </el-form-item>
                <div v-else>
                    <div v-if="row.value === true">true</div>
                    <div v-else-if="row.value === false">false</div>
                    <div v-else>{{ row.value }}</div>
                </div>
            </template>
            <template #opt_button="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-button class="global-btn-second" size="small" @click="saveData(tVariableForm)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-button>
                    <el-button class="global-btn-second" size="small" @click="cancalData(tVariableForm)"
                        ><i class="ri-close-line"></i>取消
                    </el-button>
                </div>
                <div v-else>
                    <el-button
                        :disabled="disabled"
                        :title="text"
                        class="global-btn-second"
                        size="small"
                        @click="editTaskVariable(row, index)"
                        ><i class="ri-edit-line"></i>编辑
                    </el-button>
                    <el-button
                        :disabled="disabled"
                        :title="text"
                        class="global-btn-second"
                        size="small"
                        @click="delTaskVariable(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-button>
                </div>
            </template>
        </y9Table>
    </el-form>
</template>

<script lang="ts" setup>
    import { defineProps, onMounted, reactive, ref } from 'vue';
    import { deleteTaskVar, getTaskList, saveTaskVariable, taskVarList } from '@/api/processAdmin/processControl';

    const props = defineProps({
        processInstanceId: String,
        suspended: Boolean
    });
    const tVariableForm = ref<FormInstance>();
    const rules = reactive<FormRules>({
        key: { required: true, message: '请输入变量名', trigger: 'blur' },
        value: { required: true, message: '请输入变量值', trigger: 'blur' }
    });
    const data = reactive({
        taskId: '',
        taskList: [],
        text: '流程实例处于挂起状态,不可操作',
        disabled: false,
        editReadonly: false,
        optType: '',
        editIndex: '',
        isEdit: false,
        isEmptyData: false,
        formData: {
            key: '',
            value: ''
        },
        varTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '任务id', key: 'taskId' },
                { title: '变量名', key: 'key', slot: 'varKey' },
                { title: '变量值', key: 'value', slot: 'renderVal' },
                { title: '操作', width: '220', slot: 'opt_button' }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'processModel',
                    labelAlign: 'center',
                    justify: 'flex'
                }
            ]
        }
    });

    let {
        taskId,
        text,
        disabled,
        editReadonly,
        optType,
        isEdit,
        isEmptyData,
        editIndex,
        formData,
        taskList,
        filterConfig,
        varTableConfig
    } = toRefs(data);

    onMounted(() => {
        if (props.suspended) {
            disabled.value = true;
            text.value = '流程实例处于挂起状态,不可操作';
        } else {
            disabled.value = false;
            text.value = '';
        }
        getTableList();
    });

    async function getTableList() {
        getTaskList(props.processInstanceId).then((res) => {
            if (res.success) {
                taskList.value = res.data;
                if (taskList.value.length > 0) {
                    taskId.value = taskList.value[0].taskId;
                    reloadTable();
                }
            }
        });
    }

    async function reloadTable() {
        let res = await taskVarList(taskId.value);
        varTableConfig.value.tableData = res.data;
    }

    function taskChange(val) {
        taskId.value = val;
        reloadTable();
    }

    function addVariable() {
        optType.value = 'add';
        for (let i = 0; i < varTableConfig.value.tableData.length; i++) {
            if (varTableConfig.value.tableData[i].key == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = varTableConfig.value.tableData.length;
            varTableConfig.value.tableData.push({ taskId: taskId.value, key: '', value: '' });
            formData.value.value = '';
            formData.value.key = '';
            isEdit.value = false;
            editReadonly.value = false;
        }
    }

    const editTaskVariable = (rows, index) => {
        optType.value = 'edit';
        editReadonly.value = true;
        editIndex.value = index;
        formData.value.value = rows.value;
        formData.value.key = rows.key;
        isEdit.value = true;
    };

    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveTaskVariable(optType.value, taskId.value, formData.value.key, formData.value.value).then((res) => {
                    loading.close();
                    ElNotification({
                        title: res.success ? '成功' : '失败',
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (res.success) {
                        editIndex.value = '';
                        isEmptyData.value = false;
                        reloadTable();
                    }
                });
            }
        });
    };

    const cancalData = (refForm) => {
        tVariableForm.value.resetFields();
        editIndex.value = '';
        formData.value.value = '';
        formData.value.key = '';

        for (let i = 0; i < varTableConfig.value.tableData.length; i++) {
            if (varTableConfig.value.tableData[i].key == '') {
                varTableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delTaskVariable = (row) => {
        ElMessageBox.confirm('你确定要删除任务变量吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteTaskVar(taskId.value, row.key);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    reloadTable();
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消删除',
                    offset: 65
                });
            });
    };
</script>

<style lang="scss">
    @import '@/theme/global.scss';

    .tVariable .y9-filter .y9-filter-item {
        float: right;
    }

    .tVariable .el-form-item--default {
        margin-bottom: 0px;
    }

    .tVariable .el-form-item {
        margin-bottom: 0px;
    }

    .tVariable .el-form-item__error {
        color: var(--el-color-danger);
        font-size: 12px;
        line-height: 1;
        padding-top: 2px;
        position: relative;
        top: 0%;
        left: 0;
    }
</style>
