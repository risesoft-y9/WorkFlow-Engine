package net.risesoft.controller.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文相关接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoForm")
@Slf4j
public class FormNTKOController {

    private static final String FIREFOX_KEY = "Firefox";
    private static final String UTF8LINK_KEY = "=?UTF-8?B?";
    private static final String CONTENT_DIS_KEY = "Content-Disposition";
    private static final String USER_AGENT_KEY = "USER-AGENT";
    private final Y9FileStoreService y9FileStoreService;
    private final PersonApi personApi;
    private final OrgUnitApi orgUnitApi;
    private final ProcessParamApi processParamApi;
    private final DraftApi draftApi;
    private final Y9WordApi y9WordApi;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong 是否套红
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @PostMapping(value = "/deleteWordByIsTaoHong")
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong,
        @RequestParam String processSerialNumber, @RequestParam String tenantId, @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载历史正文
     *
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param fileType 文件类型
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载历史正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downLoadHistoryDoc")
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String fileType,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9WordHistoryModel map = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
        String fileStoreId = map.getFileStoreId();
        ServletOutputStream out;
        try {
            setResponse(response, request, processSerialNumber, fileType);
            out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载历史正文失败", e);
        }
    }

    /**
     * 下载正文
     *
     * @param id 文件id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/download")
    public void download(@RequestParam String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            setResponse(response, request, processSerialNumber, fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败，异常为：", e);
        }
    }

    private void setResponse(HttpServletResponse response, HttpServletRequest request, String processSerialNumber,
        String fileType) throws UnsupportedEncodingException {
        ProcessParamModel processModel =
            processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        String title = StringUtils.isNotBlank(processModel.getTitle()) ? processModel.getTitle() : "正文";
        title = ToolUtil.replaceSpecialStr(title);
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
            title = new String(title.getBytes("gb2312"), StandardCharsets.ISO_8859_1);
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
            response.setHeader("Content-type", "text/html;charset=GBK");
            response.setContentType("application/octet-stream");
        } else {
            if (userAgent.contains(FIREFOX_KEY)) {
                title = UTF8LINK_KEY + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
            } else {
                title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                title = StringUtils.replace(title, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
        }
    }

    /**
     * 下载正文（抄送）
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文（抄送）", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType, @RequestParam String processSerialNumber,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response,
        HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Y9WordModel word = y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String fileStoreId = word.getFileStoreId();
            setResponse(response, request, processSerialNumber, fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败（抄送），异常为：", e);
        }
    }

    /**
     * 下载正文文件
     *
     * @param id 正文id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文文件", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            setResponse(response, request, processSerialNumber, fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文文件失败，异常为：", e);
        }
    }

    /**
     * 获取正文信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return Map
     */
    @GetMapping(value = "/getUpdateWord")
    public Y9WordModel getUpdateWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        return y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
    }

    /**
     * 新建正文空白模板
     */
    @FlowableLog(operationName = "新建正文空白模板", operationType = FlowableOperationTypeEnum.ADD)
    @GetMapping("/openBlankWordTemplate")
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "tags"
            + File.separator + "template" + File.separator + "blankTemplate.doc";
        File file = new File(filePath);
        try (ServletOutputStream out = response.getOutputStream(); FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bi = IOUtils.buffer(fs)) {
            String fileName = file.getName();
            String agent = request.getHeader(USER_AGENT_KEY);
            if (agent.contains(FIREFOX_KEY)) {
                fileName =
                    UTF8LINK_KEY + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=" + fileName);
            int b;
            byte[] by = new byte[1024];
            while ((b = bi.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (Exception e) {
            LOGGER.error("新建正文空白模板失败", e);
        }
    }

    /**
     * 打开正文文件
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openDoc")
    public void openDoc(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String bindValue, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId =
            y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, bindValue).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader(USER_AGENT_KEY);
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains(FIREFOX_KEY)) {
                Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            byte[] buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (Exception e) {
            LOGGER.error("打开正文文件失败", e);
        }
    }

    /**
     * 打开套红模板文件
     *
     * @param templateGUID 模板id
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openTaohongTemplate")
    public void openDocumentTemplate(@RequestParam String templateGUID, @RequestParam String processSerialNumber,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");// 删除未套红的正文
        String content = y9WordApi.openDocumentTemplate(tenantId, userId, templateGUID).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            byte[] result = java.util.Base64.getDecoder().decode(content);
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
            LOGGER.error("套红正文失败", e);
        }
    }

    /**
     * 打开历史版本正文文件
     *
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam(required = false) String taskId, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9WordHistoryModel map = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
        String fileStoreId = map.getFileStoreId();
        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader(USER_AGENT_KEY);
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains(FIREFOX_KEY)) {
                Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            byte[] buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (Exception e) {
            LOGGER.error("打开历史版本正文文件失败", e);
        }
    }

    /**
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openPdf")
    public void openPdf(@RequestParam String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = y9WordApi.openPdf(tenantId, userId, processSerialNumber).getData();

        try (ServletOutputStream out = response.getOutputStream()) {
            byte[] buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            response.reset();
            if (buf != null) {
                response.setHeader("Content-Length", String.valueOf(buf.length));
            }
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                    out.flush();
                }
            }
        } catch (Exception e) {
            LOGGER.error("打开PDF或者TIF文件失败", e);
        }
    }

    /**
     * 打开撤销PDF后的正文
     *
     * @param processSerialNumber 流程编号
     * @param istaohong 是否套红
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openRevokePDFAfterDocument")
    public void openRevokePDFAfterDocument(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String istaohong, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        // 删除转PDF的文件
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId =
            y9WordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader(USER_AGENT_KEY);
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains(FIREFOX_KEY)) {
                fileName =
                    UTF8LINK_KEY + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=" + fileName);

            byte[] buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (Exception e) {
            LOGGER.error("打开撤销PDF后的正文失败", e);
        }
    }

    /**
     * 保存word转PDF的正文
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param isTaoHong 是否套红
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @FlowableLog(operationName = "保存word转PDF的正文", operationType = FlowableOperationTypeEnum.SAVE)
    @SuppressWarnings("unused")
    @PostMapping(value = "/saveAsPDFFile")
    public String saveAsPDFFile(@RequestParam(required = false) String fileType,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletRequest request,
        HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title;
        String result = "success:false";
        try {
            String documentTitle = "";
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (draftModel != null) {
                    documentTitle = draftModel.getTitle();
                }
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            title = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            Boolean result2 =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result2)) {
                result = "success:true";
            }
        } catch (Exception e) {
            LOGGER.error("保存正文失败", e);
        }
        return result;
    }

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid 委办局id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return List<Map < String, Object>>
     */
    @GetMapping(value = "/list")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid,
        @RequestParam String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(currentBureauGuid) && StringUtils.isNotBlank(positionId)) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            currentBureauGuid = orgUnit.getParentId();
        }
        return y9WordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * 上传正文文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @return Map<String, Object>
     */
    @FlowableLog(operationName = "上传正文文件", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            String fileName = file.getOriginalFilename();
            LOGGER.debug("fileName={}", fileName);
            if (fileName != null && fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            if (fileName != null && fileName.contains("\\")) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType = null;// 文件类型
            if (fileName != null) {
                fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            }
            if (fileType != null && !(fileType.equals(".doc") || fileType.equals(".docx") || fileType.equals(".pdf")
                || fileType.equals(".tif"))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong = "";
            if (fileType != null) {
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                    isTaoHong = "2";
                } else {
                    isTaoHong = "0";
                }
            }
            String documentTitle = "";
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (draftModel != null) {
                    documentTitle = draftModel.getTitle();
                }
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            String title = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result)) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType != null && (fileType.equals(".pdf") || fileType.equals(".tif"))) {
                    map.put("isPdf", true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("上传正文失败", e);
        }
        return map;
    }

    /**
     * 办件保存正文
     *
     * @param fileType 文件类型
     * @param isTaoHong 是否套红
     * @param docCategory 文档类别
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @FlowableLog(operationName = "保存正文", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/uploadWord")
    public String uploadWord(@RequestParam(required = false) String fileType,
        @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String docCategory,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title;
        String result = "success:false";
        try {
            String documentTitle = "";
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (draftModel != null) {
                    documentTitle = draftModel.getTitle();
                }
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            title = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            Boolean result2 =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, docCategory, taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result2)) {
                result = "success:true";
            }
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return result;
    }
}
