package net.risesoft.api.processadmin;

import net.risesoft.model.processadmin.HistoricActivityInstanceModel;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricActivityApi {

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;HistoricActivityInstanceModel&gt;
     */
    List<HistoricActivityInstanceModel> getByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @param year              年度
     * @return List<HistoricActivityInstanceModel>
     */
    List<HistoricActivityInstanceModel> getByProcessInstanceIdAndYear(String tenantId, String processInstanceId,
                                                                      String year);
}
