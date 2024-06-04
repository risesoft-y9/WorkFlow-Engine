<template>
    <div class="margin-bottom-20">
        <el-input
            @keyup.enter.native="reloadTable()"
            v-model="userName"
            :placeholder="$t('请输入接收人姓名')"
            :size="fontSizeObj.buttonSize" 
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            style="width: 200px"
            clearable
        ></el-input>
        <el-button style="margin-left: 10px" type="primary" @click="reloadTable"
        :size="fontSizeObj.buttonSize" :style="{ fontSize: fontSizeObj.baseFontSize }"
            ><i class="ri-search-line"></i>{{ $t('搜索') }}</el-button
        >
        <el-button v-if="type == 'my'" type="primary" @click="delChaoSong" :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            ><i class="ri-folder-received-line"></i>{{ $t('收回') }}</el-button
        >
    </div>
    <y9Table
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
        @select-all="handleSelectionChange"
        @select="handleSelectionChange"
    >
    </y9Table>
</template>

<script lang="ts" setup>
    import {inject, reactive} from 'vue';
    import { getChaoSongList, deleteList } from '@/api/flowableUI/chaoSong';
    import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
    const { t } = useI18n();
    const props = defineProps({
        processInstanceId: String,
        type: String,
    });
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo'); 
    const data = reactive({
        userName: '',
        multipleSelection: [],
        tableConfig: {
            //表格配置
            columns: [
                { title: '', type: 'selection', width: '50', fixed: 'left' },
                { title: computed(() =>t('序号')), type: 'index', width: '60' },
                { title: computed(() =>t('抄送人')), key: 'senderName', width: '180' },
                { title: computed(() =>t('抄送部门')), key: 'sendDeptName' },
                { title: computed(() =>t('抄送时间')), key: 'createTime', width: '140' },
                { title: computed(() =>t('接收人')), key: 'userName', width: '180' },
                { title: computed(() =>t('接收部门')), key: 'userDeptName' },
                { title: computed(() =>t('阅读时间')), key: 'readTime', width: '140' },
            ],
            tableData: [],
            pageConfig: {
                currentPage: 1,
                pageSize: 20,
                total: 0,
                pageSizeOpts:[10, 20, 30, 50, 100]
            },
        },
    });

    let { userName, multipleSelection, tableConfig } = toRefs(data);

    onMounted(() => {
        reloadTable();
    });

    function handleSelectionChange(data) {
        multipleSelection.value = data;
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

    async function reloadTable() {
        if (props.processInstanceId != '' && props.processInstanceId != undefined) {
            let page = tableConfig.value.pageConfig.currentPage;
            let rows = tableConfig.value.pageConfig.pageSize;
            getChaoSongList(props.type, userName.value, props.processInstanceId, page, rows).then((res) => {
                if (res.success) {
                    tableConfig.value.tableData = res.rows;
                    tableConfig.value.pageConfig.total = res.total;
                }
            });
        }
    }

    function delChaoSong() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择要收回的抄送'), offset: 65, appendTo: '.margin-bottom-20' });
        } else {
            let ids = [];
            for (let i = 0; i < multipleSelection.value.length; i++) {
                ids.push(multipleSelection.value[i].id);
            }
            deleteList(ids.toString()).then((res) => {
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.margin-bottom-20' });
                    reloadTable();
                } else {
                    ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.margin-bottom-20' });
                }
            });
        }
    }
</script>

<style scoped>
.margin-bottom-20 {
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
}
</style>
