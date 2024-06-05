package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemDoneApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoneService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

@RequiredArgsConstructor
@Service(value = "doneService")
@Transactional(readOnly = true)
public class DoneServiceImpl implements DoneService {

    private final Item4PositionApi item4PositionApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final FormDataApi formDataApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final ItemDoneApi itemDoneApi;

    private final ProcessParamApi processParamApi;

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        retMap = officeDoneInfo4PositionApi.searchByPositionId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
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
                int chaosongNum = chaoSong4PositionApi.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
                mapTemp.put("chaosongNum", chaosongNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        retMap = officeDoneInfo4PositionApi.searchByPositionId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<OfficeDoneInfoModel> list = (List<OfficeDoneInfoModel>)retMap.get("rows");
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<OfficeDoneInfoModel>>() {});
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
                int chaosongNum = chaoSong4PositionApi.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
                mapTemp.put("chaosongNum", chaosongNum);
                formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
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

                int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, userId, processInstanceId);
                mapTemp.put("follow", countFollow > 0 ? true : false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
    }

    @Override
    public Y9Page<Map<String, Object>> searchList(String itemId, String tableName, String searchMapStr, Integer page, Integer rows) {
        ItemPage<ActRuDetailModel> itemPage = new ItemPage<ActRuDetailModel>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemDoneApi.findByUserIdAndSystemName(tenantId, userId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoneApi.searchByUserIdAndSystemName(tenantId, userId, item.getSystemName(), tableName, searchMapStr, page, rows);
            }
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<ActRuDetailModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            Map<String, Object> formDataMap = null;
            for (ActRuDetailModel hpim : hpiModelList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
                String processSerialNumber = hpim.getProcessSerialNumber();
                try {
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    // String processDefinitionId = hpim.getProcessDefinitionId();
                    // String startTime = hpim.getStartTime().substring(0, 16), endTime = hpim.getEndTime().substring(0,
                    // 16);
                    // mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    // mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", sdf.format(hpim.getLastTime()));
                    mapTemp.put("taskDefinitionKey", "");

                    ProcessParamModel processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);

                    mapTemp.put("user4Complete", processParam.getCompleter());
                    int chaosongNum = chaoSong4PositionApi.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
                    mapTemp.put("chaosongNum", chaosongNum);
                    formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
                    /*if (formDataMap.get("leaveType") != null) {
                        String leaveType = (String)formDataMap.get("leaveType");
                        for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                            if (leaveType.equals(leaveTypeEnum.getValue())) {
                                formDataMap.put("leaveType", leaveTypeEnum.getName());
                                break;
                            }
                        }
                    }*/
                    mapTemp.putAll(formDataMap);
                    mapTemp.put("processInstanceId", processInstanceId);
                    int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, userId, processInstanceId);
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
