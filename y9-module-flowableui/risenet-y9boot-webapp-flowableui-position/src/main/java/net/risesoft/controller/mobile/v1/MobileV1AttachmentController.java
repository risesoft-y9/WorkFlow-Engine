package net.risesoft.controller.mobile.v1;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/attachment")
public class MobileV1AttachmentController {

    private final Y9FileStoreService y9FileStoreService;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TransactionWordApi transactionWordApi;

    /**
     * 删除附件
     *
     * @param ids 附件ids
     */
    @RequestMapping(value = "/delFile")
    public Y9Result<String> delFile(@RequestParam @NotBlank String ids) {
        Map<String, Object> map;
        try {
            map = attachment4PositionApi.delFile(Y9LoginUserHolder.getTenantId(), ids);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 附件下载
     *
     * @param id 附件id
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            Map<String, Object> map = attachment4PositionApi.attachmentDownload(Y9LoginUserHolder.getTenantId(), id);
            String filename = (String)map.get("filename");
            String fileStoreId = (String)map.get("fileStoreId");
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
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
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 正文下载
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String itemId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Map<String, Object> fileDocument = transactionWordApi.findWordByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
            String filename = fileDocument.get("fileName") != null ? (String)fileDocument.get("fileName") : "正文.doc";
            String fileStoreId = transactionWordApi.openDocument(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), processSerialNumber, itemId);
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
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
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 附件列表
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list")
    public Y9Page<Map<String, Object>> list(@RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String fileSource, @RequestParam int page, @RequestParam int rows) throws Exception {
        Map<String, Object> map;
        try {
            map = attachment4PositionApi.getAttachmentList(Y9LoginUserHolder.getTenantId(), processSerialNumber, fileSource, page, rows);
            if ((boolean)map.get("success")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
                return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()), Long.parseLong(map.get("total").toString()), list, "获取成功");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 附件上传
     *
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/upload")
    public Y9Result<String> upload(@RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId, @RequestParam(required = false) String describes, @RequestParam String processSerialNumber,
        @RequestParam(required = false) String fileSource) throws Exception {
        Map<String, Object> map;
        try {
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            if (fileName != null) {
                fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            }
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), Y9LoginUserHolder.getTenantId(), "attachmentFile", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            map = attachment4PositionApi.upload(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), fileName, y9FileStore.getDisplayFileSize(), processInstanceId, taskId, describes, processSerialNumber, fileSource, y9FileStore.getId());
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("上传成功");
            }
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

    /**
     * 正文上传
     *
     * @param processSerialNumber 流程编号
     * @param documentTitle 文件标题
     * @param file 文件
     * @param fileType 文件类型
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/uploadWord")
    public Y9Result<String> uploadWord(@RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String documentTitle, @RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String fileType, @RequestParam(required = false) String taskId) {
        String result;
        try {
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), Y9LoginUserHolder.getTenantId(), "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, "正文.doc");
            result = transactionWordApi.uploadWord(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), documentTitle, fileType, processSerialNumber, "0", taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains("true")) {
                return Y9Result.successMsg("上传成功");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return Y9Result.failure("上传失败");
    }
}
