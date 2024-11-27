<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-01-29 18:33:52
 * @LastEditors: zhangchongjie
 * @Description:  附件列表
-->
<template>
    <el-container
        v-loading="loading"
        :element-loading-text="$t('正在保存中')"
        :style="style"
        class="filecontainer"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <el-aside class="formaside" style="width: 100%; height: auto; overflow: auto; padding: 1% 0% 2% 0%">
            <y9Card :showHeader="false" style="width: 80%; height: calc(100% - 20px); margin: auto">
                <div
                    v-if="basicData.itembox == 'add' || basicData.itembox == 'draft' || basicData.itembox == 'todo'"
                    class="margin-bottom-20 fileList"
                >
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        plain
                        type="primary"
                        @click="addFiles"
                        ><i class="ri-file-add-line"></i>{{ $t('添加') }}
                    </el-button>
                    <a
                        v-if="downloadZipShow"
                        :href="downloadZipUrl + '&processSerialNumber=' + basicData.processSerialNumber"
                        style="margin: 0 12px"
                    >
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            plain
                            type="primary"
                            ><i class="ri-folder-download-line"></i>{{ $t('zip下载') }}
                        </el-button>
                    </a>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        type="primary"
                        @click="delFile"
                        ><i class="ri-delete-bin-line"></i>{{ $t('删除') }}
                    </el-button>
                </div>
                <div
                    v-if="
                        basicData.itembox != 'add' &&
                        basicData.itembox != 'draft' &&
                        basicData.itembox != 'todo' &&
                        downloadZipShow
                    "
                    class="margin-bottom-20 fileList"
                >
                    <a
                        :href="downloadZipUrl + '&processSerialNumber=' + basicData.processSerialNumber"
                        style="margin: 0 12px"
                    >
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            plain
                            type="primary"
                            ><i class="ri-folder-download-line"></i>{{ $t('zip下载') }}
                        </el-button>
                    </a>
                </div>
                <y9Table
                    :config="fileTableConfig"
                    class="y9Tablefile"
                    @select="handleSelectionChange"
                    @select-all="handleSelectionChange"
                    @on-curr-page-change="onCurrPageChange"
                    @on-page-size-change="onPageSizeChange"
                >
                    <template #name="{ row, column, index }">
                        <el-link
                            :href="row.jodconverterURL"
                            :title="$t('点击预览')"
                            :underline="false"
                            target="_blank"
                            type="primary"
                        >
                            {{ row.name }}
                        </el-link>
                    </template>
                    <template #opt="{ row, column, index }">
                        <a :href="downloadUrl + '&id=' + row.id" style="text-decoration: none"
                            ><i :title="$t('点击下载')" class="ri-download-2-line" style="font-size: 18px"></i
                        ></a>
                    </template>
                </y9Table>
                <y9Dialog v-model:config="dialogConfig">
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
            </y9Card>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
    import { computed, defineProps, inject, onMounted, reactive, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import { delAttachment, getAttachmentList } from '@/api/flowableUI/attachment';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    let style = 'height:calc(100vh - 210px) !important; width: 100%;';
    if (settingStore.pcLayout == 'Y9Horizontal') {
        style = 'height:calc(100vh - 240px) !important; width: 100%;';
    }
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        basicData: {
            type: Object,
            default: () => {
                return {};
            }
        },
        processSerialNumber: String
    });

    let total = ref(0);
    const data = reactive({
        y9UserInfo: {},
        multipleSelection: [],
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
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                {
                    title: computed(() => t('名称')),
                    type: 'name',
                    slot: 'name',
                    width: 'auto',
                    align: 'left',
                    minWidth: '200'
                },
                { title: computed(() => t('文件大小')), key: 'fileSize', width: '100' },
                { title: computed(() => t('上传时间')), key: 'uploadTime', width: '145' },
                { title: computed(() => t('上传人')), key: 'personName', width: '110' },
                //{ title: "上传部门", key: "deptName", width: '150', },
                { title: computed(() => t('操作')), key: 'opt', width: '60', slot: 'opt' }
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
        filesList: [],
        loading: false,
        downloadUrl: '',
        downloadZipUrl: '',
        downloadZipShow: false
    });

    let {
        y9UserInfo,
        multipleSelection,
        fileTableConfig,
        dialogConfig,
        filesList,
        loading,
        upload,
        downloadUrl,
        downloadZipUrl,
        downloadZipShow
    } = toRefs(data);

    onMounted(() => {
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
        downloadUrl.value =
            import.meta.env.VUE_APP_HOST +
            import.meta.env.VUE_APP_NAME +
            '/vue/attachment/attachmentDownload?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        downloadZipUrl.value =
            import.meta.env.VUE_APP_HOST +
            import.meta.env.VUE_APP_NAME +
            '/vue/attachment/packDownload?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        if (
            props.basicData.itembox == 'add' ||
            props.basicData.itembox == 'draft' ||
            props.basicData.itembox == 'todo'
        ) {
            fileTableConfig.value.columns.push({ title: '', type: 'selection', width: '50', fixed: 'left' });
        }
        //if(props.basicData.itembox != "add"){
        reloadTable();
        //}
    });

    function getFileData(status) {
        if (status) {
            dialogConfig.value.show = false;
            reloadTable();
        }
    }

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
        let png = 'png,bmp,jpg,jpeg,gif,tiff,ico,tif';
        downloadZipShow.value = false;
        let page = fileTableConfig.value.pageConfig.currentPage;
        let rows = fileTableConfig.value.pageConfig.pageSize;
        getAttachmentList(props.basicData.processSerialNumber, page, rows).then((res) => {
            let fileList = res.rows;
            for (let i in fileList) {
                fileList[i].jodconverterURL = import.meta.env.VUE_APP_JODCONVERTERURL + fileList[i].downloadUrl;
                let arr = fileList[i].downloadUrl.split('.');
                let type = arr[arr.length - 1];
                if (png.indexOf(type) > -1) {
                    //图片使用外网地址，使用前端配置
                    fileList[i].downloadUrl =
                        import.meta.env.VUE_APP_HOST + 'itemAdmin' + fileList[i].downloadUrl.split('/itemAdmin')[1];
                    fileList[i].jodconverterURL = import.meta.env.VUE_APP_JODCONVERTERURL + fileList[i].downloadUrl;
                }
            }
            fileTableConfig.value.tableData = fileList;
            fileTableConfig.value.pageConfig.total = res.total;
            if (fileList.length > 0) {
                downloadZipShow.value = true;
            }
        });
    }

    function download(row) {
        window.open(
            import.meta.env.VUE_APP_HOST +
                import.meta.env.VUE_APP_NAME +
                '/vue/attachment/attachmentDownload?id=' +
                row.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token')
        );
    }

    function addFiles() {
        filesList.value = [];
        upload.value.formData = {
            processSerialNumber: props.basicData.processSerialNumber,
            processInstanceId: props.basicData.processInstanceId,
            taskId: props.basicData.taskId,
            fileSource: ''
        };
        upload.value.url =
            import.meta.env.VUE_APP_HOST +
            import.meta.env.VUE_APP_NAME +
            '/vue/attachment/upload?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        Object.assign(dialogConfig.value, {
            show: true,
            width: '40%',
            title: computed(() => t('添加附件')),
            showFooter: false
        });
    }

    function delFile() {
        if (multipleSelection.value.length === 0) {
            ElMessage({ type: 'error', message: t('请选择附件'), offset: 65, appendTo: '.filecontainer' });
        } else {
            let ids = [];
            for (let item of multipleSelection.value) {
                ids.push(item.id);
                if (y9UserInfo.value.personId != item.personId) {
                    ElMessage({
                        type: 'error',
                        message: t('只能删除自己上传的文件'),
                        offset: 65,
                        appendTo: '.filecontainer'
                    });
                    return;
                }
            }
            ElMessageBox.confirm(t('是否删除附件?'), t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info',
                appendTo: '.filecontainer'
            })
                .then(() => {
                    delAttachment(ids.join(',')).then((res) => {
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.filecontainer' });
                            reloadTable();
                        } else {
                            ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.filecontainer' });
                        }
                    });
                })
                .catch(() => {
                    ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.filecontainer' });
                });
        }
    }
</script>

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

    .fileList .el-button--primary.is-plain {
        --el-button-bg-color: white;
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
            font-size: 16px;
            margin-right: 10px;
        }

        .full-screen-icon:hover {
            color: var(--el-color-primary);
        }

        .ri-close-line {
            color: $iconColor;
            cursor: pointer;
            font-size: 20px;
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
        font-size: 14px;
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
            font-size: 16px;
        }
    }

    .filecontainer {
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
    }
</style>
<style lang="scss">
    .y9Tablefile .el-table {
        height: auto !important;
    }
</style>
