package net.risesoft.service;

import java.util.List;


import net.risesoft.entity.SignDeptInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SignDeptInfoService {

    void deleteById(String id);

    List<SignDeptInfo> getSignDeptList(String processInstanceId, String deptType);

    void saveSignDept(String processInstanceId, String deptType, String deptIds);

    void saveSignDeptInfo(String id, String userName);
}
