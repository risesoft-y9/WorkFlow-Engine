<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:53:53
 * @LastEditors: mengjuhua
 * @Description:  草稿箱
-->
<template>
    <y9Table
        ref="filterRef"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #update>
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
        <template #optButton="{ row, column, index }">
            <el-button
                class="global-btn-third"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                @click="delDraft(row)"
            >
                <i class="ri-delete-bin-line"></i>{{ $t('删除') }}
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <div style="text-align: right">
            <span class="dialog-footer">
                <el-button
                    class="global-btn-third"
                    @click="delDraftInfo"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    >{{ $t('彻底删除') }}</el-button
                >
                <el-button
                    class="global-btn-third"
                    @click="removeDraftInfo"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    >{{ $t('放入回收站') }}</el-button
                >
                <el-button
                    class="global-btn-third"
                    @click="dialogConfig.show = false"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    >{{ $t('取消') }}</el-button
                >
            </span>
        </div>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject } from 'vue';
    import { getDraftList, draftViewConf, removeDraft, deleteDraft } from '@/api/flowableUI/draft';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const emits = defineEmits(['refreshCount']);
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    // const tableHeight = ref(useSettingStore().getWindowHeight - 280 - 20);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    // window.onresize = () => {
    //     return (() => {
    //         tableHeight.value = useSettingStore().getWindowHeight - 280 - 20;
    //     })();
    // };
    const data = reactive({
        filterRef: '',
        currFilters: {}, //当前选择的过滤数据
        draftId: '',
        viewConfig: [],
        itemId: flowableStore.getItemId,
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
                    span: settingStore.device === 'mobile' ? 12 : 9,
                    slotName: 'update'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        }
    });

    let { filterRef, currFilters, draftId, viewConfig, itemId, tableConfig, dialogConfig, filterConfig } = toRefs(data);

    onMounted(() => {
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
        let res = await draftViewConf(itemId.value);
        if (res.success) {
            viewConfig.value = res.data;
            viewConfig.value.forEach((element) => {
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
                        minWidth: 200,
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        className: 'y9draft_tablecell',
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
            });
            reloadTable();
        }
    }

    function refreshTable() {
        currFilters.value.name = '';
        filterRef.value.elTableFilterRef.onReset();
        reloadTable();
    }

    async function reloadTable() {
        tableConfig.value.loading = true;
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        let res = await getDraftList(itemId.value, currFilters.value.name, page, rows);
        if (res.success) {
            tableConfig.value.tableData = res.rows;
            tableConfig.value.pageConfig.total = res.total;
        }
        tableConfig.value.loading = false;
    }

    function openDoc(row) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: itemId.value,
            processSerialNumber: row.processSerialNumber,
            itembox: 'draft',
            listType: 'draft'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/edit', query: query });
    }

    function delDraft(row) {
        draftId.value = row.id;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '25%',
            title: computed(() => t('是否删除该草稿？')),
            showFooter: false
        });
    }

    async function delDraftInfo() {
        dialogConfig.value.show = false;
        const loading = ElLoading.service({ lock: true, text: t('正在处理中'), background: 'rgba(0, 0, 0, 0.3)' });
        let res = await deleteDraft(draftId.value);
        loading.close();
        ElMessage({
            type: res.success ? 'success' : 'error',
            message: res.msg,
            offset: 65,
            appendTo: '.el-table__body-wrapper'
        });
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }

    async function removeDraftInfo() {
        dialogConfig.value.show = false;
        const loading = ElLoading.service({ lock: true, text: t('正在处理中'), background: 'rgba(0, 0, 0, 0.3)' });
        let res = await removeDraft(draftId.value);
        ElMessage({
            type: res.success ? 'success' : 'error',
            message: res.msg,
            offset: 65,
            appendTo: '.el-table__body-wrapper'
        });
        loading.close();
        if (res.success) {
            emits('refreshCount');
            reloadTable();
        }
    }
</script>

<style lang="scss">
    .control .el-table__body .cell .el-button i,
    .control .el-table__body .cell .el-button span {
        color: white !important;
        cursor: pointer;
    }

    .y9draft_tablecell {
        .cell {
            display: flex;
        }
    }
</style>
