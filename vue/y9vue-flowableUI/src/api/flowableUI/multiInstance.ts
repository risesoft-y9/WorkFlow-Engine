import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取加减签任务列表
export function getAddOrDeleteMultiInstance(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url:'/vue/multiInstance/getAddOrDeleteMultiInstance',
    method: 'get',
    params: params
  });
}


//加签
export function addExecutionId(processInstanceId,executionId,taskId,userChoice,selectUserId,num,isSendSms,isShuMing,smsContent){
  const params = {
    processInstanceId:processInstanceId,
    executionId:executionId,
    taskId:taskId,
    userChoice:userChoice,
    selectUserId:selectUserId,
    isSendSms:isSendSms,
    num:num,
    isShuMing:isShuMing,
    smsContent:smsContent
  };
  return flowableRequest({
    url:'/vue/multiInstance/addExecutionId',
    method: 'post',
    params: params
  });
}

//并行减签
export function removeExecution(executionId,taskId,elementUser){
  const params = {
    executionId:executionId,
    taskId:taskId,
    elementUser:elementUser
  };
  return flowableRequest({
    url:'/vue/multiInstance/removeExecution',
    method: 'post',
    params: params
  });
}


//串行减签
export function removeExecution4Sequential(executionId,taskId,elementUser,num){
  const params = {
    executionId:executionId,
    taskId:taskId,
    elementUser:elementUser,
    num:num
  };
  return flowableRequest({
    url:'/vue/multiInstance/removeExecution4Sequential',
    method: 'post',
    params: params
  });
}

//设置主办人
export function setSponsor(taskId){
  const params = {
    taskId:taskId
  };
  return flowableRequest({
    url:'/vue/multiInstance/setSponsor',
    method: 'post',
    params: params
  });
}
