<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-13 14:59:12
 * @LastEditors: zhangchongjie
 * @Description:  个人所有件
-->
<template>
    <y9Table
        ref="filterRef"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #buttonslot>
            <el-button
                :size="sizeObjInfo.buttonSize"
                :style="{ fontSize: sizeObjInfo.baseFontSize }"
                class="global-btn-main"
                @click="reloadTable"
            >
                <i class="ri-search-line"></i>
                <span>{{ $t('搜索') }}</span>
            </el-button>
            <el-button
                :size="sizeObjInfo.buttonSize"
                :style="{ fontSize: sizeObjInfo.baseFontSize }"
                class="global-btn-third"
                @click="refreshTable"
            >
                <i class="ri-refresh-line"></i>
                <span>{{ $t('刷新') }}</span>
            </el-button>
        </template>
        <template #title="{ row, column, index }">
            <el-link
                :style="{ color: 'blue', fontSize: sizeObjInfo.baseFontSize }"
                :underline="false"
                @click="openDoc(row)"
                >{{ row.documentTitle == '' ? $t('未定义标题') : row.documentTitle }}
            </el-link>
        </template>
        <template #itembox="{ row, column, index }">
            <font v-if="row.itembox == 'done'" style="color: #d81e06">{{ $t('办结') }}</font>
            <font v-else-if="row.itembox == 'doing'">{{ $t('在办') }}</font>
            <font v-else-if="row.itembox == 'todo'" style="color: #228b22">{{ $t('待办') }}</font>
            <font v-else></font>
        </template>
        <template #optButton="{ row, column, index }">
            <el-button
                :style="{ fontSize: sizeObjInfo.smallFontSize }"
                class="global-btn-third"
                @click="openHistoryList(row)"
                ><i class="ri-sound-module-fill"></i>{{ $t('历程') }}
            </el-button>
            <el-button
                :style="{ fontSize: sizeObjInfo.smallFontSize }"
                class="global-btn-third"
                size="small"
                @click="openFlowChart(row)"
                ><i class="ri-flow-chart"></i>{{ $t('流程图') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <HistoryList
            v-if="dialogConfig.type == 'process'"
            ref="historyListRef"
            :processInstanceId="processInstanceId"
        />
        <flowChart
            v-if="dialogConfig.type == 'flowChart'"
            ref="flowchartRef"
            :processDefinitionId="processDefinitionId"
            :processInstanceId="processInstanceId"
        />
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, ref, watch } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { getSearchList } from '@/api/flowableUI/search';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import HistoryList from '@/views/process/historyList.vue';
    import flowChart from '@/views/flowchart/index4List.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const sizeObjInfo: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const flowableStore = useFlowableStore();
    const router = useRouter();
    const currentrRute = useRoute();
    const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

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
                {
                    title: computed(() => t('标题')),
                    key: 'documentTitle',
                    width: 'auto',
                    slot: 'title',
                    align: 'left',
                    minWidth: '200'
                },
                { title: computed(() => t('发起人')), key: 'creatUserName', width: '170' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '155' },
                { title: computed(() => t('结束时间')), key: 'endTime', width: '155' },
                { title: computed(() => t('状态')), key: 'itembox', width: '60', slot: 'itembox' },
                { title: computed(() => t('文件去向')), key: 'taskAssignee', width: '170' },
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
                            { label: computed(() => t('未办结')), value: 'todo' },
                            { label: computed(() => t('已办结')), value: 'done' }
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
        processDefinitionId: ''
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
            currentPage: tableConfig.value.pageConfig.currentPage.toString(),
            itemName: '综合搜索'
        });
        let type = currentrRute.query.type ? currentrRute.query.type : '';
        itemList.value = flowableStore.itemList;
        filterConfig.value.itemList.forEach((items) => {
            if (items.type == 'select' && items.key == 'itemId') {
                items.props.options.push({ label: computed(() => t('全部')), value: '' });
                itemList.value.forEach((element) => {
                    items.props.options.push({ label: element.name, value: element.url });
                });
                items.value = items.props.options[0].value; //默认选择第一项
                return items.props.options;
            } else if (items.key == 'state') {
                return items.props.options;
            }
        });
        if (type == 'doing') {
            filterConfig.value.itemList.forEach((items) => {
                if (items.key == 'state') {
                    items.value = 'todo';
                    currFilters.value.state = 'todo';
                    return items.props.options;
                }
            });
        } else if (type == 'done') {
            filterConfig.value.itemList.forEach((items) => {
                if (items.key == 'state') {
                    items.value = 'done';
                    currFilters.value.state = 'done';
                }
            });
        } else {
            reloadTable();
        }
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
        let res = await getSearchList(
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

    async function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            type: 'process',
            title: t('历程') + '【' + row.documentTitle + '】',
            showFooter: false
        });
    }

    async function openFlowChart(row) {
        processInstanceId.value = row.processInstanceId;
        processDefinitionId.value = row.processDefinitionId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            type: 'flowChart',
            title: t('流程图') + '【' + row.documentTitle + '】',
            showFooter: false
        });
    }

    async function openDoc(row) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: row.itemId,
            processSerialNumber: row.processSerialNumber,
            itembox: row.itembox,
            processInstanceId: row.processInstanceId,
            taskId: row.taskId,
            listType: 'searchList'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/edit', query: query });
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
        font-size: v-bind('sizeObjInfo.mediumFontSize');
    }

    :global(.el-date-picker .el-date-picker__header .el-date-picker__prev-btn),
    :global(.el-date-picker .el-date-picker__header .el-date-picker__next-btn),
    :global(.el-date-picker .el-year-table) {
        font-size: v-bind('sizeObjInfo.smallFontSize');
    }
</style>
