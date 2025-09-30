<template>
    <div class="from-file" style="width: 100%; height: 97.7%; margin: 15px auto">
        <div style="padding-bottom: 15px; vertical-align: middle">
            <div class="att-files">{{ $t('附件：') }}</div>
            <el-button-group class="ml-4" style="margin-left: 10px" text>
                <el-button v-if="optShow" :title="$t('添加附件')" @click="addFiles" size="mini" text
                    ><i class="ri-file-add-line"></i
                ></el-button>
                <el-button v-if="downloadZipShow" :title="$t('下载附件zip')" size="mini" text>
                    <a
                        :href="downloadZipUrl + '&processSerialNumber=' + basicData.processSerialNumber"
                        style="text-decoration: none; color: inherit"
                    >
                        <i class="ri-file-download-line" @click="downloaFiles"></i>
                    </a>
                </el-button>
                <el-button v-if="optShow" :title="$t('上移文件')" size="mini" @click="moveUp" text>
                    <i class="ri-upload-line"></i>
                </el-button>
                <el-button v-if="optShow" :title="$t('下移文件')" size="mini" @click="moveDown" text>
                    <i class="ri-download-line"></i>
                </el-button>
            </el-button-group>
        </div>

        <y9Table :config="fileTableConfig" @on-current-change="handleCurrentChange">
            <template #name="{ row, column, index }">
                <el-link
                    :href="row.jodconverterURL"
                    :title="$t('点击预览')"
                    :underline="false"
                    target="_blank"
                    type="primary"
                    >{{ row.name }}
                </el-link>
            </template>
            <template #opt="{ row, column, index }">
                <a :href="downloadUrl + '&id=' + row.id" style="text-decoration: none">
                    <i
                        :style="{ fontSize: fontSizeObj.mediumFontSize }"
                        :title="$t('点击下载')"
                        class="ri-download-cloud-2-line"
                    ></i>
                </a>
                <i
                    v-if="y9UserInfo.personId == row.personId && optShow"
                    :style="{ fontSize: fontSizeObj.mediumFontSize, marginLeft: '5px', color: '#586cb1' }"
                    :title="$t('删除附件')"
                    class="ri-delete-bin-line"
                    @click="delFile(row)"
                ></i>
            </template>
        </y9Table>

        <y9Dialog v-model:config="dialogConfig" class="addfileDialog">
            <template #header>
                <div style="display: flex; height: 30px; align-items: baseline; width: 100%">
                    <div class="y9-dialog-header-title">
                        {{ dialogConfig.title }}
                    </div>
                </div>
                <div class="y9-dialog-header-icon">
                    <i
                        v-if="!dialogConfig.fullscreen"
                        class="ri-fullscreen-line full-screen-icon"
                        @click="dialogConfig.fullscreen = true"
                    ></i>
                    <i
                        v-else
                        class="ri-fullscreen-exit-line full-screen-icon"
                        @click="dialogConfig.fullscreen = false"
                    ></i>
                    <i class="ri-close-line" @click="dialogConfig.show = false"></i>
                </div>
            </template>
            <div style="height: auto; width: 100%; min-height: 200px; max-height: 500px">
                <y9Upload v-if="upload.url != ''" :upload="upload" @getFileData="getFileData"></y9Upload>
            </div>
        </y9Dialog>
    </div>
</template>

