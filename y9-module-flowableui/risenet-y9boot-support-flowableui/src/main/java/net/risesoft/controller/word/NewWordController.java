package net.risesoft.controller.word;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AuditLogSaveService;
import net.risesoft.util.Y9DownloadUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文（后端调用实现）
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/newWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class NewWordController {

    private static final String CONTENT_DIS_KEY = "Content-Disposition";
    private final Y9FileStoreService y9FileStoreService;
    private final Y9WordApi y9WordApi;
    private final DraftApi draftApi;
    private final ProcessParamApi processParamApi;
    private final AuditLogSaveService auditLogSaveService;
    private final OrgUnitApi orgUnitApi;

    /**
     * 获取文档标题
     */
    private String getDocumentTitle(String processSerialNumber) {
        try {
            ProcessParamModel processModel = processParamApi.findByProcessSerialNumber(processSerialNumber).getData();
            return processModel != null ? processModel.getTitle() : "正文";
        } catch (Exception e) {
            LOGGER.warn("获取文档标题失败，使用默认标题", e);
            return "正文";
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return null;
        }
        return fileName.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 获取办件正文信息
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping("/getWord")
    public Y9Result<Y9WordInfo> getWord(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam String itemId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String bindValue) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9FlowableHolder.getPositionId();
            Y9WordInfo wordInfo = y9WordApi.showWord(processSerialNumber, itemId, "", taskId, bindValue).getData();
            String documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(processSerialNumber).getData();
                if (draftModel != null) {
                    documentTitle = draftModel.getTitle();
                }
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            wordInfo.setDocumentTitle(StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文");
            wordInfo.setProcessInstanceId(processInstanceId);
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, positionId).getData();
            OrgUnit currentBureau = orgUnitApi.getOrgUnitBureau(tenantId, orgUnit.getParentId()).getData();
            wordInfo.setCurrentBureauGuid(currentBureau != null ? currentBureau.getId() : "");
            return Y9Result.success(wordInfo, "获取信息成功");
        } catch (Exception e) {
            LOGGER.error("获取办件正文信息失败，异常：", e);
        }
        return Y9Result.failure("获取办件正文信息失败");
    }

    /**
     * 保存正文
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param file 文件
     * @return Map<String, Object>
     */
    @FlowableLog(operationName = "保存正文", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveWord")
    public Y9Result<Object> saveWord(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String taskId, @RequestParam MultipartFile file) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 处理文件名
            String fileName = Y9DownloadUtil.extractFileName(file.getOriginalFilename());
            // 验证文件类型
            String fileType = getFileExtension(fileName);
            // 获取文档标题
            String title = getDocumentTitle(processSerialNumber);
            // 确定是否套红
            String isTaoHong = "0";
            // 上传文件
            Y9FileStore y9FileStore = uploadFile(file, tenantId, processSerialNumber, title + fileType);
            // 保存正文信息
            boolean uploadSuccess = saveWordInfo(title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore);
            if (uploadSuccess) {
                auditLogSaveService.wordUploadLog(title, processSerialNumber, y9FileStore);
            } else {
                return Y9Result.failure("保存失败");
            }
        } catch (Exception e) {
            return Y9Result.failure("保存正文失败");
        }
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存正文信息
     */
    private boolean saveWordInfo(String title, String fileType, String processSerialNumber, String isTaoHong,
        String taskId, Y9FileStore y9FileStore) {
        try {
            Boolean result =
                y9WordApi
                    .uploadWord(title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LOGGER.error("保存正文信息失败", e);
            return false;
        }
    }

    /**
     * 上传文件到文件存储服务
     */
    private Y9FileStore uploadFile(MultipartFile file, String tenantId, String processSerialNumber, String fileName)
        throws Exception {
        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
        return y9FileStoreService.uploadFile(file.getInputStream(), fullPath, fileName);
    }

}