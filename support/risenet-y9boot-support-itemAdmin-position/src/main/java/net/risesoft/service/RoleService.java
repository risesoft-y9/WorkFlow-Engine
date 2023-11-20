package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.platform.OrgUnit;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface RoleService {

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
    List<Map<String, Object>> findPermUser(String itemId, String processDefinitionId, String taskDefKey, Integer principalType, String id, String processInstanceId);

    List<OrgUnit> findPermUser4SUbmitTo(String itemId, String processDefinitionId, String taskDefKey, String processInstanceId);

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
    List<Map<String, Object>> findPermUserByName(String name, String itemId, String processDefinitionId, String taskDefKey, Integer principalType, String processInstanceId);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    List<Map<String, Object>> findPermUserSendReceive(String id);
}
