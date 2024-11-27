<template>
    <div style="margin-bottom: 8px">
        <el-button-group>
            <el-button type="primary" @click="addOptionValue"><i class="ri-add-line"></i>新增</el-button>
            <el-button type="primary" @click="moveUp"><i class="ri-arrow-up-line"></i>上移</el-button>
            <el-button type="primary" @click="moveDown"><i class="ri-arrow-down-line"></i>下移</el-button>
            <el-button type="primary" @click="optionValueOrder"><i class="ri-save-line"></i>保存</el-button>
        </el-button-group>
    </div>
    <el-form ref="optionValueForm" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" @on-current-change="handleCurrentChange">
            <template #name="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="name">
                    <el-input v-model="formData.name" clearable />
                </el-form-item>
                <span v-else>{{ row.name }}</span>
            </template>
            <template #code="{ row, column, index }">
                <el-form-item v-if="editIndex === index" prop="code">
                    <el-input v-model="formData.code" clearable />
                </el-form-item>
                <span v-else>{{ row.code }}</span>
            </template>
            <template #defaultSelected="{ row, column, index }">
                <i
                    v-if="row.defaultSelected == 1"
                    class="ri-check-line"
                    style="color: green; font-weight: bold; font-size: 22px"
                    title="点击设置默认选中"
                    @click="checkOption(row)"
                ></i>
                <i
                    v-if="row.defaultSelected == 0"
                    class="ri-close-line"
                    style="color: red; font-weight: bold; font-size: 22px"
                    title="点击设置默认选中"
                    @click="checkOption(row)"
                ></i>
            </template>
            <template #opt="{ row, column, index }">
                <div v-if="editIndex === index">
                    <el-button class="global-btn-second" size="small" @click="saveData(optionValueForm)"
                        ><i class="ri-book-mark-line"></i>保存
                    </el-button>
                    <el-button class="global-btn-second" size="small" @click="cancalData(optionValueForm)"
                        ><i class="ri-close-line"></i>取消
                    </el-button>
                </div>
                <div v-else>
                    <el-button class="global-btn-second" size="small" @click="editOptionValue(row, index)"
                        ><i class="ri-edit-line"></i>修改
                    </el-button>
                    <el-button class="global-btn-second" size="small" @click="delProperty(row)"
                        ><i class="ri-delete-bin-line"></i>删除
                    </el-button>
                </div>
            </template>
        </y9Table>
    </el-form>
</template>
<script lang="ts" setup>
    import { defineProps, onMounted, reactive, ref } from 'vue';
    import type { ElLoading, ElMessage, FormInstance } from 'element-plus';
    import {
        delOptionValue,
        getOptionValueList,
        saveOptionValue,
        saveOrder,
        updateOptionValue
    } from '@/api/itemAdmin/optionClass';

    const optionValueForm = ref<FormInstance>();
    const rules = reactive<FormRules>({
        name: { required: true, message: '请输入数据名称', trigger: 'blur' },
        code: { required: true, message: '请输入数据代码', trigger: 'blur' }
    });

    const props = defineProps({
        row: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const data = reactive({
        editIndex: '',
        currentRow: '',
        formData: { id: '', name: '', code: '', type: '', tabIndex: 0 },
        tableConfig: {
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '数据名称', key: 'name', width: 'auto', slot: 'name' },
                { title: '数据代码', key: 'code', width: 'auto', slot: 'code' },
                { title: '默认选中', key: 'defaultSelected', width: '120', slot: 'defaultSelected' },
                { title: '操作', width: '160', slot: 'opt' }
            ],
            tableData: [],
            pageConfig: false,
            height: 'auto'
        }
    });

    let { tableConfig, editIndex, formData, currentRow } = toRefs(data);

    onMounted(() => {
        getList();
    });

    async function getList() {
        let res = await getOptionValueList(props.row.type);
        tableConfig.value.tableData = res.data;
    }

    const isEmptyData = ref(false);
    const addOptionValue = () => {
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = tableConfig.value.tableData.length;
            tableConfig.value.tableData.push({ id: '', name: '', code: '' });
            formData.value.id = '';
            formData.value.name = '';
            formData.value.code = '';
        }
    };

    const editOptionValue = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.name = rows.name;
        formData.value.code = rows.code;
        formData.value.tabIndex = rows.tabIndex;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                formData.value.type = props.row.type;
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveOptionValue(formData.value).then((res) => {
                    loading.close();
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        editIndex.value = '';
                        isEmptyData.value = false;
                        getList();
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
            }
        });
    };

    const moveUp = () => {
        //上移
        if (currentRow.value.name == undefined) {
            ElMessage({ type: 'error', message: '请点击选择一条数据', offset: 65 });
            return;
        }
        let index = 0;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == tableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = tableConfig.value.tableData[index - 1];
            let currRow = tableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            tableConfig.value.tableData.splice(index - 1, 1);
            tableConfig.value.tableData.splice(index, 0, upRow);
        } else {
            ElMessage({ type: 'error', message: '已经是第一条，不可上移', offset: 65 });
        }
    };

    const moveDown = () => {
        //下移
        if (currentRow.value.name == undefined) {
            ElMessage({ type: 'error', message: '请点击选择一条数据', offset: 65 });
            return;
        }
        let index = 0;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == tableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == tableConfig.value.tableData.length) {
            ElMessage({ type: 'error', message: '已经是最后一条，不可下移', offset: 65 });
        } else {
            let downRow = tableConfig.value.tableData[index + 1];
            let currRow = tableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            tableConfig.value.tableData.splice(index + 1, 1);
            tableConfig.value.tableData.splice(index, 0, downRow);
        }
    };

    const optionValueOrder = () => {
        let ids = [];
        for (let item of tableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            loading.close();
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
                getList();
            } else {
                ElMessage({ message: res.msg, type: 'error', offset: 65 });
            }
        });
    };

    const checkOption = (row) => {
        updateOptionValue(row.id).then((res) => {
            if (res.success) {
                getList();
            }
            ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
        });
    };

    const handleCurrentChange = (val) => {
        currentRow.value = val;
    };

    const cancalData = (refForm) => {
        editIndex.value = '';
        formData.value.typeName = '';
        refForm.resetFields();
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delProperty = (rows) => {
        ElMessageBox.confirm('您确定要删除数据吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                delOptionValue(rows.id).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        getList();
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
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

<style lang="scss"></style>
