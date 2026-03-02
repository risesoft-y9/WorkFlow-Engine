package net.risesoft.controller.wps;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.Y9DownloadUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController
@RequestMapping("/mobile/docWps")
@Slf4j
@Validated
@RequiredArgsConstructor
public class WpsRestController {

    private static final String DOCX_KEY = ".docx";
    private final OrgUnitApi orgUnitApi;
    private final ProcessParamApi processParamApi;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9WordApi y9WordApi;

    /**
     * 下载正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam String tenantId, @RequestParam String processSerialNumber,
        HttpServletResponse response, HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber));
            String y9FileStoreId = y9WordApi.openDocumentByProcessSerialNumber(tenantId, processSerialNumber).getData();
            y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    private String getTitle(String processSerialNumber) {
        ProcessParamModel processModel =
            processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        return processModel.getTitle() != null ? processModel.getTitle() : "正文";
    }

    /**
     * 打开套红模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param templateGuid 模板id
     */
    @GetMapping(value = "/getTaoHongTemplate")
    public void getTaoHongTemplate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String templateGuid, HttpServletResponse response) {
        String content = y9WordApi.openDocumentTemplate(tenantId, userId, templateGuid).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            byte[] result;
            result = java.util.Base64.getDecoder().decode(content);
            ByteArrayInputStream bin = new ByteArrayInputStream(result);
            int b;
            byte[] by = new byte[1024];
            response.reset();
            response.setHeader("Content-Type", "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("打开套红模板异常", e);
        }
    }

    /**
     * 撤销红头
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     */
    @GetMapping(value = "/revokeRedHeader")
    public void revokeRedHeader(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, HttpServletResponse response, HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber));
            y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "1");
            String y9FileStoreId = y9WordApi.openDocumentByProcessSerialNumber(tenantId, processSerialNumber).getData();
            y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("撤销红头异常", e);
        }
    }

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return List<Map < String, Object>>
     */
    @GetMapping(value = "/taoHongTemplateList")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam String tenantId, @RequestParam String userId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        OrgUnit currentBureau = orgUnitApi.getOrgUnitBureau(tenantId, userId).getData();
        String currentBureauGuid = currentBureau != null ? currentBureau.getId() : "";
        if (StringUtils.isBlank(currentBureauGuid)) {
            currentBureauGuid = person.getParentId();
        }
        return y9WordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param file 文件
     * @return Map<String, Object>
     */
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String istaohong,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "上传失败");
        try {
            // 处理文件名
            String fileName = Y9DownloadUtil.extractFileName(file.getOriginalFilename());
            // 获取文件类型
            String fileType = getFileType(fileName);
            // 验证文件类型
            if (Y9DownloadUtil.isValidFileType(fileType)) {
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif,.ofd文件");
                return map;
            }
            // 确定套红状态
            String isTaoHong = determineTaoHongStatus(fileType, istaohong);
            // 获取文档标题
            String title = getDocumentTitle(tenantId, processInstanceId);
            // 上传文件
            Y9FileStore y9FileStore = uploadFile(file, tenantId, processSerialNumber, title, fileType);
            // 保存正文信息
            boolean uploadSuccess =
                saveWordInfo(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore);
            if (uploadSuccess) {
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", "上传成功");
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                    map.put("isPdf", true);
                }
            } else {
                map.put("msg", "保存正文信息失败");
            }
        } catch (Exception e) {
            map.put("msg", "上传正文失败: " + e.getMessage());
            LOGGER.error("上传正文异常", e);
        }
        return map;
    }

    /**
     * 获取文档标题
     *
     * @param tenantId 租户ID
     * @param processInstanceId 流程实例ID
     * @return 文档标题
     */
    private String getDocumentTitle(String tenantId, String processInstanceId) {
        try {
            ProcessParamModel processModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            return processModel.getTitle() != null ? processModel.getTitle() : "正文";
        } catch (Exception e) {
            LOGGER.warn("获取文档标题失败，使用默认标题", e);
            return "正文";
        }
    }

    /**
     * 确定套红状态
     *
     * @param fileType 文件类型
     * @param istaohong 套红参数
     * @return 套红状态
     */
    private String determineTaoHongStatus(String fileType, String istaohong) {
        if (fileType == null) {
            return "0";
        }
        switch (fileType) {
            case ".pdf":
            case ".tif":
                return "2";
            case ".ofd":
                return "3";
            default:
                return StringUtils.isNotBlank(istaohong) ? istaohong : "0";
        }
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型
     */
    private String getFileType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return DOCX_KEY;
        }
        return !fileName.contains(".") ? DOCX_KEY : fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 上传文件到文件存储服务
     *
     * @param file 上传的文件
     * @param tenantId 租户ID
     * @param processSerialNumber 流程编号
     * @param title 文档标题
     * @param fileType 文件类型
     * @return Y9FileStore 对象
     * @throws Exception 上传异常
     */
    private Y9FileStore uploadFile(MultipartFile file, String tenantId, String processSerialNumber, String title,
        String fileType) throws Exception {
        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
        return y9FileStoreService.uploadFile(file.getInputStream(), fullPath, title + fileType);
    }

    /**
     * 保存正文信息
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param title 文档标题
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param isTaoHong 套红状态
     * @param taskId 任务ID
     * @param y9FileStore 文件存储对象
     * @return 是否保存成功
     */
    private boolean saveWordInfo(String tenantId, String userId, String title, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, Y9FileStore y9FileStore) {
        try {
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LOGGER.error("保存正文信息失败", e);
            return false;
        }
    }
}
