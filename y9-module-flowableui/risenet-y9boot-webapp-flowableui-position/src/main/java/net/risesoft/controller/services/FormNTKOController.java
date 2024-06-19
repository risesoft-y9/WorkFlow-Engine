package net.risesoft.controller.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoForm")
@Slf4j
public class FormNTKOController {

    private final Y9FileStoreService y9FileStoreService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    private final ProcessParamApi processParamApi;

    private final Draft4PositionApi draft4PositionApi;

    private final TransactionWordApi transactionWordApi;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong 是否套红
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/deleteWordByIsTaoHong")
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong, @RequestParam String processSerialNumber, @RequestParam String tenantId, @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载正文
     *
     * @param id 文件id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam String id, @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            // Y9FileStore y9FileStore = y9FileStoreService.getById(id);
            // String fileName = y9FileStore.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败", e);
        }
    }

    /**
     * 下载正文（抄送）
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType, @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Map<String, Object> map = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            String fileStoreId = map.get("fileStoreId").toString();
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            // Y9FileStore y9FileStore = y9FileStoreService.getById(id);
            // String fileName = y9FileStore.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败", e);
        }
    }

    /**
     * 下载历史正文
     *
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param fileType 文件类型
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/downLoadHistoryDoc")
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String fileType, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
            // Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            // String fileName = y9FileStore.getFileName();
            String userAgent = request.getHeader("User-Agent");
            String title;
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
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
     * @param id 正文id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            String documentTitle;
            String[] pId = processInstanceId.split(",");
            processInstanceId = pId[0];
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title").toString();
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? documentTitle : "正文";
            // Y9FileStore y9FileStore = y9FileStoreService.getById(id);
            // String fileName = y9FileStore.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败", e);
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
    @RequestMapping(value = "/getUpdateWord")
    public Map<String, Object> getUpdateWord(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        try {
            map = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取正文信息失败", e);
        }
        return map;
    }

    /**
     * 新建正文空白模板
     */
    @RequestMapping("/openBlankWordTemplate")
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "tags" + File.separator + "template" + File.separator + "blankTemplate.doc";
        ServletOutputStream out = null;
        BufferedInputStream bi = null;
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            String agent = request.getHeader("USER-AGENT");
            if (agent.contains("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            FileInputStream fs = new FileInputStream(file);
            bi = IOUtils.buffer(fs);
            out = response.getOutputStream();
            int b;
            byte[] by = new byte[1024];
            while ((b = bi.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (Exception e) {
            LOGGER.error("新建正文空白模板失败", e);
        } finally {
            try {
                if (bi != null) {
                    bi.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 打开正文
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/openDoc")
    public void openDoc(@RequestParam String processSerialNumber, @RequestParam String itemId, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = transactionWordApi.openDocument(tenantId, userId, processSerialNumber, itemId);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains("Firefox")) {
                Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文失败", e);
            }
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
        } catch (IOException e) {
            LOGGER.error("打开正文失败", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 套红模板
     *
     * @param templateGUID 模板id
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/openTaohongTemplate")
    public void openDocumentTemplate(@RequestParam String templateGUID, @RequestParam String processSerialNumber, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");// 删除未套红的正文
        String content = transactionWordApi.openDocumentTemplate(tenantId, userId, templateGUID);
        ServletOutputStream out = null;
        try {
            byte[] result;
            out = response.getOutputStream();
            result = jodd.util.Base64.decode(content);
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
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 打开历史正文
     *
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains("Firefox")) {
                Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());

            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文失败", e);
            }
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
        } catch (IOException e) {
            LOGGER.error("打开正文失败", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/openPdf")
    public void openPdf(@RequestParam String processSerialNumber, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = transactionWordApi.openPdf(tenantId, userId, processSerialNumber);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文失败", e);
            }
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            if (buf != null) {
                response.setHeader("Content-Length", String.valueOf(buf.length));
            }
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                    out.flush();
                }
            }
        } catch (IOException e) {
            LOGGER.error("打开正文失败", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
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
    @RequestMapping(value = "/openRevokePDFAfterDocument")
    public void openRevokePDFAfterDocument(@RequestParam String processSerialNumber, @RequestParam(required = false) String istaohong, @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        // 删除转PDF的文件
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId = transactionWordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文失败", e);
            }
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
        } catch (IOException e) {
            LOGGER.error("打开正文失败", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 选择套红
     *
     * @param activitiUser 人员id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @RequestMapping(value = "/openTaoHong")
    public String openTaoHong(@RequestParam(required = false) String activitiUser, @RequestParam String tenantId, @RequestParam String userId, Model model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, activitiUser).getData();
        model.addAttribute("currentBureauGuid", currentBureau.getId());
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        return "intranet/taohongNTKO";
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
    @SuppressWarnings("unused")
    @RequestMapping(value = "/saveAsPDFFile")
    public String saveAsPDFFile(@RequestParam(required = false) String fileType, @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {
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
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            LOGGER.error("保存正文失败", e);
        }
        return result;
    }

    @RequestMapping("/showDownPdfTool")
    public String showDownPdfTool() {
        return "intranet/downPdfTool";
    }

    @RequestMapping(value = "/showHistoryDoc")
    public String showHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId, @RequestParam(required = false) String historyFileType, ModelMap model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
        model.putAll(map);
        model.addAttribute("taskId", taskId);
        model.addAttribute("historyFileType", historyFileType);
        return "history/openHistoryDoc";
    }

    /**
     * 获取正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param itembox 状态
     * @param taskId 任务id
     * @param browser 浏览器
     * @param positionId 岗位id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @RequestMapping("/showWord")
    public String showWord(@RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam String itemId, @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId, @RequestParam(required = false) String browser,
        @RequestParam(required = false) String positionId, @RequestParam String tenantId, @RequestParam(required = false) String userId, Model model) {
        Map<String, Object> map = transactionWordApi.showWord(tenantId, userId, processSerialNumber, itemId, itembox, taskId);
        model.addAllAttributes(map);
        Object documentTitle;
        if (StringUtils.isBlank(processInstanceId)) {
            Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
            documentTitle = retMap.get("title");
        } else {
            String[] pInstanceId = processInstanceId.split(",");
            ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, pInstanceId[0]);
            documentTitle = processModel.getTitle();
            processInstanceId = pInstanceId[0];
        }
        model.addAttribute("documentTitle", documentTitle != null ? documentTitle : "正文");
        model.addAttribute("browser", browser);
        model.addAttribute("processInstanceId", processInstanceId);
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("positionId", positionId);
        model.addAttribute("userId", userId);
        return "intranet/webOfficeNTKO";
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
    @RequestMapping(value = "/list")
    public List<Map<String, Object>> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid, @RequestParam String tenantId, @RequestParam(required = false) String userId, @RequestParam(required = false) String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(currentBureauGuid)) {
            if (StringUtils.isNotBlank(positionId)) {
                Position position = positionApi.get(tenantId, positionId).getData();
                currentBureauGuid = position.getParentId();
            }
        }
        return transactionWordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid);
    }

    /**
     * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String, Object> upload(@RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId, @RequestParam MultipartFile file) {
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
            if (fileType != null && !(fileType.equals(".doc") || fileType.equals(".docx") || fileType.equals(".pdf") || fileType.equals(".tif"))) {
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
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            String result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains("true")) {
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
     * 草稿箱保存正文
     *
     * @param fileType 文件类型
     * @param isTaoHong 是否套红
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @RequestMapping(value = "/uploadWord", method = RequestMethod.POST)
    public String uploadWord(@RequestParam(required = false) String fileType, @RequestParam(required = false) String isTaoHong, @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title;
        String result = "success:false";
        try {
            Object documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }

            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return result;
    }

}
