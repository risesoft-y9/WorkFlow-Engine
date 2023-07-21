package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.email.CustomEmailApi;
import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "searchService")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private OfficeFollowApi officeFollowManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private CustomEmailApi customEmailApi;

    @Autowired
    private IdentityApi identityManager;

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String taskIds = "", assigneeIds = "", itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        StringBuffer assigneeNames = new StringBuffer();
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Person personTemp = personApi.getPerson(tenantId, assignee);
                        if (personTemp != null) {
                            assigneeNames.append(personTemp.getName());
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
                                Person ownerUser = personApi.getPerson(Y9LoginUserHolder.getTenantId(), assigneeId);
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName() + (ownerUser.getDisabled() ? "(已禁用)" : ""), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames.append("等，共" + iList.size() + "人");
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
                            Person personTemp = personApi.getPerson(tenantId, assignee);
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
            boolean b = taskList.size() > 5;
            if (b) {
                assigneeNames.append("等，共" + taskList.size() + "人");
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames.toString());
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> getEmailList(Integer page, Integer rows, String startDateStr, String endDateStr, Integer fileType, String userName, String title) {
        try {
            Map<String, Object> map = customEmailApi.search(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), page, rows, startDateStr, endDateStr, fileType, userName, title);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> getSearchList(String searchTerm, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            retMap = officeDoneInfoManager.searchAllByUserId(tenantId, userId, searchTerm, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
                try {
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String startTime = hpim.getStartTime().substring(0, 16);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", hpim.getItemName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(hpim.getEndTime())) {
                        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId", listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }

                    int countFollow = officeFollowManager.countByProcessInstanceId(tenantId, userId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0 ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> getYuejianList(String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            retMap = chaoSongInfoManager.searchAllByUserId(tenantId, userId, searchName, itemId, userName, state, year, page, rows);
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), (List<Map<String, Object>>)retMap.get("rows"), "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

}
