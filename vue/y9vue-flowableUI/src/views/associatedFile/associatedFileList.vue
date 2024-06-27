<template>
    <el-container :style="style" class="associat-file">
        <el-aside style="width: 100%; height: auto; overflow: auto; padding: 1% 0% 2% 0%">
            <y9Card :showHeader="false" style="width: 80%; height: calc(100% - 20px); margin: auto">
                <div v-if="btnShow" class="margin-bottom-20 assFile">
                    <el-button type="primary" @click="addAssociated" :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }" plain
                        ><i class="ri-file-add-line"></i>{{ $t('添加') }}</el-button
                    >
                    <el-button type="primary" @click="delAssociated" :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        ><i class="ri-delete-bin-line"></i>{{ $t('删除') }}</el-button
                    >
                </div>
                <y9Table :config="fileTableConfig" @select-all="handleSelectionChange" @select="handleSelectionChange">
                    <template #title="{ row, column, index }">
                        <el-link :style="{color: 'blue', fontSize: fontSizeObj.baseFontSize}" :underline="false" @click="openDoc(row)">{{
                            row.documentTitle
                        }}</el-link>
                    </template>
                    <template #itembox="{ row, column, index }">
                        <font v-if="row.itembox == 'done'" style="color: #d81e06">{{ $t('办结') }}</font>
                        <font v-else-if="row.itembox == 'doing'">{{ $t('在办') }}</font>
                        <font v-else-if="row.itembox == 'todo'" style="color: #228b22">{{ $t('待办') }}</font>
                        <font v-else></font>
                    </template>
                    <template #opt="{ row, column, index }">
                        <el-link type="primary" :underline="false" @click="openHistoryList(row)">{{ $t('历程') }}</el-link>
                    </template>
                </y9Table>

                <y9Dialog v-model:config="dialogConfig">
                    <addAssociatedFile
                        ref="addAssociatedFileRef"
                        v-if="dialogConfig.type == 'addFile'"
                        :itemId="itemId"
                        :processSerialNumber="processSerialNumber"
                        :dialogConfig="dialogConfig"
                        :reloadTable="reloadTable"
                    />
                    <historyList
                        ref="historyListRef"
                        v-if="dialogConfig.type == 'history'"
                        :processInstanceId="processInstanceId"
                    />
                </y9Dialog>
            </y9Card>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
    import { ref, defineProps, onMounted, inject, reactive } from 'vue';
    import historyList from '@/views/process/historyList.vue';
    import { getAssociatedFileList, delAssociatedFile } from '@/api/flowableUI/associatedFile';
    import addAssociatedFile from '@/views/associatedFile/addAssociatedFile.vue';
    import { useSettingStore } from "@/store/modules/settingStore";
    import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
    const { t } = useI18n();    
    const settingStore = useSettingStore()
    let style = 'height:calc(100vh - 210px) !important; width: 100%;';
    if(settingStore.pcLayout == 'Y9Horizontal'){
        style = 'height:calc(100vh - 240px) !important; width: 100%;';
    }
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo'); 
    const props = defineProps({
        basicData: {
            type: Object,
            default: () => {
                return {};
            },
        },
        processSerialNumber: String,
    });

    const data = reactive({
        btnShow: false,
        processSerialNumber: '',
        processInstanceId: '',
        itemId: '',
        multipleSelection: [],
        fileTableConfig: {
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('文件编号')), key: 'number', width: '180' },
                { title: computed(() => t('标题')), key: 'documentTitle', slot: 'title', align: 'left', width: 'auto',minWidth:'200' },
                { title: computed(() => t('开始时间')), key: 'startTime', width: '170' },
                { title: computed(() => t('状态')), key: 'itembox', width: '70', slot: 'itembox' },
                { title: computed(() => t('操作')), key: 'opt', width: '70', slot: 'opt' },
            ],
            tableData: [],
            pageConfig: false,
            border: 0,
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {},
        },
    });

    let { btnShow, processSerialNumber, processInstanceId, itemId, multipleSelection, fileTableConfig, dialogConfig } =
        toRefs(data);

    onMounted(() => {
        if (
            props.basicData.itembox == 'add' ||
            props.basicData.itembox == 'draft' ||
            props.basicData.itembox == 'todo'
        ) {
            btnShow.value = true;
            fileTableConfig.value.columns.push({ title: '', type: 'selection', width: '50', fixed: 'left' });
        }
        //if(props.basicData.itembox != "add"){
        reloadTable();
        //}
    });

    function handleSelectionChange(data) {
        multipleSelection.value = data;
    }

    function reloadTable() {
        getAssociatedFileList(props.processSerialNumber).then((res) => {
            fileTableConfig.value.tableData = res.data;
        });
    }

    function addAssociated() {
        itemId.value = props.basicData.itemId;
        processSerialNumber.value = props.processSerialNumber;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '65%',
            title: computed(() => t('添加关联流程')),
            type: 'addFile',
            showFooter: false,
        });
    }

    function delAssociated() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择关联流程'), offset: 65, appendTo: '.associat-file' });
        } else {
            let ids = [];
            for (let i = 0; i < multipleSelection.value.length; i++) {
                ids.push(multipleSelection.value[i].processInstanceId);
            }
            ElMessageBox.confirm(t('是否删除关联流程?'), t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info',
                appendTo: '.associat-file'
            })
                .then(() => {
                    delAssociatedFile(props.processSerialNumber, ids.join(',')).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.associat-file' });
                            reloadTable();
                        } else {
                            ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.associat-file' });
                        }
                    });
                })
                .catch(() => {
                    ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.associat-file' });
                });
        }
    }

    function openHistoryList(row) {
        processInstanceId.value = row.processInstanceId;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '75%',
            title: t('历程')+'【' + row.documentTitle + '】',
            type: 'history',
            showFooter: false,
        });
    }

    function openDoc(row) {
        // let curPageUrl = window.document.location.href;
        // let pathName = window.document.location.pathname;
        // let pos = curPageUrl.indexOf(pathName);
        // let localhostPath = curPageUrl.substring(0, pos);
        window.open(import.meta.env.VUE_APP_CONTEXT + 'index?processInstanceId=' +row.processInstanceId +'&type=fromHistory');
    }
</script>

<style>
    .el-main-table {
        padding: 0px;
    }
    .el-table__header-wrapper {
        border-top: 1px solid #ebeef5;
    }

    .assFile .el-button--primary.is-plain {
        --el-button-bg-color: white;
    }
</style>

<style scoped lang="scss">
.associat-file {
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
}
</style>
