package net.risesoft.service;

import java.util.List;

import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
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
    List<ItemRoleOrgUnitModel> findCsUser(String id, Integer principalType, String processInstanceId);

    /**
     * Description: 抄送选人搜索
     *
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<ItemRoleOrgUnitModel> findCsUserSearch(String name, Integer principalType, String processInstanceId);

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
    List<ItemRoleOrgUnitModel> findPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId);

    List<OrgUnit> findPermUser4SUbmitTo(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId);

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
    List<ItemRoleOrgUnitModel> findPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    List<ItemRoleOrgUnitModel> findPermUserSendReceive(String id);
}
