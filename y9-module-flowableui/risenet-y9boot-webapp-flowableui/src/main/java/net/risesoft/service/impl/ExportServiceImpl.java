package net.risesoft.service.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemAllApi;
import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ExcelHandlerService;
import net.risesoft.service.ExportService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

    private final ProcessParamApi processParamApi;

    private final FormDataApi formDataApi;

    private final ItemAllApi itemAllApi;

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private final IdentityApi identityApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final ExcelHandlerService excelHandlerService;

    private final OptionClassApi optionClassApi;

    private static Map<String, Object> map = new HashMap<>();

    @Override
    public void select(OutputStream outStream, String[] processSerialNumbers, String[] columns, String itemBox) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Y9Result<List<ActRuDetailModel>> listY9Result =
            itemAllApi.searchByProcessSerialNumbers(tenantId, positionId, processSerialNumbers);
        List<Map<String, Object>> mapList = new ArrayList<>();
        int serialNumber = 1;
        Map<String, Object> mapTemp;
        ProcessParamModel processParam;
        String processInstanceId;
        Map<String, Object> formData;
        for (ActRuDetailModel ardModel : listY9Result.getData()) {
            mapTemp = new HashMap<>(16);
            processInstanceId = ardModel.getProcessInstanceId();
            try {
                String processSerialNumber = ardModel.getProcessSerialNumber();
                mapTemp.put("serialNumber", serialNumber++);
                processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                mapTemp.put("systemCNName", processParam.getSystemCnName());
                mapTemp.put("bureauName", processParam.getHostDeptName());
                formData = formDataApi.getData(tenantId, processParam.getItemId(), processSerialNumber).getData();
                mapTemp.putAll(handleFormData(formData));
                // 当前办理人和当前环节
                switch (itemBox) {
                    case "todo":
                        mapTemp.putAll(getTaskNameAndUserName4Todo(ardModel));
                        break;
                    case "recycle":
                    case "monitorRecycle":
                    case "monitorDoing":
                        mapTemp.putAll(getTaskNameAndUserName4Doing(processParam));
                        break;
                    case "monitorDone":
                        mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                        break;
                    case "haveDone":
                        if (ardModel.isEnded()) {
                            mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                        } else {
                            mapTemp.putAll(getTaskNameAndUserName4Doing(processParam));
                        }
                        break;
                    case "all":
                        if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO.getValue())) {
                            mapTemp.putAll(getTaskNameAndUserName4Todo(ardModel));
                        } else {
                            if (!ardModel.isEnded()) {
                                mapTemp.putAll(getTaskNameAndUserName4Doing(processParam));
                            } else {
                                mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("获取已办列表失败" + processInstanceId, e);
            }
            mapList.add(mapTemp);
        }
        excelHandlerService.export(outStream, mapList, columns);
    }

    private Map<String, Object> getTaskNameAndUserName4Todo(ActRuDetailModel ardModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", ardModel.getTaskDefName());
        map.put("taskAssignee", ardModel.getAssigneeName());
        return map;
    }

    private Map<String, Object> getTaskNameAndUserName4Doing(ProcessParamModel processParam) {
        String tenantId = Y9LoginUserHolder.getTenantId(), processInstanceId = processParam.getProcessInstanceId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        List<SignDeptDetailModel> signDeptDetailList = signDeptDetailApi
            .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processParam.getProcessSerialNumber())
            .getData();
        String userName, taskName;
        Map<String, Object> map = new HashMap<>();
        boolean isSign = signDeptDetailList.stream()
            .anyMatch(sdd -> sdd.getStatus().equals(SignDeptDetailStatusEnum.DOING.getValue()));
        // 当前节点如果是子流程的节点
        if (isSign) {
            taskName = signDeptDetailList.get(0).getTaskName();
            userName = signDeptDetailList.get(0).getSenderName();
        } else {
            List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
            taskName = taskList.get(0).getName();
            userName = listTemp.get(0);
        }
        map.put("taskName", taskName);
        map.put("taskAssignee", userName);
        return map;
    }

    private Map<String, Object> getTaskNameAndUserName4Done(ProcessParamModel processParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", "已办结");
        map.put("taskAssignee", processParam.getCompleter());
        return map;
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                            } else {
                                assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                break;
                            }
                            j++;
                        }
                    }
                }
            } else {
                String assignee = task.getAssignee();
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(assigneeNames);
        return list;
    }

    private Map<String, Object> handleFormData(Map<String, Object> formData) {
        if (map.isEmpty()) {
            optionClassApi.findAll(Y9LoginUserHolder.getTenantId()).getData().forEach(item -> {
                map.put(item.getType() + "." + item.getCode(), item.getName());
            });
        }
        Map<String, Object> formDataTemp = new HashMap<>(formData);
        for (Map.Entry<String, Object> entry : formDataTemp.entrySet()) {
            if (null != entry.getValue()) {
                if ("[]".equals(entry.getValue())) {
                    entry.setValue("否");
                } else if ("[\"1\"]".equals(entry.getValue())) {
                    entry.setValue("是");
                }
                if (StringUtils.isNotBlank(entry.getValue().toString())
                    && null != map.get(entry.getKey() + "." + entry.getValue())) {
                    entry.setValue(map.get(entry.getKey() + "." + entry.getValue()));
                }
            } else {
                entry.setValue("");
            }
        }
        return formDataTemp;
    }
}