<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, toRefs } from 'vue';
    import { delAttachment, getAttachmentList, saveOrder } from '@/api/flowableUI/attachment';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const data = reactive({
        optShow: false,
        basicData: {},
        filesList: [],
        y9UserInfo: {},
        upload: {
            accept: 'file', //允许文件类型后缀名,'.jpg,.png,.pdf',逗号分隔，String类型file,为全部类型
            maxCount: 10, //最多上传数量
            size: 1024, //最大文件大小50M
            checkedBoxSelect: false, //是否需要全选单选按钮
            isMultiple: true, //是否支持多选
            showSize: true, //是否显示体积大小
            isClear: false, //每次选择是否清除上一次文件
            trueProgressBar: true, //是否需要根据文件大小展示进度条,如果是true需要在axios添加onUploadProgress属性
            url: '', //上传url
            formData: {
                processSerialNumber: '',
                processInstanceId: '',
                taskId: '',
                fileSource: ''
            } //参数key value形式
        },
        fileTableConfig: {
            columns: [
                //{ title: "序号", type:'index', width: '60', },
                { title: computed(() => t('名称')), type: 'name', slot: 'name', align: 'left' },
                //{ title: "文件大小", key: "fileSize", width: '100',},
                { title: computed(() => t('上传时间')), key: 'uploadTime', width: '145' },
                { title: computed(() => t('上传人')), key: 'personName', width: '100' },
                { title: computed(() => t('操作')), key: 'opt', width: '75', slot: 'opt' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            fullscreen: false,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        },
        downloadUrl: '',
        downloadZipUrl: '',
        downloadZipShow: false,
        currentRow: []
    });

    let {
        optShow,
        basicData,
        filesList,
        y9UserInfo,
        fileTableConfig,
        dialogConfig,
        upload,
        downloadUrl,
        downloadZipUrl,
        downloadZipShow,
        currentRow
    } = toRefs(data);

    onMounted(() => {
        downloadZipShow.value = false;
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
        downloadUrl.value =
            import.meta.env.VUE_APP_CONTEXT +
            'vue/attachment/download?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        downloadZipUrl.value =
            import.meta.env.VUE_APP_CONTEXT +
            'vue/attachment/packDownload?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
    });

    function getFileData(status) {
        if (status) {
            dialogConfig.value.show = false;
            reloadTable();
        }
    }

    function initFileList(data) {
        basicData.value = data;
        if (
            basicData.value.itembox == 'add' ||
            basicData.value.itembox == 'draft' ||
            basicData.value.itembox == 'todo'
        ) {
            // fileTableConfig.value.columns.push({ title: '', type: 'selection',width: '50', fixed: 'left'});
            optShow.value = true;
        }
        reloadTable();
    }

    defineExpose({
        initFileList,
        fileTableConfig
    });

    function reloadTable() {
        let png = 'png,bmp,jpg,jpeg,gif,tiff,ico,tif';
        downloadZipShow.value = false;
        getAttachmentList(basicData.value.processSerialNumber, 1, 50).then((res) => {
            let fileList = res.rows;
            for (let i in fileList) {
                fileList[i].downloadUrl =
                    import.meta.env.VUE_APP_CONTEXT + 's/' + fileList[i].fileStoreId + '.' + fileList[i].fileType;
                fileList[i].jodconverterURL = import.meta.env.VUE_APP_JODCONVERTERURL + fileList[i].downloadUrl;
                fileList[i].uploadTime = fileList[i].uploadTime.substring(0, 16);
            }
            fileTableConfig.value.tableData = fileList;
            if (fileList.length > 0) {
                downloadZipShow.value = true;
            }
        });
    }

    function download(row) {
        window.open(
            import.meta.env.VUE_APP_CONTEXT +
                'vue/attachment/download?id=' +
                row.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token')
        );
    }

    function addFiles() {
        filesList.value = [];
        upload.value.formData = {
            processSerialNumber: basicData.value.processSerialNumber,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId,
            fileSource: ''
        };
        upload.value.url =
            import.meta.env.VUE_APP_CONTEXT +
            'vue/attachment/upload?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        Object.assign(dialogConfig.value, {
            show: true,
            width: '40%',
            title: computed(() => t('附件上传')),
            showFooter: false
        });
    }

    function delFile(row) {
        let ids = [];
        ids.push(row.id);
        if (y9UserInfo.value.personId != row.personId) {
            ElMessage({ type: 'error', message: t('只能删除自己上传的文件'), offset: 65, appendTo: '.from-file' });
            return;
        }
        ElMessageBox.confirm(t('是否删除附件?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.from-file'
        })
            .then(() => {
                delAttachment(ids.join(',')).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.from-file' });
                        reloadTable();
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.from-file' });
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.from-file' });
            });
    }

    function handleCurrentChange(val) {
        currentRow.value = val;
    }

    const moveUp = () => {
        //上移
        if (currentRow.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请点击选中一条数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let index = 0;
        for (let i = 0; i < fileTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == fileTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = fileTableConfig.value.tableData[index - 1];
            let currRow = fileTableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            fileTableConfig.value.tableData.splice(index - 1, 1);
            fileTableConfig.value.tableData.splice(index, 0, upRow);
            saveFileOrder();
        } else {
            ElNotification({
                title: '操作提示',
                message: '已经是第一条，不可上移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        }
    };

    const moveDown = () => {
        //下移
        if (currentRow.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请选择数据', type: 'error', duration: 2000, offset: 80 });
            return;
        }

        let index = 0;
        for (let i = 0; i < fileTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == fileTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == fileTableConfig.value.tableData.length) {
            ElNotification({
                title: '操作提示',
                message: '已经是最后一条，不可下移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        } else {
            let downRow = fileTableConfig.value.tableData[index + 1];
            let currRow = fileTableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            fileTableConfig.value.tableData.splice(index + 1, 1);
            fileTableConfig.value.tableData.splice(index, 0, downRow);
            saveFileOrder();
        }
    };

    function saveFileOrder() {
        let ids = [];
        for (let item of fileTableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        //const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            //loading.close();
            if (res.success) {
                //ElNotification({ title: '操作提示', message: res.msg, type: 'success', duration: 2000, offset: 80 });
                //getViewConfigList();
            } else {
                ElNotification({ title: '操作提示', message: '移动失败', type: 'error', duration: 2000, offset: 80 });
            }
        });
    }
</script>
<style lang="scss">
    .addfileDialog .y9-dialog-content {
        margin-bottom: 30px !important;
    }
</style>
<style lang="scss" scoped>
    @import '@/theme/global.scss';

    .el-main-table {
        padding: 0px;
    }

    .el-table__header-wrapper {
        border-top: 1px solid #ebeef5;
    }

    .el-table-column--selection .cell {
        padding-left: 10px;
        padding-right: 10px;
    }

    .fm-form .el-checkbox {
        margin-right: 0px !important;
    }

    .addfile .el-progress-bar__outer {
        background-color: #bbb;
    }

    .loading {
        position: absolute;
        left: 0;
        top: 0;
        right: 0;
        bottom: 0;
        background: black;
        opacity: 0.8;
    }

    .upload-demo {
        width: 80% !important;
    }

    .progress {
        width: 200px;
        height: 200px;
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -100px;
        margin-top: -50px;
        z-index: 99999;
    }

    .y9-dialog-header-title {
        margin-right: 16px;
        word-break: break-all;
    }

    .y9-dialog-header-icon {
        display: flex;
        align-items: center;

        .full-screen-icon {
            color: $iconColor;
            cursor: pointer;
            font-size: v-bind('fontSizeObj.mediumFontSize');
            margin-right: 10px;
        }

        .full-screen-icon:hover {
            color: var(--el-color-primary);
        }

        .ri-close-line {
            color: $iconColor;
            cursor: pointer;
            font-size: v-bind('fontSizeObj.extraLargeFont');
        }

        .ri-close-line:hover {
            color: var(--el-color-primary);
        }
    }

    .empty {
        min-height: 200px;
        display: flex;
        justify-content: center;
        color: #bbb;
        font-size: v-bind('fontSizeObj.smallFontSize');
    }

    // 文件列表 样式修改
    :deep(.el-upload-list) {
        // border: 1px solid #eee;
        margin: 30px 0 0 -72px;
        z-index: 2;
        width: calc(100% + 140px);
        height: 200px;
        overflow: auto;

        .el-upload-list__item-name .el-icon {
            color: var(--el-color-primary);
        }

        .el-upload-list__item-file-name {
            color: var(--el-color-primary);
        }

        .el-upload-list__item .el-icon--close {
            font-size: v-bind('fontSizeObj.mediumFontSize');
        }
    }

    // 表格的 复选框 样式 居中
    :deep(.el-table__inner-wrapper) {
        .cell {
            .el-checkbox {
                margin-right: 0px;
            }
        }
    }

    .att-files {
        display: inline;
        font-weight: 500;
        font-size: v-bind('fontSizeObj.baseFontSize');
        line-height: 25px;
    }

    .addFile-bnt {
        display: inline-block;
        vertical-align: middle;
        cursor: pointer;
        margin-left: 5px;

        i {
            font-size: v-bind('fontSizeObj.extraLargeFont');
            color: rgb(88, 108, 177);
            vertical-align: middle;
        }
    }

    .downloaFiles-bnt {
        display: inline-block;
        vertical-align: middle;
        cursor: pointer;
        margin-left: 5px;

        i {
            font-size: v-bind('fontSizeObj.extraLargeFont');
            color: rgb(88, 108, 177);
            vertical-align: middle;
        }
    }

    :deep(.el-table) {
        .cell {
            font-size: v-bind('fontSizeObj.smallFontSize');
        }

        .el-table__empty-text {
            font-size: v-bind('fontSizeObj.smallFontSize');
        }
    }

    .from-file {
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

        /*message */
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .el-button {
            padding: 8px 8px !important;
            box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0.06);
            i {
                margin-right: 0px;
                font-size: v-bind('fontSizeObj.extraLargeFont');
                color: rgb(88, 108, 177);
                vertical-align: middle;
            }
        }
    }
</style>
