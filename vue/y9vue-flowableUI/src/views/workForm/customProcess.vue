<template>
    <div
        v-loading="loading"
        :element-loading-text="$t('正在发送中')"
        class="block"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <el-timeline>
            <template v-for="(task, index) in taskList">
                <el-timeline-item v-if="index == 0" v-bind:key="index" :timestamp="task.taskName" placement="top">
                    <el-card>
                        <el-select
                            v-if="task.add"
                            v-model="taskKey"
                            :placeholder="$t('添加节点')"
                            :size="fontSizeObj.buttonSize"
                            style="margin-bottom: 10px"
                            @change="addTask(index)"
                        >
                            <el-option
                                v-for="item in taskNodes"
                                :key="item.taskDefKey"
                                :label="item.taskDefName"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                :value="item.taskDefKey"
                            >
                            </el-option>
                        </el-select>
                        <el-divider content-position="left">{{ $t('办理人') }}</el-divider>
                        <el-tag key="tag" :disable-transitions="false" type="info" @close="handleClose(tag)">
                            {{ task.orgName }}
                        </el-tag>
                    </el-card>
                </el-timeline-item>
                <el-timeline-item v-else :[key]="index" :timestamp="task.taskName" placement="top">
                    <el-card>
                        <div>
                            <el-select
                                v-if="task.add && task.type != 'endEvent'"
                                v-model="taskKey"
                                :placeholder="$t('添加节点')"
                                :size="fontSizeObj.buttonSize"
                                style="margin-bottom: 10px"
                                @change="addTask(index)"
                            >
                                <el-option
                                    v-for="item in taskNodes"
                                    :[key]="item.taskDefKey"
                                    :label="item.taskDefName"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    :value="item.taskDefKey"
                                >
                                </el-option>
                            </el-select>
                            <i
                                v-if="task.delete"
                                :style="{ fontSize: fontSizeObj.mediumFontSize, color: '#888', float: 'right' }"
                                :title="$t('删除任务')"
                                class="el-icon-circle-close"
                                @click="delTask(index)"
                            ></i>
                        </div>
                        <el-divider v-if="task.type != 'endEvent'" content-position="left"
                            >{{ $t('办理人')
                            }}<i
                                :style="{ fontSize: fontSizeObj.mediumFontSize }"
                                :title="$t('办理人设置')"
                                class="ri-user-settings-line"
                                @click="userSetting(task.taskKey, index, task.orgList)"
                            ></i
                        ></el-divider>
                        <span v-if="task.type == 'endEvent'">{{ $t('流程结束') }}</span>
                        <template v-for="org in task.orgList">
                            <el-tag
                                :[key]="org.id"
                                :disable-transitions="false"
                                closable
                                type="info"
                                @close="handleClose(org.id, index)"
                            >
                                {{ org.name }}
                            </el-tag>
                        </template>
                    </el-card>
                </el-timeline-item>
            </template>
        </el-timeline>
        <div style="text-align: right; margin: 15px">
            <span slot="footer" class="dialog-footer">
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    type="primary"
                    @click="saveProcess"
                    >{{ $t('确定') }}</el-button
                >
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="cancel"
                    >{{ $t('取消') }}</el-button
                >
            </span>
        </div>
    </div>
    <el-drawer
        v-model="drawer"
        :append-to-body="true"
        :title="$t('办理人选择')"
        custom-class="customDrawer"
        direction="rtl"
        size="40%"
    >
        <userChoise ref="userChoiseRef" :basicData="basicData" @set-UserChoise="setUser" />
    </el-drawer>
</template>

