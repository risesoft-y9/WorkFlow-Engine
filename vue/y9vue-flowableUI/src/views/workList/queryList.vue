<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:53:18
 * @LastEditors: mengjuhua
 * @Description:  监控在办
-->
<template>
    <y9Table
        ref="filterRef"
        :filterConfig="filterConfig"
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #button>
            <el-button
                class="global-btn-main"
                @click="showQuery"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            >
                <i class="ri-search-eye-line"></i>
                <span>{{ $t('高级搜索') }}</span>
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
                >{{ row.title == '' ? $t('未定义标题') : row.title }}</el-link
            >
        </template>
        <template #itembox="{ row, column, index }">
            <font v-if="row.itembox == 'done'" :style="{ color: '#d81e06', fontSize: fontSizeObj.baseFontSize }"
                >{{ $t('办结') }}</font
            >
            <font v-else-if="row.itembox == 'doing'" :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('在办') }}</font>
            <font v-else-if="row.itembox == 'todo'" :style="{ color: '#228b22', fontSize: fontSizeObj.baseFontSize }"
                >{{ $t('待办') }}</font
            >
            <font v-else></font>
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
        </template>
    </y9Table>
    <el-drawer
        v-model="drawer"
        :title="$t('高级搜索')"
        :direction="direction"
        :append-to-body="true"
        custom-class="querydrawer"
        :size="450"
    >
        <el-form :model="form" label-width="100px">
            <el-form-item :label="$t('状态')">
                <el-select v-model="form.itembox" :placeholder="$t('办理状态')" clearable>
                    <el-option :label="$t('全部')" value="" />
                    <el-option :label="$t('待办')" value="todo" />
                    <el-option :label="$t('在办')" value="doing" />
                    <el-option :label="$t('办结')" value="done" />
                </el-select>
            </el-form-item>
            <el-form-item :label="$t('开始时间')">
                <el-date-picker
                    v-model="form.createDate"
                    type="daterange"
                    unlink-panels
                    :range-separator="$t('至')"
                    :shortcuts="shortcuts"
                    value-format="YYYY-MM-DD"
                    clearable
                />
            </el-form-item>
            <template v-for="item in formField">
                <el-form-item :label="item.fieldCnName">
                    <template v-if="item.queryType == 'input' || item.queryType == 'textarea'">
                        <el-input v-model="item.value" clearable />
                    </template>
                    <template v-if="item.queryType == 'date'">
                        <el-date-picker
                            v-model="item.value"
                            type="daterange"
                            unlink-panels
                            :range-separator="$t('至')"
                            :shortcuts="shortcuts"
                            value-format="YYYY-MM-DD"
                            clearable
                        />
                    </template>
                    <template v-if="item.queryType == 'select'">
                        <el-select v-model="item.value" :placeholder="item.fieldCnName" clearable>
                            <el-option
                                v-for="option in item.optionValue"
                                :label="option.label == undefined ? option.value : option.label"
                                :value="option.value"
                            />
                        </el-select>
                    </template>
                    <template v-if="item.queryType == 'radio'">
                        <el-radio-group v-model="item.value" :placeholder="item.fieldCnName" clearable>
                            <el-radio
                                v-for="option in item.optionValue"
                                :label="option.value"
                                :value="option.value"
                            ></el-radio>
                        </el-radio-group>
                    </template>
                    <template v-if="item.queryType == 'checkbox'">
                        <el-checkbox-group v-model="item.value" :placeholder="item.fieldCnName" clearable>
                            <el-checkbox
                                v-for="option in item.optionValue"
                                :label="option.label == undefined ? option.value : option.label"
                                :value="option.value"
                            />
                        </el-checkbox-group>
                    </template>
                </el-form-item>
            </template>
        </el-form>
        <template #footer>
            <div style="flex: auto">
                <el-button
                    class="global-btn-main"
                    @click="queryList"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                >
                    <i class="ri-search-line"></i>
                    <span>{{ $t('查询') }}</span>
                </el-button>
                <el-button
                    class="global-btn-third"
                    @click="clearQuery"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                >
                    <i class="ri-repeat-line"></i>
                    <span>{{ $t('清空') }}</span>
                </el-button>
                <el-button
                    class="global-btn-third"
                    @click="drawer = false"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                >
                    <i class="ri-close-line"></i>
                    <span>{{ $t('取消') }}</span>
                </el-button>
            </div>
        </template>
    </el-drawer>
    <y9Dialog v-model:config="dialogConfig">
        <historyList ref="historyListRef" :processInstanceId="processInstanceId" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject, computed } from 'vue';
    import historyList from '@/views/process/historyList.vue';
    import { getFormField, getOptionValueList } from '@/api/flowableUI/form';
    import { getQueryList, viewConf } from '@/api/flowableUI/workList';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const router = useRouter();
    const flowableStore = useFlowableStore();
    const emits = defineEmits(['refreshCount']);

    //const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);

    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        itemId: '',
        processInstanceId: '',
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
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 24,
                    slotName: 'button'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        },
        drawer: false,
        direction: 'rtl',
        formField: [],
        form: {},
        tableName: '',
        shortcuts: [
            {
                text: t('最近一周'),
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                    return [start, end];
                }
            },
            {
                text: t('最近一个月'),
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                    return [start, end];
                }
            },
            {
                text: t('最近三个月'),
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                    return [start, end];
                }
            }
        ],
        viewConfig: [],
        queryInit: false
    });

    let {
        filterRef,
        currFilters,
        filterConfig,
        itemId,
        processInstanceId,
        tableConfig,
        dialogConfig,
        drawer,
        direction,
        formField,
        form,
        tableName,
        shortcuts,
        viewConfig,
        queryInit
    } = toRefs(data);

    onMounted(() => {
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

    async function getViewConfig() {
        let res = await viewConf(flowableStore.getItemId, 'queryList');
        if (res.success) {
            viewConfig.value = res.data;
            let searchArr = [];
            tableConfig.value.columns = [];
            for (let element of viewConfig.value) {
                if (element.columnName == 'opt') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        slot: 'optButton'
                    });
                } else if (element.columnName == 'itembox') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        slot: 'itembox'
                    });
                } else if (element.columnName == 'title') {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        minWidth: 300,
                        align: element.disPlayAlign,
                        className: 'y9todo_tablecell',
                        slot: 'title'
                    });
                } else {
                    tableConfig.value.columns.push({
                        title: computed(() => t(element.disPlayName)),
                        key: element.columnName,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign
                    });
                }
            }
            reloadTable();
        }
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

    async function showQuery() {
        drawer.value = true;
        if (queryInit.value) {
            formField.value.forEach((element) => {
                if (element.queryType == 'checkbox') {
                    //处理checkbox值
                    if (element.value != null && element.value != undefined && element.value != '') {
                        let arr = element.value.split(',');
                        element.value = arr;
                    } else {
                        element.value = [];
                    }
                } else if (element.queryType == 'date') {
                    //处理date值
                    if (element.value != null && element.value != undefined && element.value != '') {
                        let arr = element.value.split(',');
                        element.value = arr;
                    } else {
                        element.value = '';
                    }
                }
            });
            return;
        }
        let res = await getFormField(itemId.value);
        if (res.success) {
            queryInit.value = true;
            formField.value = [];
            res.data.forEach((element) => {
                tableName.value = element.tableName;
                if (element.queryType == 'radio' || element.queryType == 'checkbox' || element.queryType == 'select') {
                    if (element.optionValue.indexOf('[') > -1) {
                        //静态数据
                        element.optionValue = JSON.parse(element.optionValue);
                    } else if (element.optionValue.indexOf('(') > -1) {
                        //动态数据
                        let str = element.optionValue.split('(')[1];
                        let type = str.slice(0, str.length - 1); //数据字典类型标识
                        getOptionValueList(type).then((res) => {
                            if (res.success) {
                                let data = res.data;
                                let option = []; //选项
                                for (let obj of data) {
                                    let optionObj = {};
                                    optionObj.value = obj.code;
                                    optionObj.label = obj.name;
                                    option.push(optionObj);
                                }
                                element.optionValue = option;
                            }
                        });
                    }
                }
                formField.value.push(element);
            });
        }
    }

    async function queryList() {
        await getViewConfig();
        drawer.value = false;
    }

    function clearQuery() {
        form.value.itembox = '';
        form.value.createDate = '';
        formField.value.forEach((element) => {
            element.value = '';
        });
    }

    async function refreshTable() {
        await clearQuery();
        reloadTable();
    }

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        tableConfig.value.loading = true;
        let searchMapStr = '';
        let searchArr = [];
        formField.value.forEach((element) => {
            if (element.queryType == 'checkbox') {
                //处理checkbox值
                if (element.value != undefined && element.value.length > 0) {
                    let str = element.value.join(',');
                    element.value = str;
                }
            }
            if (element.queryType == 'date') {
                //处理date值
                if (element.value != null && element.value != undefined && element.value != '') {
                    element.value = element.value.join(',');
                }
            }
            let obj = {};
            obj.columnName = element.fieldName;
            obj.value = element.value;
            obj.queryType = element.queryType;
            searchArr.push(obj);
            if (element.value != null && element.value != undefined && element.value != '') {
                let obj = {
                    title: element.fieldCnName,
                    key: element.fieldName,
                    width: 150,
                    align: 'center'
                };
                let add = true;
                tableConfig.value.columns.forEach((item) => {
                    if (item.key == element.fieldName) {
                        add = false;
                    }
                });
                if (add) {
                    tableConfig.value.columns.splice(tableConfig.value.columns.length - 1, 0, obj);
                }
            }
        });
        searchMapStr = JSON.stringify(searchArr);
        let createDate =
            form.value.createDate != undefined && form.value.createDate != null ? form.value.createDate.toString() : '';
        let itembox = form.value.itembox != undefined && form.value.itembox != null ? form.value.itembox : '';
        let res = await getQueryList(itemId.value, itembox, createDate, tableName.value, searchMapStr, page, rows);
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
            itembox: row.itembox,
            taskId: row.taskId,
            processInstanceId: row.processInstanceId,
            listType: 'queryList'
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
            title: t('历程')+'【' + row.title + '】',
            type: 'history',
            showFooter: false
        });
    }
</script>
<style lang="scss">
    .querydrawer .el-drawer__header {
        padding-bottom: 22px !important;
        border-bottom: 1px solid #f4f4f4 !important;
    }
    .querydrawer .el-drawer__body {
        padding-top: 0 !important;
    }
    .querydrawer .el-select {
        width: 100%;
    }
</style>

<style scoped>
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
