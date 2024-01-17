package net.risesoft.controller.mobile.v1;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 附件，正文接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequestMapping("/mobile/v1/attachment")
public class MobileV1AttachmentController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private Attachment4PositionApi attachmentManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 附件ids
     * @param response
     */
    @RequestMapping(value = "/delFile")
    public Y9Result<String> delFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = attachmentManager.delFile(tenantId, ids);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 附件id
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/download")
    public void download(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Map<String, Object> map = attachmentManager.attachmentDownload(tenantId, id);
            String filename = (String)map.get("filename");
            String fileStoreId = (String)map.get("fileStoreId");
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");// IE浏览器
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
     * 正文下载
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processSerialNumber, @RequestParam String itemId, HttpServletResponse response,
        HttpServletRequest request) throws Exception {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Map<String, Object> fileDocument = transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            String filename = fileDocument.get("fileName") != null ? (String)fileDocument.get("fileName") : "正文.doc";
            String fileStoreId = transactionWordManager.openDocument(tenantId, userId, processSerialNumber, itemId);
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");// 火狐浏览器
            } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                filename = URLEncoder.encode(filename, "UTF-8");// IE浏览器
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");// IE浏览器
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
     * 附件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list")
    public Y9Page<Map<String, Object>> list(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processSerialNumber, @RequestParam(required = false) String fileSource, int page,
        int rows, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = attachmentManager.getAttachmentList(tenantId, processSerialNumber, fileSource, page, rows);
            if ((boolean)map.get("success")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
                return Y9Page.success(page, Integer.valueOf(map.get("totalpage").toString()), Long.valueOf(map.get("total").toString()), list, "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 附件上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
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
    public Y9Result<String> upload(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) MultipartFile file, @RequestParam String processInstanceId,
        @RequestParam String taskId, @RequestParam String describes, @RequestParam String processSerialNumber, @RequestParam(required = false) String fileSource, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, "UTF-8");
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            fileName = URLDecoder.decode(fileName, "UTF-8");
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "attachmentFile", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            map = attachmentManager.upload(tenantId, userId, positionId, fileName, y9FileStore.getDisplayFileSize(), processInstanceId, taskId, describes, processSerialNumber, fileSource, y9FileStore.getId());
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上传失败");
    }

    /**
     * 正文上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param documentTitle 文件标题
     * @param file 文件
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value = "/uploadWord")
    public Y9Result<String> uploadWord(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String documentTitle, @RequestParam(required = false) MultipartFile file,
        @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String taskId) {
        String result = "";
        try {
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, "正文.doc");
            result = transactionWordManager.uploadWord(tenantId, userId, documentTitle, fileType, processSerialNumber, "0", taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains("true")) {
                return Y9Result.successMsg("上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上传失败");
    }
}
