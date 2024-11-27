<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-10-18 17:56:19
 * @LastEditors: mengjuhua
 * @Description:  回收站
-->
<template>
    <y9Table :config="tableConfig" @on-curr-page-change="onCurrPageChange" @on-page-size-change="onPageSizeChange">
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
                :style="{ fontSize: fontSizeObj.smallFontSize }"
                class="global-btn-third"
                @click="reductionDraft(row)"
            >
                <i class="ri-restart-line"></i>{{ $t('还原') }}
            </el-button>
            <el-button :style="{ fontSize: fontSizeObj.smallFontSize }" class="global-btn-third" @click="delDraft(row)">
                <i class="ri-delete-bin-line"></i>{{ $t('删除') }}
            </el-button>
        </template>
    </y9Table>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive } from 'vue';
    import { deleteDraft, draftViewConf, getDraftRecycleList, reduction } from '@/api/flowableUI/draft';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const emits = defineEmits(['refreshCount']);
    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    //const tableHeight = ref(useSettingStore().getWindowHeight - 255);
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};

    const data = reactive({
        viewConfig: [],
        itemId: flowableStore.getItemId,
        searchTerm: '',
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
        }
    });
    let { viewConfig, searchTerm, itemId, tableConfig } = toRefs(data);

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
                        width: element.disPlayWidth,
                        align: element.disPlayAlign,
                        className: 'y9draftRecycle_tablecell',
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

    async function reloadTable() {
        let page = tableConfig.value.pageConfig.currentPage;
        let rows = tableConfig.value.pageConfig.pageSize;
        tableConfig.value.loading = true;
        let res = await getDraftRecycleList(itemId.value, searchTerm.value, page, rows);
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
            itembox: 'draft',
            listType: 'draftRecycle'
        };
        flowableStore.$patch({
            //设置打开当前页
            currentPage: tableConfig.value.pageConfig.currentPage.toString()
        });
        router.push({ path: link + '/edit', query: query });
    }

    async function delDraft(row) {
        ElMessageBox.confirm(t('是否彻底删除数据，删除后将无法恢复？'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.y9-table-div'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteDraft(row.id);
                ElMessage({
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    offset: 65,
                    appendTo: '.y9-table-div'
                });
                if (result.success) {
                    reloadTable();
                    emits('refreshCount');
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65,
                    appendTo: '.y9-table-div'
                });
            });
    }

    async function reductionDraft(row) {
        const loading = ElLoading.service({ lock: true, text: t('正在处理中'), background: 'rgba(0, 0, 0, 0.3)' });
        let res = await reduction(row.id);
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
        loading.close();
        if (res.success) {
            reloadTable();
            emits('refreshCount');
        }
    }
</script>

<style lang="scss" scoped>
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

<style lang="scss">
    .y9draftRecycle_tablecell {
        .cell {
            display: flex;
        }
    }
</style>
