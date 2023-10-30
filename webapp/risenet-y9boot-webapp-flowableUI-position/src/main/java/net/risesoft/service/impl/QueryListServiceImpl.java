package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.QueryListApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.QueryListService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Service(value = "queryListService")
@Transactional(readOnly = true)
public class QueryListServiceImpl implements QueryListService {

    @Autowired
    private Item4PositionApi itemManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private OfficeFollow4PositionApi officeFollowManager;

    @Autowired
    private QueryListApi queryListApi;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private IdentityApi identityManager;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfoApi;

    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionManager.getPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser =
                                    positionManager.getPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
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
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            Position personTemp = positionManager.getPosition(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    @Override
    public Y9Page<Map<String, Object>> queryList(String itemId, String state, String createDate, String tableName,
        String searchMapStr, Integer page, Integer rows) {
        ItemPage<ActRuDetailModel> itemPage = new ItemPage<ActRuDetailModel>();
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            itemPage = queryListApi.getQueryList(tenantId, userId, item.getSystemName(), state, createDate, tableName,
                searchMapStr, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> hpiModelList =
                objectMapper.convertValue(list, new TypeReference<List<ActRuDetailModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            Map<String, Object> formDataMap = null;
            for (ActRuDetailModel hpim : hpiModelList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
                String processSerialNumber = hpim.getProcessSerialNumber();
                try {
                    OfficeDoneInfoModel officeDoneInfo =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId);
                    String startTime = officeDoneInfo.getStartTime().substring(0, 16);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", StringUtils.isBlank(officeDoneInfo.getEndTime()) ? "--"
                        : officeDoneInfo.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", officeDoneInfo.getUserComplete());
                    mapTemp.put("creatUserName", officeDoneInfo.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    String level = officeDoneInfo.getUrgency();
                    String number = officeDoneInfo.getDocNumber();
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(officeDoneInfo.getEndTime())) {
                        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                    formDataMap = formDataManager.getData(tenantId, itemId, processSerialNumber);
                    mapTemp.putAll(formDataMap);
                    mapTemp.put("processInstanceId", processInstanceId);
                    int countFollow = officeFollowManager.countByProcessInstanceId(tenantId, userId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0 ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalpages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }
}
