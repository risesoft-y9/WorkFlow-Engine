<template>
    <div class="div1">
        <el-checkbox v-model="process">{{ $t('流程办结提醒') }}</el-checkbox>
        <el-checkbox v-model="task" @change="handleAllTaskDone">{{ $t('任务完成提醒') }}</el-checkbox>
        <el-checkbox v-model="nodeArrived" @change="handleAllNodeArrived">{{ $t('节点到达提醒') }}</el-checkbox>
        <el-checkbox v-model="nodeDone" @change="handleAllNodeDone">{{ $t('节点完成提醒') }}</el-checkbox>
        <el-button
            :style="{ fontSize: fontSizeObj.smallFontSize }"
            size="small"
            style="margin-bottom: 10px"
            type="primary"
            @click="setRemind"
        >
            <i class="ri-save-line"></i>{{ $t('保存') }}
        </el-button>
        <!-- <div class="slider-demo-block">
          <el-slider v-model="value" :marks="marks" :step="10" :max="40" size="small" style="width:80%" show-stops/>
        </div> -->
    </div>
    <el-row :gutter="20">
        <el-col :span="16">
            <y9Table
                ref="reminderTableRef"
                v-model:selectedVal="remindData"
                :config="reminderTableConfig"
                @select="handleSelectionChange"
                @select-all="handleSelectionChange"
            ></y9Table>
        </el-col>
        <el-col :span="8" class="right-checkbox">
            <y9Table :config="nodeTableConfig">
                <template #optButton="{ row, column, index }">
                    <el-checkbox
                        v-model="boxArriveGroup"
                        :label="row.taskDefKey + ':' + row.taskDefName"
                        border
                        size="small"
                        style="margin-right: 5px"
                        @change="handleNodeArrived"
                        >{{ $t('到达') }}
                    </el-checkbox>
                    <el-checkbox
                        v-model="boxDoneGroup"
                        :label="row.taskDefKey + ':' + row.taskDefName"
                        border
                        size="small"
                        @change="handleNodeDone"
                        >{{ $t('完成') }}
                    </el-checkbox>
                </template>
            </y9Table>
        </el-col>
    </el-row>
</template>

