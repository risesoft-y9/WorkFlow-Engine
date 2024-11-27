<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:54:23
 * @LastEditors: mengjuhua
 * @Description:  部门所有件
-->
<template>
    <y9Table
        ref="filterRef"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #search>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="reloadTable"
            >
                <i class="ri-search-line"></i>
                <span>{{ $t('搜索') }}</span>
            </el-button>
        </template>
        <template #update>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                @click="refreshTable"
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
            >
                {{ row.documentTitle == '' ? $t('未定义标题') : row.documentTitle }}
            </el-link>
        </template>
        <template #itemBox="{ row, column, index }">
            <font v-if="row.itembox == 'done'" style="color: #d81e06">{{ $t('办结') }}</font>
            <font v-else-if="row.itembox == 'doing'">{{ $t('在办') }}</font>
            <font v-else></font>
        </template>
        <template #opt="{ row, column, index }">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                size="small"
                @click="openHistoryList(row)"
            >
                <i class="ri-sound-module-fill"></i>{{ $t('历程') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <historyList ref="historyListRef" :processInstanceId="processInstanceId" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, toRefs, watch } from 'vue';
    import historyList from '@/views/process/historyList.vue';
    import { deptList } from '@/api/flowableUI/monitor';
    import { useRoute, useRouter } from 'vue-router';
    import y9_storage from '@/utils/storage';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    const emits = defineEmits(['refreshCount']);
    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

    const data = reactive({
        filterRef: '',
        currFilters: {
            name: '',
            userName: '',
            state: 'all',
            year: ''
        }, //当前选择的过滤数据
        itemId: '',
        processInstanceId: '',
        taskId: '',
        y9UserInfo: {},
        tableConfig: {
            //表格配置
            border: false,
            loading: false,
            headerBackground: true,
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('文件编号')), key: 'number', width: '180' },
                { title: computed(() => t('标题')), key: 'documentTitle', slot: 'documentTitle', align: 'left' },
                { title: computed(() => t('发起人')), key: 'creatUserName', width: '150' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '160' },
                { title: computed(() => t('结束时间')), key: 'endTime', width: '160' },
                { title: computed(() => t('办件状态')), key: 'itembox', width: '90', slot: 'itemBox' },
                { title: computed(() => t('文件去向')), key: 'taskAssignee', width: '150' },
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
                    key: 'state',
                    label: computed(() => t('状态')),
                    props: {
                        placeholder: computed(() => t('请选择状态')),
                        options: [
                            { label: computed(() => t('全部')), value: 'all' },
                            { label: computed(() => t('未办结')), value: 'todo' },
                            { label: computed(() => t('已办结')), value: 'done' }
                        ]
                    },
                    span: settingStore.device === 'mobile' ? 6 : 3
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
                        formatValueType: 'year'
                    },
                    span: settingStore.device === 'mobile' ? 6 : 3
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 1,
                    slotName: 'search'
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 8,
                    slotName: 'update'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        }
    });

    let {
        filterRef,
        currFilters,
        filterConfig,
        y9UserInfo,
        itemId,
        processInstanceId,
        taskId,
        tableConfig,
        dialogConfig
    } = toRefs(data);

    onMounted(() => {
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
        itemId.value = flowableStore.getItemId;
        if (flowableStore.currentPage.indexOf('_back') > -1) {
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
            if (newVal.name || newVal.state || newVal.userName || newVal.year) {
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
        filterRef.value.elTableFilterRef.onReset();
        reloadTable();
    }

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        tableConfig.value.loading = true;
        let res = await deptList(
            itemId.value,
            currFilters.value.name,
            currFilters.value.userName,
            currFilters.value.state,
            currFilters.value.year,
            page,
            rows
        );
        tableConfig.value.loading = false;
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    function openDoc(row) {
        let query = {
            itemId: itemId.value,
            processSerialNumber: row.processSerialNumber,
            itembox: row.itembox == 'doing' ? 'monitorDoing' : 'monitorDone',
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'deptList'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: '/index/edit', query: query });
    }

    function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            title: t('历程') + '【' + row.documentTitle + '】',
            type: 'history',
            showFooter: false
        });
    }
</script>

<style>
    .control .el-table__body .cell .el-button i,
    .control .el-table__body .cell .el-button span {
        color: white !important;
        cursor: pointer;
    }

    .searchList .searchName {
        padding: 0 10px;
    }
</style>
