import Request from '@/api/lib/request';

var flowableRequest = new Request();

/**
 * 获取快捷发送人
 * @param itemId 
 * @param taskKey 
 * @returns 
 */
export function getAssignee(itemId,taskKey){
  const params = {
    itemId,
    taskKey
  };
  return flowableRequest({
    url:'/vue/quickSend/getAssignee',
    method: 'get',
    params: params
  });
}

/**
 * 保存快捷发送人
 * @param itemId 
 * @param taskKey 
 * @param assignee 
 * @returns 
 */
export function saveOrUpdate(itemId,taskKey,assignee){
  const params = {
    itemId,
    taskKey,
    assignee
  };
  return flowableRequest({
    url:'/vue/quickSend/saveOrUpdate',
    method: 'POST',
    params: params
  });
}