<script lang="ts" setup>
    import { inject } from 'vue';
    import userChoise from '@/views/workForm/userChoise4Custom.vue';
    import { useRouter } from 'vue-router';
    import { getContainEndEvent4UserTask, getTargetNodes, saveCustomProcess } from '@/api/flowableUI/buttonOpt';
    import y9_storage from '@/utils/storage';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        basicData: {
            type: Object,
            default: () => {
                return {};
            }
        },
        dialogConfig: {
            type: Object
        }
    });

    const router = useRouter();
    const data = reactive({
        userChoiseRef: '',
        y9UserInfo: {},
        drawer: false,
        loading: false,
        taskList: [],
        taskName: '',
        taskKey: '',
        processDefinitionId: '',
        taskNodes: [],
        basicData: {},
        settingIndex: 0
    });

    let {
        userChoiseRef,
        y9UserInfo,
        drawer,
        loading,
        taskList,
        taskName,
        taskKey,
        processDefinitionId,
        taskNodes,
        basicData,
        settingIndex
    } = toRefs(data);

    computed(() => {
        y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
    });

    loadData();

    function loadData() {
        basicData.value = props.basicData;
        processDefinitionId.value = props.basicData.processDefinitionId;
        taskList.value = [];
        taskNodes.value = [];
        taskName.value = '';
        taskKey.value = '';
        getTargetNodes(processDefinitionId.value, '').then((res) => {
            if (res.success) {
                taskNodes.value = res.data;
                let task = {
                    id: taskList.value.length,
                    type: taskNodes.value[0].type,
                    taskName: taskNodes.value[0].taskDefName,
                    taskKey: taskNodes.value[0].taskDefKey,
                    orgName: y9UserInfo.value.name,
                    add: true,
                    delete: false
                };
                taskList.value.push(task);
                taskNodes.value.splice(0, 1);
            }
        });
    }

    function cancel() {
        props.dialogConfig.show = false;
    }

    function addTask(index) {
        //添加任务节点
        let taskNode = {};
        taskNodes.value.forEach((element) => {
            if (element.taskDefKey == taskKey.value) {
                taskNode = element;
            }
        });
        let task = {
            id: taskList.value.length + 1,
            type: taskNode.type,
            taskName: taskNode.taskDefName,
            taskKey: taskNode.taskDefKey,
            orgList: [],
            add: true,
            delete: true
        };
        taskList.value[index].add = false;
        taskList.value[index].delete = false;
        taskList.value.push(task);
        taskName.value = '';
        taskKey.value = '';
        getTargetNodes(processDefinitionId.value, taskNode.taskDefKey).then((res) => {
            if (res.success) {
                taskNodes.value = res.data;
            }
        });
    }

    function delTask(index) {
        //删除任务节点
        ElMessageBox.confirm(t('确定删除该任务?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.block'
        })
            .then(() => {
                taskList.value.splice(index, 1);
                taskList.value[index - 1].add = true;
                taskList.value[index - 1].delete = true;
                getTargetNodes(processDefinitionId.value, taskList.value[index - 1].taskKey).then((res) => {
                    if (res.success) {
                        taskNodes.value = res.data;
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消删除任务'), offset: 65, appendTo: '.block' });
            });
    }

    function userSetting(taskKey, index, orgList) {
        //办理人选择
        drawer.value = true;
        settingIndex.value = index;
        setTimeout(() => {
            userChoiseRef.value.show(taskKey, orgList);
        }, 500);
    }

    function setUser(orgList) {
        //设置办理人
        drawer.value = false;
        taskList.value.forEach(function (element, index) {
            if (index == settingIndex.value) {
                element.orgList = orgList;
            }
        });
    }

    function handleClose(id, index) {
        //删除办理人
        taskList.value.forEach(function (element, index0) {
            if (index0 == index) {
                let orgList = element.orgList;
                orgList.forEach(function (item, index1) {
                    if (item.id == id) {
                        orgList.splice(index1, 1);
                        taskList.value[index0].orgList = orgList;
                    }
                });
            }
        });
    }

    function saveProcess() {
        //保存流程定制信息并发送
        if (taskList.value[taskList.value.length - 1].type != 'endEvent') {
            getContainEndEvent4UserTask(processDefinitionId.value).then((res) => {
                if (res.success) {
                    let str = t('具有办结权限的节点如下：');
                    res.data.forEach(function (item) {
                        str += '【' + item.taskDefName + '】';
                    });
                    ElMessage({
                        type: 'error',
                        message: t('流程未配置结束节点，') + str,
                        offset: 65,
                        appendTo: '.block'
                    });
                }
            });
            return;
        }
        let taskName = '';
        for (let element of taskList.value) {
            if (element.id != 0 && element.orgList.length == 0 && element.type != 'endEvent') {
                taskName = element.taskName;
                break;
            }
        }
        if (taskName != '') {
            ElMessage({
                type: 'error',
                message: t('任务节点') + '【' + taskName + '】' + t('未配置办理人'),
                offset: 65,
                appendTo: '.block'
            });
            return;
        }
        ElMessageBox.confirm(t('确定生成该流程定制并发送?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'warning',
            appendTo: '.block'
        })
            .then(() => {
                let jsonData = JSON.stringify(taskList.value).toString();
                loading.value = true;
                saveCustomProcess(
                    basicData.value.itemId,
                    basicData.value.processSerialNumber,
                    basicData.value.processDefinitionKey,
                    jsonData
                ).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, appendTo: '.block' });
                    loading.value = false;
                    if (res.success) {
                        //let link = this.$route.matched[0].path;
                        let query = {
                            itemId: basicData.value.itemId,
                            refreshCount: true
                        };
                        // this.$router.push({
                        //   path:link + '/todo',
                        //   query: query
                        // }).catch(err => {
                        //   console.log('输出报错',err);
                        // });
                        router.push({ path: '/todo', query: query });
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消保存'), offset: 65, appendTo: '.block' });
            });
    }
</script>

<style>
    .customProcess .el-select {
        width: 80px;
    }

    .customProcess .el-input__inner {
        width: 80px;
        text-align: center;
        padding-right: 10px;
    }

    .customProcess .el-input__suffix {
        display: none;
    }

    .customProcess .el-input__inner::placeholder {
        color: #000;
    }

    .customProcess .el-divider__text {
        padding: 0px 6px;
        color: #888;
    }

    .customProcess .el-timeline-item__timestamp {
        color: #555;
    }

    .customProcess .el-dialog__body {
        padding: 15px;
    }

    .customProcess .el-timeline {
        padding-left: 10px;
    }

    .customProcess .el-tag.el-tag--info {
        color: #333;
        margin-right: 10px;
        margin-top: 5px;
    }

    .customDrawer .el-drawer__body {
        padding: 10px 20px;
    }

    .customDrawer .el-drawer__header {
        padding: 10px 20px;
        background-color: #f8f8f8;
        margin-bottom: 0px;
    }

    .customDrawer .el-drawer__header span {
        font-size: v-bind('fontSizeObj.mediumFontSize');
    }
</style>

<style scoped>
    .block {
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
