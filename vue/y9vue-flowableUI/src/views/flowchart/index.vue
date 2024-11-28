<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-10 16:34:51
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-13 09:21:31
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\flowchart\index.vue
-->

<template>
    <el-container :style="style">
        <el-aside style="width: 100%; height: auto; overflow: auto; padding: 1% 0% 2% 0%">
            <y9Card :showHeader="false" style="width: 80%; height: calc(100% - 20px); margin: auto">
                <div class="containers" style="width: 100%">
                    <div>
                        <div
                            style="
                                width: 15px;
                                height: 15px;
                                display: inline-block;
                                background-color: #f3faf2;
                                border: 1px solid green;
                                margin-right: 5px;
                            "
                        ></div>
                        已经过的节点
                        <div
                            style="
                                width: 15px;
                                height: 15px;
                                display: inline-block;
                                background-color: #f9e8e9;
                                border: 1px solid red;
                                margin: 0 5px;
                            "
                        ></div>
                        当前节点
                        <div
                            style="
                                width: 15px;
                                height: 15px;
                                display: inline-block;
                                background-color: #eff1fa;
                                border: 1px solid black;
                                margin: 0 5px;
                            "
                        ></div>
                        未经过的节点
                        <el-button class="global-btn-second" size="small" style="float: right" @click="handleZoom(-0.2)"
                            >缩小
                        </el-button>
                        <el-button
                            class="global-btn-second"
                            size="small"
                            style="float: right; margin-right: 10px"
                            @click="handleZoom(0.2)"
                            >放大
                        </el-button>
                    </div>
                    <div ref="canvas" class="canvas" style="height: 500px; width: 100%"></div>
                </div>
            </y9Card>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
    import moment from 'moment';
    import BpmnViewer from 'bpmn-js/lib/Viewer';
    import MoveCanvasModule from 'diagram-js/lib/navigation/movecanvas';
    import { defineProps, onMounted, reactive } from 'vue';
    import { getFlowChart, getTaskList } from '@/api/flowableUI/flowchart';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    let style = 'height:calc(100vh - 210px) !important; width: 100%;';
    if (settingStore.pcLayout == 'Y9Horizontal') {
        style = 'height:calc(100vh - 240px) !important; width: 100%;';
    }

    const data = reactive({
        bpmnViewer: null,
        canvas: null,
        xmlStr: '',
        taskList: [],
        scale: 0.9
    });

    let { bpmnViewer, canvas, xmlStr, taskList, scale } = toRefs(data);

    const props = defineProps({
        processDefinitionId: String,
        processInstanceId: String
    });

    onMounted(() => {
        initBpmnModeler();
    });

    async function initBpmnModeler() {
        bpmnViewer.value = new BpmnViewer({
            container: canvas.value,
            // height:'800px',
            additionalModules: [
                //MoveModule,//可以调整元素
                //ModelingModule,//基础T具 MoveModule、setcolor 等依赖于此
                MoveCanvasModule //移动整个画布
            ]
        });
        await getXml();
        bindEvents();
    }

    async function getXml() {
        try {
            let params = { resourceType: 'xml', processDefinitionId: props.processDefinitionId, processInstanceId: '' };
            let res = await getFlowChart(params);
            if (res.success) {
                xmlStr.value = res.data;
                const result = await bpmnViewer.value.importXML(xmlStr.value);

                const canvas1 = bpmnViewer.value.get('canvas');
                canvas1.zoom('fit-viewport', 'auto');
                canvas1.zoom(0.9);

                const res2 = await getTaskList(props.processInstanceId);
                if (res2.success) {
                    taskList.value = res2.data;
                    canvas1._activePlane.rootElement.children.forEach((item) => {
                        //处理UserTask
                        if (item.type == 'bpmn:UserTask') {
                            let highlight = true;
                            res2.data.forEach((element) => {
                                if (element.activityId == item.id) {
                                    highlight = false;
                                    if (element.endTime) {
                                        canvas1.addMarker(element.activityId, 'history');
                                    } else {
                                        canvas1.addMarker(element.activityId, 'current');
                                    }
                                }
                            });
                            if (highlight) {
                                canvas1.addMarker(item.id, 'highlight');
                            }
                        }
                    });
                    //处理其他，路线
                    res2.data.forEach((element) => {
                        if (element.activityType != 'userTask') {
                            if (element.endTime) {
                                if (element.activityType == 'sequenceFlow') {
                                    canvas1.addMarker(element.activityId, 'sequenceflow');
                                } else {
                                    canvas1.addMarker(element.activityId, 'history');
                                }
                            } else {
                                canvas1.addMarker(element.activityId, 'current');
                            }
                        }
                    });
                }
            }
        } catch (err) {
            console.log(err.message, err.warnings);
        }
    }

    async function bindEvents() {
        const eventBus = bpmnViewer.value.get('eventBus');
        const overlays = bpmnViewer.value.get('overlays');
        eventBus.on('element.hover', (e) => {
            if (e.element.type == 'bpmn:UserTask') {
                const elementId = e.element.id;
                genNodeDetailBox(e.element, e, overlays);
            }
        });
        eventBus.on('element.out', (e) => {
            if (e.element.type == 'bpmn:UserTask') {
            }
            overlays.clear();
        });
    }

    function genNodeDetailBox(detail, e, overlays) {
        const tempDiv = document.createElement('div');
        setTimeout(() => {
            let userName = '';
            let opinion = '';
            let startTime = '';
            let endTime = '';
            let time = '';
            let deleteReason = '';
            taskList.value.forEach((element) => {
                if (detail.id == element.activityId) {
                    userName = element.calledProcessInstanceId;
                    opinion = element.tenantId;
                    startTime = convertTime(element.startTime);
                    endTime = convertTime(element.endTime);
                    time = element.executionId;
                    deleteReason = element.deleteReason != null ? element.deleteReason : '';
                }
            });
            tempDiv.innerHTML =
                '<p><font>审批人员:</font>' +
                userName +
                '</p>' +
                '<p><font>意见内容:</font>' +
                opinion +
                '</p>' +
                '<p><font>开始时间:</font>' +
                startTime +
                '</p>' +
                '<p><font>结束时间:</font>' +
                endTime +
                '</p>' +
                '<p><font>审批耗时:</font>' +
                time +
                '</p>' +
                '<p><font>节点描述:</font>' +
                deleteReason;
            tempDiv.className = 'tipBox';
            tempDiv.style.width = '300px';
            tempDiv.style.backgroundColor = '#eee';
            overlays.add(detail.id, 'note', {
                position: { top: 0, left: e.element.width + 2 },
                html: tempDiv
            });
        }, 100);
    }

    function handleZoom(flag) {
        if (flag < 0 && scale.value <= 0.7) {
            return;
        }
        if (flag > 0 && scale.value >= 1.3) {
            return;
        }
        scale.value += flag;
        bpmnViewer.value.get('canvas').zoom(scale.value);
    }

    function convertTime(time) {
        if (time) {
            time = moment(new Date(time)).format('YYYY-MM-DD HH:mm:ss');
            return time;
        } else {
            return '--';
        }
    }
</script>

<style>
    .tipBox {
        background-color: #777;
        border: 1px solid #888;
        border-radius: 3%;
        font-size: 16px !important;
    }

    .tipBox p {
        margin-left: 8px;
        margin-bottom: 5px;
        margin-top: 5px;
        font-size: 16px !important;
    }

    .tipBox font {
        font-weight: bold;
        font-size: 16px !important;
    }

    .highlight .djs-visual > :nth-child(1) {
        /* stroke: #0a65ee !important; */
        fill: #eff1fa !important;
    }

    .history .djs-visual > :nth-child(1) {
        stroke: green !important;
        fill: #f3faf2 !important;
    }

    .sequenceflow .djs-visual > :nth-child(1) {
        stroke: green !important;
    }

    .current .djs-visual > :nth-child(1) {
        stroke: red !important;
        fill: #f9e8e9 !important;
        stroke-dasharray: 4, 4;
    }
</style>
<style lang="scss">
    .bjs-container {
        .bjs-powered-by {
            display: none;
        }
    }
</style>
