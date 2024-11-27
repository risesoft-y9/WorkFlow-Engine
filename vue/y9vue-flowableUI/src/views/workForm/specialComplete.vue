<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-01-26 17:36:06
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-01-30 09:53:23
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\workForm\specialComplete.vue
-->
<template>
    <div
        v-loading="loading"
        :element-loading-text="$t('正在处理中')"
        class="special-reason"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <el-divider content-position="left">{{ $t('特殊办结原因') }}</el-divider>
        <el-input
            v-model="reason"
            :placeholder="$t('请输入内容')"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            maxlength="50"
            resize="none"
            rows="4"
            show-word-limit
            style="margin-bottom: 15px"
            type="textarea"
        ></el-input>
        <div style="text-align: right; margin-bottom: 10px">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="submit()"
                ><i class="ri-check-line" style="margin-right: 4px"></i>{{ $t('提交') }}
            </el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { inject } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { buttonApi } from '@/api/flowableUI/buttonOpt';
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
        }
    });

    const router = useRouter();
    const currentrRute = useRoute();
    const data = reactive({
        loading: false,
        reason: ''
    });

    let { loading, reason } = toRefs(data);

    function submit() {
        if (reason.value == '') {
            ElMessage({ type: 'error', message: t('请输入办结原因'), offset: 65, appendTo: '.special-reason' });
            return;
        }
        loading.value = true;
        buttonApi.specialComplete(props.basicData.taskId, reason.value).then((res) => {
            loading.value = false;
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.special-reason' });
                let link = currentrRute.matched[0].path;
                let query = {
                    itemId: props.basicData.itemId,
                    refreshCount: true
                };
                let listType = currentrRute.query.listType;
                let path = link + '/' + listType;
                // if(link.indexOf('workIndex') > -1){
                //   path = link + '/monitorBanjian';
                // }
                router.push({
                    //核心语句
                    path: path, //跳转的路径
                    query: query //路由传参时push和query搭配使用 ，作用时传递参数
                });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.special-reason' });
            }
        });
    }
</script>

<style scoped>
    :deep(.el-divider__text.is-left) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    /*message */
    .special-reason {
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
</style>
