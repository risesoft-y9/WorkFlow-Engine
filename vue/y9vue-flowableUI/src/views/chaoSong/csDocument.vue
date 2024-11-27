<template>
    <el-container
        v-loading="loading"
        :style="style"
        class="csDocument-container"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
        element-loading-text="拼命加载中"
    >
        <div :class="{ 'document-shrink': !processFlag }" class="document">
            <div class="buttonDiv">
                <FloatButton :list="operationBtnList" />
            </div>
            <div class="tab-nav">
                <Tabs v-model:activeName="activeName" :list="dataList" class="nav" @tab-click="tabClick"></Tabs>
            </div>

            <!-- 收缩 -->
            <div class="shrink" @click="handlerClickShrink">
                <div v-if="processFlag">
                    <i class="ri-arrow-right-s-line"></i>
                    <i class="ri-arrow-right-s-line" style="margin-left: -23px"></i>
                </div>
                <div v-else>
                    <i class="ri-arrow-left-s-line"></i>
                    <i class="ri-arrow-left-s-line" style="margin-left: -23px"></i>
                </div>
            </div>

            <div style="margin-top: 104px">
                <!-- 表单 -->
                <newForm
                    v-show="activeName == 'y9form'"
                    ref="myForm"
                    :basicData="basicData"
                    :processInstanceId="processInstanceId"
                />
                <!-- 附件 -->
                <fileList
                    v-if="activeName == 'attach'"
                    ref="fileListRef"
                    :basicData="basicData"
                    :processSerialNumber="processSerialNumber"
                />
                <!-- 关联流程 -->
                <associatedFileList
                    v-if="activeName == 'associatedFile'"
                    ref="associatedFileListRef"
                    :basicData="basicData"
                    :processSerialNumber="processSerialNumber"
                />
                <!-- 沟通交流 -->
                <speakInfo v-if="activeName == 'speakInfo'" ref="speakInfoRef" :processInstanceId="processInstanceId" />
                <!-- 历程 -->
                <processListCom
                    v-if="activeName == 'process'"
                    ref="processListRef"
                    :processInstanceId="processInstanceId"
                />
                <!-- 流程图 -->
                <flowChart
                    v-if="activeName == 'flowChart'"
                    ref="flowchartRef"
                    :processDefinitionId="basicData.processDefinitionId"
                    :processInstanceId="processInstanceId"
                />
            </div>
        </div>
        <div v-if="processFlag" :style="processStyle" class="process-content">
            <el-card style="height: 100%; box-shadow: 0px 0px 0px 0px #333">
                <template #header>
                    <div class="card-header">
                        <span :style="{ fontSize: fontSizeObj.mediumFontSize }">{{ $t('流转状态') }}</span>
                    </div>
                </template>
                <div
                    style="
                        height: 94%;
                        overflow-y: auto;
                        overflow-x: hidden;
                        display: flex;
                        background-color: white;
                        justify-content: center;
                        align-items: self-start;
                    "
                >
                    <!-- 没数据 显示暂无数据 -->
                    <ProcessStatus v-if="processTimeLineList.length > 0" :list="processTimeLineList" />
                    <el-empty v-else :description="$t('暂无数据')" />
                </div>
            </el-card>
        </div>
        <y9Dialog v-model:config="dialogConfig">
            <csUserChoise ref="csUserChoiseRef" :basicData="basicData" @csRefreshCount="updateLeftListCount" />
        </y9Dialog>
    </el-container>
</template>

