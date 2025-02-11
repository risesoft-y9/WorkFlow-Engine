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
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param principalType
     * @param id
     * @param processInstanceId
     * @return
     */
    List<ItemRoleOrgUnitModel> listAllPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId);

    /**
     * Description:
     *
     * @param id
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<ItemRoleOrgUnitModel> listCsUser(String id, Integer principalType, String processInstanceId);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    List<ItemRoleOrgUnitModel> listCsUser4Bureau(String id);

    /**
     * Description: 抄送选人搜索
     *
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    List<ItemRoleOrgUnitModel> listCsUserSearch(String name, Integer principalType, String processInstanceId);

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
    List<ItemRoleOrgUnitModel> listPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId);

    List<OrgUnit> listPermUser4SUbmitTo(String itemId, String processDefinitionId, String taskDefKey,
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
    List<ItemRoleOrgUnitModel> listPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    List<ItemRoleOrgUnitModel> listPermUserSendReceive(String id);
}
