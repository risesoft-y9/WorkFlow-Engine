package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.Person;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ReceiveDeptAndPersonApi {

    /**
     * 根据name模糊搜索收发单位
     * 
     * @param tenantId 租户id
     * @param name 搜索名称
     * @return Map&lt;String, Object&gt;
     */
    List<Map<String, Object>> findByDeptNameLike(String tenantId, String name);

    /**
     * 获取所有收发单位
     * 
     * @param tenantId
     * @return
     */
    List<Map<String, Object>> getReceiveDeptTree(String tenantId);

    /**
     * 
     * Description: 获取所有收发单位
     * 
     * @param tenantId
     * @param orgUnitId
     * @param name
     * @return
     */
    List<Map<String, Object>> getReceiveDeptTreeById(String tenantId, String orgUnitId, String name);

    /**
     * 
     * Description: 根据收发单位id,获取人员集合
     * 
     * @param tenantId
     * @param deptId
     * @return
     */
    public List<Person> getSendReceiveByDeptId(String tenantId, String deptId);

    /**
     * 根据人员id,获取对应的收发单位
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Map&lt;String, Object&gt;
     */
    public List<Map<String, Object>> getSendReceiveByUserId(String tenantId, String userId);
}
