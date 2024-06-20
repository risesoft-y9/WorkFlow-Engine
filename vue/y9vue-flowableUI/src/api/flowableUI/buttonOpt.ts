import Request from '@/api/lib/request';

var flowableRequest = new Request();
export const buttonApi = {
  //收回
  takeback(taskId,reason){
    const params = {
      taskId:taskId,
      reason:reason
    };
    return flowableRequest({
      url: "/vue/buttonOperation/takeback",
      method: 'post',
      params: params
    });
  },

  //特殊办结
  specialComplete(taskId,reason){
    const params = {
      taskId:taskId,
      reason:reason
    };
    return flowableRequest({
      url: "/vue/buttonOperation/specialComplete",
      method: 'post',
      params: params
    });
  },

  //获取退回收回任务列表
  getTaskList(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/getTaskList",
      method: 'get',
      params: params
    });
  },

  //退回
  rollback(taskId,reason){
    const params = {
      taskId:taskId,
      reason:reason
    };
    return flowableRequest({
      url: "/vue/buttonOperation/rollback",
      method: 'post',
      params: params
    });
  },

  //退回拟稿人，不用选人，直接退回
  rollbackToStartor(taskId){
    const params = {
      taskId:taskId,
      reason:""
    };
    return flowableRequest({
      url: "/vue/buttonOperation/rollbackToStartor",
      method: 'post',
      params: params
    });
  },

  //退回发送人，不用选人，直接退回
  rollbackToSender(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/rollbackToSender",
      method: 'post',
      params: params
    });
  },

  //并行办理完成
  handleParallel(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/handleParallel",
      method: 'post',
      params: params
    });
  },

  //获取串行办理人顺序
  getHandleSerial(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/getHandleSerial",
      method: 'post',
      params: params
    });
  },

  //串行送下一人
  handleSerial(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/handleSerial",
      method: 'post',
      params: params
    });
  },
  
  //签收
  claim(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/claim",
      method: 'post',
      params: params
    });
  },

  //拒签
  refuseClaim(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/refuseClaim",
      method: 'post',
      params: params
    });
  },

  //撤销签收
  unclaim(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/unclaim",
      method: 'post',
      params: params
    });
  },

  //直接发送至流程启动人
  directSend(processInstanceId,taskId,routeToTask){
    const params = {
      processInstanceId:processInstanceId,
      taskId:taskId,
      routeToTask:routeToTask
    };
    return flowableRequest({
      url: "/vue/buttonOperation/directSend",
      method: 'post',
      params: params
    });
  },

  //返回任务发送人，不用选人，直接发送
  sendToSender(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/sendToSender",
      method: 'post',
      params: params
    });
  },

  //发送拟稿人，不用选人，直接发送
  sendToStartor(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/buttonOperation/sendToStartor",
      method: 'post',
      params: params
    });
  },

  //流程办结
  complete(taskId,infoOvert){
    const params = {
      taskId:taskId,
      infoOvert:infoOvert
    };
    return flowableRequest({
      url: "/vue/document/complete",
      method: 'post',
      params: params
    });
  },

  //恢复待办
  multipleResumeToDo(processInstanceIds){
    const params = {
      processInstanceIds:processInstanceIds
    };
    return flowableRequest({
      url: "/vue/document/multipleResumeToDo",
      method: 'post',
      params: params
    });
  },

  //获取协办人员办理情况
  getParallelNames(taskId){
    const params = {
      taskId:taskId
    };
    return flowableRequest({
      url: "/vue/document/getParallelNames",
      method: 'get',
      params: params
    });
  },

  //获取签收任务配置和发送人员
  signTaskConfig(itemId,processDefinitionId,taskDefinitionKey,processSerialNumber){
    const params = {
      itemId:itemId,
      processDefinitionId:processDefinitionId,
      taskDefinitionKey:taskDefinitionKey,
      processSerialNumber:processSerialNumber
    };
    return flowableRequest({
      url: "/vue/document/signTaskConfig",
      method: 'get',
      params: params
    });
  },

  //定制流程办理完成，办结
  customProcessHandle(itemId,processSerialNumber,processDefinitionKey,multiInstance,nextNode,processInstanceId,taskId,infoOvert){
    const params = {
      itemId:itemId,
      processSerialNumber:processSerialNumber,
      processDefinitionKey:processDefinitionKey,
      multiInstance:multiInstance,
      nextNode:nextNode,
      processInstanceId:processInstanceId,
      taskId:taskId,
      infoOvert:infoOvert
    };
    return flowableRequest({
      url: "/vue/buttonOperation/customProcessHandle",
      method: 'post',
      params: params
    });
  },
  //提交
  submitTo(itemId,taskId,processSerialNumber){
    const params = {
      itemId:itemId,
      taskId:taskId,
      processSerialNumber:processSerialNumber
    };
    return flowableRequest({
      url: "/vue/document/submitTo",
      method: 'post',
      params: params
    });
  }
}

//重定向(选择任意流程节点重定向)
export function reposition(taskId,routeToTaskId,userChoice,processSerialNumber,sponsorGuid,isSendSms,isShuMing,smsContent){
  const params = {
    taskId:taskId,
    routeToTaskId:routeToTaskId,
    userChoice:userChoice,
    processSerialNumber:processSerialNumber,
    sponsorGuid:sponsorGuid,
    isSendSms:isSendSms,
    isShuMing:isShuMing,
    smsContent:smsContent
  };
  return flowableRequest({
    url: "/vue/buttonOperation/reposition",
    method: 'post',
    params: params
  });
}

//发送
export function forwarding(itemId,processInstanceId,taskId,processDefinitionKey,processSerialNumber,sponsorHandle,
  userChoice,sponsorGuid,routeToTaskId,isSendSms,isShuMing,smsContent){
  const params = {
    itemId:itemId,
    processInstanceId:processInstanceId,
    taskId:taskId,
    processDefinitionKey:processDefinitionKey,
    processSerialNumber:processSerialNumber,
    sponsorHandle:sponsorHandle,
    userChoice:userChoice,
    sponsorGuid:sponsorGuid,
    routeToTaskId:routeToTaskId,
    isSendSms:isSendSms,
    isShuMing:isShuMing,
    smsContent:smsContent
  };
  return flowableRequest({
    url: "/vue/document/forwarding",
    method: 'post',
    params: params
  });
}

//获取目标路由
export function getTargetNodes(processDefinitionId,taskDefKey){
  const params = {
    processDefinitionId:processDefinitionId,
    taskDefKey:taskDefKey
  };
  return flowableRequest({
    url: "/vue/buttonOperation/getTargetNodes",
    method: 'get',
    params: params
  });
}


//获取定制流程信息
export function getCustomProcessTaskList(processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url: "/vue/buttonOperation/getCustomProcessTaskList",
    method: 'get',
    params: params
  });
}

//获取有办结权限的任务节点
export function getContainEndEvent4UserTask(processDefinitionId){
  const params = {
    processDefinitionId:processDefinitionId
  };
  return flowableRequest({
    url: "/vue/buttonOperation/getContainEndEvent4UserTask",
    method: 'get',
    params: params
  });
}


//保存流程定制信息
export function saveCustomProcess(itemId,processSerialNumber,processDefinitionKey,jsonData){
  const params = {
    itemId:itemId,
    processSerialNumber:processSerialNumber,
    processDefinitionKey:processDefinitionKey,
    jsonData:jsonData
  };
  return flowableRequest({
    url: "/vue/buttonOperation/saveCustomProcess",
    method: 'post',
    params: params
  });
}

