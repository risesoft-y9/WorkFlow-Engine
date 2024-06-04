<template>
    <el-container style="height: 100%; width: 100%" class="opinion-container">
        <el-header style="width: 100%; height: auto; overflow: hidden">
            <div>
                <div style="margin: 8px 0">
                    <span>{{ $t(title) }}</span>
                    <i
                        v-if="msgshow"
                        class="el-icon-warning-outline"
                        :title="$t('请先点击新建或编辑意见')"
                        style="color: red"
                    ></i>
                </div>
                <el-input
                    ref="opinioninput"
                    type="textarea"
                    resize="none"
                    :disabled="disabled"
                    :rows="8"
                    :placeholder="$t('请输入内容')"
                    v-model="opinionContent"
                    maxlength="200"
                    show-word-limit
                ></el-input>
                <div style="text-align: right; margin-top: 5px">
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        style="margin-left: 15px"
                        @click="saveOrUpdateOpinion()"
                        :disabled="disabled"
                        >{{ $t('保存') }}</el-button
                    >
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="saveCommon()"
                        >{{ $t('存为常用语') }}</el-button
                    >
                </div>
            </div>
        </el-header>
        <el-main style="width: 100%; height: 40%; padding: 0 20px">
            <div style="margin-bottom: 10px">
                <span>{{ $t('请选择意见') }}</span>
                <el-button
                    style="padding: 7px 7px; margin-left: 8px"
                    @click="setCommonManage()"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    >{{ $t('常用语设置') }}</el-button
                >
            </div>
            <div class="comment" style="background-color: #fff; height: 70%; overflow: auto">
                <template v-for="item in commonSentencesData" v-bind:[key]="item.id">
                    <li @dblclick="selectComment($event)">{{ item.content }}</li>
                </template>
            </div>
        </el-main>
        <y9Dialog v-model:config="dialogConfig">
            <addUser v-if="dialogConfig.type == 'addUser'" ref="addUserRef" />
        </y9Dialog>
    </el-container>
</template>

