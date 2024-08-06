package net.risesoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

public class DocumentUtil {

    public Map<String, Object> documentDetail(String itemId, String processDefinitionId, String processSerialNumber,
        String processInstanceId, String taskDefinitionKey, String taskId, String itembox, String activitiUser,
        String formIds, String formNames) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        Map<String, Object> map = new HashMap<>(16);
        // 表单数据
        List<String> formIdList = Y9Util.stringToList(formIds, SysVariables.COMMA);
        List<String> formNameList = Y9Util.stringToList(formNames, SysVariables.COMMA);
        List<Map<String, Object>> formListMap = new ArrayList<>();
        for (int i = 0; i < formIdList.size(); i++) {
            Map<String, Object> formMap = new HashMap<>(16);
            formMap.put("formId", formIdList.get(i));
            formMap.put("formName", formNameList.get(i));
            Y9Result<Map<String, Object>> y9Result =
                Y9Context.getBean(FormDataApi.class).getFromData(tenantId, formIdList.get(i), processSerialNumber);
            formMap.putAll(y9Result.isSuccess() ? y9Result.getData() : new HashMap<>(16));
            formListMap.add(formMap);
        }
        map.put("formDataListMap", formListMap);

        // 意见框
        List<Map<String, Object>> opinioListMap = new ArrayList<>();
        List<ItemOpinionFrameBindModel> opinionFrameList = Y9Context.getBean(ItemOpinionFrameBindApi.class)
            .findByItemIdAndProcessDefinitionId(tenantId, itemId, processDefinitionId).getData();
        for (ItemOpinionFrameBindModel opinionFrame : opinionFrameList) {
            Map<String, Object> opinionMap = new HashMap<>(16);
            String opinionFrameMark = opinionFrame.getOpinionFrameMark();
            List<OpinionListModel> listMap =
                Y9Context.getBean(OpinionApi.class).personCommentList(tenantId, userId, processSerialNumber, taskId,
                    itembox, opinionFrameMark, itemId, taskDefinitionKey, activitiUser, "").getData();
            opinionMap.put("opinionFrameMark", opinionFrameMark);
            opinionMap.put("opinionFrameName", opinionFrame.getOpinionFrameName());
            opinionMap.put("opinionList", listMap);
            if (!opinioListMap.contains(opinionMap)) {
                opinioListMap.add(opinionMap);
            }
        }
        map.put("opinioListMap", opinioListMap);

        // 附件
        Y9Page<AttachmentModel> y9Page =
            Y9Context.getBean(AttachmentApi.class).getAttachmentList(tenantId, processSerialNumber, "", 1, 100);
        map.put("fileAttachment", y9Page);

        // 正文
        TransactionWordModel fileDocument = Y9Context.getBean(TransactionWordApi.class)
            .findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
        map.put("fileDocument", fileDocument);
        return map;
    }

}
