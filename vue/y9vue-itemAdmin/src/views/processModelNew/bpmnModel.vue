<template>
  <div id="bpmnDiv" style="height: 100%;">
    <!-- <my-process-palette /> -->
    <my-process-designer
      :key="`designer-${reloadIndex}`"
      :options="{
        taskResizingEnabled: true,
        eventResizingEnabled: true,
        minimap: {
          open: true
        }
      }"
      v-model="xmlString"
      v-bind="controlForm"
      keyboard
      ref="processDesigner"
      @element-click="elementClick"
      @element-contextmenu="elementContextmenu"
      @init-finished="initModeler"
      v-if="showDesigner"
      :processId="processId"
      :processName="processName"
      @saveModelXml="saveModelXml"
      @closeDialog="closeDialog"
    />
    <PropertiesPanel v-if="showPanel" :elementObj="elementObj" :elementType="elementType" :bpmnModeler="bpmnModeler" class="process-panel" />
  </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import translations from '@/translations';
// 自定义渲染（隐藏了 label 标签）
import CustomRenderer from '@/components/bpmnModel/modules/custom-renderer';
// 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
import CustomContentPadProvider from '@/components/bpmnModel/package/designer/plugins/content-pad';
// 自定义左侧菜单（修改 默认任务 为 用户任务）
import CustomPaletteProvider from '@/components/bpmnModel/package/designer/plugins/palette';
import Log from '@/components/bpmnModel/package/Log';

import PropertiesPanel from '@/components/bpmnModel/package/penal/PropertiesPanel.vue';

// 任务resize
// import resizeTask from "bpmn-js-task-resize/lib";
// 小地图
// import minimapModule from 'diagram-js-minimap';

import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
import {getModelXml} from '@/api/processAdmin/processModel';
const props = defineProps({
    editId:String,
  })
const data = reactive({
    xmlString: '',
    showDesigner:false,
    bpmnModeler: null,
    reloadIndex: 0,
    controlForm: {
      processId: '',
      processName: '',
      simulation: true,
      labelEditing: false,
      labelVisible: false,
      prefix: 'flowable',
      headerButtonSize: 'default',
      events: ['element.click', 'element.contextmenu'],
      // additionalModel: []
      additionalModel: [CustomContentPadProvider, CustomPaletteProvider]
    },
    showPanel:false,
    elementObj:{},
    elementType:'',
    processId:'',
    processName:'',
	})
	
	let {
    xmlString,
    bpmnModeler,
    reloadIndex,
    controlForm,
    showPanel,
    elementObj,
    elementType,
    showDesigner,
    processId,
    processName
	} = toRefs(data);

  onMounted(async ()=>{
    if(props.editId != ''){
      let res = await getModelXml(props.editId);
      if(res.success){
        xmlString.value = res.data.xml;
        processId.value = res.data.key;
        processName.value = res.data.name;
      }
    }
    showDesigner.value = true;
  });


  function initModeler(modeler) {
    setTimeout(() => {
      bpmnModeler.value = modeler;
      const canvas = modeler.get('canvas');
      const rootElement = canvas.getRootElement();
      Log.prettyPrimary('Process Id:', rootElement.id);
      Log.prettyPrimary('Process Name:', rootElement.businessObject?.name);
      showPanel.value = true;
    }, 10);
  }

  function elementClick(element) {
    elementObj.value = element;
    elementType.value = element.type;
  }
  function elementContextmenu(element) {
    console.log('elementContextmenu:', element);
  }

  const emits = defineEmits(['saveModelXml','closeDialog']);
  function saveModelXml() {
    emits("saveModelXml");
  }

  function closeDialog() {
    emits("closeDialog");
  }

</script>

<style lang="scss">
body {
  overflow: hidden;
  margin: 0;
  box-sizing: border-box;
}
#bpmnDiv {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: inline-flex;
  grid-template-columns: 100px auto max-content;
}
.demo-info-bar {
  position: fixed;
  right: 8px;
  bottom: 108px;
  z-index: 1;
}
.demo-control-bar {
  position: fixed;
  right: 8px;
  bottom: 48px;
  z-index: 1;
}
.open-model-button {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 32px;
  background: rgba(64, 158, 255, 1);
  color: #ffffff;
  cursor: pointer;
}
.zoom-in-right-enter-active,
.zoom-in-right-leave-active {
  opacity: 1;
  transform: scaleY(1) translateY(-48px);
  transition: all 300ms cubic-bezier(0.23, 1, 0.32, 1);
  transform-origin: right center;
}
.zoom-in-right-enter,
.zoom-in-right-leave-active {
  opacity: 0;
  transform: scaleX(0) translateY(-48px);
}
.info-tip {
  position: absolute;
  width: 480px;
  top: 0;
  right: 64px;
  z-index: 10;
  box-sizing: border-box;
  padding: 0 16px;
  color: #333333;
  background: #f2f6fc;
  transform: translateY(-48px);
  border: 1px solid #ebeef5;
  border-radius: 4px;
  &::before,
  &::after {
    content: "";
    width: 0;
    height: 0;
    border-width: 8px;
    border-style: solid;
    position: absolute;
    right: -15px;
    top: 50%;
  }
  &::before {
    border-color: transparent transparent transparent #f2f6fc;
    z-index: 10;
  }
  &::after {
    right: -16px;
    border-color: transparent transparent transparent #ebeef5;
    z-index: 1;
  }
}
.control-form {
  .el-radio {
    width: 100%;
    line-height: 32px;
  }
}
.element-overlays {
  box-sizing: border-box;
  padding: 8px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  color: #fafafa;
}

body,
body * {
  /* 滚动条 */
  &::-webkit-scrollbar-track-piece {
    background-color: #fff; /*滚动条的背景颜色*/
    -webkit-border-radius: 0; /*滚动条的圆角宽度*/
  }
  &::-webkit-scrollbar {
    width: 10px; /*滚动条的宽度*/
    height: 8px; /*滚动条的高度*/
  }
  &::-webkit-scrollbar-thumb:vertical {
    /*垂直滚动条的样式*/
    height: 50px;
    background-color: rgba(153, 153, 153, 0.5);
    -webkit-border-radius: 4px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
  }
  &::-webkit-scrollbar-thumb {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.3);
    -webkit-border-radius: 4px;
  }
  &::-webkit-scrollbar-thumb:hover {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.5);
    -webkit-border-radius: 4px;
  }
}
</style>
