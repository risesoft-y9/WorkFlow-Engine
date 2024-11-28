<template>
    <el-container
        v-loading="loading"
        :element-loading-text="$t('正在发送中')"
        class="userChoise"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
        style="height: 600px"
    >
        <el-header style="height: 30px; padding: 0; margin-bottom: 15px">
            <el-tooltip :content="$t(remindContent)" effect="dark" placement="top">
                <!-- <span style="display: inline-block;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;width: 100%;color:red;" >{{remindContent}}</span> -->
                <el-tag
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    size="large"
                    style="
                        background-color: #cdd3e8;
                        display: block;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        width: 100%;
                        line-height: 3vh;
                        text-indent: 0.5vw;
                    "
                >
                    <i
                        :style="{ fontSize: fontSizeObj.mediumFontSize, verticalAlign: 'middle' }"
                        class="ri-information-line"
                    ></i
                    >{{ $t(remindContent) }}
                </el-tag>
            </el-tooltip>
        </el-header>
        <el-container>
            <el-aside style="height: 480px; border: 0" width="50%">
                <personTree
                    ref="personTreeRef"
                    :basicData="basicData"
                    :fromType="fromType"
                    :reposition="repositionSign"
                    @onTreeDbClick="selectedNode"
                />
            </el-aside>
            <div
                style="
                    height: 480px;
                    padding-top: 0px;
                    text-align: center;
                    background-color: fff;
                    width: 11.8%;
                    display: flex;
                    align-items: center;
                    flex-direction: column;
                    fontsizeobjjustify-content: center;
                "
            >
                <!-- <el-button type="primary" size="small" @click="selectedToRight" style="width:80%;height:35px;">右移<i class="el-icon-d-arrow-right"></i></el-button> -->
                <el-divider direction="vertical" />
                <el-checkbox v-model="zhuxieban" :checked="false" :class="ishide" @change="zhuxiebanChange">
                    {{ $t('主协办') }}
                </el-checkbox>
                <div class="moveRight" @click="selectedToRight">{{ $t('右移') }}</div>
                <el-divider direction="vertical" />
            </div>
            <el-main style="height: 480px" width="38.2%">
                <div
                    style="
                        color: #586cb1;
                        width: 120px;
                        float: right;
                        position: relative;
                        height: 35px;
                        line-height: 35px;
                        text-align: right;
                        margin-right: 5px;
                    "
                >
                    <el-link :style="{ fontSize: fontSizeObj.baseFontSize }" :underline="false" @click="moveTop"
                        ><i class="ri-arrow-up-line" style="zindex: 1"></i>{{ $t('上移') }}
                    </el-link>
                    <el-link
                        :style="{ marginLeft: '5px', fontSize: fontSizeObj.baseFontSize }"
                        :underline="false"
                        @click="moveBottom"
                        ><i class="ri-arrow-down-line"></i>{{ $t('下移') }}
                    </el-link>
                </div>
                <el-tabs v-model="activeName" class="usertab" @tab-click="tabclick">
                    <el-tab-pane :label="$t('收件人')" name="addressee" style="height: 360px">
                        <div style="height: 94%">
                            <el-table
                                :data="userChoice"
                                header-row-class-name="table-header"
                                height="440"
                                highlight-current-row
                                style="width: 100%"
                                @row-dblclick="delPerson"
                                @current-change="handleCurrentChange"
                            >
                                <el-table-column :label="$t('名称')" prop="name" width="auto">
                                    <template #default="name_cell">
                                        <i
                                            v-if="name_cell.row.type == 'Person' && name_cell.row.sex == '0'"
                                            class="ri-women-line"
                                            style="vertical-align: middle"
                                        />
                                        <i
                                            v-else-if="name_cell.row.type == 'Person' && name_cell.row.sex == '1'"
                                            class="ri-men-line"
                                            style="vertical-align: middle"
                                        />
                                        <i
                                            v-else-if="name_cell.row.type == 'Position'"
                                            class="ri-shield-user-line"
                                            style="vertical-align: middle"
                                        />
                                        <i
                                            v-else-if="name_cell.row.type == 'customGroup'"
                                            class="ri-shield-star-line"
                                            style="vertical-align: middle"
                                        />
                                        <i
                                            v-else-if="name_cell.row.type == 'Department'"
                                            class="ri-slack-line"
                                            style="vertical-align: middle"
                                        />
                                        {{ name_cell.row.name }}
                                    </template>
                                </el-table-column>
                                <el-table-column
                                    v-if="
                                        userChoiseData.multiInstance == 'parallel' ||
                                        userChoiseData.multiInstance == 'sequential'
                                    "
                                    :label="$t(optlabel)"
                                    align="center"
                                    prop="zxbSign"
                                    width="85"
                                >
                                    <template #default="zxbSign_cell">
                                        <el-link
                                            v-if="
                                                zxbSign_cell.row.zxbSign == '主办' || zxbSign_cell.row.zxbSign == '协办'
                                            "
                                            :title="$t('点击设为主办')"
                                            :underline="false"
                                            type="primary"
                                            @click="setSponsor(zxbSign_cell.row)"
                                        >
                                            <span v-if="zxbSign_cell.row.zxbSign == '主办'" style="color: red">{{
                                                zxbSign_cell.row.zxbSign
                                            }}</span>
                                            <span v-if="zxbSign_cell.row.zxbSign == '协办'" style="color: #586cb1">{{
                                                zxbSign_cell.row.zxbSign
                                            }}</span>
                                        </el-link>
                                        <font v-else>{{ zxbSign_cell.row.zxbSign }}</font>
                                    </template>
                                </el-table-column>
                                <el-table-column :label="$t('操作')" align="center" prop="opt" width="85">
                                    <template #default="opt_cell">
                                        <i
                                            :style="{
                                                fontSize: fontSizeObj.mediumFontSize,
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
                    <el-tab-pane :label="$t('短信提醒')" name="SMSReminder" style="height: 360px">
                        <el-checkbox v-model="awoke">{{ $t('短信提醒') }}</el-checkbox>
                        <el-checkbox v-model="awokeShuMing" @change="addShuMing">{{ $t('是否添加署名') }}</el-checkbox>
                        <table class="table-input" style="width: 100%; border-spacing: 0">
                            <tr>
                                <td style="border: 1px solid rgb(220, 223, 230)">
                                    <el-input
                                        v-model="awokeText"
                                        :placeholder="$t('请输入内容')"
                                        :rows="17"
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
                                            box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color))
                                                inset;
                                        "
                                    ></el-input>
                                </td>
                            </tr>
                        </table>
                    </el-tab-pane>
                    <!-- <el-button size="small" class="send_button" @click="send()" style="width:100%;height:35px;">发送</el-button>
                    <el-button size="small" class="reset_button" @click="resetUser()" style="width:100%;height:35px;">清空</el-button> -->
                </el-tabs>
            </el-main>
        </el-container>
        <el-footer style="text-align: right; padding: 10px 0px">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="send()"
                ><i :style="{ fontSize: fontSizeObj.mediumFontSize }" class="ri-share-forward-line"></i>{{ $t('发送') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                type="primary"
                @click="saveQuickSend()"
                ><i :style="{ fontSize: fontSizeObj.mediumFontSize }" class="ri-user-star-line"></i
                >{{ $t('存为快捷发送人') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                plain
                type="primary"
                @click="resetUser()"
                ><i :style="{ fontSize: fontSizeObj.mediumFontSize }" class="ri-delete-bin-line"></i>{{ $t('清空') }}
            </el-button>
        </el-footer>
    </el-container>
</template>

<script lang="ts" setup>
    import { inject } from 'vue';
    import personTree from '@/views/workForm/personTree.vue';
    import { getUserChoiseData, getUserCount } from '@/api/flowableUI/userChoise';
    import { forwarding, reposition } from '@/api/flowableUI/buttonOpt';
    import { addExecutionId } from '@/api/flowableUI/multiInstance';
    import { getAssignee, saveOrUpdate } from '@/api/flowableUI/quickSend';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const currentrRute = useRoute();
    const props = defineProps({
        dialogConfig: {
            type: Object,
            default: () => {
                return {};
            }
        },
        basicData: {
            type: Object,
            default: () => {
                return {};
            }
        },
        instanceData: {
            type: Object,
            default: () => {
                return {};
            }
        },
        routeToTask: String,
        reposition: String,
        fromType: String //加减签选人
    });

    const emits = defineEmits(['refreshData', 'reloadTable']);
    const flowableStore = useFlowableStore();
    const router = useRouter();
    const data = reactive({
        personTreeRef: '',
        loading: false,
        zhuxieban: false,
        processSerialNumber: '',
        processInstanceId: '',
        taskId: '',
        activeName: 'addressee',
        routeToTask: '',
        awoke: 'false',
        awokeShuMing: 'false',
        awokeText: '',
        lastfixSmsContext: '',
        remindContent: '',
        userChoiseData: {},
        userChoice: [],
        ishide: 'ishide',
        optlabel: '',
        repositionSign: '', //重定位选人
        moveShow: false, //上下移动显示
        currentRow: null
    });

    let {
        personTreeRef,
        loading,
        zhuxieban,
        processSerialNumber,
        processInstanceId,
        taskId,
        activeName,
        routeToTask,
        awoke,
        awokeShuMing,
        awokeText,
        lastfixSmsContext,
        remindContent,
        userChoiseData,
        userChoice,
        ishide,
        optlabel,
        repositionSign,
        moveShow,
        currentRow
    } = toRefs(data);

    function handleCurrentChange(val) {
        currentRow.value = val;
    }

    function tabclick() {
        awokeText.value = '您已收到一份标题为【' + flowableStore!.getDocumentTitle! + '】的办件，请及时办理。';
    }

    show();

    async function show() {
        repositionSign.value = props.reposition;
        routeToTask.value = props.routeToTask;
        userChoice.value = [];
        currentRow.value = null;
        moveShow.value = false;

        await getUserChoiseData(
            props.basicData.itemId,
            routeToTask.value,
            props.basicData.processDefinitionId,
            props.basicData.taskId,
            props.basicData.processInstanceId
        ).then((res) => {
            userChoiseData.value = res.data;
            ishide.value = 'ishide';
            optlabel.value = '';
            if (userChoiseData.value.multiInstance === 'parallel') {
                ishide.value = '';
                if (zhuxieban.value) {
                    optlabel.value = '主协办';
                }
                remindContent.value =
                    '办理说明：当前办理方式为并行办理，下一任务的收件人为多个人，这些收件人可以同时办理当前任务。';
            } else if (userChoiseData.value.multiInstance === 'sequential') {
                moveShow.value = true;
                optlabel.value = '办理顺序';
                remindContent.value =
                    '办理说明：当前办理方式为串行办理，下一任务的收件人为多个人，这些收件人可以顺序办理当前任务。';
            } else if (userChoiseData.value.multiInstance === 'subProcess') {
                remindContent.value =
                    '办理说明：当前办理方式为子流程，下一任务的收件人为多个人，这些收件人可以各自办理当前任务。';
            } else {
                remindContent.value =
                    '办理说明：当前办理方式为单人办理，下一任务的收件人若为多人，这些收件人可以进行抢占签收办理。';
            }
            personTreeRef.value.findPermUser(userChoiseData);
        });
        getAssignee(props.basicData.itemId, routeToTask.value).then((res) => {
            if (res.success) {
                for (let checkNode of res.data) {
                    selectedNode(checkNode);
                }
            }
        });
    }

    async function saveQuickSend() {
        let userChoiceId = [];
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
        let res = await saveOrUpdate(props.basicData.itemId, routeToTask.value, userChoiceId.join(','));
        ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65, appendTo: '.userChoise' });
    }

    function zhuxiebanChange(val) {
        //主协办勾选
        if (val) {
            optlabel.value = '主协办';
            for (let j = 0; j < userChoice.value.length; j++) {
                if (j === 0) {
                    userChoice.value[j].zxbSign = t('主办');
                } else {
                    userChoice.value[j].zxbSign = t('协办');
                }
            }
        } else {
            optlabel.value = '';
            for (let j = 0; j < userChoice.value.length; j++) {
                userChoice.value[j].zxbSign = '';
            }
        }
    }

    function selectedToRight() {
        //右移
        //let checkNode = personTreeRef.value.checkNodes;
        let checkNode = personTreeRef.value?.y9TreeRef?.getCheckedNodes();
        if (checkNode.length === 0) {
            ElMessage({ type: 'error', message: t('请勾选科室或收件人'), offset: 65, appendTo: '.userChoise' });
            return;
        }
        if (userChoiseData.value.multiInstance == 'common') {
            if (checkNode.length > 1) {
                ElMessage({
                    type: 'error',
                    message: t('当前办理方式为单人办理，只能选择一个办理人！'),
                    offset: 65,
                    appendTo: '.userChoise'
                });
                return;
            }
        }
        for (let i = 0; i < checkNode.length; i++) {
            selectedNode(checkNode[i]);
        }
    }

    function selectedNode(checkNode) {
        //单击选择人员
        let user = {};
        user.name = checkNode.name;
        user.id = checkNode.id;
        user.type = checkNode.orgType;
        user.sex = checkNode.sex;
        user.zxbSign = '';
        user.index = userChoice.value.length + 1;
        let ischeck = true;
        //单人节点,报销业务负责人审核,重定位选人，加减签不能选择部门,用户组
        if (
            userChoiseData.value.multiInstance == 'common' ||
            'yewufuzerenshenhe' == routeToTask.value ||
            repositionSign.value == 'reposition' ||
            props.fromType == '加减签'
        ) {
            if (user.type == 'Department' || user.type == 'customGroup') {
                return;
            }
            //单人节点,报销业务负责人审核只能选择单人
            if (userChoiseData.value.multiInstance == 'common' || 'yewufuzerenshenhe' == routeToTask.value) {
                userChoice.value = [];
            }
        }
        if (userChoice.value.length === 0) {
            if (optlabel.value == '主协办') {
                user.zxbSign = t('主办');
            } else if (optlabel.value == '办理顺序') {
                user.zxbSign = '1';
            }
            userChoice.value.push(user);
        } else {
            if (optlabel.value == '主协办') {
                user.zxbSign = t('协办');
            } else if (optlabel.value == '办理顺序') {
                user.zxbSign = (userChoice.value.length + 1).toString();
            }
            for (let j = 0; j < userChoice.value.length; j++) {
                if (userChoice.value[j].id === user.id) {
                    ischeck = false;
                }
            }
            if (ischeck) {
                userChoice.value.push(user);
            }
        }
        emits('refreshData', personTreeRef.value.checkNodes);
    }

    function setSponsor(row) {
        //设置主办
        for (let j = 0; j < userChoice.value.length; j++) {
            userChoice.value[j].zxbSign = t('协办');
            if (userChoice.value[j].id === row.id) {
                userChoice.value[j].zxbSign = t('主办');
            }
        }
    }

    function addShuMing(val) {
        //添加署名
        if (val) {
            lastfixSmsContext.value = '-- ' + userChoiseData.value.userName;
        } else {
            lastfixSmsContext.value = '';
        }
    }

    function resetUser() {
        //重置选人
        userChoice.value = [];
        personTree.value.checkNodes = [];
    }

    function delPerson(row) {
        //双击删除选人
        let newuserChoice = [];
        for (let item of userChoice.value) {
            if (item.id != row.id) {
                if (optlabel.value == '办理顺序') {
                    item.zxbSign = (newuserChoice.length + 1).toString();
                } else if (optlabel.value == '主协办' && row.zxbSign == '主办') {
                    if (newuserChoice.length == 0) {
                        item.zxbSign = t('主办');
                    }
                }
                item.index = newuserChoice.length + 1;
                newuserChoice.push(item);
            }
        }
        userChoice.value = newuserChoice;
    }

    function send() {
        //发送
        if (userChoice.value.length == 0) {
            ElMessage({ type: 'error', message: t('请选择收件人'), offset: 65, appendTo: '.userChoise' });
            return;
        }
        let userChoiceId = [];
        let sponsorGuid = '';
        if (props.fromType == '加减签') {
            for (let j = 0; j < userChoice.value.length; j++) {
                let id = userChoice.value[j].id;
                userChoiceId.push(id);
            }
            loading.value = true;
            addExecutionId(
                props.basicData.processInstanceId,
                props.instanceData.executionId,
                props.basicData.taskId,
                userChoiceId.join(';'),
                props.instanceData.assigneeId,
                props.instanceData.num,
                awoke.value,
                awokeShuMing.value,
                awokeText.value
            ).then((res) => {
                loading.value = false;
                if (res.success) {
                    ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.userChoise' });
                    emits('reloadTable');
                    props.dialogConfig.show = false;
                } else {
                    ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.userChoise' });
                }
            });
        } else {
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
                if (item.zxbSign == '主办') {
                    sponsorGuid = id;
                }
            }
            if (repositionSign.value == 'reposition') {
                //重定位发送
                repositionSend(userChoiceId, sponsorGuid);
                return;
            }
            getUserCount(userChoiceId.join(';')).then((res) => {
                if (res.success) {
                    if (res.data > 100) {
                        ElMessage({
                            type: 'error',
                            message: t('发送人数不能超过100人'),
                            offset: 65,
                            appendTo: '.userChoise'
                        });
                        return;
                    }
                    let link = currentrRute.matched[0].path;
                    loading.value = true;
                    forwarding(
                        props.basicData.itemId,
                        props.basicData.processInstanceId,
                        props.basicData.taskId,
                        props.basicData.processDefinitionKey,
                        props.basicData.processSerialNumber,
                        props.basicData.sponsorHandle,
                        userChoiceId.join(';'),
                        sponsorGuid,
                        routeToTask.value,
                        awoke.value,
                        awokeShuMing.value,
                        awokeText.value
                    ).then((res) => {
                        loading.value = false;
                        if (res.success) {
                            ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.userChoise' });
                            let query = {
                                itemId: props.basicData.itemId,
                                refreshCount: true
                            };

                            router.push({ path: link + '/todo', query: query });
                        } else {
                            ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.userChoise' });
                        }
                    });
                }
            });
        }
    }

    function repositionSend(userChoiceId, sponsorGuid) {
        //重定位
        loading.value = true;
        reposition(
            props.basicData.taskId,
            routeToTask.value,
            userChoiceId.join(';'),
            props.basicData.processSerialNumber,
            sponsorGuid.split(':')[1],
            awoke.value,
            awokeShuMing.value,
            awokeText.value
        ).then((res) => {
            loading.value = false;
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.userChoise' });
                let query = {
                    itemId: props.basicData.itemId,
                    refreshCount: true
                };

                router.push({ path: '/index/monitorDoing', query: query });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.userChoise' });
            }
        });
    }

    function moveBottom() {
        //下移
        if (currentRow.value != null) {
            let num = parseInt(currentRow.value.index);
            if (num != userChoice.value.length) {
                if (optlabel.value == '办理顺序') {
                    userChoice.value[num].zxbSign = num;
                    currentRow.value.zxbSign = num + 1;
                }
                userChoice.value[num].index = num;
                currentRow.value.index = num + 1;
                userChoice.value[num - 1] = userChoice.value.splice(num, 1, userChoice.value[num - 1])[0];
            }
        }
    }

    function moveTop() {
        //上移
        if (currentRow.value != null) {
            let num = parseInt(currentRow.value.index);
            if (num != 1) {
                if (optlabel.value == '办理顺序') {
                    userChoice.value[num - 2].zxbSign = num;
                    currentRow.value.zxbSign = num - 1;
                }
                userChoice.value[num - 2].index = num;
                currentRow.value.index = num - 1;
                userChoice.value[num - 2] = userChoice.value.splice(num - 1, 1, userChoice.value[num - 2])[0];
            }
        }
    }
