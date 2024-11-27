<!--
 * @Author: your name
 * @Date: 2021-05-14 09:31:03
 * @LastEditTime: 2021-11-11 10:54:33
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-flowableUI\src\views\flowableUI\reminder\remindMeList.vue
-->
<template>
    <y9Table
        :config="reminderTableConfig"
        :filterConfig="filterConfig"
        @select="handleSelectionChange"
        @select-all="handleSelectionChange"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    >
        <template #reminderFilter>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="setPower"
                >{{ $t('设为查看') }}
            </el-button>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { computed, inject } from 'vue';
    import { reminderMeList, setReadTime } from '@/api/flowableUI/reminder';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        taskId: String
    });

    let total = ref(0);
    const data = reactive({
        multipleSelection: [],
        reminderTableConfig: {
            columns: [
                { title: '', type: 'selection', width: 50, fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('催办人')), key: 'senderName', width: '110' },
                { title: computed(() => t('内容')), key: 'msgContent' },
                { title: computed(() => t('催办时间')), key: 'createTime', width: '180' },
                { title: computed(() => t('办理环节')), key: 'taskName', width: '180' },
                { title: computed(() => t('查看时间')), key: 'readTime', width: '180' }
            ],
            tableData: [],
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: total.value, //总条目数
                pageSizeOpts: [10, 20, 30, 50, 100]
            },
            border: 0
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 8,
                    slotName: 'reminderFilter'
                }
            ]
        }
    });

    let { filterConfig, multipleSelection, reminderTableConfig } = toRefs(data);

    watch(
        () => props.taskId,
        (newVal) => {
            reloadTable();
        }
    );

    onMounted(() => {
        reloadTable();
    });

    async function reloadTable() {
        let page = reminderTableConfig.value.pageConfig.currentPage;
        let rows = reminderTableConfig.value.pageConfig.pageSize;
        reminderMeList(props.taskId, page, rows).then((res) => {
            if (res.success) {
                reminderTableConfig.value.tableData = res.rows;
                reminderTableConfig.value.pageConfig.total = res.total;
            }
        });
    }

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        reminderTableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        reminderTableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    function setPower() {
        let ids = [];
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择催办记录'), offset: 65, appendTo: '.y9-table-div' });
        } else {
            for (let i = 0; i < multipleSelection.value.length; i++) {
                ids.push(multipleSelection.value[i].id);
            }
            setReadTime(ids.toString()).then((res) => {
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
                    reloadTable();
                } else {
                    ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.y9-table-div' });
                }
            });
        }
    }
</script>

<style scoped>
    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
