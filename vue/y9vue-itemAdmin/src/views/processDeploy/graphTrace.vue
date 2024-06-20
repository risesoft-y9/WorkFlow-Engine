<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-10 16:34:51
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-11 16:08:32
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\processDeploy\graphTrace.vue
-->

<template>

    <div class="imgDiv">
        <el-skeleton :loading="!imgHref" animation="pulse" row="10" style="width: 100%; height: 100%;">
            <div v-if="imgHref">
                <img :src="imgHref" alt="SVG Image">
            </div>
        </el-skeleton>
    </div>

    <div class="bpmnDiv">
        <div class="my-process-designer__container">
            <div class="my-process-designer__canvas" ref="bpmnCanvas"></div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import BpmnModeler from "bpmn-js/lib/Modeler.js";
import y9_storage from "@/utils/storage";
import settings from '@/settings.ts';
import {ref, defineProps, onMounted, watch, reactive} from 'vue';
import {getProcessXml} from "@/api/processAdmin/processDeploy";

const bpmnCanvas = ref(null)
const url = ref('');
let bpmnModeler = ref()
let xmlString = ref('')
let imgHref = ref('')

const props = defineProps({
    processDefinitionId: String,
})

onMounted(() => {

    initBpmnModeler();

});

async function initBpmnModeler() {
    if (bpmnModeler.value) return;

    bpmnModeler.value = new BpmnModeler({
        container: bpmnCanvas.value,
    });
    // 获取流程实例xml
    let params = { resourceType: 'xml', processDefinitionId: props.processDefinitionId,processInstanceId: ''}
    let res = await getProcessXml(params)
    if (res.success){
        xmlString.value = res.data;
        let {warnings} = await bpmnModeler.value.importXML(xmlString.value);
        if (warnings && warnings.length) {
            warnings.forEach(warn => console.warn(warn));
        }
        // 解析bpmn为svg图片并展示
        const {err, svg} = await bpmnModeler.value.saveSVG();
        let {href, filename} = await setEncoded("svg", '', svg);
        imgHref.value = href;
    }else {
        ElMessage({type: 'error', message: "发生异常", offset: 65});
    }
}

async function setEncoded(type, filename = "diagram", data) {
    filename = bpmnModeler.value.getDefinitions().rootElements[0].id;
    const encodedData = encodeURIComponent(data);
    return {
        filename: `${filename}.${type}`,
        href: `data:image/svg+xml;charset=UTF-8,${encodedData}`,
        data: data
    };
}

</script>

<style>
.graphTrace .el-dialog__body {
    padding: 5px 10px;
}
.imgDiv {
    min-height:400px;
    min-width:400px;
    width:100%;
    height:100%;
    overflow: auto;
    text-align: center;
}
.bpmnDiv {
    visibility: hidden;
    height: 0;
    width: 0;
}
</style>