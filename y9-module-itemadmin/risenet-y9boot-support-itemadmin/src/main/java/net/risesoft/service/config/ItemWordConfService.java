package net.risesoft.service.config;

import java.util.List;


import net.risesoft.entity.ItemWordConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemWordConfService {

    void bindRole(String id, String roleIds);

    void copyWordConf(String itemId, String processDefinitionId);

    void delete(String id);

    void deleteRole(String id, String roleId);

    Boolean getPermissionWord(String positionId, String itemId, String processDefinitionId, String taskDefKey, String wordType);

    List<ItemWordConf> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
                                                                       String taskDefKey);

    void save(String wordType, String itemId, String processDefinitionId, String taskDefKey);
}
