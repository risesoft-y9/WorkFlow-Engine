<!--
 * @Author: your name
 * @Date: 2022-05-05 09:43:05
 * @LastEditTime: 2023-08-29 14:56:25
 * @LastEditors: zhangchongjie
 * @Description: 正文模板
-->
<template>
        <el-progress
            v-if="uploadLoading"
            type="line"
            :percentage="percentage"
            :stroke-width="18"
            class="progress"
            status="success"
            :text-inside="true"
            :show-text="true"
        ></el-progress>
        <y9Table :config="wordTemplateTableConfig" :filterConfig="filterConfig">
            <template v-slot:slotDate>
                <el-button-group>
                    <el-upload
                        :http-request="saveFile"
                        action=""
                        :show-file-list="false"
                        :limit="1"
                        ref="upload"
                        :before-upload="beforeUpload"
                        :auto-upload="true"
                    >
                        <el-button type="primary">
                            <i class="ri-upload-line"></i>上传模板(文件格式doc/docx)
                        </el-button>
                    </el-upload>
                </el-button-group>
            </template>
            <template #fName="{ row, column, index }">
                <el-link type="primary" :underline="false" @click="downloadFile(row)">{{ row.fileName }}</el-link>
            </template>
            <template #opt="{ row, column, index }">
                <el-button class="global-btn-second" size="small" @click="bookMarkBind(row)">
                    <i class="ri-book-mark-line"></i>书签配置
                </el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delTemplate(row)">
                    <i class="ri-delete-bin-line"></i>删除
                </el-button>
            </template>
        </y9Table>
        <y9Dialog v-model:config="dialogConfig">
            <y9Table :config="bindTableConfig">
                <template #columnBind="{ row, column, index }">
                    <div class="bookMarkBind" v-if="editIndex === index">
                        <el-form ref="bookMarkBindForm" :model="formData" :rules="rules" :inline="true">
                            <el-form-item prop="tableName">
                                <el-select
                                    style="width: 12vw"
                                    v-model="formData.tableName"
                                    placeholder="请选择业务表"
                                    @change="tableChange"
                                >
                                    <el-option
                                        v-for="item in tableList"
                                        :key="item.id"
                                        :label="item.tableName"
                                        :value="item.id"
                                    >
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item prop="columnName">
                                <el-select v-model="formData.columnName" placeholder="请选表字段">
                                    <el-option v-for="item in columnList" :key="item" :label="item" :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-form>
                    </div>
                    <span v-else>{{ row.tableColumn }} </span>
                </template>
                <template #opt_bind="{ row, column, index }">
                    <div v-if="editIndex === index">
                        <el-button
                            class="global-btn-second"
                            size="small"
                            @click="saveData(row, bookMarkBindForm, row.wordTemplateId, row.wordTemplateType)"
                        >
                            <i class="ri-book-mark-line"></i>保存
                        </el-button>
                        <el-button class="global-btn-second" size="small" @click="cancalData(row)">
                            <i class="ri-close-line"></i>取消
                        </el-button>
                    </div>
                    <div v-else>
                        <el-button class="global-btn-second" size="small" @click="deleteBookMarKBind(row)">
                            <i class="ri-delete-bin-line"></i>移除
                        </el-button>
                        <el-button class="global-btn-second" size="small" @click="bindTableColumn(row, index)">
                            <i class="ri-database-2-line"></i>绑定数据库字段
                        </el-button>
                    </div>
                </template>
            </y9Table>
        </y9Dialog>
