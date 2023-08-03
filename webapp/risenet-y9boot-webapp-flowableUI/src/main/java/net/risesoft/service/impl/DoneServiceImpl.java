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

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoneService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "doneService")
@Transactional(readOnly = true)
public class DoneServiceImpl implements DoneService {

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private OfficeFollowApi officeFollowManager;

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = itemManager.getByItemId(tenantId, itemId);
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        retMap = officeDoneInfoManager.searchByUserId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiList =
            objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp = null;
        for (OfficeDoneInfoModel hpim : hpiList) {
            mapTemp = new HashMap<String, Object>(16);
            try {
                String processInstanceId = hpim.getProcessInstanceId();
                String processDefinitionId = hpim.getProcessDefinitionId();
                String startTime = hpim.getStartTime().substring(0, 16), endTime = hpim.getEndTime().substring(0, 16);
                String processSerialNumber = hpim.getProcessSerialNumber();
                String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                String level = hpim.getUrgency();
                String number = hpim.getDocNumber();
                String completer = StringUtils.isBlank(hpim.getUserComplete()) ? "无" : hpim.getUserComplete();
                mapTemp.put("itemName", itemName);
                mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                mapTemp.put("processInstanceId", processInstanceId);
                mapTemp.put("processDefinitionId", processDefinitionId);
                mapTemp.put("processDefinitionKey", processDefinitionKey);
                mapTemp.put("startTime", startTime);
                mapTemp.put("endTime", endTime);
                mapTemp.put("taskDefinitionKey", "");
                mapTemp.put("user4Complete", completer);
                mapTemp.put("itemId", itemId);
                mapTemp.put("level", level);
                mapTemp.put("number", number);
                int chaosongNum =
                    chaoSongInfoManager.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
                mapTemp.put("chaosongNum", chaosongNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
            Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = itemManager.getByItemId(tenantId, itemId);
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        retMap = officeDoneInfoManager.searchByUserId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<OfficeDoneInfoModel> list = (List<OfficeDoneInfoModel>)retMap.get("rows");
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiModelList =
            objectMapper.convertValue(list, new TypeReference<List<OfficeDoneInfoModel>>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp = null;
        Map<String, Object> formDataMap = null;
        ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
        for (OfficeDoneInfoModel hpim : hpiModelList) {
            mapTemp = new HashMap<String, Object>(16);
            String processInstanceId = hpim.getProcessInstanceId();
            try {
                String processDefinitionId = hpim.getProcessDefinitionId();
                String startTime = hpim.getStartTime().substring(0, 16), endTime = hpim.getEndTime().substring(0, 16);
                String processSerialNumber = hpim.getProcessSerialNumber();
                String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                String level = hpim.getUrgency();
                String number = hpim.getDocNumber();
                String completer = StringUtils.isBlank(hpim.getUserComplete()) ? "无" : hpim.getUserComplete();
                mapTemp.put("itemName", itemName);
                mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                mapTemp.put("processDefinitionId", processDefinitionId);
                mapTemp.put("processDefinitionKey", processDefinitionKey);
                mapTemp.put("startTime", startTime);
                mapTemp.put("endTime", endTime);
                mapTemp.put("taskDefinitionKey", "");
                mapTemp.put("user4Complete", completer);
                mapTemp.put("itemId", itemId);
                mapTemp.put("level", level);
                mapTemp.put("number", number);
                int chaosongNum =
                    chaoSongInfoManager.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
                mapTemp.put("chaosongNum", chaosongNum);
                formDataMap = formDataManager.getData(tenantId, itemId, processSerialNumber);
                if (formDataMap.get("leaveType") != null) {
                    String leaveType = (String)formDataMap.get("leaveType");
                    for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                        if (leaveType.equals(leaveTypeEnum.getValue())) {
                            formDataMap.put("leaveType", leaveTypeEnum.getName());
                            break;
                        }
                    }
                }
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
        return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
            Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
    }
}
