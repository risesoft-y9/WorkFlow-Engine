import Request from '@/api/lib/request';
var flowableRequest = new Request();

//获取协作状态列表
export function processInstanceList(page,rows,title){
  const params = {
    page:page,
    rows:rows,
    title:title
  };
  return flowableRequest({
    url: "/vue/processInstance/processInstanceList",
    method: 'get',
    params: params
  });
}

