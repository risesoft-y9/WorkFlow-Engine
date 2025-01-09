package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ItemMonitorApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.ItemMonitorService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemMonitorServiceImpl implements ItemMonitorService {

    private final TaskApi taskApi;

    private final ItemApi itemApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final IdentityApi identityApi;

    private final ItemMonitorApi itemMonitorApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final FormDataApi formDataApi;

    @Override
    public Y9Page<Map<String, Object>> pageTodoList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageDoingList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageDoneList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageAllList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemMonitorApi.findBySystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    boolean isSubProcessChildNode = processDefinitionApi.isSubProcessChildNode(tenantId,
                        taskList.get(0).getProcessDefinitionId(), taskList.get(0).getTaskDefinitionKey()).getData();
                    if (isSubProcessChildNode) {
                        // 针对SubProcess
                        mapTemp.put("taskName", "送会签");
                        mapTemp.put("taskAssignee", "");
                    } else {
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskAssignee", listTemp.get(0));
                    }
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName",
                        orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /**
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO.getValue())) {
                        mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    } else {
                        mapTemp.put(SysVariables.ITEMBOX, StringUtils.isBlank(processParam.getCompleter())
                            ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
                    }
                    mapTemp.put(SysVariables.ITEMBOX, StringUtils.isBlank(processParam.getCompleter())
                        ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取已办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
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
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");// 并行时，领导选取时存在顺序，因此这里也存在顺序
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

    @Override
    public Map<String, Object> pageRecycleList(String itemId, Integer page, Integer rows) {
        return null;
    }
}
