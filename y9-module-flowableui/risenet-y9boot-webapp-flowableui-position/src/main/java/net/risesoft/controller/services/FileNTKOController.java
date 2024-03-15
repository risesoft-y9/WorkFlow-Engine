package net.risesoft.controller.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@Controller
@RequestMapping(value = "/services/ntkoFile")
@Slf4j
public class FileNTKOController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private Attachment4PositionApi attachment4PositionApi;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 打开正文
     *
     * @param fileId
     */
    @RequestMapping(value = "/openDoc")
    @ResponseBody
    public void openDoc(@RequestParam(required = false) String fileId, @RequestParam(required = false) String tenantId,
        HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId);
        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            String fileName = file.getName();
            if (-1 != agent.indexOf("Firefox")) {
                fileName = "=?UTF-8?B?"
                    + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayInputStream bin = new ByteArrayInputStream(buf);
            int b = 0;
            byte[] by = new byte[1024];
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件
     *
     * @param processSerialNumber
     * @param fileName
     * @param itembox
     * @param taskId
     * @param browser
     * @param fileId
     * @param tenantId
     * @param userId
     * @param positionId
     * @param fileUrl
     * @param model
     * @return
     */
    @RequestMapping("/showWord")
    public String showWord(@RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String fileName, @RequestParam(required = false) String itembox,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String browser,
        @RequestParam(required = false) String fileId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, @RequestParam(required = false) String positionId,
        @RequestParam(required = false) String fileUrl, Model model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId);
            String downloadUrl =
                y9Config.getCommon().getItemAdminBaseUrl() + "/s/" + file.getFileStoreId() + "." + file.getFileType();
            model.addAttribute("fileName", file.getName());
            model.addAttribute("browser", browser);
            model.addAttribute("fileUrl", downloadUrl);
            model.addAttribute("tenantId", tenantId);
            model.addAttribute("userId", userId);
            model.addAttribute("taskId", taskId);
            model.addAttribute("positionId", positionId);
            model.addAttribute("itembox", itembox);
            model.addAttribute("fileId", fileId);
            model.addAttribute("userName", person.getName());
            model.addAttribute("processSerialNumber", processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "file/webOfficeNTKO";
    }

    /**
     * 更新附件
     *
     * @param fileId
     * @param processSerialNumber
     * @param positionId
     * @param taskId
     * @param tenantId
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadWord", method = RequestMethod.POST)
    @ResponseBody
    public String uploadWord(@RequestParam(required = false) String fileId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String positionId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletRequest request, HttpServletResponse response) {
        String result = "success:false";
        try {
            LOGGER.debug("*****************fileId={}", fileId);
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId);
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, file.getName());
            result = attachment4PositionApi.updateFile(tenantId, userId, positionId, fileId,
                y9FileStore.getDisplayFileSize(), taskId, y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
