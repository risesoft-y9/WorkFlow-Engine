<template>
  <div class="process-panel__container" style="width:520px;">
    <el-collapse v-model="activeTab">
      <el-collapse-item name="base">
        <template #title>
          <div class="panel-tab__title"><i class="ri-file-info-line" style="vertical-align: middle;"></i>常规</div>
        </template>
        <ElementBaseInfo :id-edit-disabled="idEditDisabled" :id="elementId" :business-object="elementBusinessObject" :type="elementType" />
      </el-collapse-item>
      <!-- <el-collapse-item name="message" v-if="elementType === 'Process'" key="message">
        <template #title>
          <div class="panel-tab__title"><el-icon><comment /></el-icon>消息与信号</div>
        </template>
        <SignalAndMassage />
      </el-collapse-item> -->
      <el-collapse-item name="condition" v-if="conditionFormVisible" key="condition">
        <template #title>
          <div class="panel-tab__title"><i class="ri-focus-3-line" style="vertical-align: middle;"></i>流转条件</div> 
        </template>
        <FlowCondition :businessObject="elementBusinessObject" :id="elementId" :type="elementType" />
      </el-collapse-item>
      <!-- <el-collapse-item name="form" v-if="formVisible" key="form">
        <template #title>

          <div class="panel-tab__title"><el-icon><list /></el-icon>表单</div>
        </template>
        <ElementForm :id="elementId" :type="elementType" />
      </el-collapse-item> -->
      <el-collapse-item name="task" v-if="elementType.indexOf('Task') !== -1" key="task">
        <template #title>
          <div class="panel-tab__title"><i class="ri-t-box-line" style="vertical-align: middle;"></i>任务</div>
        </template>
        <ElementTask :id="elementId" :type="elementType" :businessObject="elementBusinessObject" :updateSign="updateSign"/>
      </el-collapse-item>
      <el-collapse-item name="multiInstance" v-if="elementType.indexOf('Task') !== -1" key="multiInstance">
        <template #title>
          <div class="panel-tab__title"><i class="ri-table-line" style="vertical-align: middle;"></i>多实例</div>
        </template>
        <ElementMultiInstance :id="elementId" :businessObject="elementBusinessObject" :type="elementType"  @updateLoopCharacteristicsType="updateUserTask"/>
      </el-collapse-item>
      <el-collapse-item name="listeners" key="listeners">
        <template #title>

          <div class="panel-tab__title"><i class="ri-notification-2-line" style="vertical-align: middle;"></i>执行监听器</div>
        </template>
        <ElementListeners :id="elementId" :type="elementType" />
      </el-collapse-item>
      <el-collapse-item name="taskListeners" v-if="elementType === 'UserTask'" key="taskListeners">
        <template #title>

          <div class="panel-tab__title"><i class="ri-notification-2-line" style="vertical-align: middle;"></i>任务监听器</div>
        </template>
        <UserTaskListeners :id="elementId" :type="elementType" />
      </el-collapse-item>
      <!-- <el-collapse-item name="extensions" key="extensions">
        <template #title>

          <div class="panel-tab__title"><el-icon><circle-plus /></el-icon>扩展属性</div>
        </template>
        <ElementProperties :id="elementId" :type="elementType" />
      </el-collapse-item> -->
      <!-- <el-collapse-item name="other" key="other">
        <template #title>

          <div class="panel-tab__title"><el-icon><promotion /></el-icon>其他</div>
        </template>
        <ElementOtherConfig :id="elementId" />
      </el-collapse-item> -->
    </el-collapse>
  </div>
