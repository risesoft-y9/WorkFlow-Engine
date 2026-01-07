<!--
 * @Author: zhangchongjie
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2026-01-07 11:30:35
 * @LastEditors: mengjuhua
 * @Description:  抄送人员选择
-->
<template>
    <el-container
        v-loading="loading"
        :element-loading-text="$t('正在发送中')"
        class="csUserChoise"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
        style="height: 600px"
    >
        <el-container>
            <el-aside class="context_div" width="50%">
                <csTree ref="csTreeRef" :basicData="basicData" @onTreeDbClick="selectedNode" />
            </el-aside>
            <div class="context_div move_center_div">
                <!-- <el-button type="primary" size="small" style="width:80%;margin-left:10%;height:35px;" @click="selectedToRight">右移<i class="el-icon-d-arrow-right"></i></el-button> -->
                <el-divider direction="vertical" />
                <div class="moveRight" @click="selectedToRight">{{ $t('右移') }}</div>
                <el-divider direction="vertical" />
            </div>
            <el-main class="context_div" width="38.2%">
                <el-tabs v-model="activeName" class="usertab" @tab-click="tabclick">
                    <el-tab-pane :label="$t('收件人')" name="addressee" style="height: 480px">
                        <div style="height: 94%">
                            <el-table
                                :data="userChoice"
                                header-row-class-name="table-header"
                                height="480"
                                style="width: 100%"
                                @row-dblclick="delPerson"
                            >
                                <el-table-column :label="$t('名称')" prop="name" width="auto">
                                    <template #default="name_cell">
                                        <i
                                            v-if="name_cell.row.type == 'Person' && name_cell.row.sex == '0'"
                                            class="ri-women-line"
                                            style="vertical-align: middle"
                                        ></i>
                                        <i
                                            v-else-if="name_cell.row.type == 'Person' && name_cell.row.sex == '1'"
                                            class="ri-men-line"
                                            style="vertical-align: middle"
                                        ></i>
                                        <i
                                            v-else-if="name_cell.row.type == 'Position'"
                                            class="ri-shield-user-line"
                                            style="vertical-align: middle"
                                        ></i>
                                        <i
                                            v-else-if="name_cell.row.type == 'customGroup'"
                                            class="ri-shield-star-line"
                                            style="vertical-align: middle"
                                        ></i>
                                        <i
                                            v-else-if="name_cell.row.type == 'Department'"
                                            class="ri-slack-line"
                                            style="vertical-align: middle"
                                        ></i>
                                        {{ name_cell.row.name }}
                                    </template>
                                </el-table-column>
                                <el-table-column :label="$t('操作')" align="center" prop="opt" width="85">
                                    <template #default="opt_cell">
                                        <i
                                            :style="{
                                                fontSize: fontSizeObj.largeFontSize,
                                                color: '#586cb1',
                                                cursor: 'pointer'
                                            }"
                                            class="ri-close-line"
                                            @click="delPerson(opt_cell.row)"
                                        ></i>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane :label="$t('短信提醒')" name="SMSReminder" style="height: 525px">
                        <el-checkbox v-model="awoke">{{ $t('短信提醒') }}</el-checkbox>
                        <el-checkbox v-model="awokeShuMing" @change="addShuMing">{{ $t('是否添加署名') }}</el-checkbox>
                        <table class="table-input" style="width: 100%; border-spacing: 0">
                            <tbody>
                                <tr>
                                    <td style="border: 1px solid rgb(220, 223, 230)">
                                        <el-input
                                            v-model="awokeText"
                                            :placeholder="$t('请输入内容')"
                                            :rows="19"
                                            maxlength="100"
                                            resize="none"
                                            type="textarea"
                                        ></el-input>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="border: 1px solid rgb(220, 223, 230); border-top-width: 0px">
                                        <el-input
                                            v-model="lastfixSmsContext"
                                            :placeholder="$t('是否署名')"
                                            :readonly="true"
                                            style="
                                                box-shadow: 0 0 0 0px
                                                    var(--el-input-border-color, var(--el-border-color)) inset;
                                            "
                                        ></el-input>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </el-tab-pane>
                </el-tabs>
            </el-main>
        </el-container>
        <el-footer style="text-align: right; padding: 10px 0px">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="send()"
            >
                <i :style="{ fontSize: fontSizeObj.mediumFontSize }" class="ri-share-forward-line"></i>{{ $t('发送') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                plain
                type="primary"
                @click="resetUser()"
            >
                <i :style="{ fontSize: fontSizeObj.mediumFontSize }" class="ri-delete-bin-line"></i>{{ $t('清空') }}
            </el-button>
        </el-footer>
    </el-container>
</template>

<script lang="ts" setup>
    import { inject, reactive, ref, toRefs } from 'vue';
    import csTree from '@/views/chaoSong/dialogContent/csPersonTree.vue';
    import { chaoSongSave } from '@/api/flowableUI/chaoSong';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        basicData: {
            type: Object,
            default: () => {
                return {};
            }
        },
        dialogConfig: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const emits = defineEmits(['csRefreshCount', 'update-BasicData']);
    const flowableStore = useFlowableStore();

    let csTreeRef = ref();
    const data = reactive({
        loading: false,
        activeName: 'addressee',
        awoke: 'false',
        awokeShuMing: 'false',
        awokeText: '',
        lastfixSmsContext: '',
        smsPersonId: '',
        userChoice: []
    });

    let { loading, activeName, awoke, awokeShuMing, awokeText, lastfixSmsContext, smsPersonId, userChoice } =
        toRefs(data);

    function tabclick() {
        awokeText.value = t('您已收到一份标题为【') + flowableStore.getDocumentTitle + t('】的阅件，请及时办理。');
    }

    function addShuMing(val) {
        if (val) {
            lastfixSmsContext.value = '-- 姓名';
        } else {
            lastfixSmsContext.value = '';
        }
    }

    function selectedToRight() {
        // let checkNode = csTreeRef.value.checkNodes;
        let checkNode = csTreeRef.value?.y9TreeRef?.getCheckedNodes();
        if (checkNode.length === 0) {
            ElMessage({ type: 'error', message: t('请勾选科室或收件人'), offset: 65, appendTo: '.csUserChoise' });
            return;
        }
        for (let i = 0; i < checkNode.length; i++) {
            selectedNode(checkNode[i]);
        }
    }

    function selectedNode(checkNode) {
        let user: any = {};
        user.name = checkNode.name;
        user.id = checkNode.id;
        user.type = checkNode.orgType;
        user.sex = checkNode.sex;
        user.opt = '';
        let ischeck = true;
        for (let j = 0; j < userChoice.value.length; j++) {
            if (userChoice.value[j].id === user.id) {
                ischeck = false;
            }
        }
        if (ischeck) {
            userChoice.value.push(user);
        }
    }

    function resetUser() {
        userChoice.value = [];
        csTreeRef.value.checkNodes = [];
    }

    function delPerson(row) {
        let newuserChoice = [];
        for (let j = 0; j < userChoice.value.length; j++) {
            if (userChoice.value[j].id != row.id) {
                newuserChoice.push(userChoice.value[j]);
            }
        }
        userChoice.value = newuserChoice;
    }

    function send() {
        if (userChoice.value.length == 0) {
            ElMessage({ type: 'error', message: t('请选择收件人'), offset: 65, appendTo: '.csUserChoise' });
            return;
        }
        let userChoiceId: any = [];
        for (let item of userChoice.value) {
            let id = '';
            if (item.type == 'Person') {
                id = '3:' + item.id;
            } else if (item.type == 'Department') {
                id = '2:' + item.id;
            } else if (item.type == 'customGroup') {
                id = '7:' + item.id;
            } else if (item.type == 'Position') {
                id = '6:' + item.id;
            }
            userChoiceId.push(id);
        }
        loading.value = true;
        chaoSongSave(
            props.basicData.processInstanceId,
            props.basicData.itemId,
            props.basicData.processSerialNumber,
            props.basicData.processDefinitionKey,
            userChoiceId.join(';'),
            awoke.value,
            awokeShuMing.value,
            awokeText.value,
            smsPersonId.value
        )
            .then((res) => {
                loading.value = false;
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65 });
                    if (props.basicData.processInstanceId == '' || props.basicData.processInstanceId == undefined) {
                        //新建就抄送，处理启动流程后返回的流程实例id
                        let baseData = {
                            processInstanceId: res.data.processInstanceId,
                            taskId: res.data.taskId,
                            itembox: 'todo'
                        };
                        emits('update-BasicData', baseData);
                    }
                    emits('csRefreshCount');
                    props.dialogConfig.show = false;
                } else {
                    ElMessage({ type: 'error', message: res.msg, offset: 65 });
                }
            })
            .catch((err) => {
                loading.value = false;
                ElMessage({ type: 'info', message: t('发生异常'), offset: 65 });
            });
    }
