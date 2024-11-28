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
        <template #title="{ row, column, index }">
            <el-link v-if="!row.read" :underline="false" style="color: blue" @click="openEmail(row)">
                {{ row.subject }}
            </el-link>
            <el-link v-else :underline="false" @click="openEmail(row)">{{ row.subject }}</el-link>
        </template>
        <template #folder="{ row, column, index }">
            <font v-if="row.folder == -2">{{ $t('发件箱') }}</font>
            <font v-if="row.folder == -3" style="color: #228b22">{{ $t('收件箱') }}</font>
        </template>
    </y9Table>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, watch } from 'vue';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const settingStore = useSettingStore();
    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '55' },
                { title: computed(() => t('主题')), key: 'subject', width: 'auto', slot: 'title', align: 'left' },
                { title: computed(() => t('发件人')), key: 'fromPersonName', width: '150' },
                { title: computed(() => t('收件人')), key: 'toPersonNames', width: '350' },
                { title: computed(() => t('发送/接收时间')), key: 'createTime', width: '160' },
                { title: computed(() => t('所在文件夹')), key: 'folder', width: '140', slot: 'folder' }
            ],
            tableData: [],
            height: window.innerHeight - 280,
            highlightCurrentRow: false,
            pageConfig: {
                currentPage: 1,
                pageSize: 20,
                total: 0
            }
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'search',
                    key: 'title',
                    props: {
                        placeholder: computed(() => t('请输入主题搜索'))
                    },
                    span: settingStore.device === 'mobile' ? 6 : 5
                },
                {
                    type: 'input',
                    key: 'userName',
                    label: computed(() => t('发送/收件人')),
                    props: {
                        placeholder: computed(() => t('请输入发送/收件人'))
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'select',
                    key: 'fileType',
                    label: computed(() => t('文件夹')),
                    props: {
                        placeholder: computed(() => t('请选择文件夹')),
                        options: [
                            { label: computed(() => t('全部')), value: 'null' },
                            { label: computed(() => t('收件箱')), value: '-3' },
                            { label: computed(() => t('发件箱')), value: '-2' }
                        ]
                    },
                    span: settingStore.device === 'mobile' ? 6 : 3
                },
                {
                    type: 'date',
                    key: 'startDate',
                    label: computed(() => t('发送/接收日期')),
                    props: {
                        placeholder: computed(() => t('开始日期')),
                        clearable: true,
                        dateType: 'date',
                        format: 'YYYY-MM-DD',
                        formatValueType: 'timestampThirteen',
                        disabledDate: (time) => {
                            if (currFilters.value.endDate) {
                                return time.getTime() > currFilters.value.endDate;
                            }
                        }
                    },
                    span: settingStore.device === 'mobile' ? 6 : 4
                },
                {
                    type: 'date',
                    key: 'endDate',
                    label: computed(() => t('至')),
                    props: {
                        placeholder: computed(() => t('结束日期')),
                        clearable: true,
                        dateType: 'date',
                        format: 'YYYY-MM-DD',
                        formatValueType: 'timestampThirteen',
                        disabledDate: (time) => {
                            if (currFilters.value.startDate) {
                                return time.getTime() < currFilters.value.startDate;
                            }
                        }
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
                    span: settingStore.device === 'mobile' ? 12 : 4,
                    slotName: 'update'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        },
        y9UserInfo: {}
    });

    let { filterRef, currFilters, filterConfig, tableConfig, y9UserInfo } = toRefs(data);

    onMounted(() => {
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
        reloadTable();
    });

    //监听过滤条件改变时，获取列表数据
    watch(
        () => currFilters.value,
        (newVal) => {
            if (newVal.title || newVal.userName || newVal.fileType || newVal.startDate || newVal.endDate) {
                reloadTable();
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    function refreshTable() {
        filterRef.value.elTableFilterRef.onReset();
        reloadTable();
    }

    async function reloadTable() {
        let fileType1 = currFilters.value.fileType;
        if (fileType1 == 'null') {
            fileType1 = null;
        }
        tableConfig.value.loading = true;
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        // let res = await getEmailList(currFilters.value.title,currFilters.value.userName,fileType1,currFilters.value.startDate,currFilters.value.endDate,page,rows);
        // if(res.success){
        //   tableConfig.value.tableData = res.rows;
        //   tableConfig.value.currentPage = res.currPage;
        //   tableConfig.value.pageConfig.total = res.total;
        // }
        tableConfig.value.loading = false;
    }

    function openEmail(row) {
        let url = settings.emailURL + '/custom/email/' + row.emailId + '/' + y9UserInfo.value.personId;
        window.location.href = url;
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

<style scoped></style>