</template>
<script lang="ts" setup>
import { ref, defineProps,provide, onMounted, watch,reactive} from 'vue';
import ElementBaseInfo from "./base/ElementBaseInfo.vue";
import ElementOtherConfig from "./other/ElementOtherConfig.vue";
import ElementTask from "./task/ElementTask.vue";
import ElementMultiInstance from "./multi-instance/ElementMultiInstance.vue";
import FlowCondition from "./flow-condition/FlowCondition.vue";
import SignalAndMassage from "./signal-message/SignalAndMessage.vue";
import ElementListeners from "./listeners/ElementListeners.vue";
import ElementProperties from "./properties/ElementProperties.vue";
import ElementForm from "./form/ElementForm.vue";
import UserTaskListeners from "./listeners/UserTaskListeners.vue";
/**
 * 侧边栏
 * @Author MiyueFE
 * @Home https://github.com/miyuesc
 * @Date 2021年3月31日18:57:51
 */
 const props = defineProps({
    bpmnModeler: {
			type: Object,
			default:() => { return {} }
		},
    elementObj: {
			type: Object,
			default:() => { return {} }
		},
    width: {
      type: Number,
      default: 480
    },
    idEditDisabled: {
      type: Boolean,
      default: false
    }
	})

  const data = reactive({
    activeTab: ["base",'condition','task','multiInstance'],
    elementBusinessObject: {}, // 元素 businessObject 镜像，提供给需要做判断的组件使用
    conditionFormVisible: false, // 流转条件设置
    formVisible: false, // 表单配置
    elementType:'',
    elementId:'',
    updateSign:false,//多实例修改消息
	})
	
	let {
    activeTab,
    elementBusinessObject, 
    conditionFormVisible, 
    formVisible,
    elementType,
    elementId,
    updateSign,
	} = toRefs(data);

  onMounted(()=>{
    initModels();
  });

  function initModels() {
      // 初始化 modeler 以及其他 moddle
      if (props.bpmnModeler) {
        window.bpmnInstances = {
          modeler: props.bpmnModeler,
          modeling: props.bpmnModeler.get("modeling"),
          moddle: props.bpmnModeler.get("moddle"),
          eventBus: props.bpmnModeler.get("eventBus"),
          bpmnFactory: props.bpmnModeler.get("bpmnFactory"),
          elementFactory: props.bpmnModeler.get("elementFactory"),
          elementRegistry: props.bpmnModeler.get("elementRegistry"),
          replace: props.bpmnModeler.get("replace"),
          selection: props.bpmnModeler.get("selection")
        };
        getActiveElement();
      }
    }

    function getActiveElement() {
      
      // 初始第一个选中元素 bpmn:Process
      initFormOnChanged(null);
      // props.bpmnModeler.on("import.done", e => {
      //   initFormOnChanged(null);
      // });
      // 监听选择事件，修改当前激活的元素以及表单
      props.bpmnModeler.on("selection.changed", ({ newSelection }) => {
        initFormOnChanged(newSelection[0] || null);
      });
      props.bpmnModeler.on("element.changed", ({ element }) => {
        // 保证 修改 "默认流转路径" 类似需要修改多个元素的事件发生的时候，更新表单的元素与原选中元素不一致。
        // if (element && element.id === props.elementId) {
        //   initFormOnChanged(element);
        // }
      });
      // initFormOnChanged(props.elementObj);
    }

    // 初始化数据
    function initFormOnChanged(element) {
      let activatedElement = element;
      if (!activatedElement) {
        activatedElement =
          window.bpmnInstances.elementRegistry.find(el => el.type === "bpmn:Process") ??
          window.bpmnInstances.elementRegistry.find(el => el.type === "bpmn:Collaboration");
      }
      if (!activatedElement) return;
      // Log.printBack(`select element changed: id: ${activatedElement.id} , type: ${activatedElement.businessObject.$type}`);
      // Log.prettyInfo("businessObject", activatedElement.businessObject);
      window.bpmnInstances.bpmnElement = activatedElement;
      // this.bpmnElement = activatedElement;
      elementId.value = activatedElement.id;
      elementType.value = activatedElement.type.split(":")[1] || "";
      elementBusinessObject.value = JSON.parse(JSON.stringify(activatedElement.businessObject));
      conditionFormVisible.value = !!(
        elementType.value === "SequenceFlow" &&
        activatedElement.source &&
        activatedElement.source.type.indexOf("StartEvent") === -1
      );
      formVisible.value = elementType.value === "UserTask" || elementType.value === "StartEvent";
    }

    function updateUserTask() {
      updateSign.value = !updateSign.value;
    }

</script>
<style lang="scss" scoped>
:deep(.el-button)  {
  height: 32px;
}
</style>