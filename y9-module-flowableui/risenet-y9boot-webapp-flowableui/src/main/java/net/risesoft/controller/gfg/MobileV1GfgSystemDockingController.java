package net.risesoft.controller.gfg;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.validation.constraints.NotBlank;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
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
@RequestMapping("/mobile/v1/gfg/sysDocking")
public class MobileV1GfgSystemDockingController {

    private final FormDataApi formDataApi;

    private final ItemApi itemApi;

    private final DocumentApi documentApi;

    private final ProcessParamService processParamService;

    private final Y9FileStoreService y9FileStoreService;

    private final AttachmentApi attachmentApi;

    /**
     *
     * @param itemId 事项id
     * @param mappingId 对接系统标识
     * @param targetTaskDefKey 目标任务节点key
     * @param targetPositionIds 目标任务节点办理岗位id，多岗位,隔开
     * @param formJsonData 表单数据
     * @param startTaskDefKey 开始任务节点key
     * @param startPositionId 开始任务节点办理岗位id
     * @param files 附件列表
     * @return
     */
    @RequestMapping(value = "/startAndForwarding")
    public Y9Result<Object> startAndForwarding(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String mappingId, @RequestParam @NotBlank String targetTaskDefKey,
        @RequestParam @NotBlank String targetPositionIds, @RequestParam @NotBlank String formJsonData,
        @RequestParam(required = false) String startTaskDefKey, @RequestParam(required = false) String startPositionId,
        @RequestParam(required = false) MultipartFile[] files) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            // 1、验证绑定信息 2、处理表单数据并保存 3、保存流程参数 4、保存附件数据 5、启动流程
            Y9Result<StartProcessResultModel> startResult =
                this.startProcess(itemId, mappingId, formJsonData, startTaskDefKey, startPositionId, files);
            if (!startResult.isSuccess()) {
                return Y9Result.failure(startResult.getMsg());
            }
            // 6、发送
            Y9Result<String> forwarding = documentApi.forwarding(tenantId, positionId,
                startResult.getData().getTaskId(), targetPositionIds, targetTaskDefKey, "", "");
            if (!forwarding.isSuccess()) {
                return Y9Result.failure("启动流程成功，发送失败，请前往电子公文系统中进行手动发送！");
            }
            return Y9Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("startAndForwarding error", e);
        }
        return Y9Result.failure("操作异常");
    }

    /**
     *
     * @param itemId 事项id
     * @param mappingId 对接系统标识
     * @param formJsonData 表单数据
     * @param startTaskDefKey 开始任务节点key
     * @param startPositionId 开始任务节点办理岗位id
     * @param files 附件列表
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/startProcess")
    public Y9Result<StartProcessResultModel> startProcess(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String mappingId, @RequestParam @NotBlank String formJsonData,
        @RequestParam(required = false) String startTaskDefKey, @RequestParam(required = false) String startPositionId,
        @RequestParam(required = false) MultipartFile[] files) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            String userId = Y9LoginUserHolder.getPersonId();
            // 1、验证绑定信息
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            if (null == item) {
                return Y9Result.failure("[" + mappingId + "]对接的事项不存在");
            }
            List<ItemMappingConfModel> mappingList = itemApi.getItemMappingConf(tenantId, itemId, mappingId).getData();
            long count =
                mappingList.stream().filter(mapping -> !"guid".equalsIgnoreCase(mapping.getColumnName())).count();
            if (0 == count) {
                return Y9Result.failure("[" + mappingId + "]至少配置一个非guid字段");
            }
            List<String> tableNameList = new ArrayList<>();
            mappingList.forEach(mapping -> {
                if (!tableNameList.contains(mapping.getTableName())) {
                    tableNameList.add(mapping.getTableName());
                }
            });
            // 2、处理表单数据并保存
            Map<String, Object> formData = Y9JsonUtil.readValue(formJsonData, Map.class);
            assert formData != null;
            String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            Map<String, Object> bindFormDataMap = new CaseInsensitiveMap();
            Map<String, Object> bindFormDataMap4All = new CaseInsensitiveMap();
            AtomicBoolean success = new AtomicBoolean(true);
            tableNameList.forEach(tableName -> {
                bindFormDataMap.clear();
                bindFormDataMap.put("guid", guid);
                mappingList.stream().filter(mapping -> mapping.getTableName().equals(tableName)).forEach(mapping -> {
                    if (null != formData.get(mapping.getMappingName())) {
                        String text = formData.get(mapping.getMappingName()).toString();
                        bindFormDataMap.put(mapping.getColumnName(), text);
                        bindFormDataMap4All.put(mapping.getColumnName(), text);
                    }
                });
                Y9Result<String> insertFormDataResult = formDataApi.insertFormData(tenantId, tableName, guid,
                    Y9JsonUtil.writeValueAsString(bindFormDataMap));
                if (!insertFormDataResult.isSuccess()) {
                    success.set(false);
                }
            });
            if (!success.get()) {
                return Y9Result.failure("保存表单数据失败，联系运维人员查看事项管理日志！");
            }
            // 3、保存流程参数
            String title =
                null != bindFormDataMap4All.get("title") ? bindFormDataMap4All.get("title").toString() : "暂无标题";
            String number =
                null != bindFormDataMap4All.get("number") ? bindFormDataMap4All.get("number").toString() : "";
            String level = null != bindFormDataMap4All.get("level") ? bindFormDataMap4All.get("level").toString() : "";
            Y9Result<String> processParamResult =
                processParamService.saveOrUpdate(itemId, guid, "", title, number, level, false);
            if (!processParamResult.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            // 4、保存附件数据
            if (null != files) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String fileName = FilenameUtils.getName(originalFilename);
                        if (fileName != null) {
                            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
                        }
                        String fullPath =
                            Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "attachmentFile", guid);
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
                        Y9Result<String> y9Result = attachmentApi.upload(tenantId, userId, positionId, fileName,
                            fileSizeString, "", "", "", guid, "", y9FileStore.getId());
                        if (!y9Result.isSuccess()) {
                            LOGGER.info("***********************" + title + "**********保存附件失败");
                            return Y9Result.failure("保存附件失败");
                        }
                    }
                }
            }
            // 5、启动流程
            Y9Result<StartProcessResultModel> y9Result =
                documentApi.startProcessByTheTaskKey(tenantId, positionId, itemId, guid, item.getWorkflowGuid(),
                    startTaskDefKey, startPositionId);
            if (y9Result.isSuccess()) {
                return Y9Result.success(y9Result.getData(), "提交成功");
            }
            return Y9Result.failure(y9Result.getMsg());
        } catch (Exception e) {
            LOGGER.error("提交失败", e);
            return Y9Result.failure("提交失败");
        }
    }
}
