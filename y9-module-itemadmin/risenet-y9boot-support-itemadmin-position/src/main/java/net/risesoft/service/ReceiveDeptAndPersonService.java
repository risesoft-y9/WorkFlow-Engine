package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
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
    Y9Result<String> delDepartment(String id);

    /**
     * 删除人员
     *
     * @param id
     * @return
     */
    Y9Result<String> delPerson(String id);

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
    List<Map<String, Object>> personList(String deptId);

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
    Y9Result<String> saveDepartment(String id);

    /**
     * 保存排序
     *
     * @param ids
     * @return
     */
    Y9Result<String> saveOrder(String ids);

    /**
     * 保存收发岗位
     *
     * @param deptId
     * @param ids
     * @return
     */
    Y9Result<String> savePosition(String deptId, String ids);

    /**
     *
     * Description: 保存是否可发送
     *
     * @param receive
     * @param ids
     * @return
     */
    Y9Result<String> setReceive(boolean receive, String ids);

    /**
     *
     * Description: 保存是否可发送
     *
     * @param send
     * @param ids
     * @return
     */
    Y9Result<String> setSend(boolean send, String ids);

}
