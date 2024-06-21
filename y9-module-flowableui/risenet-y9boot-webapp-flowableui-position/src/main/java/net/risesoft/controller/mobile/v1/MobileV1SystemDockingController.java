package net.risesoft.controller.mobile.v1;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 对接系统接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/sysDocking")
public class MobileV1SystemDockingController {

    private final FormDataApi formDataApi;

    private final Item4PositionApi item4PositionApi;

    private final Document4PositionApi document4PositionApi;

    private final ProcessParamService processParamService;

    private final Y9FileStoreService y9FileStoreService;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TaskApi taskApi;

    /**
     * 对接系统提交接口
     *
     * @param itemId 事项id
     * @param mappingId 对接系统标识
     * @param positionChoice 接收岗位id，多岗位,隔开
     * @param formJsonData 表单数据
     * @param files 附件列表
     * @return Y9Result<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/startAndForwarding")
    public Y9Result<Map<String, Object>> startAndForwarding(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String mappingId, @RequestParam @NotBlank String routeToTaskId, @RequestParam @NotBlank String positionChoice, @RequestParam @NotBlank String formJsonData,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) MultipartFile[] files) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            /*
              1保存表单数据和流转参数数据
             */
            Map<String, Object> mapFormData = Y9JsonUtil.readValue(formJsonData, Map.class);
            List<ItemMappingConfModel> list = item4PositionApi.getItemMappingConf(tenantId, itemId, mappingId);
            Map<String, Object> bindFormDataMap = new CaseInsensitiveMap();
            for (ItemMappingConfModel mapping : list) {
                if (mapFormData != null && null != mapFormData.get(mapping.getMappingName())) {
                    String text = mapFormData.get(mapping.getMappingName()).toString();
                    bindFormDataMap.put(mapping.getColumnName(), text);
                }
            }
            String title = null != bindFormDataMap.get("title") ? bindFormDataMap.get("title").toString() : "无标题";
            String number = null != bindFormDataMap.get("number") ? bindFormDataMap.get("number").toString() : "";
            String level = null != bindFormDataMap.get("level") ? bindFormDataMap.get("level").toString() : "";
            String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            if (bindFormDataMap.get("guid") == null || StringUtils.isBlank(bindFormDataMap.get("guid").toString())) {
                bindFormDataMap.put("guid", guid);
                bindFormDataMap.put("processInstanceId", guid);
            } else {
                guid = bindFormDataMap.get("guid").toString();
            }
            String processInstanceId = "";
            if (StringUtils.isNotBlank(taskId)) {
                TaskModel taskModel = taskApi.findById(tenantId, taskId);
                if (null == taskModel) {
                    return Y9Result.failure("待办已被处理");
                } else {
                    processInstanceId = taskModel.getProcessInstanceId();
                }
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, guid, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String bindFormJsonData = Y9JsonUtil.writeValueAsString(bindFormDataMap);
            String tempIds = item4PositionApi.getFormIdByItemId(tenantId, itemId, item.getWorkflowGuid());
            if (StringUtils.isNotBlank(tempIds)) {
                List<String> tempIdList = Y9Util.stringToList(tempIds, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", bindFormJsonData);
                for (String formId : tempIdList) {
                    formDataApi.saveFormData(tenantId, formId, bindFormJsonData);
                }
            }
            /*
              3启动流程并发送
             */
            Map<String, Object> map = document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, "", itemId, guid, item.getWorkflowGuid(), positionChoice, "", routeToTaskId, new HashMap<>());
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, "操作成功");
            }
            return Y9Result.failure(map.get("msg").toString());
        } catch (Exception e) {
            LOGGER.error("startAndForwarding error", e);
            return Y9Result.failure("操作失败");
        }
    }

    /**
     * 对接系统提交接口
     *
     * @param itemId 事项id
     * @param mappingId 对接系统标识
     * @param positionChoice 接收岗位id，多人,隔开
     * @param formJsonData 表单数据
     * @param files 附件列表
     * @return Y9Result<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/startProcess")
    public Y9Result<Map<String, Object>> startProcess(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String mappingId, @RequestParam @NotBlank String positionChoice, @RequestParam @NotBlank String formJsonData, @RequestParam(required = false) MultipartFile[] files) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            String userId = Y9LoginUserHolder.getPersonId();
            Map<String, Object> mapFormData = Y9JsonUtil.readValue(formJsonData, Map.class);
            List<ItemMappingConfModel> list = item4PositionApi.getItemMappingConf(tenantId, itemId, mappingId);
            Map<String, Object> bindFormDataMap = new CaseInsensitiveMap();
            for (ItemMappingConfModel mapping : list) {
                if (mapFormData != null && null != mapFormData.get(mapping.getMappingName())) {
                    String text = mapFormData.get(mapping.getMappingName()).toString();
                    bindFormDataMap.put(mapping.getColumnName(), text);
                }
            }
            String title = null != bindFormDataMap.get("title") ? bindFormDataMap.get("title").toString() : "无标题";
            String number = null != bindFormDataMap.get("number") ? bindFormDataMap.get("number").toString() : "";
            String level = null != bindFormDataMap.get("level") ? bindFormDataMap.get("level").toString() : "";
            String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            if (bindFormDataMap.get("guid") == null || StringUtils.isBlank(bindFormDataMap.get("guid").toString())) {
                bindFormDataMap.put("guid", guid);
                bindFormDataMap.put("processInstanceId", guid);
            } else {
                guid = bindFormDataMap.get("guid").toString();
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, guid, "", title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String bindFormJsonData = Y9JsonUtil.writeValueAsString(bindFormDataMap);
            String tempIds = item4PositionApi.getFormIdByItemId(tenantId, itemId, item.getWorkflowGuid());
            if (StringUtils.isNotBlank(tempIds)) {
                List<String> tempIdList = Y9Util.stringToList(tempIds, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", bindFormJsonData);
                for (String formId : tempIdList) {
                    formDataApi.saveFormData(tenantId, formId, bindFormJsonData);
                }
            }
            if (null != files) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String fileName = FilenameUtils.getName(originalFilename);
                        if (fileName != null) {
                            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
                        }
                        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "attachmentFile", guid);
                        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
                        DecimalFormat df = new DecimalFormat("#.00");
                        long fileSize = file.getSize();
                        String fileSizeString;
                        if (fileSize < 1024) {
                            fileSizeString = df.format((double)fileSize) + "B";
                        } else if (fileSize < 1048576) {
                            fileSizeString = df.format((double)fileSize / 1024) + "K";
                        } else if (fileSize < 1073741824) {
                            fileSizeString = df.format((double)fileSize / 1048576) + "M";
                        } else {
                            fileSizeString = df.format((double)fileSize / 1073741824) + "G";
                        }
                        Y9Result<String> y9Result = attachment4PositionApi.upload(tenantId, userId, positionId, fileName, fileSizeString, "", "", "", guid, "", y9FileStore.getId());
                        if (!y9Result.isSuccess()) {
                            LOGGER.info("***********************" + title + "**********保存附件失败");
                            return Y9Result.failure("保存附件失败");
                        }
                    }
                }
            }
            Map<String, Object> map = document4PositionApi.startProcess(tenantId, positionId, itemId, guid, item.getWorkflowGuid(), positionChoice);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, "提交成功");
            }
            return Y9Result.failure(map.get("msg").toString());
        } catch (Exception e) {
            LOGGER.error("提交失败", e);
            return Y9Result.failure("提交失败");
        }
    }
}
