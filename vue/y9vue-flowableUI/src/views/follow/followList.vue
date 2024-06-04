<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:59:13
 * @LastEditors: mengjuhua
 * @Description:  我的关注
-->
<template>
    <y9Table
        ref="filterRef"
        :filterConfig="filterConfig"
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
        @select-all="handleSelectionChange"
        @select="handleSelectionChange"
    >
        <template #buttonslot>
            <el-button
                class="global-btn-main"
                @click="delFollow"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            >
                <i class="ri-star-line"></i>
                <span>{{ $t('取消关注') }}</span>
            </el-button>
            <el-button
                class="global-btn-third"
                @click="refreshTable"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            >
                <i class="ri-refresh-line"></i>
                <span>{{ $t('刷新') }}</span>
            </el-button>
        </template>
        <template #documentTitle="{ row, column, index }">
            <el-link
                :style="{ color: 'blue', fontSize: fontSizeObj.baseFontSize }"
                :underline="false"
                @click="openDoc(row)"
                >{{ row.documentTitle }}</el-link
            >
        </template>
        <template #itemBox="{ row, column, index }">
            <font v-if="row.itembox == 'done'" style="color: #d81e06">{{ $t('办结') }}</font>
            <font v-else-if="row.itembox == 'doing'">{{ $t('在办') }}</font>
            <font v-else-if="row.itembox == 'todo'" style="color: #228b22">{{ $t('待办') }}</font>
            <font v-else></font>
        </template>
        <template #opt="{ row, column, index }">
            <el-button
                class="global-btn-third"
                @click="openHistoryList(row)"
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                ><i class="ri-sound-module-fill"></i>{{ $t('历程') }}</el-button
            >
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <HistoryList ref="historyListRef" :processInstanceId="processInstanceId" />
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject, computed } from 'vue';
    import type { FormInstance, ElMessageBox, ElMessage, ElLoading } from 'element-plus';
    import HistoryList from '@/views/process/historyList.vue';
    import { delOfficeFollow, followList } from '@/api/flowableUI/follow';
    import { useRoute, useRouter } from 'vue-router';
    import settings from '@/settings';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const settingStore = useSettingStore();
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    const emits = defineEmits(['refreshCount']);
    //const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        processInstanceId: '',
        multipleSelection: [],
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                { title: '', type: 'selection', width: '50', align: 'center' },
                { title: computed(() => t('序号')), type: 'index', width: '55' },
                { title: computed(() => t('类别')), key: 'fileType', width: '90' },
                { title: computed(() => t('文件编号')), key: 'numbers', width: '190' },
                { title: computed(() => t('标题')), key: 'documentTitle', slot: 'documentTitle', width: 'auto', align: 'left' },
                { title: computed(() => t('发起人')), key: 'userName', width: '180' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '140' },
                { title: computed(() => t('状态')), key: 'itembox', width: '60', slot: 'itemBox' },
                { title: computed(() => t('文件去向')), key: 'taskAssignee', width: '180' },
                { title: computed(() => t('操作')), key: 'opt', width: '100', slot: 'opt' }
            ],
            tableData: [],
            //height: tableHeight,
            openAutoComputedHeight: true,
            pageConfig: {
                currentPage: 1,
                pageSize: 20,
                total: 0,
                pageSizeOpts: [10, 20, 30, 50, 100]
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
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'search',
                    key: 'name',
                    props: {
                        placeholder: computed(() => t('请输入标题或者文号搜索'))
                    },
                    span: settingStore.device === 'mobile' ? 12 : 6
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 2,
                    slotName: 'buttonslot'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        }
    });

    let { filterRef, currFilters, filterConfig, processInstanceId, tableConfig, dialogConfig, multipleSelection } =
        toRefs(data);

    onMounted(() => {
        reloadTable();
    });

    //监听过滤条件改变时，获取列表数据
    watch(
        () => currFilters.value,
        (newVal) => {
            if (newVal.name) {
                reloadTable();
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }
    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function refreshTable() {
        currFilters.value.name = '';
        filterRef.value.elTableFilterRef.onReset();
        tableConfig.value.pageConfig.currentPage = 1;
        tableConfig.value.pageConfig.pageSize = 20;
        setTimeout(() => {
            reloadTable();
        }, 500);
    }

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        tableConfig.value.loading = true;
        let res = await followList(currFilters.value.name, page, rows);
        tableConfig.value.loading = false;
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    function openDoc(row) {
        let query = {
            itemId: row.itemId,
            processSerialNumber: row.processSerialNumber,
            itembox: row.itembox,
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'follow'
        };
        let link = currentrRute.matched[0].path;
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/edit', query: query });
    }

    function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            title: t('历程')+'【' + row.documentTitle + '】',
            type: 'history',
            showFooter: false
        });
    }

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    async function delFollow() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择要取消关注的文件'), appendTo: '.y9-table-div' });
            return;
        }
        let ids = [];
        for (let obj of multipleSelection.value) {
            ids.push(obj.processInstanceId);
        }
        let res = await delOfficeFollow(ids.join(','));
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }
</script>

<style scoped>
    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