<script lang="ts" setup>
    import type { CSSProperties } from 'vue';
    import { computed, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { getBpmList, remindTaskList, saveRemindInstance } from '@/api/flowableUI/reminder';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const props = defineProps({
        processInstanceId: String,
        reloadTable: Function
    });

    interface Mark {
        style: CSSProperties;
        label: string;
    }

    type Marks = Record<number, Mark | string>;
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const value = ref([10, 20, 30, 40]);
    const marks = reactive<Marks>({
        10: '流程办结提醒',
        20: '任务完成提醒',
        30: '节点到达提醒',
        40: {
            style: {
                color: '#1989FA',
                width: '200px'
            },
            label: '节点完成提醒'
        }
    });
    const data = reactive({
        remindData: [],
        reminderTableRef: '',
        process: false,
        task: false,
        nodeArrived: false,
        nodeDone: false,
        boxArriveGroup: [],
        boxDoneGroup: [],
        selectedRemindData: [],
        taskIds: '',
        remindType: '',
        multipleSelection: [],
        reminderTableConfig: {
            columns: [
                { title: '', type: 'selection', width: 50, fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('办件人')), key: 'userName', width: '160' },
                { title: computed(() => t('开始时间')), key: 'createTime', width: '170' },
                { title: computed(() => t('持续时间')), key: 'duration' },
                { title: computed(() => t('办理环节')), key: 'taskName', width: '160' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
        },
        nodeTableConfig: {
            columns: [
                { title: computed(() => t('节点名称')), key: 'taskDefName' },
                { title: computed(() => t('操作')), key: 'opt', width: '180', slot: 'optButton' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
        }
    });

    let {
        remindData,
        reminderTableRef,
        process,
        task,
        nodeArrived,
        nodeDone,
        boxArriveGroup,
        boxDoneGroup,
        selectedRemindData,
        taskIds,
        remindType,
        multipleSelection,
        nodeTableConfig,
        reminderTableConfig
    } = toRefs(data);

    watch(
        () => props.processInstanceId,
        (newVal) => {
            reloadTable();
        }
    );

    onMounted(() => {
        reloadTable();
        getBpmNodeList();
    });

    async function reloadTable() {
        remindTaskList(props.processInstanceId).then((res) => {
            if (res.success) {
                reminderTableConfig.value.tableData = res.data.rows;
                taskIds.value = res.data.taskIds;
                remindType.value = res.data.remindType;
                if (remindType.value.indexOf('processComplete') != -1) {
                    process.value = true;
                }
                // if (remindType.value.indexOf("taskComplete") != -1){
                //   task.value = true;
                // }
                if (remindType.value.indexOf('nodeArrive') != -1) {
                    nodeArrived.value = true;
                }
                if (remindType.value.indexOf('nodeComplete') != -1) {
                    nodeDone.value = true;
                }

                setTimeout(() => {
                    reminderTableConfig.value.tableData.forEach((item) => {
                        if (taskIds.value.indexOf(item.taskId) != -1) {
                            reminderTableRef.value.elTableRef.toggleRowSelection(item, true);
                            task.value = true;
                        }
                    });
                }, 500);
            }
        });
    }

    function getBpmNodeList() {
        getBpmList(props.processInstanceId).then((res) => {
            if (res.success) {
                nodeTableConfig.value.tableData = res.data.rows;
                let arriveTaskKey = res.data.arriveTaskKey;
                if (arriveTaskKey.length > 0) {
                    boxArriveGroup.value = arriveTaskKey.split(',');
                }
                let completeTaskKey = res.data.completeTaskKey;
                if (completeTaskKey.length > 0) {
                    boxDoneGroup.value = completeTaskKey.split(',');
                }
            }
        });
    }

    function handleSelectionChange(data) {
        task.value = data.length == 0 ? false : true;
        selectedRemindData.value = data;
    }

    onMounted(() => {});

    function handleAllTaskDone(val) {
        //取消全选
        if (!val) {
            reminderTableRef.value.elTableRef.clearSelection();
        }
    }

    function handleAllNodeArrived() {
        //取消节点选中
        boxArriveGroup.value = [];
    }

    function handleNodeArrived() {
        //列表中有选中项时选中全选按钮
        nodeArrived.value = boxArriveGroup.value.length == 0 ? false : true;
    }

    function handleAllNodeDone() {
        boxDoneGroup.value = [];
    }

    function handleNodeDone() {
        //列表中有选中项时选中全选按钮
        nodeDone.value = boxDoneGroup.value.length == 0 ? false : true;
    }

    function clear() {
        //清空数据
        reminderTableConfig.value.tableData = [];
        nodeTableConfig.value.tableData = [];
        process.value = false;
        task.value = false;
        nodeArrived.value = false;
        nodeDone.value = false;
        boxArriveGroup.value = [];
        boxDoneGroup.value = [];
        selectedRemindData.value = [];
    }

    function setRemind() {
        //保存
        if (!task.value && !nodeArrived.value && !nodeDone.value) {
            ElMessage({ type: 'error', message: t('请勾选要配置提醒类型！'), offset: 65, appendTo: '.div1' });
            return false;
        }
        if (task.value && selectedRemindData.value.length == 0) {
            ElMessage({ type: 'error', message: t('请选择要提醒的任务'), offset: 65, appendTo: '.div1' });
            return false;
        }
        if (nodeArrived.value && boxArriveGroup.value.length == 0) {
            ElMessage({ type: 'error', message: t('请选择要提醒到达的节点'), offset: 65, appendTo: '.div1' });
            return false;
        }
        if (nodeDone.value && boxDoneGroup.value.length == 0) {
            ElMessage({ type: 'error', message: t('请选择要提醒完成的节点'), offset: 65, appendTo: '.div1' });
            return false;
        }
        let task_Ids = selectedRemindData.value
            .map((item) => {
                return item.taskId;
            })
            .join(',');
        let arriveTaskKey = boxArriveGroup.value.map((item) => item).join(',');
        let completeTaskKey = boxDoneGroup.value.map((item) => item).join(',');
        saveRemindInstance(props.processInstanceId, task_Ids, process.value, arriveTaskKey, completeTaskKey).then(
            (res) => {
                if (res.success) {
                    ElMessage({ type: 'success', message: t('保存成功'), offset: 65, appendTo: '.div1' });
                    props.reloadTable();
                }
            }
        );
    }
</script>
<style lang="scss" scoped>
    .div1 {
        margin-left: 8px;
    }

    .div1 {
        :deep(.el-checkbox__label) {
            padding-left: 5px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    .div1 .el-checkbox {
        margin-right: 20px;
        margin-left: 10px;
        vertical-align: middle;
    }

    .slider-demo-block {
        display: flex;
        align-items: center;
    }

    .slider-demo-block .el-slider {
        margin-top: 0;
        margin-left: 12px;
    }

    .right-checkbox {
        :deep(.el-checkbox.is-bordered.el-checkbox--small .el-checkbox__label) {
            font-size: v-bind('fontSizeObj.smallFontSize');
        }
    }

    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
