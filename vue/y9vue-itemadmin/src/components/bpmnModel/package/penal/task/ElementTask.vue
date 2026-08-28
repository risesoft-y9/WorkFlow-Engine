<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-08-03 09:24:41
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-08-04 17:18:34
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\components\bpmnModel\package\penal\task\ElementTask.vue
-->
<template>
  <div class="panel-tab__content">
    <el-form size="small" label-width="90px" @submit.prevent>
      <!-- <el-form-item label="异步延续">
        <el-checkbox v-model="taskConfigForm.asyncBefore" label="异步前" @change="changeTaskAsync" />
        <el-checkbox v-model="taskConfigForm.asyncAfter" label="异步后" @change="changeTaskAsync" />
        <el-checkbox v-model="taskConfigForm.exclusive" v-if="taskConfigForm.asyncAfter || taskConfigForm.asyncBefore" label="排除" @change="changeTaskAsync" />
      </el-form-item> -->
      <UserTask v-if="type == 'UserTask'" :updateSign="updateSign" :id="id" :type="type" />
      <ScriptTask v-if="type == 'ScriptTask'" :id="id" :type="type" />
      <ReceiveTask v-if="type == 'ReceiveTask'" :id="id" :type="type" />
    </el-form>
  </div>
</template>

<script lang="ts" setup>
  import { ref, defineProps,provide, onMounted, watch,reactive} from 'vue';
  import UserTask from "./task-components/UserTask.vue";
  import ScriptTask from "./task-components/ScriptTask.vue";
  import ReceiveTask from "./task-components/ReceiveTask.vue";
  const props = defineProps({
    id: String,
    type: String,
    updateSign:Boolean
  })
  const data = reactive({
    taskConfigForm: {
      asyncAfter: false,
      asyncBefore: false,
      exclusive: false
    },
	})
	
	let {
    taskConfigForm,
	} = toRefs(data)

  watch(() => props.id,(newVal) => {
    let bpmnElement = window.bpmnInstances.bpmnElement;
    taskConfigForm.value.asyncBefore = bpmnElement?.businessObject?.asyncBefore;
    taskConfigForm.value.asyncAfter = bpmnElement?.businessObject?.asyncAfter;
    taskConfigForm.value.exclusive = bpmnElement?.businessObject?.exclusive;
  },{deep:true,})


  function changeTaskAsync() {
    if (!taskConfigForm.value.asyncBefore && !taskConfigForm.value.asyncAfter) {
      taskConfigForm.value.exclusive = false;
    }
    window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, {
      ...taskConfigForm.value
    });
  }
</script>
