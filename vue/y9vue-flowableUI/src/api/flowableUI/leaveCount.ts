import Request from '@/api/lib/request';
var flowableRequest = new Request();

//获取请假统计列表
export function countList(leaveType,userName,deptName,startTime,endTime){
  const params = {
    leaveType:leaveType,
    userName:userName,
    deptName:deptName,
    startTime:startTime,
    endTime:endTime
  };
  return flowableRequest({
    url: "/vue/leaveCount/countList",
    method: 'get',
    params: params
  });
}

