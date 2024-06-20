import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取关注列表
export function followList(searchName,page,rows){
  const params = {
    searchName:searchName,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/officeFollow/followList",
    method: 'get',
    params: params
  });
}

//获取左侧关注菜单数字
export function getFollowCount(){
  const params = {};
  return flowableRequest({
    url: "/vue/officeFollow/getFollowCount",
    method: 'get',
    params: params
  });
}

//保存关注
export function saveOfficeFollow(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url: "/vue/officeFollow/saveOfficeFollow",
    method: 'post',
    params: params
  });
}

//取消关注
export function delOfficeFollow(processInstanceId){
  const params = {
    processInstanceIds:processInstanceId
  };
  return flowableRequest({
    url: "/vue/officeFollow/delOfficeFollow",
    method: 'post',
    params: params
  });
}
