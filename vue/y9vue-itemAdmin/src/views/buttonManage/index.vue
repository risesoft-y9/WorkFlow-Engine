<template>
    <div class="buttonManage">
        <y9Card :showHeader="false">
            <el-form ref="commonForm" :model="commonFormData" :rules="rules" style="height: 100%">
                <y9Table :config="commonTableConfig" :filterConfig="commonFilterConfig">
                    <template #common_btn>
                        <el-button class="global-btn-main" type="primary" @click="addButton('common')"
                            ><i class="ri-add-line"></i>普通按钮
                        </el-button>
                    </template>
                    <template #cname="{ row, column, index }">
                        <el-form-item v-if="editIndex_common === index" prop="name">
                            <el-input v-model="commonFormData.name" clearable />
                        </el-form-item>
                        <span v-else>{{ row.name }}</span>
                    </template>
                    <template #c_customId="{ row, column, index }">
                        <el-form-item v-if="editIndex_common === index" prop="customId">
                            <el-input
                                ref="customSign_c"
                                v-model="commonFormData.customId"
                                :disabled="isEdit"
                                clearable
                            />
                        </el-form-item>
                        <span v-else>{{ row.customId }}</span>
                    </template>
                    <template #common_opt="{ row, column, index }">
                        <div v-if="editIndex_common === index">
                            <el-button class="global-btn-second" size="small" @click="saveData('common', commonForm)"
                                ><i class="ri-book-mark-line"></i>保存
                            </el-button>
                            <el-button class="global-btn-second" size="small" @click="cancalData('common', commonForm)">
                                <i class="ri-close-line"></i>取消
                            </el-button>
                        </div>
                        <div v-else>
                            <el-button class="global-btn-second" size="small" @click="bindDetail(row)"
                                ><i class="ri-book-3-line"></i>绑定详情
                            </el-button>
                            <el-button class="global-btn-second" size="small" @click="editButton('common', row, index)"
                                ><i class="ri-edit-line"></i>修改
                            </el-button>
                            <el-button
                                class="global-btn-danger"
                                size="small"
                                type="danger"
                                @click="delButton('common', row)"
                                ><i class="ri-delete-bin-line"></i>删除
                            </el-button>
                        </div>
                    </template>
                </y9Table>
            </el-form>
        </y9Card>
        <y9Card :showHeader="false">
            <div class="sendList">
                <el-form ref="sendForm" :model="sendFormData" :rules="rules" style="height: 100%">
                    <y9Table :config="sendTableConfig" :filterConfig="sendFilterConfig">
                        <template #send_btn>
                            <el-button class="global-btn-main" type="primary" @click="addButton('send')"
                                ><i class="ri-add-line"></i>发送按钮
                            </el-button>
                        </template>
                        <template #sname="{ row, column, index }">
                            <el-form-item v-if="editIndex_send === index" prop="name">
                                <el-input v-model="sendFormData.name" clearable />
                            </el-form-item>
                            <span v-else>{{ row.name }}</span>
                        </template>
                        <template #s_customId="{ row, column, index }">
                            <el-form-item v-if="editIndex_send === index" prop="customId">
                                <el-input
                                    ref="customSign_s"
                                    v-model="sendFormData.customId"
                                    :disabled="isEdit"
                                    clearable
                                />
                            </el-form-item>
                            <span v-else>{{ row.customId }}</span>
                        </template>
                        <template #send_opt="{ row, column, index }">
                            <div v-if="editIndex_send === index">
                                <el-button class="global-btn-second" size="small" @click="saveData('send', sendForm)"
                                    ><i class="ri-book-mark-line"></i>保存
                                </el-button>
                                <el-button class="global-btn-second" size="small" @click="cancalData('send', sendForm)">
                                    <i class="ri-close-line"></i>取消
                                </el-button>
                            </div>
                            <div v-else>
                                <el-button class="global-btn-second" size="small" @click="bindDetail(row)"
                                    ><i class="ri-book-3-line"></i>绑定详情
                                </el-button>
                                <el-button
                                    class="global-btn-second"
                                    size="small"
                                    @click="editButton('send', row, index)"
                                >
                                    <i class="ri-edit-line"></i>修改
                                </el-button>
                                <el-button
                                    class="global-btn-danger"
                                    size="small"
                                    type="danger"
                                    @click="delButton('send', row)"
                                    ><i class="ri-delete-bin-line"></i>删除
                                </el-button>
                            </div>
                        </template>
                    </y9Table>
                </el-form>
            </div>
        </y9Card>
        <y9Dialog v-model:config="dialogConfig">
            <BindDetailRef ref="bindDetailRef" :row="row" />
        </y9Dialog>
    </div>
