<template>
    <el-card
        v-loading="loading"
        :element-loading-text="$t('正在处理中')"
        :style="style"
        class="speakInfo"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <el-container style="height: 100%">
            <el-main
                style="height: 70%; padding: 0px; background-color: rgb(255, 255, 255); position: relative; width: 97%"
            >
                <div ref="chatForm" style="height: 45vh; overflow: auto">
                    <template v-for="item in speakInfoListData" v-if="speakInfoListData.length > 0">
                        <div v-if="item.userId == userId" v-bind:key="item.id" class="speakInfocontent">
                            <li>
                                <span class="rightTime">{{ item.createTime }}</span>
                                <div class="rightname">
                                    <span style="margin-right: 10px">{{ item.userName }}</span>
                                    <span
                                        ><el-avatar
                                            ><img
                                                :style="{ fontSize: fontSizeObj.biggerrFontSize }"
                                                src="@/assets/avatar.png" /></el-avatar
                                    ></span>
                                </div>
                            </li>
                            <li>
                                <div style="width: 84%; float: right">
                                    <span class="rightcontent right-say-content">
                                        {{ item.content }}
                                        <!-- 三角 -->
                                        <span class="right-triangle"></span>
                                    </span>
                                </div>
                                <span class="rightname"
                                    ><i
                                        v-if="item.edited"
                                        :title="$t('点击删除')"
                                        class="el-icon-delete"
                                        style="color: red"
                                        @click="del(item.id)"
                                    ></i
                                ></span>
                            </li>
                        </div>
                        <div v-else v-bind:key="item.id" class="speakInfocontent">
                            <li style="display: flex">
                                <span class="leftname">
                                    <span
                                        ><el-avatar
                                            ><img
                                                :style="{ fontSize: fontSizeObj.biggerrFontSize }"
                                                src="@/assets/avatar.png" /></el-avatar
                                    ></span>
                                    <span style="margin-left: 10px">{{ item.userName }}</span>
                                </span>
                                <span class="leftcontent" style="margin: 12px 0 0 2px"> {{ item.createTime }}</span>
                            </li>
                            <li>
                                <!-- <span class="leftname"></span> -->
                                <!-- 对话框 -->
                                <div style="width: 84%">
                                    <span class="leftcontent distance say-content">
                                        {{ item.content }}
                                        <!-- 三角 -->
                                        <span class="triangle"></span>
                                    </span>
                                </div>
                            </li>
                        </div>
                    </template>
                    <template v-else>
                        <el-empty :description="$t('暂无数据')" />
                    </template>
                </div>
            </el-main>

            <el-footer style="height: 35%">
                <el-divider border-style="double" style="margin: 24px 0px 0px; background-color: #f0f4ff" />
                <el-row :gutter="20">
                    <el-col :span="1" style="text-align: left; padding: 10px 10px 10px 7px">
                        <el-avatar
                            ><img :style="{ fontSize: fontSizeObj.biggerrFontSize }" src="@/assets/avatar.png" />
                        </el-avatar>
                    </el-col>
                    <el-col :span="23" style="padding-left: 18px">
                        <el-input
                            v-model="content"
                            :placeholder="$t('请输入内容')"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            maxlength="200"
                            resize="none"
                            rows="4"
                            show-word-limit
                            type="textarea"
                        ></el-input>
                    </el-col>
                </el-row>
                <el-row :gutter="20" style="text-align: right">
                    <el-col :span="24">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            style="float: right"
                            type="primary"
                            @click="save()"
                            >{{ $t('提交') }}
                        </el-button>
                    </el-col>
                </el-row>
            </el-footer>
        </el-container>
    </el-card>
</template>

