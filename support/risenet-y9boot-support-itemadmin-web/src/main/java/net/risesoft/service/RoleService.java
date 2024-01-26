package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.platform.OrgUnit;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface RoleService {

    /**
     * 委办局树查找
     * 
     * @param name
     * @param nodeId
     * @return
     */
    List<Map<String, Object>> bureauTreeSearch(String name, String nodeId);

    /**
     * Description:
     * 
     * @param id
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> findCsUser(String id, Integer principalType, String processInstanceId);

    /**
     * Description: 抄送选人搜索
     * 
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> findCsUserSearch(String name, Integer principalType, String processInstanceId);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param principalType
     * @param id
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> findPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId);

    /**
     * Description:
     * 
     * @param processDefinitionId
     * @param taskDefKey
     * @param principalType
     * @param id
     * @param name
     * @return
     */
    List<Map<String, Object>> findPermUserByName(String processDefinitionId, String taskDefKey, Integer principalType,
        String id, String name);

    /**
     * Description:
     * 
     * @param name
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> findPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    List<Map<String, Object>> findPermUserSendReceive(String id);

    /**
     * Description:
     * 
     * @param tenantId
     * @param parentId
     * @return
     */
    OrgUnit getParent(String tenantId, String parentId);

    /**
     * Description:
     * 
     * @param tenantId
     * @param nodeId
     * @param parentId
     * @return
     */
    OrgUnit getParent(String tenantId, String nodeId, String parentId);

    /**
     * Description:
     * 
     * @param tenantId
     * @param nodeId
     * @param parentId
     * @param orgUnitList
     * @param isParent
     */
    void recursionUpToOrg(String tenantId, String nodeId, String parentId, List<OrgUnit> orgUnitList, boolean isParent);
}
