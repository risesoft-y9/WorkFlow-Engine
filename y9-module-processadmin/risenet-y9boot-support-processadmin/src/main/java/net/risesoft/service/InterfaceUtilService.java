package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;


import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.config.Y9ProcessAdminProperties;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
@Slf4j
@Service(value = "interfaceUtilService")
@RequiredArgsConstructor
public class InterfaceUtilService {

    private final ProcessParamApi processParamApi;
    private final ItemInterfaceApi itemInterfaceApi;
    private final AsyncUtilService asyncUtilService;
    private final InterfaceMethodService interfaceMethodService;
    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    /**
     * 流程启动，办结接口调用
     *
     * @param executionEntity
     * @param variables
     * @param condition
     * @throws Exception
     */
    public void interfaceCallByProcess(ExecutionEntityImpl executionEntity, Map<String, Object> variables,
        String condition) throws Exception {
        String processDefinitionId = executionEntity.getProcessDefinitionId();
        String taskDefinitionKey = "";
        String tenantId = "";
        String processSerialNumber = "";
        String processInstanceId = executionEntity.getProcessInstanceId();
        String itemId = "";
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Y9Result<List<InterfaceModel>> y9Result = null;
        Boolean interfaceSwitch = y9ProcessAdminProperties.getInterfaceSwitch();
        if (!interfaceSwitch) {
            return;
        }
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processSerialNumber = (String)variables.get("processSerialNumber");
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            itemId = processParamModel.getItemId();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            interfaceMethodService.saveErrorLog(tenantId, processInstanceId, "", "", "interfaceCallByProcess", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asyncUtilService.asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info,
                        processInstanceId, processDefinitionId, "", "", null);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, "", "",
                        null);

                }
            }
        }

    }

    /**
     * 路由经过接口调用
     *
     * @param flow
     * @param condition
     * @throws Exception
     */
    public void interfaceCallBySequenceFlow(FlowableSequenceFlowTakenEventImpl flow, String condition)
        throws Exception {
        String processDefinitionId = flow.getProcessDefinitionId();
        String taskDefinitionKey = flow.getId();
        String tenantId = "";
        String processInstanceId = "";
        String processSerialNumber = "";
        String itemId = "";
        Y9Result<List<InterfaceModel>> y9Result = null;
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Boolean interfaceSwitch = y9ProcessAdminProperties.getInterfaceSwitch();
        if (!interfaceSwitch) {
            return;
        }
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processInstanceId = flow.getProcessInstanceId();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (processParamModel == null) {// 起草第一步的线，processParamModel为null，不需要调用接口
                LOGGER.info("*********************流程实例ID:{}", processInstanceId);
                return;
            }
            itemId = processParamModel.getItemId();
            processSerialNumber = processParamModel.getProcessSerialNumber();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            interfaceMethodService.saveErrorLog(tenantId, processInstanceId, flow.getId(), flow.getId(),
                "interfaceCallBySequenceFlow", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asyncUtilService.asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info,
                        processInstanceId, processDefinitionId, flow.getId(), taskDefinitionKey, null);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, processDefinitionId,
                        flow.getId(), taskDefinitionKey, null);

                }
            }
        }

    }

    /**
     * 任务创建，完成接口调用
     *
     * @param task
     * @param variables
     * @param condition
     * @throws Exception
     */
    public void interfaceCallByTask(DelegateTask task, Map<String, Object> variables, String condition)
        throws Exception {
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String tenantId = "";
        String processSerialNumber = "";
        String itemId = "";
        Integer loopCounter = null;
        String orgUnitId = task.getAssignee();
        Y9Result<List<InterfaceModel>> y9Result = null;
        Boolean interfaceSwitch = y9ProcessAdminProperties.getInterfaceSwitch();
        if (!interfaceSwitch) {
            return;
        }
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processSerialNumber = (String)variables.get("processSerialNumber");
            loopCounter = variables.get("loopCounter") != null ? (Integer)variables.get("loopCounter") : null;
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            itemId = processParamModel.getItemId();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            interfaceMethodService.saveErrorLog(tenantId, task.getProcessInstanceId(), task.getId(),
                task.getTaskDefinitionKey(), "interfaceCallByTask", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asyncUtilService.asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info,
                        task.getProcessInstanceId(), processDefinitionId, task.getId(), task.getTaskDefinitionKey(),
                        loopCounter);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, task.getProcessInstanceId(), processDefinitionId,
                        task.getId(), task.getTaskDefinitionKey(), loopCounter);

                }
            }
        }
    }

    /**
     * 同步调用接口
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @throws Exception
     */
    public void syncInterface(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET.getValue())) {
            interfaceMethodService.getMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId,
                taskId, taskKey, loopCounter);

        } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST.getValue())) {
            interfaceMethodService.postMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId,
                taskId, taskKey, loopCounter);
        }
    }

}
