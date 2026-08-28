<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-08-03 09:24:41
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-08-07 17:44:21
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\components\bpmnModel\package\penal\task\task-components\UserTask.vue
-->
<template>
  <div style="margin-top: 16px">
    <el-form-item label="处理用户">
      <!-- <el-select v-model="userTaskForm.assignee" @change="updateElementTask('assignee')">
        <el-option v-for="ak in mockData" :key="'ass-' + ak" :label="`用户${ak}`" :value="`user${ak}`" />
      </el-select> -->
      <el-input v-model="userTaskForm.assignee" :readonly="true"></el-input>
    </el-form-item>
    <el-form-item label="候选用户">
      <!-- <el-select v-model="userTaskForm.candidateUsers" multiple collapse-tags @change="updateElementTask('candidateUsers')">
        <el-option v-for="uk in mockData" :key="'user-' + uk" :label="`用户${uk}`" :value="`user${uk}`" />
      </el-select> -->
      <el-input v-model="userTaskForm.candidateUsers" :readonly="true"></el-input>
    </el-form-item>
    <!-- <el-form-item label="候选分组">
      <el-select v-model="userTaskForm.candidateGroups" multiple collapse-tags @change="updateElementTask('candidateGroups')">
        <el-option v-for="gk in mockData" :key="'ass-' + gk" :label="`分组${gk}`" :value="`group${gk}`" />
      </el-select>
    </el-form-item> -->
    <!-- <el-form-item label="到期时间">
      <el-input v-model="userTaskForm.dueDate" clearable @change="updateElementTask('dueDate')" />
    </el-form-item>
    <el-form-item label="跟踪时间">
      <el-input v-model="userTaskForm.followUpDate" clearable @change="updateElementTask('followUpDate')" />
    </el-form-item>
    <el-form-item label="优先级">
      <el-input v-model="userTaskForm.priority" clearable @change="updateElementTask('priority')" />
    </el-form-item> -->
  </div>
</template>

<script lang="ts" setup>
import { ref, defineProps,provide, onMounted, watch,reactive,nextTick} from 'vue';
const props = defineProps({
  id: String,
  type: String,
  updateSign:Boolean
})
const data = reactive({
    defaultTaskForm: {
      assignee: "",
      candidateUsers: "",
      candidateGroups: [],
      dueDate: "",
      followUpDate: "",
      priority: ""
    },
    userTaskForm: {},
    mockData: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    bpmnElement:'',
	})
	
	let {
    defaultTaskForm,
    userTaskForm,
    mockData,
    bpmnElement
	} = toRefs(data);

  watch(() => props.updateSign,(newVal) => {
    let businessObject = window.bpmnInstances.bpmnElement.businessObject;
    if(businessObject.loopCharacteristics != null){//多实例
      userTaskForm.value.assignee = "${elementUser}";
      userTaskForm.value.candidateUsers = "";
    }else{//单实例
      userTaskForm.value.assignee = "${user}";
      userTaskForm.value.candidateUsers = "${users}";
    }
    updateElementTask('assignee');
    updateElementTask('candidateUsers');
  },{deep:true,})

  watch(() => props.id,(newVal) => {
    userTaskForm.value = {}
    resetTaskForm();
    let businessObject = window.bpmnInstances.bpmnElement.businessObject;
    if(businessObject.loopCharacteristics != null){//多实例
      userTaskForm.value.assignee = "${elementUser}";
      userTaskForm.value.candidateUsers = "";
    }else{//单实例
      userTaskForm.value.assignee = "${user}";
      userTaskForm.value.candidateUsers = "${users}";
    }
    updateElementTask('assignee');
    updateElementTask('candidateUsers');
  },{deep:true,})

  onMounted(()=>{
    bpmnElement.value = window.bpmnInstances.bpmnElement;
    nextTick(() => {
      userTaskForm.value = {}
      resetTaskForm();
      let businessObject = window.bpmnInstances.bpmnElement.businessObject;
      if(businessObject.loopCharacteristics != null){//多实例
        userTaskForm.value.assignee = "${elementUser}";
        userTaskForm.value.candidateUsers = "";
      }else{//单实例
        userTaskForm.value.assignee = "${user}";
        userTaskForm.value.candidateUsers = "${users}";
      }
      updateElementTask('assignee');
      updateElementTask('candidateUsers');
      
      // if(businessObject.incoming != null && businessObject.incoming != undefined){//获取路由路线
      //   businessObject.incoming.forEach(element => {
      //     let bpmnElementF = window.bpmnInstances.elementRegistry.get(element.id);
      //     if(element.sourceRef.$type == 'bpmn:ExclusiveGateway'){
      //       let body = '${routeToTaskId=="'+bpmnElement.value.id+'"}';//设置表达式
      //       let condition = window.bpmnInstances.moddle.create('bpmn:FormalExpression', {body});
      //       window.bpmnInstances.modeling.updateProperties(bpmnElementF, { conditionExpression: condition });
      //     }
      //   });
      // }
    })
  });

  function resetTaskForm() {
    for (let key in defaultTaskForm.value) {
      let value;
      // if (key === "candidateUsers" || key === "candidateGroups") {
      //   value = this.bpmnElement?.businessObject[key] ? this.bpmnElement.businessObject[key].split(",") : [];
      // } else {
        value = bpmnElement.value?.businessObject[key] || defaultTaskForm.value[key];
      // }
      userTaskForm.value[key] = value
    }
  }
  function updateElementTask(key) {
    let taskAttr = Object.create(null);
    // if (key === "candidateUsers" || key === "candidateGroups") {
    //   taskAttr[key] = this.userTaskForm[key] && this.userTaskForm[key].length ? this.userTaskForm[key].join() : null;
    // } else {
      taskAttr[key] = userTaskForm.value[key] || null;
    // }
    window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, taskAttr);
  }
</script>
