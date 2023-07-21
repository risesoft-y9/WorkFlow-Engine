package net.risesoft.service;

import java.util.Map;

import net.risesoft.entity.ReceiveDepartment;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ReceiveDeptAndPersonService {

    /**
     * 获取部门收发人员数量
     * 
     * @param id
     * @return
     */
    Integer countByDeptId(String id);

    /**
     * 删除收发单位
     * 
     * @param id
     * @return
     */
    Map<String, Object> delDepartment(String id);

    /**
     * 删除人员
     * 
     * @param id
     * @return
     */
    Map<String, Object> delPerson(String id);

    /**
     * 根据部门id获取信息
     * 
     * @param id
     * @return
     */
    ReceiveDepartment findByDeptId(String id);

    /**
     * 获取单位收发人员
     * 
     * @param deptId
     * @return
     */
    Map<String, Object> personList(String deptId);

    /**
     * Description:
     * 
     * @param receiveDeptAndPerson
     * @return
     */
    ReceiveDepartment save(ReceiveDepartment receiveDeptAndPerson);

    /**
     * 保存收发部门
     * 
     * @param id
     * @return
     */
    Map<String, Object> saveDepartment(String id);

    /**
     * 保存排序
     * 
     * @param ids
     * @return
     */
    Map<String, Object> saveOrder(String ids);

    /**
     * 保存收发人员
     * 
     * @param deptId
     * @param ids
     * @return
     */
    Map<String, Object> savePerson(String deptId, String ids);

    /**
     * Description: 保存是否可发送
     * 
     * @param receive
     * @param ids
     * @return
     */
    Map<String, Object> setReceive(boolean receive, String ids);

    /**
     * Description: 保存是否可发送
     * 
     * @param send
     * @param ids
     * @return
     */
    Map<String, Object> setSend(boolean send, String ids);

}