<script lang="ts" setup>
    import { defineProps, inject, nextTick, onMounted, reactive, watch } from 'vue';
    import { delSpeakInfo, saveSpeakInfo, speakInfoList } from '@/api/flowableUI/speakInfo';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    let style = 'height:calc(100vh - 260px) !important;overflow: auto; width: 80%;margin: auto; margin-top: 1%';
    if (settingStore.pcLayout == 'Y9Horizontal') {
        style = 'height:calc(100vh - 296px) !important; overflow: auto; width: 80%;margin: auto; margin-top: 1%';
    }
    const props = defineProps({
        processInstanceId: String,
        clickCount: Number
    });
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const data = reactive({
        loading: false,
        speakInfoListData: [],
        content: '',
        userId: '',
        chatForm: ''
    });

    let { loading, speakInfoListData, content, userId, chatForm } = toRefs(data);

    if (props.processInstanceId != undefined) {
        getSpeakInfoList();
    }

    onMounted(() => {
        scrollBottom();
    });

    watch(
        () => props.clickCount,
        (val) => {
            scrollBottom();
        },
        {
            deep: true,
            immediate: true
        }
    );

    function scrollBottom() {
        setTimeout(() => {
            nextTick(() => {
                let scroll = chatForm.value;
                scroll.scrollTop = scroll.scrollHeight;
            });
        }, 100);
    }

    function getSpeakInfoList() {
        speakInfoList(props.processInstanceId).then((res) => {
            speakInfoListData.value = res.data.rows;
            userId.value = res.data.userId;
        });
    }

    function save() {
        if (content.value === '') {
            ElMessage({ type: 'error', message: t('内容不能为空'), offset: 65, appendTo: '.speakInfo' });
            return;
        }
        loading.value = true;
        saveSpeakInfo(props.processInstanceId, content.value).then((res) => {
            loading.value = false;
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.speakInfo' });
                getSpeakInfoList();
                content.value = '';
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.speakInfo' });
            }
        });
    }

    function del(id) {
        ElMessageBox.confirm(t('是否删除该信息?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.speakInfo'
        })
            .then(() => {
                loading.value = true;
                delSpeakInfo(id).then((res) => {
                    loading.value = false;
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.speakInfo' });
                        getSpeakInfoList();
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.speakInfo' });
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.speakInfo' });
            });
    }
</script>

<style lang="scss" scoped>
    body {
        height: 100%;
        width: 100%;
        position: absolute;
    }

    .speakInfo .el-textarea {
        width: 100%;
        margin: 10px 0 10px 0;
    }

    .speakInfocontent {
        min-height: 80px;
        /* border-top: 1px solid #dcdfe6; */
        height: auto;
        margin: 0 2%;
    }

    .speakInfocontent li {
        list-style-type: none;
        min-height: 30px;
        height: auto;
        display: inline-block;
        width: 100%;
        margin-top: 8px;
    }

    .rightcontent {
        position: relative;
        color: #8b8b8b;
        display: inline-block;
        float: right;
    }

    .rightTime {
        width: 84%;
        position: relative;
        text-align: right;
        color: #8b8b8b;
        display: inline-block;
        line-height: 40px;
    }

    .rightname {
        // width: 15%;
        float: right;
        color: #6eaaf2;
        display: inline-flex;
        line-height: 40px;
    }

    .leftcontent {
        position: relative;
        text-align: left;
        // float: right;
        color: #8b8b8b;
        display: inline-block;
    }

    .distance {
        margin-top: -0.5%;
    }

    .say-content {
        border: 1px solid #eee;
        padding: 10px;
        border-radius: 5px;
        position: relative;
    }

    .right-say-content {
        border: 1px solid var(--el-color-primary);
        padding: 10px;
        border-radius: 5px;
        position: relative;
        margin: -0.5% 0% 0 0;
    }

    .right-triangle {
        width: 15px;
        border-color: var(--el-color-primary) transparent transparent var(--el-color-primary);
        border-style: solid;
        border-width: 1px;
        position: absolute;
        background-color: #fff;
        height: 15px;
        transform: rotate(45deg);
        top: -8px;
        right: 10px;
    }

    .triangle {
        width: 15px;
        border-color: #eee transparent transparent #eee;
        border-style: solid;
        border-width: 1px;
        position: absolute;
        background-color: #fff;
        top: -7px;
        left: 10px;
        height: 15px;
        transform: rotate(45deg);
    }

    .leftname {
        width: 15%;
        color: #6eaaf2;
        text-align: right;
        display: inline-block;
        display: inline-flex;
        line-height: 40px;
    }

    :deep(.el-empty__description p) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .speakInfo {
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
