package net.risesoft.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController
@RequestMapping("/vue/attachment")
public class AttachmentRestController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private Attachment4PositionApi attachmentManager;

    /**
     * 附件下载
     *
     * @param id 附件id
     * @param response
     * @param request
     */
    @RequestMapping(value = "/attachmentDownload", method = RequestMethod.GET, produces = "application/json")
    public void attachmentDownload(@RequestParam(required = true) String id, HttpServletResponse response,
        HttpServletRequest request) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Map<String, Object> map = attachmentManager.attachmentDownload(tenantId, id);
            String filename = (String)map.get("filename");
            String filePath = (String)map.get("fileStoreId");
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            y9FileStoreService.downloadFileToOutputStream(filePath, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除附件
     *
     * @param ids 附件ids，逗号隔开
     * @return
     */
    @RequestMapping(value = "/delFile", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delFile(@RequestParam(required = true) String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.delFile(tenantId, ids);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取附件列表
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 文件来源
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAttachmentList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Map<String, Object>> getAttachmentList(@RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = false) String fileSource, @RequestParam(required = true) int page,
        @RequestParam(required = true) int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.getAttachmentList(tenantId, processSerialNumber, fileSource, page, rows);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 上传附件
     *
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 文件来源
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> upload(MultipartFile file, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String describes,
        @RequestParam(required = true) String processSerialNumber, @RequestParam(required = false) String fileSource) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, "UTF-8");
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            String storeId = y9FileStore.getId();
            String fileSize =
                Y9FileUtil.getDisplayFileSize(y9FileStore.getFileSize() != null ? y9FileStore.getFileSize() : 0);
            map = attachmentManager.upload(tenantId, userId, Y9LoginUserHolder.getPositionId(), fileName, fileSize,
                processInstanceId, taskId, describes, processSerialNumber, fileSource, storeId);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上传失败");
    }

}