<script lang="ts" setup>
    import { inject, computed } from 'vue';
    import {
        commonSentencesList,
        saveCommonSentences,
        personalComment,
        saveOpinion,
        delOpinion
    } from '@/api/flowableUI/opinion';
    import { changeChaoSongState } from '@/api/flowableUI/chaoSong';
    import addUser from '@/views/opinion/addUser.vue';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        processInstanceId: String
    });

    const emits = defineEmits(['childFunction']);

    const data = reactive({
        msgshow: true,
        title: '添加意见',
        disabled: true,
        loading: false,
        selectTime: '',
        opinionContent: '',
        commonSentencesData: [],
        opinionId: '',
        opinionFrameMark: '',
        userId: '',
        userName: '',
        deptId: '',
        basicData: {},
        opinioninput: '',
        opinionModel: {},
        opinionData: {},
        oldContent: '',
        addUserRef: '',
        commonManageRef: '',
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let treeData = addUserRef.value.treeSelectedData;
                    if (treeData.length == 0) {
                        ElMessage({ type: 'error', message: t('请选择人员'), offset: 65, appendTo: '.opinion-container' });
                        reject();
                    }
                    if (treeData.length > 1) {
                        ElMessage({
                            type: 'error',
                            message: t('只能选择一个人员'),
                            offset: 65,
                            appendTo: '.opinion-container'
                        });
                        reject();
                    }
                    userId.value = treeData[0].id;
                    userName.value = treeData[0].name;
                    deptId.value = treeData[0].parentId;

                    resolve();
                });
            },
            visibleChange: (visible) => {}
        }
    });

    let {
        msgshow,
        title,
        disabled,
        loading,
        selectTime,
        opinionContent,
        commonSentencesData,
        opinionId,
        opinionFrameMark,
        userId,
        userName,
        deptId,
        basicData,
        opinioninput,
        opinionModel,
        opinionData,
        oldContent,
        addUserRef,
        commonManageRef,
        dialogConfig
    } = toRefs(data);

    getCommonSentences();

    function getCommonSentences() {
        commonSentencesList().then((res) => {
            commonSentencesData.value = res.data;
        });
    }

    function cleardata() {
        opinionId.value = '';
        opinionContent.value = '';
        opinionModel.value = {};
        userName.value = '';
        userId.value = '';
        deptId.value = '';
        oldContent.value = '';
    }

    function addOpinion(data, baseData) {
        disabled.value = false;
        msgshow.value = false;
        basicData.value = baseData;
        opinionFrameMark.value = data.opinionFrameMark;
        cleardata();
        opinionData.value = data;
        personalComment(opinionId.value).then((res) => {
            opinioninput.value.focus();
            if (res.success) {
                selectTime.value = res.data.date;
            }
            title.value = '添加意见';
        });
    }

    function editOpinion(data, baseData) {
        disabled.value = false;
        msgshow.value = false;
        basicData.value = baseData;
        opinionFrameMark.value = data.opinionFrameMark;
        opinionId.value = data.opinionId;
        opinionData.value = data;
        personalComment(opinionId.value).then((res) => {
            opinioninput.value.focus();
            if (res.success) {
                opinionModel.value = res.data.opinion;
                opinionContent.value = opinionModel.value.content;
                userName.value = opinionModel.value.userName;
                userId.value = opinionModel.value.userId;
                deptId.value = opinionModel.value.deptId;
                selectTime.value = res.data.date;
                oldContent.value = res.data.opinion.content;
            }
            title.value = '编辑意见';
        });
    }

    function deleteOpinion(data, baseData) {
        basicData.value = baseData;
        ElMessageBox.confirm(t('确定删除该意见?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.opinion-container'
        })
            .then(() => {
                delOpinion(data.opinionId).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.opinion-container' });
                        if (data.opinionId == opinionId.value) {
                            msgshow.value = true;
                            disabled.value = true;
                            title.value = '添加意见';
                            cleardata();
                        }
                        emits('childFunction', data); //删除成功，发送重新加载意见框消息
                        if (basicData.value.itembox == 'yuejian') {
                            //抄送件删除意见后，更新批阅件状态
                            changeChaoSongState(basicData.value.chaosongId, 'delete');
                        }
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.opinion-container' });
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.opinion-container' });
            });
    }

    function saveChange() {
        //保存未保存的意见内容
        if (opinionContent.value != oldContent.value) {
            saveOrUpdateOpinion();
        }
    }

    function saveOrUpdateOpinion() {
        if (opinionId.value == '') {
            opinionModel.value.id = opinionId.value;
            opinionModel.value.opinionFrameMark = opinionFrameMark.value;
            opinionModel.value.processSerialNumber = basicData.value.processSerialNumber;
            opinionModel.value.processInstanceId = basicData.value.processInstanceId;
            opinionModel.value.taskId = basicData.value.taskId;
            opinionModel.value.userId = userId.value;
            opinionModel.value.deptId = deptId.value;
            opinionModel.value.createDate = selectTime.value;
        }
        opinionModel.value.userId = userId.value;
        opinionModel.value.deptId = deptId.value;
        opinionModel.value.createDate = selectTime.value;
        opinionModel.value.content = opinionContent.value;
        if (opinionContent.value == '') {
            ElMessage({ type: 'error', message: t('内容不能为空'), offset: 65, appendTo: '.opinion-container' });
            return;
        }
        let jsonData = JSON.stringify(opinionModel.value).toString();

        saveOpinion(jsonData).then((res) => {
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, appendTo: '.opinion-container' });
                emits('childFunction', opinionData.value);
                if (opinionId.value == '') {
                    opinionId.value = res.data.id;
                    opinionModel.value = res.data;
                    title.value = '编辑意见';
                }
                oldContent.value = opinionModel.value.content;
                if (basicData.value.itembox == 'yuejian') {
                    //抄送件填写意见后，更新批阅件状态
                    changeChaoSongState(basicData.value.chaosongId, 'add');
                }
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.opinion-container' });
            }
        });
    }

    function selectComment(e) {
        opinionContent.value = opinionContent.value + e.target.innerHTML;
    }

    function setCommonManage() {
        Object.assign(dialogConfig.value, {
            show: true,
            width: '35%',
            title: computed(() => t('常用语设置')),
            type: 'opinion',
            showFooter: false
        });
    }

    function saveCommon() {
        if (opinionContent.value === '') {
            ElMessage({ type: 'error', message: t('内容不能为空'), offset: 65, appendTo: '.opinion-container' });
            return;
        }
        saveCommonSentences(opinionContent.value).then((res) => {
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.opinion-container' });
                getCommonSentences();
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.opinion-container' });
            }
        });
    }

    function addDaiLuUser() {
        Object.assign(dialogConfig.value, {
            show: true,
            width: '20%',
            title: computed(() => t('选择人员')),
            type: 'addUser',
            showFooter: true
        });
    }

    defineExpose({
        addOpinion,
        editOpinion,
        deleteOpinion,
        saveChange
    });
</script>

<style>
    .comment li {
        padding: 10px 0 10px 10px;
        list-style-type: none;
    }
    .comment li:hover {
        background-color: #eee;
    }
    .comment .left_comment {
        display: inline-block;
        width: 60%;
    }
    .comment .right_button {
        display: inline-block;
    }
    .ishide {
        display: none;
    }
    .dailu .el-input {
        width: 55%;
        margin-left: 10px;
    }
</style>

<style scoped lang="scss">
    .opinion-container {
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
    }
</style>
