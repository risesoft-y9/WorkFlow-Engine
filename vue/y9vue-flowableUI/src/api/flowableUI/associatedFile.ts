import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取关联流程
export function getAssociatedFileList(processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url:'/vue/associatedFile/getAssociatedFileList',
    method: 'get',
    params: params
  });
}

//删除关联流程
export function delAssociatedFile(processSerialNumber,processInstanceIds){
  const params = {
    processSerialNumber:processSerialNumber,
    processInstanceIds:processInstanceIds
  };
  return flowableRequest({
    url:'/vue/associatedFile/delAssociatedFile',
    method: 'POST',
    params: params
  });
}

//获取关联历史文件
export function getAssociatedDoneList(itemId,title,page,rows){
  const params = {
    itemId:itemId,
    title:title,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/associatedFile/getDoneList',
    method: 'get',
    params: params
  });
}

//保存关联流程
export function saveAssociatedFile(processSerialNumber,processInstanceIds){
  const params = {
    processSerialNumber:processSerialNumber,
    processInstanceIds:processInstanceIds
  };
  return flowableRequest({
    url:'/vue/associatedFile/saveAssociatedFile',
    method: 'post',
    params: params
  });
}
