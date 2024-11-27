<template>
    <div class="orgWordTable">
        <el-form ref="orgWordForm" :model="formData" :rules="rules">
            <y9Table :config="tableConfig" :filterConfig="filterConfig">
                <template #addBtn>
                    <el-button class="global-btn-main" type="primary" @click="addOrganWord"
                        ><i class="ri-add-line"></i>新增
                    </el-button>
                </template>
                <template #name="{ row, column, index }">
                    <el-form-item v-if="editIndex === index" prop="name">
                        <el-input v-model="formData.name" clearable />
                    </el-form-item>
                    <span v-else>{{ row.name }}</span>
                </template>
                <template #custom="{ row, column, index }">
                    <el-form-item v-if="editIndex === index" prop="custom">
                        <el-input ref="customSign" v-model="formData.custom" clearable />
                    </el-form-item>
                    <span v-else>{{ row.custom }}</span>
                </template>
                <template #opt="{ row, column, index }">
                    <div v-if="editIndex === index">
                        <el-button class="global-btn-second" size="small" @click="saveData(orgWordForm)"
                            ><i class="ri-book-mark-line"></i>保存
                        </el-button>
                        <el-button class="global-btn-second" size="small" @click="cancalData(orgWordForm)"
                            ><i class="ri-close-line"></i>取消
                        </el-button>
                    </div>
                    <div v-else>
                        <el-button class="global-btn-second" size="small" @click="property(row)"
                            ><i class="ri-book-3-line"></i>机关代字
                        </el-button>
                        <el-button class="global-btn-second" size="small" @click="editOrganWord(row, index)"
                            ><i class="ri-edit-line"></i>修改
                        </el-button>
                        <el-button class="global-btn-danger" size="small" type="danger" @click="delOrganWord(row)"
                            ><i class="ri-delete-bin-line"></i>删除
                        </el-button>
                    </div>
                </template>
            </y9Table>
        </el-form>
        <y9Dialog v-model:config="dialogConfig">
            <WordManage ref="wordManageRef" :row="row" />
        </y9Dialog>
    </div>
</template>
<script lang="ts" setup>
    import { reactive, ref } from 'vue';
    import type { ElLoading, ElMessage } from 'element-plus';
    import { organWordApi } from '@/api/itemAdmin/organWord';
    import WordManage from '@/views/organWord/wordManage.vue';

    const orgWordForm = ref<FormInstance>();
    const rules = reactive<FormRules>({
        name: { required: true, message: '请输入编号名称', trigger: 'blur' },
        custom: { required: true, message: '请输入编号标识', trigger: 'blur' }
    });
    const data = reactive({
        editIndex: '',
        tableData: [],
        formData: { id: '', name: '', custom: '' },
        isEmptyData: false,
        tableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '编号名称', key: 'name', width: '400', slot: 'name' },
                { title: '编号标识', key: 'custom', width: 'auto', slot: 'custom' },
                { title: '操作人', key: 'userName', width: '150' },
                { title: '添加时间', key: 'createTime', width: '180' },
                { title: '操作', slot: 'opt', width: '260' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false //取消分页
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'addBtn'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
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
        row: ''
    });

    let { tableConfig, filterConfig, editIndex, formData, isEmptyData, dialogConfig, row } = toRefs(data);

    async function getOrganWordList() {
        let res = await organWordApi.organWordList();
        tableConfig.value.tableData = res.data;
    }

    getOrganWordList();

    const addOrganWord = () => {
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                isEmptyData.value = true;
            }
        }
        if (!isEmptyData.value) {
            editIndex.value = tableConfig.value.tableData.length;
            tableConfig.value.tableData.push({ id: '', name: '', custom: '' });
            formData.value.id = '';
            formData.value.name = '';
            formData.value.custom = '';
        }
    };

    const editOrganWord = (rows, index) => {
        editIndex.value = index;
        formData.value.id = rows.id;
        formData.value.name = rows.name;
        formData.value.custom = rows.custom;
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const property = (rows) => {
        row.value = rows;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: '机关代字【' + rows.name + '】',
            showFooter: false
        });
    };

    const customSign = ref();
    const saveData = (refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                organWordApi.checkCustom(formData.value.id, formData.value.custom).then((res) => {
                    if (res.data) {
                        const loading = ElLoading.service({
                            lock: true,
                            text: '正在处理中',
                            background: 'rgba(0, 0, 0, 0.3)'
                        });
                        organWordApi.saveOrganWord(formData.value).then((res) => {
                            loading.close();
                            if (res.success) {
                                ElMessage({ type: 'success', message: res.msg, offset: 65 });
                                editIndex.value = '';
                                isEmptyData.value = false;
                                getOrganWordList();
                            } else {
                                ElMessage({ message: res.msg, type: 'error', offset: 65 });
                            }
                        });
                    } else {
                        ElMessage({ type: 'error', message: '此编号标识已存在，请重新输入', offset: 65 });
                        formData.value.custom = '';
                        customSign.value.focus();
                    }
                });
            }
        });
    };

    const cancalData = (refForm) => {
        editIndex.value = '';
        formData.value.name = '';
        formData.value.custom = '';
        refForm.resetFields();
        for (let i = 0; i < tableConfig.value.tableData.length; i++) {
            if (tableConfig.value.tableData[i].id == '') {
                tableConfig.value.tableData.splice(i, 1);
            }
        }
        isEmptyData.value = false;
    };

    const delOrganWord = (rows) => {
        ElMessageBox.confirm('您确定要删除编号吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                organWordApi.removeOrganWord(rows.id).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        getOrganWordList();
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

<style lang="scss">
    .orgWordTable .el-form-item--default {
        margin-bottom: 0px;
    }

    .orgWordTable .el-form-item {
        margin-bottom: 0px;
    }

    .orgWordTable .el-form-item__error {
        color: var(--el-color-danger);
        font-size: 12px;
        line-height: 1;
        padding-top: 2px;
        position: relative;
        top: 0%;
        left: 0;
    }
</style>
