package net.risesoft.api;

import java.util.List;

import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.itemadmin.extend.ItemTodoTaskApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 流程实例相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicProcess")
public class HistoricProcessApiImpl implements HistoricProcessApi {

    private final CustomHistoricProcessService customHistoricProcessService;

    private final CustomTaskService customTaskService;

    private final ItemTodoTaskApi todoTaskApi;

    private final ProcessInstanceApi processInstanceApi;

    private final ChaoSongApi chaoSongApi;

    /**
     * 删除流程实例，在办件设为暂停，办结件加删除标识
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteProcessInstance(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<org.flowable.task.api.Task> list = customTaskService.listByProcessInstanceId(processInstanceId);
        boolean b = customHistoricProcessService.deleteProcessInstance(processInstanceId);
        if (b) {
            if (list != null && !list.isEmpty()) {
                for (org.flowable.task.api.Task task : list) {
                    try {
                        boolean msg1 = todoTaskApi.deleteTodoTaskByTaskId(tenantId, task.getId()).getData();
                        LOGGER.error("##############################统一待办删除：{}#################################", msg1);
                    } catch (Exception e) {
                        LOGGER.error("##############################统一待办删除失败：{}#", e.getMessage());
                    }
                }
            }
            try {
                boolean msg3 = chaoSongApi.deleteByProcessInstanceId(tenantId, processInstanceId).isSuccess();
                LOGGER.error("##############################抄送件删除：{}#################################", msg3);
            } catch (Exception e) {
                LOGGER.error("##########抄送件删除失败：{}#", e.getMessage());
            }
            try {
                boolean msg2 = processInstanceApi.deleteProcessInstance(tenantId, processInstanceId).getData();
                LOGGER.error("##############################协作状态删除：{}#################################", msg2);
            } catch (Exception e) {
                LOGGER.error("##########协作状态删除失败：{}#", e.getMessage());
            }
        }
        return Y9Result.success();
    }

    /**
     * 根据流程实例id获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricProcessInstanceModel> getById(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getById(processInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.historicProcessInstance2Model(hpi));
    }

    /**
     * 根据流程实例id和年度获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricProcessInstanceModel> getByIdAndYear(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getByIdAndYear(processInstanceId, year);
        HistoricProcessInstanceModel hpiModel = null;
        if (hpi != null) {
            hpiModel = FlowableModelConvertUtil.historicProcessInstance2Model(hpi);
        }
        return Y9Result.success(hpiModel);
    }

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return {@code Y9Result<List<HistoricProcessInstanceModel>>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricProcessInstanceModel>> getBySuperProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String superProcessInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<HistoricProcessInstance> hpiList =
            customHistoricProcessService.listBySuperProcessInstanceId(superProcessInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList));
    }

    /**
     * 根据流程实例获取父流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 父流程实例id
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricProcessInstanceModel> getSuperProcessInstanceById(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        HistoricProcessInstance hpi = customHistoricProcessService.getSuperProcessInstanceById(processInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.historicProcessInstance2Model(hpi));
    }

    /**
     * 恢复流程实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recoveryProcess(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = customHistoricProcessService.recoveryProcessInstance(processInstanceId);
        List<org.flowable.task.api.Task> list = customTaskService.listByProcessInstanceId(processInstanceId);
        if (b && (list != null && !list.isEmpty())) {
            for (org.flowable.task.api.Task task : list) {
                try {
                    boolean msg1 = todoTaskApi.recoveryTodoTaskByTaskId(tenantId, task.getId()).getData();
                    LOGGER.info("##############################统一待办还原：{}#################################", msg1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Y9Result.success();
    }

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeProcess(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(customHistoricProcessService.removeProcess(processInstanceId));
    }

    /**
     * 设置流程优先级
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param priority 优先级
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setPriority(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String priority) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customHistoricProcessService.setPriority(processInstanceId, priority);
        return Y9Result.success();
    }
}
