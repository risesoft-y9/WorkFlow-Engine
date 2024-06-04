<template>
    <el-container :style="style">
        <el-aside style="width: 100%; height: auto; overflow: auto; padding: 1% 0% 2% 0%">
            <y9Card :showHeader="false" style="width: 80%; height: calc(100% - 20px); margin: auto">
                <div class="margin-bottom-20">
                    <el-button type="primary" @click="myChaoSong" :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('我的抄送') }}({{ $t(mychaosongStr) }})</el-button>
                    <el-button type="primary" @click="otherChaoSong" :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('他人抄送') }}({{ $t(otherchaosongStr) }})</el-button>
                </div>
                <y9Table :config="processTableConfig">
                    <template #status="{ row, column, index }">
                        <i
                            v-if="row.newToDo == 1"
                            :title="$t('未阅')"
                            class="ri-chat-poll-line"
                            :style="{color: 'green', fontSize: fontSizeObj.mediumFontSize}"
                        ></i>
                        <i
                            v-else-if="row.startTime == '未开始'"
                            :title="$t('未开始')"
                            class="ri-chat-history-line"
                            :style="{color: 'green', fontSize: fontSizeObj.mediumFontSize}"
                        ></i>
                        <i
                            v-else-if="row.endTime == ''"
                            :title="$t('已阅，未处理')"
                            class="ri-eye-line"
                            :style="{color: 'blue', fontSize: fontSizeObj.mediumFontSize}"
                        ></i>
                        <i
                            v-else-if="row.endTime != ''"
                            :title="$t('已处理')"
                            class="ri-checkbox-circle-line"
                            :style="{fontSize: fontSizeObj.mediumFontSize}"
                        ></i>
                    </template>
                    <template #name="{ row, column, index }">
                        <font v-if="row.endFlag == '1'"
                            >{{ row.name }}<i class="ri-check-double-line" style="color: red" :title="$t('强制办结任务')"></i
                        ></font>
                        <font v-else>{{ row.name }}</font>
                    </template>
                </y9Table>
                <y9Dialog v-model:config="dialogConfig">
                    <chaoSongList ref="chaoSongListRef" :type="type" :processInstanceId="processInstanceId" />
                </y9Dialog>
            </y9Card>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
    import { ref, defineProps, onMounted, watch, reactive, inject } from 'vue';
    import chaoSongList from '@/views/chaoSong/chaoSongList.vue';
    import { historyList } from '@/api/flowableUI/process';
    import { useSettingStore } from "@/store/modules/settingStore";
    import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
    const { t } = useI18n();
    const settingStore = useSettingStore()
    let style = 'height:calc(100vh - 210px) !important; width: 100%;';
    if(settingStore.pcLayout == 'Y9Horizontal'){
        style = 'height:calc(100vh - 240px) !important; width: 100%;';
    }
    const props = defineProps({
        processInstanceId: String,
    });
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo')||{}; 
    const data = reactive({
        type: '',
        mychaosongStr: '无',
        otherchaosongStr: '无',
        mychaosongNum: 0,
        otherchaosongNum: 0,
        processTableConfig: {
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '55' },
                { title: computed(() => t('状态')), type: 'status', width: '55', slot: 'status' },
                { title: computed(() => t('办件人')), key: 'assignee', width: '170' },
                { title: computed(() => t('办理环节')), key: 'name', width: '120', slot: 'name' },
                { title: computed(() => t('意见内容')), key: 'opinion', align: 'left', width: 'auto' },
                //{ title: "开始时间", key: "startTime", width: '170', },
                //{ title: "结束时间", key: "endTime", width: '170', },
                { title: computed(() => t('办理时长')), key: 'time', align: 'left', width: '155' },
                { title: computed(() => t('描述')), key: 'description', align: 'left', width: '160' },
            ],
            tableData: [],
            pageConfig: false, //取消分页
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

    let { type, dialogConfig, mychaosongStr, otherchaosongStr, mychaosongNum, otherchaosongNum, processTableConfig } =
        toRefs(data);

    watch(
        () => props.processInstanceId,
        (newVal) => {
            reloadTable();
        }
    );

    onMounted(() => {
        reloadTable();
    });

    async function reloadTable() {
        let processInstanceId = props.processInstanceId == undefined ? '' : props.processInstanceId;
        let res = await historyList(props.processInstanceId);
        if (res.success) {
            processTableConfig.value.tableData = res.data.rows;

            mychaosongNum.value = res.data.mychaosongNum;
            if (mychaosongNum.value > 0) {
                mychaosongStr.value = '有';
            }
            otherchaosongNum.value = res.data.otherchaosongNum;
            if (otherchaosongNum.value > 0) {
                otherchaosongStr.value = '有';
            }
        }
    }

    function myChaoSong() {
        type.value = 'my';
        Object.assign(dialogConfig.value, {
            show: true,
            width: '60%',
            title: computed(() => t('我的抄送')),
            showFooter: false,
        });
    }

    function otherChaoSong() {
        type.value = 'other';
        Object.assign(dialogConfig.value, {
            show: true,
            width: '60%',
            title: computed(() => t('其他抄送')),
            showFooter: false,
        });
    }
</script>

<style>
    .el-main-table {
        padding: 0px;
    }
</style>
