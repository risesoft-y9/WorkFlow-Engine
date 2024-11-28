<template>
    <div
        v-loading="loading"
        :element-loading-text="$t('正在处理中')"
        class="control"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <div v-if="type == 'parallel'" class="margin-bottom-20">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="addInstance"
                ><i class="ri-add-line"></i>{{ $t('加签') }}
            </el-button>
        </div>
        <y9Table :config="tableConfig">
            <template #optButton="{ row, column, index }">
                <el-button
                    v-if="type == 'sequential' && (row.status == '正在办理' || row.status == '未开始')"
                    :style="{ fontSize: fontSizeObj.smallFontSize }"
                    size="small"
                    type="primary"
                    @click="addInstance(row)"
                    ><i class="ri-add-line"></i>{{ $t('加签') }}
                </el-button>
                <el-button
                    v-if="type == 'parallel' || (type == 'sequential' && row.status == '未开始')"
                    :style="{ fontSize: fontSizeObj.smallFontSize }"
                    size="small"
                    type="primary"
                    @click="delExecution(row)"
                    ><i class="ri-subtract-line"></i>{{ $t('减签') }}
                </el-button>
                <el-button
                    v-if="type == 'parallel'"
                    :class="{ 'margin-button': fontSizeObj.buttonSize == 'large' }"
                    :style="{ fontSize: fontSizeObj.smallFontSize }"
                    size="small"
                    type="primary"
                    @click="sponsor(row.taskId)"
                    ><i class="ri-user-settings-line"></i>{{ $t('设为主办') }}
                </el-button>
            </template>
        </y9Table>
    </div>
    <y9Dialog v-model:config="dialogConfig">
        <userChoise
            ref="userChoiseRef"
            :basicData="basicData"
            :dialogConfig="dialogConfig"
            :fromType="$t(fromType)"
            :instanceData="instanceData"
            :routeToTask="taskDefinitionKey"
            @reloadTable="multiInstanceList"
        />
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, defineProps, inject, reactive } from 'vue';
    import type { ElMessage, ElMessageBox } from 'element-plus';
    import userChoise from '@/views/workForm/userChoise.vue';
    import {
        getAddOrDeleteMultiInstance,
        removeExecution,
        removeExecution4Sequential,
        setSponsor
    } from '@/api/flowableUI/multiInstance';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        basicData: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        fromType: '',
        instanceData: {},
        loading: false,
        type: '',
        taskDefinitionKey: '',
        userId: '',
        num: 0,
        tableConfig: {
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('办理人')), key: 'assigneeName', width: 'auto' },
                { title: computed(() => t('任务节点')), key: 'name', width: '120' },
                { title: computed(() => t('操作')), key: 'opt', width: '200', slot: 'optButton' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
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
        }
    });

    let { fromType, instanceData, loading, type, taskDefinitionKey, userId, num, tableConfig, dialogConfig } =
        toRefs(data);

    const emits = defineEmits(['refreshButton']);
    multiInstanceList();

    function multiInstanceList() {
        loading.value = true;
        getAddOrDeleteMultiInstance(props.basicData.processInstanceId).then((res) => {
            loading.value = false;
            type.value = res.data.type;
            if (type.value == 'sequential') {
                tableConfig.value.columns.splice(2, 0, {
                    title: computed(() => t('任务状态')),
                    key: 'status',
                    width: '150'
                });
                for (let item of res.data.rows) {
                    if (item.status == '未开始') {
                        //串行加签后存在未开始人员，如果有发送按钮，提交按钮，办结按钮等，要提醒重新打开办件处理
                        emits('refreshButton');
                    }
                }
                if (res.data.rows[res.data.rows.length - 1].status == '正在办理') {
                    //串行减签只剩自己最后一人，送下一人按钮需要取消
                    emits('refreshButton');
                }
            } else if (type.value == 'parallel') {
                tableConfig.value.columns.splice(2, 0, {
                    title: computed(() => t('是否主办')),
                    key: 'isZhuBan',
                    width: '150'
                });
                for (let item of res.data.rows) {
                    //并行加签后，存在多人，如果有发送按钮，提交按钮，办结按钮等，要提醒重新打开办件处理，显示办理完成按钮
                    if (item.assigneeId == res.data.userId && res.data.rows.length > 1 && item.isZhuBan == '否') {
                        emits('refreshButton');
                    }
                }
                if (res.data.rows.length == 1) {
                    //并行减签只剩自己最后一人，办理完成按钮需要取消
                    emits('refreshButton');
                }
            }
            props.basicData.taskId = res.data.taskId;
            taskDefinitionKey.value = res.data.taskDefinitionKey;
            tableConfig.value.tableData = res.data.rows;
            num.value = tableConfig.value.tableData.length;
            userId.value = res.data.userId;
        });
    }

    function sponsor(taskId) {
        setSponsor(taskId).then((res) => {
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.control' });
                multiInstanceList();
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.control' });
            }
        });
    }

    function addInstance(row) {
        let params = {
            taskDefinitionKey: taskDefinitionKey.value,
            assigneeId: '',
            executionId: '',
            num: 0
        };
        if (type.value == 'sequential') {
            var selectUserName = row.assigneeName;
            ElMessageBox.confirm(`${t('确定在')}【${selectUserName}】${t('之后添加办理人吗')}？`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info',
                appendTo: '.control'
            })
                .then(() => {
                    params.assigneeId = row.assigneeId;
                    params.executionId = row.executionId;
                    params.num = row.num;
                    instanceData.value = params;
                    fromType.value = '加减签';
                    Object.assign(dialogConfig.value, {
                        show: true,
                        width: '50%',
                        title: computed(() => t('人员')),
                        showFooter: false
                    });
                })
                .catch(() => {
                    ElMessage({ type: 'info', message: t('已取消加签'), offset: 65, appendTo: '.control' });
                });
        } else {
            instanceData.value = params;
            fromType.value = '加减签';
            Object.assign(dialogConfig.value, {
                show: true,
                width: '50%',
                title: computed(() => t('人员')),
                showFooter: false
            });
        }
    }

    function delExecution(row) {
        let positionId = sessionStorage.getItem('positionId');
        if (props.basicData.itembox == 'todo' && row.assigneeId == positionId) {
            ElMessage({ type: 'error', message: t('不能减签自己'), offset: 65, appendTo: '.control' });
            return;
        }
        var selectUserName = row.assigneeName;
        ElMessageBox.confirm(`${t('真的删除')}【${selectUserName}】${t('办理人员吗？')}`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.control'
        })
            .then(() => {
                if (type.value == 'sequential') {
                    removeExecution4Sequential(row.executionId, row.taskId, row.assigneeId, row.num).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.control' });
                            multiInstanceList();
                        } else {
                            ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.control' });
                        }
                    });
                } else {
                    if (num.value == 1) {
                        ElMessage({ type: 'error', message: t('最后一人无法减签'), offset: 65, appendTo: '.control' });
                        return;
                    }
                    if (userId.value == row.assigneeId) {
                        ElMessage({ type: 'error', message: t('无法减签自己'), offset: 65, appendTo: '.control' });
                        return;
                    }
                    removeExecution(row.executionId, row.taskId, row.assigneeId).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.control' });
                            multiInstanceList();
                        } else {
                            ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.control' });
                        }
                    });
                }
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消减签'), offset: 65, appendTo: '.control' });
            });
    }
</script>

<style lang="scss" scoped>
    .margin-button {
        margin-left: 3px;
    }

    .control {
        /*message */
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        /*messageBox */
        :global(.el-message-box .el-message-box__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        :global(.el-message-box .el-message-box__title) {
            font-size: v-bind('fontSizeObj.largeFontSize');
        }

        :global(.el-message-box .el-message-box__btns button) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
</style>
