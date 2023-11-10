package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OrganWordApi {

    /**
     * 检查编号是否已经被使用了
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param characterValue 机关代字
     * @param custom 机关代字标志
     * @param year 文号年份
     * @param numberTemp numberTemp
     * @param itemId 事项id
     * @param common common
     * @param processSerialNumber 流程编号
     * @return Integer
     * @throws Exception Exception
     */
    Integer checkNumberStr(String tenantId, String userId, String characterValue, String custom, Integer year,
        Integer numberTemp, String itemId, Integer common, String processSerialNumber) throws Exception;

    /**
     * 判断机构代字custom在某个流程实例中是否已经编号,没有编号的话就查找有权限的编号的机关代字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itembox 办件状态:todo(待办),doing(在办),done(办结)
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> exist(String tenantId, String userId, String custom, String processSerialNumber,
        String processInstanceId, String itembox) throws Exception;

    /**
     *
     * Description: 查找有权限的机构代字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @throws Exception Exception
     */
    List<Map<String, Object>> findByCustom(String tenantId, String userId, String custom, String itemId,
        String processDefinitionId, String taskDefKey) throws Exception;

    /**
     * 获取编号
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param year 文号年份
     * @param common common
     * @param itemId 事项id
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getNumber(String tenantId, String userId, String custom, String characterValue, Integer year,
        Integer common, String itemId) throws Exception;

    /**
     * 获取编号的数字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param year 文号年份
     * @param common common
     * @param itemId 事项id
     * @return Integer
     * @throws Exception Exception
     */
    Integer getNumberOnly(String tenantId, String userId, String custom, String characterValue, Integer year,
        Integer common, String itemId) throws Exception;

}