</template>
<script lang="ts" setup>
    import { reactive, ref } from 'vue';
    import type { ElLoading, ElMessage } from 'element-plus';
    import { checkCustomId, getCommonButtonList, removeCommonButton, saveOrUpdate } from '@/api/itemAdmin/commonButton';
    import { checkSendCustomId, getSendButtonList, removeSendButton, saveSendButton } from '@/api/itemAdmin/sendButton';
    import BindDetailRef from '@/views/buttonManage/bindDetail.vue';

    const customSign_s = ref();
    const customSign_c = ref();
    const isEdit = ref(false);
    const editIndex_common = ref('');
    const editIndex_send = ref('');
    const commonFormData = ref({ id: '', name: '', customId: '' });
    const sendFormData = ref({ id: '', name: '', customId: '' });
    const commonForm = ref<FormInstance>();
    const sendForm = ref<FormInstance>();
    const rules = reactive<FormRules>({
        name: { required: true, message: '请输入按钮名称', trigger: 'blur' },
        customId: { required: true, message: '请输入唯一标识', trigger: 'blur' }
    });
    const data = reactive({
        commonTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '普通按钮名称', key: 'name', slot: 'cname' },
                { title: '唯一标示', key: 'customId', slot: 'c_customId', width: 'auto' },
                { title: '操作人', key: 'userName', width: '150' },
                { title: '添加时间', key: 'createTime', width: '180' },
                { title: '修改时间', key: 'updateTime', width: '180' },
                { title: '操作', slot: 'common_opt', width: '280' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        commonFilterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'common_btn'
                }
            ]
        },
        sendTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '发送按钮名称', key: 'name', slot: 'sname' },
                { title: '唯一标示', key: 'customId', slot: 's_customId' },
                { title: '操作人', key: 'userName', width: '150' },
                { title: '添加时间', key: 'createTime', width: '180' },
                { title: '修改时间', key: 'updateTime', width: '180' },
                { title: '操作', slot: 'send_opt', width: '280' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        sendFilterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'send_btn'
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
        row: ''
    });

    let { commonTableConfig, commonFilterConfig, sendTableConfig, sendFilterConfig, dialogConfig, row } = toRefs(data);

    async function getCommonList() {
        let res = await getCommonButtonList();
        commonTableConfig.value.tableData = res.data;
    }

    getCommonList();

    async function getSendList() {
        let res = await getSendButtonList();
        sendTableConfig.value.tableData = res.data;
    }

    getSendList();

    const isEmptyData_c = ref(false);
    const isEmptyData_s = ref(false);
    const addButton = (type) => {
        if (type == 'common') {
            for (let i = 0; i < commonTableConfig.value.tableData.length; i++) {
                if (commonTableConfig.value.tableData[i].id == '') {
                    isEmptyData_c.value = true;
                }
            }
            if (!isEmptyData_c.value) {
                editIndex_common.value = commonTableConfig.value.tableData.length;
                commonTableConfig.value.tableData.push({ id: '', name: '', customId: '' });
                commonFormData.value.id = '';
                commonFormData.value.name = '';
                commonFormData.value.customId = '';
                isEdit.value = false;
            }
        } else {
            for (let i = 0; i < sendTableConfig.value.tableData.length; i++) {
                if (sendTableConfig.value.tableData[i].id == '') {
                    isEmptyData_s.value = true;
                }
            }
            if (!isEmptyData_s.value) {
                editIndex_send.value = sendTableConfig.value.tableData.length;
                sendTableConfig.value.tableData.push({ id: '', name: '', customId: '' });
                sendFormData.value.id = '';
                sendFormData.value.name = '';
                sendFormData.value.customId = '';
                isEdit.value = false;
            }
        }
    };

    const editButton = (type, rows, index) => {
        if (type == 'common') {
            editIndex_common.value = index;
            commonFormData.value.id = rows.id;
            commonFormData.value.name = rows.name;
            commonFormData.value.customId = rows.customId;
            isEdit.value = true;
            for (let i = 0; i < commonTableConfig.value.tableData.length; i++) {
                if (commonTableConfig.value.tableData[i].id == '') {
                    commonTableConfig.value.tableData.splice(i, 1);
                }
            }
            isEmptyData_c.value = false;
        } else {
            editIndex_send.value = index;
            sendFormData.value.id = rows.id;
            sendFormData.value.name = rows.name;
            sendFormData.value.customId = rows.customId;
            isEdit.value = true;
            for (let i = 0; i < sendTableConfig.value.tableData.length; i++) {
                if (sendTableConfig.value.tableData[i].id == '') {
                    sendTableConfig.value.tableData.splice(i, 1);
                }
            }
            isEmptyData_s.value = false;
        }
    };

    const bindDetail = (rows) => {
        row.value = rows;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '70%',
            title: '绑定详情',
            showFooter: false
        });
    };

    const saveData = (type, refFrom) => {
        if (!refFrom) return;
        refFrom.validate((valid) => {
            if (valid) {
                if (type == 'common') {
                    checkCustomId(commonFormData.value.customId).then((res) => {
                        if (!res.data) {
                            const loading = ElLoading.service({
                                lock: true,
                                text: '正在处理中',
                                background: 'rgba(0, 0, 0, 0.3)'
                            });
                            saveOrUpdate(commonFormData.value).then((res) => {
                                loading.close();
                                if (res.success) {
                                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                                    editIndex_common.value = '';
                                    isEmptyData_c.value = false;
                                    getCommonList();
                                } else {
                                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                                }
                            });
                        } else {
                            ElMessage({ type: 'error', message: '此标识已存在，请重新输入', offset: 65 });
                            commonFormData.value.customId = '';
                            customSign_c.value.focus();
                        }
                    });
                } else {
                    checkSendCustomId(sendFormData.value.customId).then((res) => {
                        if (!res.data) {
                            const loading = ElLoading.service({
                                lock: true,
                                text: '正在处理中',
                                background: 'rgba(0, 0, 0, 0.3)'
                            });
                            saveSendButton(sendFormData.value).then((res) => {
                                loading.close();
                                if (res.success) {
                                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                                    editIndex_send.value = '';
                                    isEmptyData_s.value = false;
                                    getSendList();
                                } else {
                                    ElMessage({ message: res.msg, type: 'error', offset: 65 });
                                }
                            });
                        } else {
                            ElMessage({ type: 'error', message: '此标识已存在，请重新输入', offset: 65 });
                            sendFormData.value.customId = '';
                            customSign_s.value.focus();
                        }
                    });
                }
            }
        });
    };

    const cancalData = (type, refForm) => {
        refForm.resetFields();
        if (type == 'common') {
            editIndex_common.value = '';
            commonFormData.value.name = '';
            commonFormData.value.customId = '';

            for (let i = 0; i < commonTableConfig.value.tableData.length; i++) {
                if (commonTableConfig.value.tableData[i].id == '') {
                    commonTableConfig.value.tableData.splice(i, 1);
                }
            }
            isEmptyData_c.value = false;
        } else {
            editIndex_send.value = '';
            sendFormData.value.name = '';
            sendFormData.value.customId = '';

            for (let i = 0; i < sendTableConfig.value.tableData.length; i++) {
                if (sendTableConfig.value.tableData[i].id == '') {
                    sendTableConfig.value.tableData.splice(i, 1);
                }
            }
            isEmptyData_s.value = false;
        }
    };

    const delButton = (type, rows) => {
        ElMessageBox.confirm('您确定要删除数据吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                if (type == 'common') {
                    removeCommonButton(rows.id).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65 });
                            getCommonList();
                        } else {
                            ElMessage({ message: res.msg, type: 'error', offset: 65 });
                        }
                    });
                } else {
                    removeSendButton(rows.id).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65 });
                            getSendList();
                        } else {
                            ElMessage({ message: res.msg, type: 'error', offset: 65 });
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
    };
</script>

<style lang="scss">
    .buttonTable .el-form-item--default {
        margin-bottom: 0px;
    }

    .buttonTable .el-form-item {
        margin-bottom: 0px;
    }

    .buttonTable .el-form-item__error {
        color: var(--el-color-danger);
        font-size: 12px;
        line-height: 1;
        padding-top: 2px;
        position: relative;
        top: 0%;
        left: 0;
    }

    .buttonManage .el-card__body {
        padding-top: 10px;
    }

    .buttonManage {
        height: 100%;
    }

    .buttonManage .y9-card {
        height: 50% !important;
        box-shadow: none !important;
    }

    .buttonManage .y9-table-div {
        height: calc(100% - 52px) !important;
    }

    .buttonManage .el-table--fit {
        height: 100% !important;
    }

    .buttonManage .el-form-item {
        margin-bottom: 0px !important;
    }

    .buttonManage .y9-card-content {
        padding: 0 !important;
        background-color: #eef0f7;
        height: 100% !important;
    }

    .buttonManage .sendList {
        margin-top: 30px;
        height: calc(100% - 30px) !important;
    }
</style>
