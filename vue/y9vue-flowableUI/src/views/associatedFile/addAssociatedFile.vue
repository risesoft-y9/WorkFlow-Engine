<template>
    <y9Table
        :config="fileTableConfig"
        :filterConfig="filterConfig"
        @select="handleSelectionChange"
        @select-all="handleSelectionChange"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #fileOpt>
            <el-input
                v-model="title"
                :placeholder="$t('请输入标题文号')"
                class="addAssociatedFileInput"
                clearable
            ></el-input>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                style="margin-left: 10px"
                type="primary"
                @click="reloadTable()"
                ><i class="ri-search-line"></i>{{ $t('搜索') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                style="margin-left: 10px"
                type="primary"
                @click="saveAssociated"
                ><i class="ri-save-line"></i>{{ $t('保存') }}
            </el-button>
        </template>
        <template #itembox="{ row, column, index }">
            <font v-if="row.itembox == 'done'" style="color: #d81e06">{{ $t('办结') }}</font>
            <font v-else-if="row.itembox == 'doing'">{{ $t('在办') }}</font>
            <font v-else-if="row.itembox == 'todo'" style="color: #228b22">{{ $t('待办') }}</font>
            <font v-else></font>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { computed, defineProps, inject, onMounted, reactive } from 'vue';
    import { getAssociatedDoneList, saveAssociatedFile } from '@/api/flowableUI/associatedFile';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        dialogConfig: {
            type: Object,
            default: () => {
                return {};
            }
        },
        reloadTable: Function,
        itemId: String,
        processSerialNumber: String
    });

    const data = reactive({
        title: '',
        multipleSelection: [],
        fileTableConfig: {
            columns: [
                { title: '', type: 'selection', width: '50', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('文件编号')), key: 'number', width: '180' },
                { title: computed(() => t('类别')), key: 'itemName', width: '100' },
                { title: computed(() => t('标题')), key: 'documentTitle', align: 'left', minWidth: '300' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '165' },
                { title: computed(() => t('状态')), key: 'itembox', width: '70', slot: 'itembox' }
            ],
            tableData: [],
            pageConfig: {
                currentPage: 1,
                pageSize: 20,
                total: 0,
                pageSizeOpts: [10, 20, 30, 50, 100]
            },
            height: 450,
            border: 0
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 8,
                    slotName: 'fileOpt'
                }
            ]
        }
    });

    let { title, multipleSelection, filterConfig, fileTableConfig } = toRefs(data);

    onMounted(() => {
        reloadTable();
    });

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        fileTableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        fileTableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function reloadTable() {
        let page = fileTableConfig.value.pageConfig.currentPage;
        let rows = fileTableConfig.value.pageConfig.pageSize;
        getAssociatedDoneList('', title.value, page, rows).then((res) => {
            fileTableConfig.value.tableData = res.rows;
            fileTableConfig.value.pageConfig.total = res.total;
        });
    }

    function saveAssociated() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择要关联的文件'), offset: 65, appendTo: '.y9-table-div' });
        } else {
            let ids = [];
            for (let i = 0; i < multipleSelection.value.length; i++) {
                ids.push(multipleSelection.value[i].processInstanceId);
            }
            saveAssociatedFile(props.processSerialNumber, ids.join(',')).then((res) => {
                if (res.success) {
                    props.dialogConfig.show = false;
                    ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
                    props.reloadTable();
                } else {
                    ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
                }
            });
        }
    }
</script>

<style>
    .el-main-table {
        padding: 0px;
    }

    .el-dialog__body {
        padding: 0 20px 10px 20px;
    }

    .el-table__header-wrapper {
        border-top: 1px solid #ebeef5;
    }

    .el-table-column--selection .cell {
        padding-left: 10px;
        padding-right: 10px;
    }

    .addAssociatedFileInput .el-input__inner {
        width: 230px;
    }
</style>

<style scoped>
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