<script lang="ts" setup>
    import { computed, inject, onBeforeMount, reactive } from 'vue';
    import newForm from '@/views/workForm/newForm.vue';
    import fileList from '@/views/file/fileList.vue';
    import associatedFileList from '@/views/associatedFile/associatedFileList.vue';
    import processListCom from '@/views/process/processList.vue';
    import speakInfo from '@/views/speakInfo/speakInfo.vue';
    import csUserChoise from '@/views/chaoSong/csUserChoise.vue';
    import flowChart from '@/views/flowchart/index.vue';
    import { chaoSongData } from '@/api/flowableUI/chaoSong';
    import { delOfficeFollow, saveOfficeFollow } from '@/api/flowableUI/follow';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { processList } from '@/api/flowableUI/process';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const settingStore = useSettingStore();
    let style = 'height:calc(100vh - 105px);margin-top: 45px;';
    let processStyle = 'height: calc(100vh - 106px) !important;';
    if (settingStore.pcLayout == 'Y9Horizontal') {
        style = 'height:calc(100vh - 160px);margin-top: 45px;';
        processStyle = 'height: calc(100vh - 162px) !important;';
    }

    const router = useRouter();
    // 获取当前路由
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    let basicData1 = {
        tenantId: '',
        userId: '',
        processSerialNumber: '',
        processDefinitionId: '',
        processInstanceId: '',
        taskId: '',
        taskDefKey: '',
        formId: '',
        itembox: '',
        initDataUrl: '',
        itemId: ''
    };

    const emits = defineEmits(['refreshCount']);
    const data = reactive({
        myForm: '',
        loading: false,
        activeName: 'y9form',
        itemId: '',
        status: 0,
        addData: {}, //新建数据
        menuMap: [], //按钮菜单数据
        formList: [{ formName: '未绑定表单' }], //绑定表单数据
        basicData: basicData1,
        processSerialNumber: '', //流程编号
        processInstanceId: '',
        id: '', //抄送id
        printUrl: '', //打印url
        printFormType: '', //打印配置
        formId: '',
        formUrl: '', //表单url
        showOtherFlag: '', //是否显示关联流程，正文，沟通交流页签
        listType: '', //列表类型，判断从哪个列表跳转过来，用于返回列表
        follow: false, //是否关注该件
        fileLabel: '附件', //是否有附件
        docNum: 0, //是否有正文
        speakInfoLabel: '沟通交流', //是否沟通交流新消息
        associatedFileLabel: '关联流程', //是否有关联流程
        fileListShow: false, //点击对应的页签才加载对应组件
        processListShow: false,
        speakInfoShow: false,
        associatedFileListShow: false,
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            closeOnClickModal: false, //是否可以通过点击 modal 关闭 Dialog
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        },
        csUserChoiseRef: '',
        dataList: [],
        operationBtnList: [],
        processTimeLineList: [],
        // 收缩 流程状态
        processFlag: null
    });

    let {
        myForm,
        loading,
        activeName,
        itemId,
        status,
        addData,
        menuMap,
        formId,
        basicData,
        formList,
        processInstanceId,
        processSerialNumber,
        id,
        printUrl,
        printFormType,
        formUrl,
        showOtherFlag,
        listType,
        follow,
        fileLabel,
        docNum,
        processFlag,
        speakInfoLabel,
        associatedFileLabel,
        fileListShow,
        processListShow,
        speakInfoShow,
        associatedFileListShow,
        dialogConfig,
        csUserChoiseRef,
        dataList,
        operationBtnList,
        processTimeLineList
    } = toRefs(data);

    onBeforeMount(() => {
        const value = JSON.parse(sessionStorage.getItem('process'));
        processFlag.value = value === null ? true : value;
        itemId.value = currentrRute.query.itemId;
        status.value = currentrRute.query.status;
        id.value = currentrRute.query.id;
        processInstanceId.value = currentrRute.query.processInstanceId;
        listType.value = currentrRute.query.listType;
        getChaoSongData();
        getProcessList();
    });

    function updateLeftListCount() {
        emits('refreshCount');
    }

    async function getChaoSongData() {
        //获取抄送数据
        loading.value = true;
        let res = await chaoSongData(id.value, processInstanceId.value, itemId.value, status.value);
        loading.value = false;
        if (res.success) {
            if (status.value == 0) {
                emits('refreshCount');
            }
            let csData = res.data;
            let menuNames = csData.menuName.split(',');
            let menuKeys = csData.menuKey.split(',');
            menuMap.value = [];
            for (let i = 0; i < menuNames.length; i++) {
                let menu = {};
                menu.menuName = menuNames[i];
                menu.menuKey = menuKeys[i];
                menuMap.value.push(menu);
            }

            menuMap.value.reverse(); //将menuMap.reverse()倒序
            formList.value = csData.formList;
            formId.value = csData.formList[0].formId;
            showOtherFlag.value = csData.showOtherFlag;
            processInstanceId.value = csData.processInstanceId;
            processSerialNumber.value = csData.processSerialNumber;
            follow.value = csData.follow;
            printFormType.value = csData.printFormType;

            //修改标题数据
            flowableStore.$patch({
                documentTitle: csData.title
            });
            basicData.value.flowableUIBaseURL = csData.flowableUIBaseURL;
            basicData.value.processSerialNumber = csData.processSerialNumber;
            basicData.value.tenantId = csData.tenantId;
            basicData.value.userId = csData.userId;
            basicData.value.processDefinitionId = csData.processDefinitionId;
            basicData.value.taskDefKey = csData.taskDefKey;
            basicData.value.formId = csData.formList[0].formId;
            basicData.value.printFormId = csData.printFormId;
            basicData.value.initDataUrl = csData.initDataUrl;
            basicData.value.itemId = csData.itemId;
            basicData.value.processInstanceId = csData.processInstanceId;
            basicData.value.taskId = csData.taskId;
            basicData.value.itembox = 'yuejian';
            basicData.value.activitiUser = csData.activitiUser;
            basicData.value.processDefinitionKey = csData.processDefinitionKey;
            basicData.value.chaosongId = id.value;

            fileLabel.value = csData.fileNum == 0 ? '附件' : '附件(' + csData.fileNum + ')';
            docNum.value = csData.docNum;
            associatedFileLabel.value =
                csData.associatedFileNum == 0 ? '关联流程' : '关联流程(' + csData.associatedFileNum + ')';
            speakInfoLabel.value = csData.speakInfoNum == 0 ? '沟通交流' : '沟通交流(' + csData.speakInfoNum + ')';

            myForm.value.show(formId.value);
            loading.value = false;
        }
        setTimeout(() => {
            getTabs();
        }, 1000);
    }

    function tabClick(item) {
        //页签切换
        activeName.value = item.name;
        if (item.name == 'attach') {
            fileListShow.value = true;
        } else if (item.name == 'process') {
            processListShow.value = true;
        } else if (item.name == 'flowChart') {
        } else if (item.name == 'speakInfo') {
            speakInfoShow.value = true;
        } else if (item.name == 'associatedFile') {
            associatedFileListShow.value = true;
        }
    }

    // 页签和按钮的获取
    function getTabs() {
        dataList.value.push({ label: formList.value[0].formName, name: 'y9form' });
        if (showOtherFlag.value.includes('showFileTab')) {
            dataList.value.push({ label: fileLabel.value, name: 'attach' });
        }
        if (showOtherFlag.value.includes('showHistoryTab')) {
            dataList.value.push({ label: associatedFileLabel.value, name: 'associatedFile' });
        }
        dataList.value.push({ label: speakInfoLabel.value, name: 'speakInfo' });
        dataList.value.push({ label: '历程', name: 'process' });
        dataList.value.push({ label: '流程图', name: 'flowChart' });
        operationBtnList.value = [];
        for (let item of menuMap.value) {
            if (item.menuKey.indexOf('follow') > -1 && follow.value) {
                //关注按钮：取消关注
                operationBtnList.value.push({
                    name: '关注',
                    icon: 'ri-star-fill',
                    onClick: () => {
                        delFollow();
                    }
                });
            } else if (item.menuKey.indexOf('follow') > -1 && !follow.value) {
                //关注按钮：点击关注
                operationBtnList.value.push({
                    name: '关注',
                    icon: 'ri-star-line',
                    onClick: () => {
                        saveFollow();
                    }
                });
            } else {
                //按钮
                let iconName = 'ri-mouse-line';
                if (item.menuKey == '03') {
                    //返回
                    iconName = 'ri-arrow-go-back-fill';
                } else if (item.menuKey == '18') {
                    //抄送按钮
                    iconName = 'ri-file-copy-2-line';
                } else if (item.menuKey == '17') {
                    //打印按钮
                    iconName = 'ri-printer-line';
                }
                operationBtnList.value.push({
                    name: item.menuName,
                    icon: iconName,
                    onClick: () => {
                        buttonEvent(item.menuKey);
                    }
                });
            }
        }
    }

    async function getProcessList() {
        let res = await processList(processInstanceId.value);
        if (res.success) {
            const rows = res.data;
            let index = 0;
            for (let item of rows) {
                index++;
                let obj = {};
                obj.content = item.name;
                if (item.assignee.indexOf('（') != -1) {
                    let nameArr = [];
                    let reg = new RegExp('）', 'g');
                    let userName = item.assignee.split('（')[1].replace(reg, '');
                    nameArr.push(userName);
                    nameArr.push(item.assignee.split('（')[0]);
                    obj.name = nameArr;
                } else {
                    obj.name = item.assignee;
                }
                obj.type = 'solid';
                obj.placement = 'left';
                if (item.endTime === '') {
                    obj.timestamp = item.startTime;
                    obj.type = 'current';
                } else {
                    let timneArr = [];
                    timneArr.push(item.startTime);
                    timneArr.push(item.endTime);
                    obj.timestamp = timneArr;
                }
                if ((index & 1) === 0) {
                    obj.placement = 'right';
                }
                processTimeLineList.value.push(obj);
            }
        }
    }

    function backToList() {
        //返回列表
        let link = currentrRute.matched[0].path;
        let path = link + '/' + listType.value;
        flowableStore.$patch({
            //设置打开当前页
            currentPage: flowableStore.currentPage + '_back'
        });
        router.push({ path });
    }

    function buttonEvent(key) {
        //按钮事件
        if (key === '03') {
            //返回
            backToList();
        } else if (key === '17') {
            //打印
            if (printFormType.value == '') {
                ElMessage({
                    type: 'error',
                    message: t('未配置打印配置'),
                    offset: 65,
                    appendTo: '.csDocument-container'
                });
                return;
            }
            if (printFormType.value == '2') {
                //打印表单
                printUrl.value =
                    import.meta.env.VUE_APP_HOST_INDEX +
                    'print?formId=' +
                    basicData.value.printFormId +
                    '&processSerialNumber=' +
                    basicData.value.processSerialNumber +
                    '&processDefinitionId=' +
                    basicData.value.processDefinitionId +
                    '&taskDefKey=' +
                    basicData.value.taskDefKey +
                    '&itemId=' +
                    basicData.value.itemId +
                    '&activitiUser=' +
                    basicData.value.activitiUser +
                    '&processInstanceId=' +
                    basicData.value.processInstanceId;
                window.open(printUrl.value);
            }
        } else if (key === '18') {
            //抄送
            Object.assign(dialogConfig.value, {
                show: true,
                width: '50%',
                title: computed(() => t('人员选择')),
                type: 'csUserChoise',
                showFooter: false
            });
        }
    }

    function delFollow() {
        //取消关注
        delOfficeFollow(basicData.value.processInstanceId).then((res) => {
            if (res.success) {
                emits('refreshCount');
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.csDocument-container' });
                follow.value = false;
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.csDocument-container' });
            }
        });
    }

    function saveFollow() {
        //设置关注
        saveOfficeFollow(basicData.value.processInstanceId).then((res) => {
            if (res.success) {
                emits('refreshCount');
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.csDocument-container' });
                follow.value = true;
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.csDocument-container' });
            }
        });
    }

    // 点击收缩按钮 右边出现或不出现
    function handlerClickShrink() {
        // console.log('点击');
        processFlag.value = !processFlag.value;
        sessionStorage.setItem('process', JSON.stringify(processFlag.value));
    }