</script>

<style lang="scss">
    .csUserChoise {
        .el-main {
            background-color: #fff;
            border: 0px solid #ccc;
            padding: 0;
            overflow: hidden;
        }

        .el-tabs__header {
            width: 60%;
            background-color: #fff;
            margin: 0px;
        }

        .el-divider--vertical {
            height: 220px;
        }

        .el-aside {
            background-color: #fff;
            border: 1px solid #ccc;
            padding: 0;
        }

        .el-tabs__nav-scroll {
            padding-left: 5px;
        }

        .el-tabs__item {
            padding: 0 10px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .el-tabs__item {
            color: #c0c4cc;
        }

        .el-checkbox {
            height: 40px;
        }

        .el-menu--horizontal > .el-menu-item {
            height: 40px;
            line-height: 40px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .el-tabs__nav-wrap::after {
            height: 0px;
            background-color: #ccc;
        }

        .el-table__cell {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .el-input {
            display: block;
            width: 100%;
        }

        .el-textarea__inner {
            padding: 0px 11px;
        }

        .el-input__wrapper {
            /* box-shadow: 0 0 0 0px var(--el-input-border-color,var(--el-border-color)) inset; */
            width: 100%;
            height: 30px;
            border-radius: 50px;
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .el-button--primary.is-plain {
            --el-button-bg-color: white;
        }

        .table-header .el-table__cell {
            background-color: #ebeef5;
            color: #9ba7d0;
        }

        .el-textarea {
            display: block;
        }

        .el-table::before {
            height: 0;
        }
    }

    .el-table td,
    .el-table th {
        padding: 6px 0;
    }

    .table-input .el-input__wrapper {
        box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
    }

    #tab-SMSReminder {
        display: none;
    }
</style>

<style lang="scss" scoped>
    :deep(.el-table__empty-text) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .csUserChoise {
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        .context_div {
            height: 525px;
            border: 0;
        }

        .move_center_div {
            padding-top: 0px;
            text-align: center;
            width: 11.8%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .moveRight {
            width: 50px;
            height: 50px;
            background: url('@/assets/youyi.png') center center no-repeat;
            background-size: 60px auto;
            line-height: 45px;
            font-size: v-bind('fontSizeObj.baseFontSize');
            color: #586cb1;
            margin-left: -0.5vw;
        }
    }
</style>
