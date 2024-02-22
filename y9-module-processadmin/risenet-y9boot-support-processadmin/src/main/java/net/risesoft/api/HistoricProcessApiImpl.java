package net.risesoft.api;

import java.util.List;

import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/historicProcess")
@Slf4j
public class HistoricProcessApiImpl implements HistoricProcessApi {

    @Autowired
    private CustomHistoricProcessService customHistoricProcessService;

    @Autowired
    private CustomTaskService customTaskService;

    @Autowired
    private TodoTaskApi rpcTodoTaskManager;

    @Autowired
    private ProcessInstanceApi processInstance4PositionApi;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    /**
     * 删除流程实例，在办件设为暂停，办结件加删除标识
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/deleteProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteProcessInstance(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<org.flowable.task.api.Task> list = customTaskService.findByProcessInstanceId(processInstanceId);
        boolean b = customHistoricProcessService.deleteProcessInstance(processInstanceId);
        if (b) {
            if (list != null && list.size() > 0) {
                for (org.flowable.task.api.Task task : list) {
                    try {
                        boolean msg1 = rpcTodoTaskManager.deleteTodoTaskByTaskId(tenantId, task.getId());
                        LOGGER.info("##############################统一待办删除：{}#################################", msg1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                boolean msg2 = processInstance4PositionApi.deleteProcessInstance(tenantId, processInstanceId);
                LOGGER.info("##############################协作状态删除：{}#################################", msg2);
                boolean msg3 = chaoSongInfoManager.deleteByProcessInstanceId(tenantId, processInstanceId);
                LOGGER.info("##############################抄送件删除：{}#################################", msg3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 根据流程实例id获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricProcessInstanceModel getById(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getById(processInstanceId);
        HistoricProcessInstanceModel hpiModel = FlowableModelConvertUtil.historicProcessInstance2Model(hpi);
        return hpiModel;
    }

    /**
     * 根据流程实例id和年度获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping(value = "/getByIdAndYear", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricProcessInstanceModel getByIdAndYear(String tenantId, String processInstanceId, String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getById(processInstanceId, year);
        HistoricProcessInstanceModel hpiModel = null;
        if (hpi != null) {
            hpiModel = FlowableModelConvertUtil.historicProcessInstance2Model(hpi);
        }
        return hpiModel;
    }

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return List<HistoricProcessInstanceModel>
     */
    @Override
    @GetMapping(value = "/getBySuperProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricProcessInstanceModel> getBySuperProcessInstanceId(String tenantId,
        String superProcessInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<HistoricProcessInstance> hpiList =
            customHistoricProcessService.getBySuperProcessInstanceId(superProcessInstanceId);
        List<HistoricProcessInstanceModel> hpiMoldelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        return hpiMoldelList;
    }

    /**
     * 根据流程实例获取父流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 父流程实例id
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping(value = "/getSuperProcessInstanceById", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricProcessInstanceModel getSuperProcessInstanceById(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getSuperProcessInstanceById(processInstanceId);
        HistoricProcessInstanceModel hpiModel = FlowableModelConvertUtil.historicProcessInstance2Model(hpi);
        return hpiModel;
    }

    /**
     * 恢复流程实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/recoveryProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean recoveryProcess(String tenantId, String userId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = customHistoricProcessService.recoveryProcessInstance(processInstanceId);
        List<org.flowable.task.api.Task> list = customTaskService.findByProcessInstanceId(processInstanceId);
        if (b) {
            if (list != null && list.size() > 0) {
                for (org.flowable.task.api.Task task : list) {
                    try {
                        boolean msg1 = rpcTodoTaskManager.recoveryTodoTaskBytaskId(tenantId, task.getId());
                        LOGGER.info("##############################统一待办还原：{}#################################", msg1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return b;
    }

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/removeProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeProcess(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = customHistoricProcessService.removeProcess(processInstanceId);
        return b;
    }

    /**
     * 彻底删除流程实例,岗位
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/removeProcess4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeProcess4Position(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = customHistoricProcessService.removeProcess4Position(processInstanceId);
        return b;
    }

    /**
     * 设置流程优先级
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param priority 优先级
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/setPriority", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setPriority(String tenantId, String processInstanceId, String priority) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customHistoricProcessService.setPriority(processInstanceId, priority);
    }
}
