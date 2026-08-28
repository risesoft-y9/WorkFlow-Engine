<template>
    <div
        v-loading="loading"
        :element-loading-text="$t('拼命加载中')"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <div
            ><font color="blue">{{ $t('蓝色') }}</font
            >{{ $t('代表修改记录') }}，<font color="red">{{ $t('红色') }}</font
            >{{ $t('代表删除记录') }}。
        </div>
        <y9Table :config="fileTableConfig">
            <template #opinionContent="{ row, column, index }">
                <font v-if="row.opinionType == '1'" color="blue">{{ row.content }}</font>
                <font v-else-if="row.opinionType == '2'" color="red">{{ row.content }}</font>
                <font v-else>{{ row.content }}</font>
            </template>
            <template #time="{ row, column, index }">
                <font v-if="row.modifyDate == row.createDate"></font>
                <font v-else>{{ row.modifyDate }}</font>
            </template>
        </y9Table>
    </div>
</template>
<script lang="ts" setup>
    import { getOpinionHistoryList } from '@/api/flowableUI/opinion';
    import { computed } from 'vue';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const props = defineProps({
        opinionframemark: String,
        processSerialNumber: String
    });

    const data = reactive({
        loading: false,
        fileTableConfig: {
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('姓名')), key: 'userName', width: '110' },
                { title: computed(() => t('意见内容')), key: 'content', slot: 'opinionContent' },
                { title: computed(() => t('创建时间')), key: 'createDate', width: '180' },
                { title: computed(() => t('修改时间')), key: 'modifyDate', width: '180', slot: 'time' },
                { title: computed(() => t('操作时间')), key: 'saveDate', width: '180' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
        }
    });

    let { loading, fileTableConfig } = toRefs(data);

    reloadTable();

    function reloadTable() {
        loading.value = true;
        getOpinionHistoryList(props.processSerialNumber, props.opinionframemark).then((res) => {
            fileTableConfig.value.tableData = res.data;
            loading.value = false;
        });
    }
</script>

<style></style>
