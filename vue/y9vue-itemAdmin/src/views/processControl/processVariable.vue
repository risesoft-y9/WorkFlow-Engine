<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-06-15 15:14:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-08-15 11:22:57
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\processControl\processVariable.vue
-->
<template>
    <el-form ref="pVariableForm" :model="formData" :rules="rules" class="pVariable">
        <y9Table :config="varTableConfig" :filterConfig="filterConfig">
            <template #processModel>
                <el-button :disabled="disabled" style="margin-right: 10px" type="primary" @click="addVariable"
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
                    <el-button class="global-btn-second" size="small" @click="saveData(pVariableForm)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-button>
                    <el-button class="global-btn-second" size="small" @click="cancalData(pVariableForm)"
                        ><i class="ri-close-line"></i>取消
                    </el-button>
                </div>
                <div v-else>
                    <el-button
                        :disabled="disabled"
                        :title="text"
                        class="global-btn-second"
                        size="small"
                        @click="editProcessVariable(row, index)"
                        ><i class="ri-edit-line"></i>编辑
                    </el-button>
                    <el-button
                        :disabled="disabled"
                        :title="text"
                        class="global-btn-second"
                        size="small"
                        @click="delProcessVariable(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-button>
                </div>
            </template>
        </y9Table>
    </el-form>
</template>

<script lang="ts" setup>
    import { defineProps, onMounted, reactive, ref } from 'vue';
    import { deleteProcessVar, processVarList, saveProcessVariable } from '@/api/processAdmin/processControl';

    const props = defineProps({
        processInstanceId: String,
        suspended: Boolean
    });
    const pVariableForm = ref<FormInstance>();
    const rules = reactive<FormRules>({
        key: { required: true, message: '请输入变量名', trigger: 'blur' },
        value: { required: true, message: '请输入变量值', trigger: 'blur' }
    });
    const data = reactive({
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
                { title: '流程实例id', key: 'processInstanceId' },
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
                    slotName: 'processModel'
                }
            ]
        }
    });

    let {
        text,
        disabled,
        editReadonly,
        optType,
        isEdit,
        isEmptyData,
        editIndex,
        formData,
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
        processVarList(props.processInstanceId).then((res) => {
            if (res.success) {
                varTableConfig.value.tableData = res.data;
            }
        });
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
            varTableConfig.value.tableData.push({ processInstanceId: props.processInstanceId, key: '', value: '' });
            formData.value.value = '';
            formData.value.key = '';
            isEdit.value = false;
            editReadonly.value = false;
        }
    }

    const editProcessVariable = (rows, index) => {
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
                saveProcessVariable(
                    optType.value,
                    props.processInstanceId,
                    formData.value.key,
                    formData.value.value
                ).then((res) => {
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
                        getTableList();
                    }
                });
            }
        });
    };

    const cancalData = (refForm) => {
        pVariableForm.value.resetFields();
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

    const delProcessVariable = (row) => {
        ElMessageBox.confirm('你确定要删除流程变量吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteProcessVar(row.processInstanceId, row.key);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    getTableList();
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

    .pVariable .el-form-item--default {
        margin-bottom: 0px;
    }

    .pVariable .el-form-item {
        margin-bottom: 0px;
    }

    .pVariable .el-form-item__error {
        color: var(--el-color-danger);
        font-size: 12px;
        line-height: 1;
        padding-top: 2px;
        position: relative;
        top: 0%;
        left: 0;
    }
</style>
