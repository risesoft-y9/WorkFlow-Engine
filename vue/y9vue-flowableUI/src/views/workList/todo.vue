<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-13 14:46:44
 * @LastEditors: zhangchongjie
 * @Description:  待办件
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
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                @click="reloadTable"
            >
                <i class="ri-search-line"></i>
                <span>{{ $t('搜索') }}</span>
            </el-button>
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
        <template #title="{ row, column, index }">
            <img
                v-if="row.isNewTodo"
                :title="$t('新文件，未阅状态。')"
                src="@/assets/images/new.gif"
                style="width: 28px; margin: auto 0"
            />
            <i
                v-if="row.isForwarding"
                :title="$t('正在发送中，请稍后刷新列表...')"
                class="ri-loader-2-line"
                style="color: red"
            ></i>
            <font v-if="row.rollBack" :title="$t('退回件')" color="#FF4500">[{{ $t('退') }}]</font>
            <font v-if="row.isZhuBan == 'true'" color="#FF4500">[{{ $t('主') }}]</font>
            <font v-else-if="row.isZhuBan == 'false'" color="#A1402D">[{{ $t('协') }}]</font>
            <i
                v-if="row.speakInfoNum != 0"
                :title="$t('沟通交流消息提醒')"
                class="ri-notification-3-line"
                style="color: red"
            ></i>
            <el-link
                :style="{ color: 'blue', fontSize: fontSizeObj.baseFontSize }"
                :underline="false"
                @click="openDoc(row)"
            >
                {{ row.title == '' ? $t('未定义标题') : row.title }}
            </el-link>
        </template>
        <template #follow="{ row, column, index }">
            <i
                v-if="row.follow"
                :style="{ fontSize: fontSizeObj.extrarLargeFont, color: '#ffb800' }"
                :title="$t('点击取消关注')"
                class="ri-star-line"
                @click="delFollow(row)"
            ></i>
            <i
                v-else
                :style="{ fontSize: fontSizeObj.largeFontSize }"
                :title="$t('点击关注')"
                class="ri-star-fill"
                @click="saveFollow(row)"
            ></i>
        </template>
        <template #optButton="{ row, column, index }">
            <el-button
                v-if="row.isReminder"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                size="small"
                @click="openReminder(row)"
            >
                <i class="ri-timer-flash-line"></i>{{ $t('催办') }}
            </el-button>
            <el-button
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                size="small"
                @click="openHistoryList(row)"
            >
                <i class="ri-sound-module-fill"></i>{{ $t('历程') }}
            </el-button>
            <el-button
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                size="small"
                @click="openFlowChart(row)"
                ><i class="ri-flow-chart"></i>{{ $t('流程图') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <historyList
            v-if="dialogConfig.type == 'history'"
            ref="historyListRef"
            :processInstanceId="processInstanceId"
        />
        <remindMeList v-if="dialogConfig.type == 'reminder'" ref="remindMeListRef" :taskId="taskId" />
        <remindInstance
            v-if="dialogConfig.type == 'remindMsg'"
            ref="remindInstanceRef"
            :processInstanceId="processInstanceId"
            :reloadTable="reloadTable"
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
    import { computed, inject, onMounted, reactive, watch } from 'vue';
    import historyList from '@/views/process/historyList.vue';
    import remindMeList from '@/views/reminder/remindMeList.vue';
    import remindInstance from '@/views/reminder/remindInstance.vue';
    import flowChart from '@/views/flowchart/index4List.vue';
    import { getTodoList, todoViewConf } from '@/api/flowableUI/workList';
    import { delOfficeFollow, saveOfficeFollow } from '@/api/flowableUI/follow';
    import { useRoute, useRouter } from 'vue-router';
    import y9_storage from '@/utils/storage';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { getOptionValueList } from '@/api/flowableUI/form';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    const emits = defineEmits(['refreshCount']);
    // 调整表格高度适应屏幕
    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        viewConfig: [],
        itemId: '',
        processInstanceId: '',
        taskId: '',
        y9UserInfo: {},
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [],
            tableData: [],
            // height: tableHeight,
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
        },
        tableName: '', //表名
        processDefinitionId: ''
    });

    let {
        filterRef,
        currFilters,
        y9UserInfo,
        viewConfig,
        itemId,
        processInstanceId,
        processDefinitionId,
        taskId,
        tableConfig,
        dialogConfig,
        filterConfig,
        tableName
    } = toRefs(data);

    onMounted(() => {
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
        itemId.value = flowableStore.getItemId;
        if (currentrRute.path == '/workIndex/todo') {
            if (
                (currentrRute.query.itemId == undefined || currentrRute.query.itemId == '') &&
                flowableStore.itemId == ''
            ) {
                //默认打开事项列表第一个事项待办
                flowableStore.$patch({
                    itemId: flowableStore.getItemList[0] == undefined ? '' : flowableStore.getItemList[0].url
                });
            }
            if (currentrRute.query.itemId != undefined && currentrRute.query.itemId != flowableStore.itemId) {
                //事项切换，使用最新itemId
                flowableStore.$patch({
                    itemId: currentrRute.query?.itemId ? currentrRute.query?.itemId : ''
                });
            }
        }
        if (flowableStore.currentPage.indexOf('_back') > -1) {
            //返回列表获取当前页
            tableConfig.value.pageConfig.currentPage = flowableStore.currentPage.split('_')[0];
        }
        flowableStore.$patch({
            //重新设置
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        if (currentrRute.query.refreshCount) {
            //判断是否需要刷新左侧菜单数字
            emits('refreshCount');
        }
        getViewConfig();
    });

    watch(
        () => currentrRute.query.itemId,
        (newVal, oldVal) => {
            if (currentrRute.path == '/workIndex/todo' && newVal != oldVal && currentrRute.query.itemId != undefined) {
                //监听事项切换，更改事项id
                flowableStore.$patch({
                    itemId: currentrRute.query.itemId
                });
                getViewConfig();
            }
        }
    );

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

    async function getViewConfig() {
        let res = await todoViewConf(flowableStore.getItemId);
        if (res.success) {
            viewConfig.value = res.data;
            let searchArr = [];
            for (let element of viewConfig.value) {
                if (element.columnName == 'opt') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        slot: 'optButton'
                    });
                } else if (element.columnName == 'title') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        minWidth: 200,
                        align: element.disPlayAlign,
                        className: 'y9todo_tablecell',
                        slot: 'title'
                    });
                } else if (element.columnName == 'follow') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        slot: 'follow'
                    });
                } else {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign
                    });
                }
                if (element.openSearch == 1) {
                    //搜索条件
                    tableName.value = element.tableName;
                    let searchItem = {
                        type: element.inputBoxType,
                        key: element.columnName,
                        span: Number(element.spanWidth),
                        label: element.labelName == '' ? element.disPlayName : element.labelName,
                        props: {}
                    };
                    searchItem.props.placeholder =
                        t('请输入') + (element.labelName == '' ? element.disPlayName : element.labelName);
                    if (element.inputBoxType == 'select') {
                        let optionClass = element.optionClass;
                        searchItem.props.placeholder = t('请选择');
                        searchItem.props.options = [];
                        let option = { label: t('全部'), value: '' };
                        searchItem.props.options.push(option);
                        searchItem.value = '';
                        let res = await getOptionValueList(optionClass.split('(')[0]);
                        if (res.success) {
                            let data = res.data;
                            for (let obj of data) {
                                let optionObj = {};
                                optionObj.value = obj.code;
                                optionObj.label = obj.name;
                                searchItem.props.options.push(optionObj);
                            }
                        }
                    } else if (element.inputBoxType == 'date') {
                        searchItem.props.dateType = element.inputBoxType;
                        searchItem.props.format = 'YYYY-MM-DD';
                        searchItem.props.formatValueType = false;
                        searchItem.props.valueFormat = 'YYYY-MM-DD';
                    }
                    searchArr.push(searchItem);
                }
            }
            if (searchArr.length > 0) {
                //搜索条件
                searchArr = searchArr.reverse();
                for (let obj of searchArr) {
                    // filterConfig.value.itemList.unshift(obj);
                }
            }
            reloadTable();
        }
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
        if (flowableStore.getItemId != '') {
            tableConfig.value.loading = true;
            let searchMapStr = '';
            if (JSON.stringify(currFilters.value) != '{}') {
                searchMapStr = JSON.stringify(currFilters.value);
            }
            // let res = await searchTodoList(flowableStore.getItemId,tableName.value, searchMapStr, page, rows);
            let res = await getTodoList(flowableStore.getItemId, currFilters.value.name, page, rows);
            tableConfig.value.loading = false;
            if (res.success) {
                tableConfig.value.tableData = res.rows;
                tableConfig.value.pageConfig.total = res.total;
            }
        }
    }

    function openDoc(row) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: itemId.value,
            processSerialNumber: row.processSerialNumber,
            itembox: 'todo',
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'todo'
        };
        if (row.isForwarding) {
            ElMessage({ type: 'error', message: t('该件正在发送中，请稍后刷新列表...'), appendTo: '.y9-table-div' });
            return;
        }
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/edit', query: query });
    }

    function openReminder(row) {
        taskId.value = row.taskId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: t('催办') + '【' + row.title + '】',
            type: 'reminder',
            showFooter: false
        });
    }

    function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '72%',
            title: t('历程') + '【' + row.title + '】',
            type: 'history',
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
            title: t('流程图') + '【' + row.title + '】',
            showFooter: false
        });
    }

    async function delFollow(row) {
        let res = await delOfficeFollow(row.processInstanceId);
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }

    async function saveFollow(row) {
        let res = await saveOfficeFollow(row.processInstanceId);
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }
</script>

<style lang="scss">
    .y9todo_tablecell {
        .cell {
            display: flex;
        }
    }
</style>

<style scoped>
    .margin-button {
        margin-left: 3px;
    }

    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
