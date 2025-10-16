package net.risesoft.controller.services;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.template.PrintApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.Person;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文打印相关接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/print")
@Slf4j
public class FormNTKOPrintController {

    private static final String FIREFOX_KEY = "Firefox";
    private final Y9FileStoreService y9FileStoreService;
    private final PrintApi printApi;
    private final PersonApi personApi;
    private final ProcessParamApi processParamApi;
    private final DraftApi draftApi;
    private final Y9WordApi y9WordApi;

    /**
     * 下载正文文件
     *
     * @param id 正文id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            setResponse(response, request, processSerialNumber, fileType);
            y9FileStoreService.downloadFileToOutputStream(id, out);
        } catch (Exception e) {
            LOGGER.error("下载正文文件异常", e);
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
                title = "=?UTF-8?B?"
                    + (new String(
                        org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8))))
                    + "?=";
            } else {
                title = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
                title = StringUtils.replace(title, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
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
    @RequestMapping(value = "/openDoc")
    public void openDoc(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String bindValue, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId =
            y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, bindValue).getData();

        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains(FIREFOX_KEY)) {
                org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
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
            LOGGER.error("打开正文文件异常", e);
        }
    }

    /**
     * 打开打印模板文件
     * 
     * @param itemId
     * @param tenantId
     * @param userId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/openDocument")
    public void openDocument(String itemId, String tenantId, String userId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String y9FileStoreId = printApi.openDocument(tenantId, itemId).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains(FIREFOX_KEY)) {
                org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=printForm.doc");
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
            LOGGER.error("打开打印模板文件异常", e);
        }
    }
}
