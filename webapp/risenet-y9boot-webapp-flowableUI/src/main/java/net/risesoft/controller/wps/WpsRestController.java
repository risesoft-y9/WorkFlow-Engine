package net.risesoft.controller.wps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.enums.FileTypeEnum;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/docWps")
public class WpsRestController {

    @Autowired
    private DraftApi draftManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private TransactionWordApi transactionWordManager;

    /**
     * 下载正文
     * 
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param itemId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String itemId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Object documentTitle = null;
            ProcessParamModel processModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            documentTitle = processModel.getTitle();
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String y9FileStoreId =
                transactionWordManager.openDocumentByProcessSerialNumber(tenantId, processSerialNumber);
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?"
                        + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8"))))
                        + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开套红模板
     *
     * @param tenantId
     * @param userId
     * @param templateGuid
     * @param response
     */
    @RequestMapping(value = "/getTaoHongTemplate")
    @ResponseBody
    public void getTaoHongTemplate(@RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, @RequestParam(required = false) String templateGuid,
        HttpServletResponse response) {
        String content = transactionWordManager.openDocumentTemplate(tenantId, userId, templateGuid);
        ServletOutputStream out = null;
        try {
            byte[] result = null;
            out = response.getOutputStream();
            result = jodd.util.Base64.decode(content);
            ByteArrayInputStream bin = new ByteArrayInputStream(result);
            int b = 0;
            byte[] by = new byte[1024];
            response.reset();
            response.setHeader("Content-Type", "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
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
     * 撤销红头
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param response
     * @param request
     */
    @RequestMapping(value = "/revokeRedHeader")
    @ResponseBody
    public void revokeRedHeader(@RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, @RequestParam(required = false) String processSerialNumber,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Object documentTitle = null;
            ProcessParamModel processModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            documentTitle = processModel.getTitle();
            String title = documentTitle != null ? (String)documentTitle : "正文";
            transactionWordManager.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "1");
            String y9FileStoreId =
                transactionWordManager.openDocumentByProcessSerialNumber(tenantId, processSerialNumber);
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?"
                        + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8"))))
                        + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=" + title + ".docx");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取套红模板列表
     *
     * @param tenantId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/taoHongTemplateList")
    @ResponseBody
    public List<Map<String, Object>> taoHongTemplateList(@RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        OrgUnit currentBureau = personApi.getBureau(tenantId, userId);
        String currentBureauGuid = currentBureau != null ? currentBureau.getId() : "";
        if (StringUtils.isBlank(currentBureauGuid)) {
            currentBureauGuid = userInfo.getParentId();
        }
        return transactionWordManager.taoHongTemplateList(tenantId, userId, currentBureauGuid);
    }

    /**
     * 上传正文
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String istaohong, @RequestParam(required = false) MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            String fileName = file.getOriginalFilename();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            boolean b = fileName.contains("\\");
            if (b) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType =
                !fileName.contains(".") ? ".docx" : fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            if (!(FileTypeEnum.DOC.getName().equals(fileType) || FileTypeEnum.DOCX.getName().equals(fileType)
                || FileTypeEnum.PDF.getName().equals(fileType) || FileTypeEnum.TIF.getName().equals(fileType)
                || FileTypeEnum.OFD.getName().equals(fileType))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong = "";
            if (FileTypeEnum.PDF.getName().equals(fileType) || FileTypeEnum.TIF.getName().equals(fileType)) {
                isTaoHong = "2";
            } else if (FileTypeEnum.OFD.getName().equals(fileType)) {
                isTaoHong = "3";
            } else {
                isTaoHong = "0";
                if (StringUtils.isNotBlank(istaohong)) {
                    isTaoHong = istaohong;
                }
            }
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap =
                    draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel =
                    processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath =
                Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            String result = transactionWordManager.uploadWord(tenantId, userId, title, fileType, processSerialNumber,
                isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains(UtilConsts.TRUE)) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType.equals(FileTypeEnum.PDF.getName()) || fileType.equals(FileTypeEnum.TIF.getName())) {
                    map.put("isPdf", true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }
}
