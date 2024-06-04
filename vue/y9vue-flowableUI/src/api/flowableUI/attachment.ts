/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:29
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-10 17:35:33
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\attachment.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取附件列表
export function getAttachmentList(processSerialNumber,page,rows){
  const params = {
    processSerialNumber:processSerialNumber,
    fileSource:'',
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/attachment/getAttachmentList',
    method: 'get',
    params: params
  });
}

//保存附件
export function saveAttachment(processSerialNumber,processInstanceId,taskId,fileSource,file){
  let formData = new FormData();
  formData.append("file", file);
  formData.append("processSerialNumber", processSerialNumber);
  formData.append("processInstanceId", processInstanceId);
  formData.append("taskId", taskId);
  formData.append("fileSource", fileSource);
  formData.append("describes", "1");
  return flowableRequest({
    url:'/vue/attachment/upload',
    method: 'post',
    data: formData
  });
}

//下载附件
export function attachmentDownload(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url:'/vue/attachment/attachmentDownload',
    method: 'get',
    params: params
  });
}

//删除附件
export function delAttachment(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url:'/vue/attachment/delFile',
    method: 'post',
    params: params
  });
}
