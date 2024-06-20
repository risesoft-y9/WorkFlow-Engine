<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-13 14:44:59
 * @LastEditors: zhangchongjie
 * @Description:  未阅件
-->
<template>
    <y9Table
        ref="filterRef"
        :filterConfig="filterConfig"
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
        @select="handleSelectionChange"
        @select-all="handleSelectionChange"
    >
        <template #buttonslot>
            <el-button
                class="global-btn-main"
                @click="read"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            >
                <i class="ri-book-read-line"></i>
                <span>{{ $t('批量阅件') }}</span>
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
        <template #title="{ row, column, index }">
            <el-link
                :style="{ color: 'blue', fontSize: fontSizeObj.baseFontSize }"
                :underline="false"
                @click="openDoc(row)"
                >{{ row.title }}</el-link
            >
        </template>
        <template #itembox="{ row, column, index }">
            <font v-if="row.banjie" style="color: #d81e06">{{ $t('办结') }}</font>
            <font v-else>{{ $t('在办') }}</font>
        </template>
        <template #optButton="{ row, column, index }">
            <el-button
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="openHistoryList(row)"
                ><i class="ri-sound-module-fill"></i>{{ $t('历程') }}</el-button
            >
            <el-button
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="openFlowChart(row)"
                ><i class="ri-flow-chart"></i>{{ $t('流程图') }}</el-button
            >
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <HistoryList v-if="dialogConfig.type == 'process'" ref="historyListRef" :processInstanceId="processInstanceId" />
        <flowChart v-if="dialogConfig.type == 'flowChart'" ref="flowchartRef" :processDefinitionId="processDefinitionId" :processInstanceId="processInstanceId"/>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import type { FormInstance, ElMessageBox, ElMessage, ElLoading } from 'element-plus';
    import { search, changeStatus } from '@/api/flowableUI/chaoSong';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import HistoryList from '@/views/process/historyList.vue';
    import flowChart from "@/views/flowchart/index4List.vue";
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import { computed } from 'vue';
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const flowableStore = useFlowableStore();
    const currentrRute = useRoute();
    const router = useRouter();
    const emits = defineEmits(['refreshCount']);
    
    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        year: '',
        multipleSelection: [],
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                { title: '', type: 'selection', width: 50 },
                { title: computed(() => t('序号')), type: 'index', width: '55' },
                { title: computed(() => t('类别')), key: 'itemName', width: '90' },
                { title: computed(() => t('文件编号')), key: 'number', width: '190' },
                { title: computed(() => t('标题')), key: 'title', width: 'auto', slot: 'title', align: 'left', minWidth: '200' },
                { title: computed(() => t('接收时间')), key: 'createTime', width: '140' },
                { title: computed(() => t('发送人')), key: 'senderName', width: '190' },
                { title: computed(() => t('办理情况')), key: 'banjie', width: '85', slot: 'itembox' },
                { title: computed(() => t('操作')), width: '180', slot: 'optButton' }
            ],
            tableData: [],
            //height: tableHeight,
            openAutoComputedHeight: true,
            highlightCurrentRow: false,
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
                    span: settingStore.device === 'mobile' ? 12 : 5,
                    slotName: 'buttonslot'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        },
        historyListRef: '',
        processInstanceId: '',
        processDefinitionId:''
    });

    let {
        filterRef,
        currFilters,
        filterConfig,
        tableConfig,
        multipleSelection,
        year,
        dialogConfig,
        historyListRef,
        processInstanceId,
        processDefinitionId
    } = toRefs(data);

    onMounted(() => {
        if (flowableStore.currentPage.indexOf('_back') > -1) {
            flowableStore.$patch({
                itemName: '阅件'
            });
            //返回列表获取当前页
            tableConfig.value.pageConfig.currentPage = flowableStore.currentPage.split('_')[0];
        }
        flowableStore.$patch({
            //重新设置
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
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
        tableConfig.value.loading = true;
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        let res = await search(currFilters.value.name, year.value, 0, page, rows);
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.currentPage = res.currPage;
            tableConfig.value.pageConfig.total = res.total;
        }
        tableConfig.value.loading = false;
    }

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    async function read() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择要阅读的文件'), appendTo: '.y9-table-div' });
            return;
        }
        let ids = [];
        for (let obj of multipleSelection.value) {
            ids.push(obj.id);
        }
        let res = await changeStatus(ids.toString());
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }

    function openDoc(row) {
        let query = {
            itemId: row.itemId,
            processInstanceId: row.processInstanceId,
            status: 0,
            id: row.id,
            listType: 'csTodo'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        let link = currentrRute.matched[0].path;
        router.push({ path: link + '/csEdit', query: query });
    }

    async function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            type:'process',
            title: t('历程')+'【' + row.title + '】',
            showFooter: false
        });
    }

    async function openFlowChart(row) {
        processInstanceId.value = row.processInstanceId;
        processDefinitionId.value = row.processDefinitionId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            type:'flowChart',
            title: t('流程图')+'【' + row.title + '】',
            showFooter: false
        });
    }

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
</script>

<style>
    .el-table__header-wrapper .el-table-column--selection > .cell,
    .el-table__body-wrapper .el-table-column--selection > .cell {
        display: inline;
    }
</style>

<style scoped>
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
