package net.risesoft.controller;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.documentword.DocumentWordApi;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文（后端调用实现）
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/docWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentWordRestController {

    private final Y9FileStoreService y9FileStoreService;

    private final DocumentWordApi documentWordApi;

    /**
     * 下载正文
     *
     * @param id 正文id
     */
    @FlowableLog(operationName = "下载正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            DocumentWordModel model = documentWordApi.findWordById(tenantId, id).getData();
            String title = model.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(
                        org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8))))
                        + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(model.getFileStoreId(), out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 下载历史正文
     *
     * @param id 正文id
     */
    @FlowableLog(operationName = "下载历史正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/downloadHis")
    public void downloadHis(@RequestParam @NotBlank String id, HttpServletResponse response,
        HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            DocumentWordModel model = documentWordApi.findHisWordById(tenantId, id).getData();
            String title = model.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(
                        org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8))))
                        + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(model.getFileStoreId(), out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 获取历史正文
     *
     * @param processSerialNumber 流程实例id
     * @param wordType 正文类型
     * @return Y9Result<List<DocumentWordModel>>
     */
    @FlowableLog(operationName = "获取历史正文")
    @RequestMapping(value = "/getHisWord")
    public Y9Result<List<DocumentWordModel>> getHisWord(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String wordType) {
        List<DocumentWordModel> list = documentWordApi
            .findHisByProcessSerialNumberAndWordType(Y9LoginUserHolder.getTenantId(), processSerialNumber, wordType)
            .getData();
        return Y9Result.success(list, "获取信息成功");
    }

    /**
     * 获取正文权限
     *
     * @param itemId 业务id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @param wordType 正文类型
     * @return Y9Result<Boolean>
     */
    @FlowableLog(operationName = "获取正文权限")
    @RequestMapping(value = "/getPermissionWord")
    public Y9Result<Boolean> getPermissionWord(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam @NotBlank String wordType) {
        return documentWordApi.getPermissionWord(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
            itemId, processDefinitionId, taskDefKey, wordType);
    }

    /**
     * 获取正文
     *
     * @param processSerialNumber 流程实例id
     * @param wordType 正文类型
     * @return Y9Result<DocumentWordModel>
     */
    @FlowableLog(operationName = "获取正文")
    @RequestMapping(value = "/getWord")
    public Y9Result<DocumentWordModel> getWord(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String wordType) {
        List<DocumentWordModel> list = documentWordApi
            .findByProcessSerialNumberAndWordType(Y9LoginUserHolder.getTenantId(), processSerialNumber, wordType)
            .getData();
        return Y9Result.success(list.size() == 0 ? null : list.get(0), "获取信息成功");
    }

    /**
     * 替换正文
     * 
     * @param oldId 旧文件id
     * @param taskId 任务id
     * @param file 文件
     * @return Y9Result<DocumentWordModel>
     */
    @FlowableLog(operationName = "替换正文", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/replaceWord")
    public Y9Result<DocumentWordModel> replaceWord(@RequestParam String oldId, @RequestParam String taskId,
        MultipartFile file) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            DocumentWordModel oldmodel = documentWordApi.findWordById(tenantId, oldId).getData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", oldmodel.getProcessSerialNumber());
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            DocumentWordModel model = new DocumentWordModel();
            model.setId(Y9IdGenerator.genId());
            model.setFileType(oldmodel.getFileType());
            model.setFileName(oldmodel.getFileName());
            model.setFileSize(y9FileStore.getDisplayFileSize());
            model.setUserId(userId);
            model.setUserName(person.getName());
            model.setType(oldmodel.getType());
            model.setSaveDate(sdf.format(new Date()));
            model.setProcessSerialNumber(oldmodel.getProcessSerialNumber());
            model.setProcessInstanceId(oldmodel.getProcessInstanceId());
            model.setWordType(oldmodel.getWordType());
            model.setTaskId(taskId);
            model.setUpdateDate(sdf.format(new Date()));
            model.setFileStoreId(y9FileStore.getId());
            documentWordApi.replaceWord(tenantId, model, oldId, taskId);
            return Y9Result.success(model);
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return Y9Result.failure("上传正文失败");
    }
}
