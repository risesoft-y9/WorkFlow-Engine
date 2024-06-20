<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2022-11-24 15:24:23
 * @LastEditors: mengjuhua
 * @Description:  催办
-->
<template>
    <y9Table
        :config="reminderTableConfig"
        :filterConfig="filterConfig"
        @select-all="handleSelectionChange"
        @select="handleSelectionChange"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #reminderFilter>
            <el-button-group style="display: flex;">
                <el-button type="primary" @click="reminder" :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('催办') }}</el-button>
                <el-button type="primary" @click="myReminder" :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('我的催办') }}</el-button>
                <el-button type="primary" @click="allReminder" :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('所有催办') }}</el-button>
            </el-button-group>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <remindList
            v-if="dialogConfig.type == 'reminder'"
            ref="remindListRef"
            :processInstanceId="processInstanceId"
            :type="type"
        />
        <div v-if="dialogConfig.type == 'remindMsg'">
            <el-input
                type="textarea"
                resize="none"
                :rows="5"
                :placeholder="$t('请输入内容')"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                maxlength="50"
                v-model="msgContent"
                show-word-limit
            ></el-input>
            <el-button type="primary" style="margin-top: 8px;float: right;" @click="dialogConfig.show = false"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('取消') }}</el-button>
            <el-button type="primary" style="margin-top: 8px;margin-right: 8px;float: right;" @click="sendReminder()"
            :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('发送催办') }}</el-button>
        </div>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { taskList, saveReminder } from '@/api/flowableUI/reminder';
    import remindList from '@/views/reminder/remindList.vue';
    import { onMounted, ref, watch, reactive, toRefs, inject, computed } from 'vue';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo')||{}; 
    const props = defineProps({
        processInstanceId: String,
        taskId: String,
    });

    let total = ref(0);
    const data = reactive({
        type: '',
        processInstanceId: '',
        msgContent: '',
        taskIds: [] as any[],
        multipleSelection: [] as any[],
        reminderTableConfig: {
            columns: [
                { title: '', type: 'selection', width: '50', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('办件人')), key: 'userName' },
                { title: computed(() => t('开始时间')), key: 'createTime', width: '190' },
                { title: computed(() => t('持续时间')), key: 'duration' },
                { title: computed(() => t('办理环节')), key: 'taskName', width: '180' },
            ],
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value, //总条目数
                pageSizeOpts:[10, 20, 30, 50, 100]
            },
            border: 0,
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 8,
                    slotName: 'reminderFilter',
                },
            ],
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            type: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {},
        },
    });

    let {
        type,
        processInstanceId,
        msgContent,
        taskIds,
        filterConfig,
        multipleSelection,
        reminderTableConfig,
        dialogConfig,
    } = toRefs(data);

    watch(
        () => props.taskId,
        (newVal) => {
            reloadTable();
        }
    );

    onMounted(() => {
        reloadTable();
    });

    async function reloadTable() {
        let page = reminderTableConfig.value.pageConfig.currentPage;
        let rows = reminderTableConfig.value.pageConfig.pageSize;
        taskList(props.processInstanceId, page, rows).then((res) => {
            if (res.success) {
                reminderTableConfig.value.tableData = res.rows;
                reminderTableConfig.value.pageConfig.total = res.total;
            }
        });
    }

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        reminderTableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }
    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        reminderTableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    function reminder() {
        msgContent.value = '';
        taskIds.value = [];
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择要催办的办件人员'), offset: 65, appendTo: '.y9-table-div' });
        } else {
            for (let i = 0; i < multipleSelection.value.length; i++) {
                taskIds.value.push(multipleSelection.value[i].taskId);
            }
            Object.assign(dialogConfig.value, {
                show: true,
                width: '40%',
                title: computed(() => t('催办信息')),
                type: 'remindMsg',
                showFooter: false,
            });
        }
    }

    function sendReminder() {
        if (msgContent.value == '') {
            ElMessage({ type: 'error', message: t('内容不能为空'), offset: 65, appendTo: '.y9-table-div' });
            return;
        }
        saveReminder(props.processInstanceId, taskIds.value.toString(), msgContent.value).then((res) => {
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
                reloadTable();
                dialogConfig.value.show = false;
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
            }
        });
    }

    function myReminder() {
        type.value = 'my';
        processInstanceId.value = props.processInstanceId;

        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: computed(() => t('我的催办')),
            type: 'reminder',
            showFooter: false,
        });
    }

    function allReminder() {
        type.value = 'all';
        processInstanceId.value = props.processInstanceId;

        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: computed(() => t('所有催办')),
            type: 'reminder',
            showFooter: false,
        });
    }
</script>

<style>
    .el-table .cell.el-tooltip {
        white-space: nowrap;
        min-width: 50px;
        padding-left: 16px;
    }
</style>

<style scoped>
 /*message */
 :global(.el-message .el-message__content) {
    font-size: v-bind('fontSizeObj.baseFontSize');
}
</style>
