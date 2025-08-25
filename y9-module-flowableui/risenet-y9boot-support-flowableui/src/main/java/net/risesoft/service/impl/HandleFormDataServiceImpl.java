package net.risesoft.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.HandleFormDataService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
public class HandleFormDataServiceImpl implements HandleFormDataService {

    private final FormDataApi formDataApi;

    @Override
    public void execute(String itemId, List<Map<String, Object>> items, List<String> processSerialNumbers) {
        Y9Result<Map<String, Map<String, Object>>> formDataResult =
            formDataApi.getDataByProcessSerialNumbers(Y9LoginUserHolder.getTenantId(), itemId, processSerialNumbers);
        if (formDataResult.isSuccess()) {
            Map<String, Map<String, Object>> formDataResultData = formDataResult.getData();
            items.forEach(map -> {
                Map<String, Object> formDataMap = formDataResultData.get(map.get("processSerialNumber").toString());
                if (null != formDataMap) {
                    formatFormData(formDataMap);
                    map.putAll(formDataMap);
                } else {
                    LOGGER.error("流程序列号{}对应的表单数据为null！", map.get("processSerialNumber"));
                }
            });
        } else {
            LOGGER.error("获取表单数据失败！");
        }
    }

    void formatFormData(Map<String, Object> map) {
        if (map.get("leaveType") != null) {
            String leaveType = (String)map.get("leaveType");
            ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
            for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                if (leaveType.equals(leaveTypeEnum.getValue())) {
                    map.put("leaveType", leaveTypeEnum.getName());
                    break;
                }
            }
        }
    }
}