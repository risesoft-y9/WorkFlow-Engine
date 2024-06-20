<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-13 14:53:55
 * @LastEditors: zhangchongjie
 * @Description:  办结件
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
            >
                {{ row.title == '' ? $t('未定义标题') : row.title }}
            </el-link>
        </template>
        <template #follow="{ row, column, index }">
            <i
                v-if="row.follow"
                @click="delFollow(row)"
                :title="$t('点击取消关注')"
                class="ri-star-line"
                :style="{ fontSize: fontSizeObj.extrarLargeFont, color: '#ffb800' }"
            ></i>
            <i
                v-else
                class="ri-star-fill"
                @click="saveFollow(row)"
                :title="$t('点击关注')"
                :style="{ fontSize: fontSizeObj.largeFontSize }"
            ></i>
        </template>
        <template #optButton="{ row, column, index }">
            <el-button
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="openHistoryList(row)"
            >
                <i class="ri-sound-module-fill"></i>{{ $t('历程') }}
            </el-button>
            <el-button
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="openFlowChart(row)"
                ><i class="ri-flow-chart"></i>{{ $t('流程图') }}</el-button
            >
            <el-button v-if="settings.huifudaiban"
                size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                :class="{ 'margin-button': fontSizeObj.buttonSize == 'large' }"
                class="global-btn-third"
                @click="resumeTodo(row)"
            >
                <i class="ri-restart-line"></i>{{ $t('恢复待办') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <HistoryList v-if="dialogConfig.type == 'process'" ref="historyListRef" :processInstanceId="processInstanceId" />
        <flowChart v-if="dialogConfig.type == 'flowChart'" ref="flowchartRef" :processDefinitionId="processDefinitionId" :processInstanceId="processInstanceId"/>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject, computed } from 'vue';
    import HistoryList from '@/views/process/historyList.vue';
    import flowChart from "@/views/flowchart/index4List.vue";
    import { getDoneList, searchDoneList, doneViewConf } from '@/api/flowableUI/workList';
    import { saveOfficeFollow, delOfficeFollow } from '@/api/flowableUI/follow';
    import { buttonApi } from '@/api/flowableUI/buttonOpt';
    import { useRoute, useRouter } from 'vue-router';
    import settings from '@/settings';
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
    //const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);
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
        },
        tableName: '', //表名
        processDefinitionId:''
    });

    let {
        filterRef,
        currFilters,
        filterConfig,
        y9UserInfo,
        viewConfig,
        itemId,
        processInstanceId,
        processDefinitionId,
        tableConfig,
        dialogConfig,
        tableName
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
        getViewConfig();
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

    async function getViewConfig() {
        let res = await doneViewConf(itemId.value);
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
                        className: 'y9done_tablecell',
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
        tableConfig.value.loading = true;
        let searchMapStr = '';
        if (JSON.stringify(currFilters.value) != '{}') {
            searchMapStr = JSON.stringify(currFilters.value);
        }
        // let res = await searchDoneList(itemId.value,tableName.value, searchMapStr, page, rows);
        let res = await getDoneList(itemId.value, currFilters.value.name, page, rows);
        tableConfig.value.loading = false;
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
    }

    function openDoc(row) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: itemId.value,
            processSerialNumber: row.processSerialNumber,
            itembox: 'done',
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'done'
        };
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

    async function resumeTodo(row) {
        ElMessageBox.confirm(t('是否恢复待办？'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.el-table--fit'
        })
            .then(async () => {
                let result = await buttonApi.multipleResumeToDo(row.processInstanceId);
                ElMessage({
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    offset: 65,
                    appendTo: '.el-table--fit'
                });
                if (result.success) {
                    reloadTable();
                    emits('refreshCount');
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消操作'),
                    offset: 65,
                    appendTo: '.el-table--fit'
                });
            });
    }

    async function delFollow(row) {
        let res = await delOfficeFollow(row.processInstanceId);
        ElMessage({
            type: res.success ? 'success' : 'error',
            message: res.msg,
            offset: 65,
            appendTo: '.el-table--fit'
        });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }

    async function saveFollow(row) {
        let res = await saveOfficeFollow(row.processInstanceId);
        ElMessage({
            type: res.success ? 'success' : 'error',
            message: res.msg,
            offset: 65,
            appendTo: '.el-table--fit'
        });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }
</script>

<style lang="scss">
    .y9done_tablecell {
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
</style>
