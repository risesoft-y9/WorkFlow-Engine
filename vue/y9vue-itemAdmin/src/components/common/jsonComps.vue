<!--
 * @Author: yihong Yh599598!@#
 * @Date: 2025-07-04 10:15:59
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-07 14:45:09
 * @FilePath: \vue\y9vue-itemAdmin\src\components\common\jsonComps.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
    <div class="jsonComps" :style="{ display: paramObject.display }">
        <el-upload
            v-if="paramObject.jsonBtn == 'all' || paramObject.jsonBtn == 'import'"
            action=""
            class="upload-div"
            :show-file-list="false"
            v-bind:http-request="importJson"
            accept=".json"
        >
            <el-button class="global-btn-secord" :size="paramObject.btnSize"
                ><i class="ri-download-2-line"></i>{{ $t('导入') }}</el-button
            >
        </el-upload>
        <el-button
            v-if="paramObject.jsonBtn == 'all' || paramObject.jsonBtn == 'export'"
            :size="paramObject.btnSize"
            style="margin-left: 0px"
            class="global-btn-second"
            @click="exportJson()"
            ><i class="ri-upload-2-line"></i>导出
        </el-button>
    </div>
</template>
<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import axios from 'axios';

    const props = defineProps({
        reloadData: Function,
        paramObject: {
            //{id:接口id,type:对应后台处理的配置类型,display:"block|flex",jsonBtn:'all|import|export'}
            type: Object,
            default: () => {
                return { display: 'block', jsonBtn: 'all', btnSize: '' };
            }
        }
    });

    const data = reactive({
        percentage: 0
    });

    let { percentage } = toRefs(data);

    const importJson = async (params) => {
        if (!params.file) {
            ElMessage({ message: '请选择一个 JSON 文件', type: 'error', offset: 65 });
            return;
        }
        percentage.value = 0;
        let config = {
            onUploadProgress: (progressEvent) => {
                //progressEvent.loaded:已上传文件大小,progressEvent.total:被上传文件的总大小
                let percent = ((progressEvent.loaded / progressEvent.total) * 100) | 0;
                percentage.value = percent;
            },
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: 'Bearer ' + y9_storage.getObjectItem(settings.siteTokenKey, 'access_token')
            }
        };
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        const formData = new FormData();
        formData.append('file', params.file);
        formData.append('id', props.paramObject.id);
        formData.append('type', props.paramObject.type);
        formData.append('access_token', y9_storage.getObjectItem(settings.siteTokenKey, 'access_token'));
        try {
            axios
                .post(import.meta.env.VUE_APP_CONTEXT + 'vue/json/importJson', formData, config)
                .then((res) => {
                    loading.close();
                    if (res.data.success) {
                        props.reloadData();
                    }
                    ElMessage({
                        type: res.data.success ? 'success' : 'error',
                        message: res.data.msg,
                        offset: 65
                    });
                })
                .catch((err) => {
                    console.log('errerrerrerrerr', err);
                    ElMessage({ type: 'error', message: '发生异常', offset: 65 });
                });
        } catch (error) {
            console.error('导入失败:', error);
        }
    };

    const exportJson = async () => {
        let id = props.paramObject.id;
        let type = props.paramObject.type;
        window.open(
            import.meta.env.VUE_APP_CONTEXT +
                'vue/json/exportJson?id=' +
                id +
                '&type=' +
                type +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token')
        );
    };
</script>
<style scoped lang="scss">
    .jsonComps {
        text-align: right;
        margin-left: 10px;
        display: flex;
    }
    .jsonComps .upload-div {
        margin-right: 10px;
    }
</style>
