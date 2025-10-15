package net.risesoft.controller.wps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
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
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.ToolUtil;
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

    private final DraftApi draftApi;

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
    @RequestMapping(value = "/download")
    public void download(@RequestParam String tenantId, @RequestParam String processSerialNumber,
        HttpServletResponse response, HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            setResponse(response, request, processSerialNumber);
            String y9FileStoreId = y9WordApi.openDocumentByProcessSerialNumber(tenantId, processSerialNumber).getData();
            y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    private void setResponse(HttpServletResponse response, HttpServletRequest request, String processSerialNumber)
        throws UnsupportedEncodingException {
        ProcessParamModel processModel =
            processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        String title = processModel.getTitle() != null ? processModel.getTitle() : "正文";
        title = ToolUtil.replaceSpecialStr(title);
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
            title = new String(title.getBytes("gb2312"), "ISO8859-1");
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
            response.setHeader("Content-type", "text/html;charset=GBK");
            response.setContentType("application/octet-stream");
        } else {
            if (userAgent.contains("Firefox")) {
                title = "=?UTF-8?B?"
                    + (new String(
                        org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8))))
                    + "?=";
            } else {
                title = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
                title = StringUtils.replace(title, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
        }
    }

    /**
     * 打开套红模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param templateGuid 模板id
     */
    @RequestMapping(value = "/getTaoHongTemplate")
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
    @RequestMapping(value = "/revokeRedHeader")
    public void revokeRedHeader(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, HttpServletResponse response, HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            setResponse(response, request, processSerialNumber);
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
    @RequestMapping(value = "/taoHongTemplateList")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam String tenantId, @RequestParam String userId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, userId).getData();
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
    @RequestMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String istaohong,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            String fileName = file.getOriginalFilename();
            LOGGER.debug("fileName={}", fileName);
            if (fileName != null && fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            if (fileName != null && fileName.contains("\\")) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType = "";
            if (fileName != null) {
                fileType =
                    !fileName.contains(".") ? ".docx" : fileName.substring(fileName.lastIndexOf(".")).toLowerCase();// 文件类型
            }

            if (!(fileType.equals(".doc") || fileType.equals(".docx") || fileType.equals(".pdf")
                || fileType.equals(".tif") || fileType.equals(".ofd"))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong;
            if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                isTaoHong = "2";
            } else if (fileType.equals(".ofd")) {
                isTaoHong = "3";
            } else {
                isTaoHong = "0";
                if (StringUtils.isNotBlank(istaohong)) {
                    isTaoHong = istaohong;
                }
            }
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result)) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                    map.put("isPdf", true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("上传正文异常", e);
        }
        return map;
    }
}
