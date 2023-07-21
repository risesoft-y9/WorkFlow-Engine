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
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class DocumentUtil {

    public Map<String, Object> documentDetail(String itemId, String processDefinitionId, String processSerialNumber, String processInstanceId, String taskDefinitionKey, String taskId, String itembox, String activitiUser, String formIds, String formNames) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        // 表单数据
        List<String> formIdList = Y9Util.stringToList(formIds, SysVariables.COMMA);
        List<String> formNameList = Y9Util.stringToList(formNames, SysVariables.COMMA);
        List<Map<String, Object>> formListMap = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < formIdList.size(); i++) {
            Map<String, Object> formMap = new HashMap<String, Object>(16);
            formMap.put("formId", formIdList.get(i));
            formMap.put("formName", formNameList.get(i));
            Map<String, Object> dataMap = Y9Context.getBean(FormDataApi.class).getFromData(tenantId, formIdList.get(i), processSerialNumber);
            formMap.putAll(dataMap);
            formListMap.add(formMap);
        }
        map.put("formDataListMap", formListMap);

        // 意见框
        List<Map<String, Object>> opinioListMap = new ArrayList<Map<String, Object>>();
        List<ItemOpinionFrameBindModel> opinionFrameList = Y9Context.getBean(ItemOpinionFrameBindApi.class).findByItemId(tenantId, itemId);
        for (ItemOpinionFrameBindModel opinionFrame : opinionFrameList) {
            Map<String, Object> opinionMap = new HashMap<String, Object>(16);
            String opinionFrameMark = opinionFrame.getOpinionFrameMark();
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            listMap = Y9Context.getBean(OpinionApi.class).personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, opinionFrameMark, itemId, taskDefinitionKey, activitiUser);
            opinionMap.put("opinionFrameMark", opinionFrameMark);
            opinionMap.put("opinionFrameName", opinionFrame.getOpinionFrameName());
            opinionMap.put("opinionList", listMap);
            if (!opinioListMap.contains(opinionMap)) {
                opinioListMap.add(opinionMap);
            }
        }
        map.put("opinioListMap", opinioListMap);

        // 附件
        Map<String, Object> fileAttachment = Y9Context.getBean(AttachmentApi.class).getAttachmentList(tenantId, userId, processSerialNumber, "", 1, 100);
        map.put("fileAttachment", fileAttachment);

        // 正文
        Map<String, Object> fileDocument = Y9Context.getBean(TransactionWordApi.class).findWordByProcessSerialNumber(tenantId, processSerialNumber);
        map.put("fileDocument", fileDocument);
        return map;
    }

}