</template>
<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive } from 'vue';
    import type { ElMessageBox, ElMessage, ElLoading } from 'element-plus';
    import type { UploadInstance } from 'element-plus';
    import {
        wordTemplateList,
        deleteWordTemplate,
        bookMarKList,
        getBookMarkBind,
        getColumns,
        saveOrUpdate,
        deleteBind,
    } from '@/api/itemAdmin/wordTemplate';
    import axios from 'axios';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings.ts';

    const upload = ref<UploadInstance>();
    const rules = reactive({
        tableName: { required: true, message: '请选择业务表', trigger: 'blur' },
        columnName: { required: true, message: '请选择表字段', trigger: 'blur' },
    });

    const filterConfig = ref({
        filtersValueCallBack: (filter) => {
            console.log(filter, 'dd');
        },
        itemList: [
            {
                type: 'slot',
                slotName: 'slotDate',
                span: 24,
            },
        ],
        showBorder: true,
        // borderRadio: '4px'
    });

    const data = reactive({
        percentage: 0,
        fileTypes: ['.doc', '.docx'],
        uploadLoading: false,
        bookMarkBindForm: {},
        formData: {
            tableName: '',
            columnName: '',
            wordTemplateId: '',
            bookMarkName: '',
            wordTemplateType: '',
        },
        tableList: [],
        columnList: [],
        editIndex: '',
        wordTemplateId: '',
        wordTemplateType: '',
        wordTemplateTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '模板名称', key: 'fileName', slot: 'fName' },
                { title: '文件大小', key: 'fileSize', width: '150' },
                { title: '上传人', key: 'personName', width: '150' },
                { title: '上传时间', key: 'uploadTime', width: '180' },
                { title: '操作', slot: 'opt', width: '240' },
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false, //取消分页
        },
        bindTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '书签名称', key: 'bookMarkName', width: '150' },
                { title: '表名.字段名', key: 'tableColumn', slot: 'columnBind' },
                { title: '绑定人员', key: 'userName', width: '100' },
                { title: '绑定时间', key: 'updateTime', width: '180' },
                { title: '操作', slot: 'opt_bind', width: '220' },
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height:'auto'
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
                if (!visible) {
                }
            },
        },
    });

    let {
        wordTemplateId,
        wordTemplateType,
        editIndex,
        percentage,
        fileTypes,
        uploadLoading,
        bookMarkBindForm,
        formData,
        tableList,
        columnList,
        wordTemplateTableConfig,
        bindTableConfig,
        dialogConfig,
    } = toRefs(data);

    async function getWordTemplateList() {
        let res = await wordTemplateList();
        wordTemplateTableConfig.value.tableData = res.data;
    }

    getWordTemplateList();

    const getToken = () => {
        return y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
    };

    const beforeUpload = (file) => {
        let extName = file.name.substring(file.name.lastIndexOf('.'));
        if (fileTypes.value[0].indexOf(extName) == -1 && fileTypes.value[1].indexOf(extName) == -1) {
            ElMessage({ type: 'error', message: '请上传指定格式的文件', offset: 65 });
            return false;
        }
    };

    const bindTableColumn = (rows, index) => {
        editIndex.value = index;
        getBookMarkBind(wordTemplateId.value, rows.bookMarkName).then((res) => {
            if (res.success) {
                if (res.data.bookMarkBind != null) {
                    formData.value = res.data.bookMarkBind;
                }
                columnList.value = res.data.columnList;
                tableList.value = res.data.tableList;
            }
        });
    };

    const tableChange = (val) => {
        for (let i = 0; i < tableList.value.length; i++) {
            if (tableList.value[i].id == val) {
                formData.value.tableName = tableList.value[i].tableName;
                break;
            }
        }
        formData.value.columnName = '';
        getColumns(val).then((res) => {
            columnList.value = res.data;
        });
    };

    const saveData = (rows, form) => {
        formData.value.bookMarkName = rows.bookMarkName;
        formData.value.wordTemplateId = wordTemplateId.value;
        formData.value.wordTemplateType = wordTemplateType.value;
        form.validate((valid) => {
            if (valid) {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                saveOrUpdate(formData.value).then((res) => {
                    loading.close();
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        editIndex.value = '';
                        formData.value.columnName = '';
                        formData.value.tableName = '';
                        getBookMarKList(wordTemplateId.value, wordTemplateType.value);
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65 });
                    }
                });
            }
        });
    };

    const deleteBookMarKBind = (rows) => {
        ElMessageBox.confirm('您确定要删除绑定的表和字段?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }).then(() => {
                deleteBind(wordTemplateId.value, rows.bookMarkName).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        getBookMarKList(wordTemplateId.value, wordTemplateType.value);
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
        }).catch(() => {
            ElMessage({
                type: 'info',
                message: '已取消删除',
                offset: 65,
            });
        });
    };

    const cancalData = (rows) => {
        editIndex.value = '';
        formData.value.columnName = '';
        formData.value.tableName = '';
    };

    const downloadFile = (row) => {
        window.open(
            import.meta.env.VUE_APP_CONTEXT + +'vue/wordTemplate/download?id=' + row.id + '&access_token=' + getToken()
        );
    };

    const saveFile = (params) => {
        percentage.value = 0;

        let formData = new FormData();
        formData.append('file', params.file);

        let config = {
            onUploadProgress: (progressEvent) => {
                //progressEvent.loaded:已上传文件大小,progressEvent.total:被上传文件的总大小
                let percent = ((progressEvent.loaded / progressEvent.total) * 100) | 0;
                percentage.value = percent;
            },
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: 'Bearer ' + getToken(),
            },
        };
        uploadLoading.value = true;
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        axios
            .post(import.meta.env.VUE_APP_CONTEXT + 'vue/wordTemplate/upload', formData, config)
            .then((res) => {
                loading.close();
                if (res.data.success) {
                    uploadLoading.value = false;
                }
                getWordTemplateList();
                upload.value.clearFiles();
                ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg, offset: 65 });
            })
            .catch((err) => {
                ElMessage({ type: 'error', message: '发生异常', offset: 65 });
            });
    };

    const delTemplate = (rows) => {
        ElMessageBox.confirm('您确定要删除当前模板?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
            .then(() => {
                deleteWordTemplate(rows.id).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        getWordTemplateList();
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消删除',
                    offset: 65,
                });
            });
    };

    const bookMarkBind = (rows) => {
        wordTemplateId.value = rows.id;
        wordTemplateType.value = rows.wordTemplateType;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '70%',
            title: '书签配置',
            showFooter: false,
        });
        getBookMarKList(rows.id, rows.wordTemplateType);
    };

    function getBookMarKList(id, type) {
        bookMarKList(id, type).then((res) => {
            bindTableConfig.value.tableData = res.data; //[{bookMarkName:'RisesoftBody',tableName:'',columnName:'',userName:'',updateTime:''}];
        });
    }
</script>

<style lang="scss" scoped>
@import "@/theme/global.scss";
</style>
