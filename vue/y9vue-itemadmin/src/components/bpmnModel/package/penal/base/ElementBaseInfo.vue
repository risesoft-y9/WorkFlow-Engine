<template>
  <div class="panel-tab__content">
    <el-form size="small" label-width="90px" @submit.prevent>
      <el-form-item label="ID">
        <el-input v-model="elementBaseInfo.id" :disabled="idEditDisabled" maxlength="25" show-word-limit clearable @change="updateBaseInfo('id')" />
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="elementBaseInfo.name" clearable @change="updateBaseInfo('name')" />
      </el-form-item>
      <!--流程的基础属性-->
      <!-- <template v-if="elementBaseInfo.$type === 'bpmn:Process'">
        <el-form-item label="版本标签">
          <el-input v-model="elementBaseInfo.versionTag" clearable @change="updateBaseInfo('versionTag')" />
        </el-form-item>
        <el-form-item label="可执行">
          <el-switch v-model="elementBaseInfo.isExecutable" active-text="是" inactive-text="否" @change="updateBaseInfo('isExecutable')" />
        </el-form-item>
      </template> -->
      <el-form-item v-if="elementBaseInfo.$type === 'bpmn:SubProcess'" label="状态">
        <el-switch v-model="elementBaseInfo.isExpanded" active-text="展开" inactive-text="折叠" @change="updateBaseInfo('isExpanded')" />
      </el-form-item>
    </el-form>
  </div>
</template>
<script lang="ts" setup>
  import { ref, defineProps,nextTick,toRefs, onMounted, watch,reactive} from 'vue';
  const props = defineProps({
    businessObject: Object,
    type: String,
    id:String,
    idEditDisabled: {
      type: Boolean,
      default: true
    }
  })
  const data = reactive({
    elementBaseInfo: {},
    bpmnElement:null,
	})

	let {
    elementBaseInfo,
    bpmnElement
	} = toRefs(data)

  watch(() => props.id,(newVal) => {
    if (newVal) {
      nextTick(() => {
        resetBaseInfo();
      })
    }
  },{deep:true,})

  onMounted(()=>{
    
  });

  function resetBaseInfo() {
    bpmnElement.value = window?.bpmnInstances?.bpmnElement || {};
    elementBaseInfo.value = JSON.parse(JSON.stringify(bpmnElement.value?.businessObject));
    
    if (elementBaseInfo.value && elementBaseInfo.value.$type === "bpmn:SubProcess") {
      elementBaseInfo.value["isExpanded"] = elementBaseInfo.value.di?.isExpanded
    }

    //UserTask和结束节点，修改排他网管路由表达式
    if(window?.bpmnInstances?.bpmnElement.type == 'bpmn:UserTask' || window?.bpmnInstances?.bpmnElement.type == 'bpmn:EndEvent'){
      if(window?.bpmnInstances?.bpmnElement.businessObject.incoming != null && window?.bpmnInstances?.bpmnElement.businessObject.incoming != undefined){//获取路由路线
        window?.bpmnInstances?.bpmnElement.businessObject.incoming.forEach(element => {
          let bpmnElementF = window.bpmnInstances.elementRegistry.get(element.id);
          if(element.sourceRef.$type == 'bpmn:ExclusiveGateway'){
            let body = '${routeToTaskId=="'+window?.bpmnInstances?.bpmnElement.id+'"}';//设置表达式
            let condition = window.bpmnInstances.moddle.create('bpmn:FormalExpression', {body});
            window.bpmnInstances.modeling.updateProperties(bpmnElementF, { conditionExpression: condition });
          }
        });
      }
    }
  }
  function updateBaseInfo(key) {
    if (key === "id") {
      if(elementBaseInfo.value[key] == ''){
        return;
      }
      window.bpmnInstances.modeling.updateProperties(window?.bpmnInstances?.bpmnElement, {
        id: elementBaseInfo.value[key],
        di: { id: `${elementBaseInfo.value[key]}_di` }
      });

      //UserTask和结束节点修改id，修改排他网管路由表达式
      if(window?.bpmnInstances?.bpmnElement.type == 'bpmn:UserTask' || window?.bpmnInstances?.bpmnElement.type == 'bpmn:EndEvent'){
        if(window?.bpmnInstances?.bpmnElement.businessObject.incoming != null && window?.bpmnInstances?.bpmnElement.businessObject.incoming != undefined){//获取路由路线
          window?.bpmnInstances?.bpmnElement.businessObject.incoming.forEach(element => {
            let bpmnElementF = window.bpmnInstances.elementRegistry.get(element.id);
            if(element.sourceRef.$type == 'bpmn:ExclusiveGateway'){
              let body = '${routeToTaskId=="'+window?.bpmnInstances?.bpmnElement.id+'"}';//设置表达式
              let condition = window.bpmnInstances.moddle.create('bpmn:FormalExpression', {body});
              window.bpmnInstances.modeling.updateProperties(bpmnElementF, { conditionExpression: condition });
            }
          });
        }
      }
      return;
    }
    if (key === "isExpanded") {
      window?.bpmnInstances?.modeling.toggleCollapse(window?.bpmnInstances?.bpmnElement);
      return;
    }
    const attrObj = Object.create(null);
    attrObj[key] = elementBaseInfo.value[key];
    window.bpmnInstances.modeling.updateProperties(window?.bpmnInstances?.bpmnElement, attrObj);
  }

</script>