</script>

<style lang="scss" scoped>
    body {
        height: 100%;
        width: 100%;
        position: absolute;
    }

    .document {
        background-color: #e7e8ec;
        height: 100%;
        position: relative;
        width: 79%;
        font: v-bind('fontSizeObj.baseFontSize') Helvetica Neue, Helvetica, PingFang SC, Tahoma, Arial, sans-serif;
    }

    .document-shrink {
        width: 100% !important;
    }

    .document .buttonDiv {
        position: absolute;
        right: 0%;
        top: 15%;
        z-index: 1;
        height: 125px;
    }

    .el-tabs__nav-wrap::after {
        height: 0px;
    }

    .document .el-tabs__active-bar {
        bottom: 5px;
        margin: 0px 15px !important;
    }

    *,
    ::after,
    ::before {
        box-sizing: content-box;
    }

    .document .el-tabs__header {
        width: 50%;
        background-color: #fff;
        border-top: 2px solid #e4e7ed;
        margin: 0px;
        height: 48px;
        line-height: 48px;
        z-index: 0;
    }

    .document .el-tabs__content {
        height: 93%;
        width: 100%;
    }

    .document .el-tabs__item {
        padding: 0px 15px;
        height: 50px;
        line-height: 50px;
    }

    .document .el-tabs__nav-scroll {
        padding-left: 15px;
    }

    .document .el-container {
        height: calc(100vh - 210px) !important;
        overflow: auto;
    }

    .tab-nav {
        width: 100%;
        height: 125px;
        position: absolute;
        top: -5%;
    }

    .shrink {
        position: absolute;
        right: 0.5%;
        top: 1.5%;
        color: var(--el-color-primary);
        cursor: pointer;
        font-size: v-bind('fontSizeObj.morerLargeFont');
    }

    .process-content {
        width: 19%;
        // height: calc(100vh - 106px);
        margin-left: 45px;
        border-left: 0px solid #aaa;
        padding: 0;

        :deep(.el-card__body) {
            height: 99%;
            padding: 0px 10px 10px 10px;
            background-color: #e7e8ec;
        }

        :deep(.el-card__header) {
            padding: calc(var(--el-card-padding) - 2px) var(--el-card-padding);
            border-bottom: 0px solid var(--el-card-border-color);
            box-sizing: border-box;
            text-align: center;
            font-weight: bold;
            background-color: #e7e8ec;
        }
    }

    :deep(.el-empty__description p) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .csDocument-container {
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    @media screen and (min-width: 1365px) and (max-width: 1860px) {
        .process-content {
            width: 28%;
        }
    }

    @media screen and (max-width: 1365px) {
        .process-content {
            width: 34%;
        }
    }
</style>
