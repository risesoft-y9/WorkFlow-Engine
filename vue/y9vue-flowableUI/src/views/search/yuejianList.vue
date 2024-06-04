<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-13 14:59:28
 * @LastEditors: zhangchongjie
 * @Description:  阅件
-->
<template>
    <y9Table
        ref="filterRef"
        :filterConfig="filterConfig"
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #buttonslot>
            <el-button
                class="global-btn-main"
                @click="reloadTable"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            >
                <i class="ri-search-line"></i>
                <span>{{ $t('搜索') }}</span>
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
        <!-- <template slot-scope="follow_cell">
        <i v-if="follow_cell.row.follow" @click="delFollow(follow_cell.row)" title="点击取消关注"  class="el-icon-star-on" style="font-size: 22px;color: #ffb800;"></i>
        <i v-else class="el-icon-star-off" @click="saveFollow(follow_cell.row)" title="点击关注" style="font-size: 18px;"></i>
      </template> -->
        <template #optButton="{ row, column, index }">
            <el-button
                class="global-btn-third"
                @click="openHistoryList(row)"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
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
        <HistoryList  v-if="dialogConfig.type == 'process'" ref="historyListRef" :processInstanceId="processInstanceId" />
        <flowChart v-if="dialogConfig.type == 'flowChart'" ref="flowchartRef" :processDefinitionId="processDefinitionId" :processInstanceId="processInstanceId"/>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import HistoryList from '@/views/process/historyList.vue';
    import flowChart from "@/views/flowchart/index4List.vue";
    import { ref, defineProps, onMounted, watch, reactive, inject, computed } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { getYuejianList, getMyItemList } from '@/api/flowableUI/search';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const flowableStore = useFlowableStore();
    const router = useRouter();
    const currentrRute = useRoute();
    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        itemList: [],
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '55' },
                { title: computed(() => t('类别')), key: 'itemName', width: '90' },
                { title: computed(() => t('文件编号')), key: 'number', width: '190' },
                { title: computed(() => t('标题')), key: 'title', width: 'auto', slot: 'title', align: 'left', minWidth: '200' },
                { title: computed(() => t('接收时间')), key: 'createTime', width: '140' },
                { title: computed(() => t('阅读时间')), key: 'readTime', width: '140' },
                { title: computed(() => t('发送人')), key: 'senderName', width: '200' },
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
                    span: settingStore.device === 'mobile' ? 6 : 5
                },
                {
                    type: 'input',
                    key: 'userName',
                    label: computed(() => t('发起人')),
                    props: {
                        placeholder: computed(() => t('请输入发起人'))
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'select',
                    key: 'itemId',
                    label: computed(() => t('类别')),
                    props: {
                        placeholder: computed(() => t('请选择类别')),
                        options: [],
                        events: {
                            change: selectchange
                        }
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'select',
                    key: 'state',
                    label: computed(() => t('状态')),
                    value: '',
                    props: {
                        placeholder: computed(() => t('请选择状态')),
                        options: [
                            { label: computed(() => t('全部')), value: '' },
                            { label: computed(() => t('未阅件')), value: '2' },
                            { label: computed(() => t('已阅件')), value: '1' }
                        ],
                        events: {
                            change: selectchange
                        }
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'date',
                    key: 'year',
                    label: computed(() => t('年度')),
                    props: {
                        placeholder: computed(() => t('选择年')),
                        clearable: true,
                        dateType: 'year',
                        format: 'YYYY',
                        formatValueType: false,
                        valueFormat: 'YYYY',
                        teleported: false,
                        events: {
                            change: selectchange
                        }
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 1,
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
        itemList,
        dialogConfig,
        historyListRef,
        processInstanceId,
        processDefinitionId
    } = toRefs(data);

    onMounted(() => {
        if (flowableStore.currentPage.indexOf('_back') > -1) {
            //返回列表获取当前页
            tableConfig.value.pageConfig.currentPage = flowableStore.currentPage.split('_')[0];
        }
        flowableStore.$patch({
            //重新设置
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        reloadTable();
        itemList.value = flowableStore.itemList;

        filterConfig.value.itemList.forEach((items) => {
            if (items.type == 'select' && items.key == 'itemId') {
                items.props.options.push({ label: computed(() => t('全部')), value: '' });
                itemList.value.forEach((element) => {
                    items.props.options.push({ label: element.name, value: element.url });
                });
                items.value = items.props.options[0].value; //默认选择第一项
                return items.props.options;
            }
        });
    });

    //监听过滤条件改变时，获取列表数据
    watch(
        () => currFilters.value,
        (newVal) => {
            // if (newVal.state || newVal.itemId || newVal.year) {
            //     reloadTable();
            // }
        },
        {
            deep: true,
            immediate: true
        }
    );

    function selectchange(val) {
        reloadTable();
    }

    function refreshTable() {
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
        let res = await getYuejianList(
            currFilters.value.name,
            currFilters.value.itemId,
            currFilters.value.userName,
            currFilters.value.state,
            currFilters.value.year,
            page,
            rows
        );
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.currentPage = res.currPage;
            tableConfig.value.pageConfig.total = res.total;
        }
        tableConfig.value.loading = false;
    }

    async function openDoc(row) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: row.itemId,
            processInstanceId: row.processInstanceId,
            status: row.status,
            id: row.id,
            listType: 'yuejianList'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/csEdit', query: query });
    }

    async function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            type:'process',
            title: t('历程') +'【'+ row.documentTitle + '】',
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

<style scoped>
    :global(.el-date-picker .el-date-picker__header .el-date-picker__header-label) {
        font-size: v-bind('fontSizeObj.mediumFontSize');
    }

    :global(.el-date-picker .el-date-picker__header .el-date-picker__prev-btn),
    :global(.el-date-picker .el-date-picker__header .el-date-picker__next-btn),
    :global(.el-date-picker .el-year-table) {
        font-size: v-bind('fontSizeObj.smallFontSize');
    }
</style>