</script>

<style>
    .userChoise .el-tabs__header {
        width: 60%;
        background-color: #fff;
        margin: 0px;
    }

    .userChoise .moveRight {
        width: 50px;
        height: 50px;
        background: url('@/assets/youyi.png') center center no-repeat;
        background-size: 60px auto;
        line-height: 45px;
        font-size: v-bind('fontSizeObj.baseFontSize');
        color: #586cb1;
        margin-left: -0.5vw;
    }

    .userChoise .el-divider--vertical {
        height: 190px;
    }

    .userChoise .el-checkbox {
        height: 40px;
    }

    .userChoise .el-textarea__inner {
        box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
        padding: 0px 11px;
    }

    .table-input .el-input__wrapper {
        box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
    }

    .userChoise .table-header .el-table__cell {
        background-color: #ebeef5;
        color: #9ba7d0;
    }

    .userChoise .el-table__cell {
        font-size: v-bind('fontSizeObj.baseFontSize');

        .el-link {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    .userChoise .el-main {
        background-color: #fff;
        border: 0px solid #ccc;
        padding: 0;
        overflow: hidden;
    }

    .userChoise .el-link [class*='el-icon-'] + span {
        margin-left: 0px;
    }

    .userChoise .el-aside {
        background-color: #fff;
        border: 1px solid #ccc;
        padding: 0;
    }

    .userChoise .el-tabs__nav-scroll {
        padding-left: 5px;
    }

    .userChoise .el-input__wrapper {
        /* box-shadow: 0 0 0 0px var(--el-input-border-color,var(--el-border-color)) inset; */
        width: 100%;
        height: 30px;
        border-radius: 50px;
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .userChoise .el-menu--horizontal > .el-menu-item {
        height: 40px;
        line-height: 40px;
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .userChoise .el-tabs__item {
        padding: 0 10px;
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .el-tabs__item {
        color: #c0c4cc;
    }

    .userChoise .el-button--small,
    .el-button--small.is-round {
        padding: 0px;
    }

    .userChoise .el-tabs__nav-wrap::after {
        height: 0px;
        background-color: #ccc;
    }

    .userChoise .el-button--primary.is-plain {
        --el-button-bg-color: white;
    }

    .userChoise .el-table td,
    .el-table th {
        padding: 6px 0;
    }

    .userChoise .el-input {
        display: block;
        width: 100%;
    }

    .userChoise .el-textarea {
        display: block;
    }

    .userChoise .el-table::before {
        height: 0;
    }

    .send_button:hover {
        background-color: #409eff;
        color: #fff;
    }

    .reset_button:hover {
        background-color: #e6a23c;
        color: #fff;
    }

    .el-checkbox__label {
        padding-left: 2px;
    }

    .ishide {
        display: none;
    }

    .userChoise .el-aside {
        /* line-height: 40px; */
        overflow: hidden;
    }

    #tab-SMSReminder {
        display: none;
    }
</style>

<style lang="scss" scoped>
    :deep(.el-table__empty-text) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
