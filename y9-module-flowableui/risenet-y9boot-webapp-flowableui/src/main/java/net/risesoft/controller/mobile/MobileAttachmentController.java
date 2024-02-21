package net.risesoft.controller.mobile;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/attachment")
public class MobileAttachmentController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private AttachmentApi attachmentManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    private static String USERAGENT = "User-Agent";

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 附件id
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/download")
    public void attachmentDownload(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String id, HttpServletResponse response,
        HttpServletRequest request) throws Exception {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Map<String, Object> map = attachmentManager.attachmentDownload(tenantId, userId, id);
            String filename = (String)map.get("filename");
            String fileStoreId = (String)map.get("fileStoreId");
            if (request.getHeader(USERAGENT).indexOf(BrowserTypeEnum.FIREFOX.getValue()) > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 附件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/list")
    public void attachmentList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String processSerialNumber,
        @RequestParam(required = false) String fileSource, int page, int rows, HttpServletResponse response)
        throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = attachmentManager.getAttachmentList(tenantId, userId, processSerialNumber, fileSource, page, rows);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "附件列表获取失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 附件上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/upload")
    public void attachmentUpload(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam(required = false) MultipartFile file,
        @RequestParam String processInstanceId, @RequestParam String taskId, @RequestParam String describes,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String fileSource,
        HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(Y9LoginUserHolder.getTenantId(), userId).getData();
            Y9LoginUserHolder.setPerson(person);
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, "UTF-8");
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            fileName = URLDecoder.decode(fileName, "UTF-8");
            String fullPath =
                Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "attachmentFile", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            map = attachmentManager.upload(tenantId, userId, fileName, y9FileStore.getDisplayFileSize(),
                processInstanceId, taskId, describes, processSerialNumber, fileSource, y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "上传附件失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids 附件ids
     * @param response
     */
    @RequestMapping(value = "/delFile")
    public void delFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = attachmentManager.delFile(tenantId, userId, ids);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 正文下载
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String processSerialNumber,
        @RequestParam String itemId, HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Map<String, Object> fileDocument =
                transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            String filename = fileDocument.get("fileName") != null ? (String)fileDocument.get("fileName") : "正文.doc";
            String fileStoreId = transactionWordManager.openDocument(tenantId, userId, processSerialNumber, itemId);
            if (request.getHeader(USERAGENT).indexOf(BrowserTypeEnum.FIREFOX.getValue()) > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
            } else if (request.getHeader(USERAGENT).toUpperCase().indexOf(BrowserTypeEnum.IE.getValue()) > 0) {
                filename = URLEncoder.encode(filename, "UTF-8");
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }
            if (StringUtils.isNotBlank(fileStoreId)) {
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
                OutputStream out = response.getOutputStream();
                y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 正文上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param documentTitle 文件标题
     * @param file 文件
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value = "/uploadWord")
    public String uploadWord(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String documentTitle,
        @RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String taskId) {
        String result = "";
        try {
            String fullPath =
                Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, "正文.doc");
            result = transactionWordManager.uploadWord(tenantId, userId, documentTitle, fileType, processSerialNumber,
                "0", taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
