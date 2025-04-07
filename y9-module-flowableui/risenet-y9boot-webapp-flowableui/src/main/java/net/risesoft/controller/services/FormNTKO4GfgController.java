package net.risesoft.controller.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.risesoft.api.itemadmin.*;
import net.risesoft.model.itemadmin.*;
import net.risesoft.util.gfg.OpinionUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
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
@RequestMapping(value = "/services/ntkoForm/gfg")
@Slf4j
public class FormNTKO4GfgController {

    private final Y9FileStoreService y9FileStoreService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final DocumentWordApi documentWordApi;

    private final WordTemplateApi wordTemplateApi;

    private final TransactionWordApi transactionWordApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final FormDataApi formDataApi;

    private final OpinionApi opinionApi;

    private final PrintLogApi printLogApi;

    private final TypeSettingInfoApi typeSettingInfoApi;

    private final PaperAttachmentApi paperAttachmentApi;

    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            DocumentWordModel model = documentWordApi.findWordById(tenantId, id).getData();
            String title = model.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), StandardCharsets.ISO_8859_1);
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title =
                        "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
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
            LOGGER.error("下载正文失败", e);
        }
    }

    /**
     * 获取正文数据
     *
     * @param processSerialNumber 流程编号id
     * @param itemId 表单id
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @return 正文数据
     */
    @RequestMapping(value = "/getFormData")
    public Y9Result<Map<String, Object>> getFormData(@RequestParam(required = false) String processSerialNumber,
                                                     @RequestParam(required = false) String itemId,
                                                     @RequestParam(required = false) String processInstanceId,
                                                     @RequestParam String tenantId,
                                                     @RequestParam String userId,
                                                     @RequestParam(required = false) String taskId,
                                                     @RequestParam(required = false) String itembox) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
        // 办文要报-厅领导意见
        String bwybBgtfzryj = "tingLeaderComment";
        List<OpinionListModel> data1 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, bwybBgtfzryj, itemId, null, null, null).getData();
        String tldContent = OpinionUtil.generateOpinions(data1);
        // 办文要报-核稿人意见
        String bwybBgthgyj = "reviewerComment";
        List<OpinionListModel> data2 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, bwybBgthgyj, itemId, null, null, null).getData();
        String hgrContent = OpinionUtil.generateOpinions(data2);
        // 办文要报-领导批示
        String bwybLdps = "leaderComment";
        List<OpinionListModel> data3 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, bwybLdps, itemId, null, null, null).getData();
        String ldContent = OpinionUtil.generateOpinions(data3);

        // 获取办文信息纸质附件清单
        List<PaperAttachmentModel> paperAttList = paperAttachmentApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

        formData.put("tingLeaderComment",tldContent);
        formData.put("reviewerComment",hgrContent);
        formData.put("leaderComment",ldContent);
        formData.put("DT_zzfj",paperAttList);

        return Y9Result.success(formData);
    }

    /**
     * 打开正文
     *
     * @param fileStoreId 文件存储id
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping(value = "/openDoc")
    public void openDoc(@RequestParam String fileStoreId, HttpServletResponse response, HttpServletRequest request) {
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
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            out = response.getOutputStream();
            byte[] buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
            IOUtils.write(buf, out);
        } catch (Exception e) {
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
     * 保存打印日志
     *
     * @param processSerialNumber 流程编号id
     * @param actionContent 动作内容
     * @param actionType 动作类型
     * @param tenantId 租户id
     * @param userId 用户id
     * @param request 请求
     * @return {@link Y9Result<Object>}
     */
    @RequestMapping(value = "/savePrintLog")
    public Y9Result<Object> savePrintLog(@RequestParam String processSerialNumber, @RequestParam String actionContent,
        @RequestParam String actionType, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintLogModel printLog = new PrintLogModel();
        printLog.setProcessSerialNumber(processSerialNumber);
        printLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        printLog.setPrintTime(sdf.format(new Date()));
        printLog.setUserId(userId);
        printLog.setUserName(person.getName());
        printLog.setActionContent(actionContent);
        printLog.setActionType(actionType);
        printLog.setIp(Y9Context.getIpAddr(request));
        printLog.setDeptId(person.getParentId());
        return printLogApi.savePrintLog(Y9LoginUserHolder.getTenantId(), printLog);
    }

    /**
     * 获取办件正文信息
     *
     * @param processSerialNumber 流程编号
     * @param wordType 文档类型
     * @param positionId 岗位id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping("/showWord")
    public Y9Result<DocumentWordModel> showWord(@RequestParam String processSerialNumber, @RequestParam String wordType,
        @RequestParam String itemId, @RequestParam(required = false) String signId,
        @RequestParam(required = false) String positionId, @RequestParam String tenantId, @RequestParam String userId) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
            DocumentWordModel wordInfo;
            if (StringUtils.isNotBlank(signId)) {// 意见签注
                SignDeptDetailModel sign = signDeptDetailApi.findById(tenantId, signId).getData();
                wordInfo = new DocumentWordModel();
                wordInfo.setId(sign.getId());
                wordInfo.setFileStoreId(sign.getFileStoreId());
                wordInfo.setCurrentBureauId(currentBureau != null ? currentBureau.getId() : "");
                wordInfo.setCurrentUserName(person.getName());
                return Y9Result.success(wordInfo, "获取信息成功");
            }
            List<DocumentWordModel> list =
                documentWordApi.findByProcessSerialNumberAndWordType(tenantId, processSerialNumber, wordType).getData();
            if (list != null && list.size() > 0) {
                wordInfo = list.get(0);
            } else {
                // 新文档，获取正文绑定模板
                String fileStoreId = wordTemplateApi.getWordTemplateBind(tenantId, itemId, wordType).getData();
                wordInfo = new DocumentWordModel();
                wordInfo.setFileStoreId(fileStoreId);
            }
            wordInfo.setCurrentBureauId(currentBureau != null ? currentBureau.getId() : "");
            wordInfo.setCurrentUserName(person.getName());
            return Y9Result.success(wordInfo, "获取信息成功");
        } catch (Exception e) {
            LOGGER.error("获取信息失败", e);
        }
        return Y9Result.failure("获取信息失败");
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
    @RequestMapping(value = "/taoHongTemplateList")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid,
        @RequestParam String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(currentBureauGuid) && StringUtils.isNotBlank(positionId)) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            currentBureauGuid = orgUnit.getParentId();
        }
        return transactionWordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * 保存清样文件
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param id 清样文件id
     * @param request 请求
     * @return Y9Result<DocumentWordModel>
     */
    @PostMapping(value = "/uploadQingyang")
    public Y9Result<String> uploadQingyang(@RequestParam String fileType, @RequestParam String processSerialNumber,
        @RequestParam String id, @RequestParam String tenantId, @RequestParam String userId, @RequestParam MultipartFile file,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        if(multipartFile == null){
            multipartFile = file;
        }
        try {
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, "清样文件" + fileType);
            LOGGER.info("==========fileType==========:"+fileType+ "=============y9FileStoreId=============:"+y9FileStore.getId());
            Boolean flag = typeSettingInfoApi.updateFile(tenantId, id, y9FileStore.getId(), fileType).isSuccess();
            if (flag) {
                return Y9Result.success(y9FileStore.getId());
            }
        } catch (Exception e) {
            LOGGER.error("保存正文失败", e);
        }
        return Y9Result.failure("保存正文失败");
    }

    /**
     * 办件保存正文
     *
     * @param fileType 文件类型
     * @param type 数据类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param wordType 文档类别
     * @param request 请求
     * @return Y9Result<DocumentWordModel>
     */
    @PostMapping(value = "/uploadWord")
    public Y9Result<DocumentWordModel> uploadWord(@RequestParam String fileType, @RequestParam Integer type,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam String wordType, @RequestParam String tenantId,
        @RequestParam String userId, @RequestParam(required = false) String signId, @RequestParam(required = false) MultipartFile file, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartFile multipartFile = null;
        if(file != null){
            multipartFile = file;
        }
        if(multipartFile == null){
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            multipartFile = multipartRequest.getFile("currentDoc");
        }
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, wordType + fileType);
            if (StringUtils.isNotBlank(signId)) {// 会签意见保存
                Y9Result res =
                    signDeptDetailApi.updateFileStoreId(Y9LoginUserHolder.getTenantId(), signId, y9FileStore.getId());
                if (!res.isSuccess()) {
                    return Y9Result.failure("保存失败");
                }
                DocumentWordModel model = new DocumentWordModel();
                model.setId(Y9IdGenerator.genId());
                model.setFileType(fileType);
                model.setFileName(wordType + fileType);
                model.setFileSize(y9FileStore.getDisplayFileSize());
                model.setUserId(userId);
                model.setUserName(person.getName());
                model.setType(type);
                model.setSaveDate(sdf.format(new Date()));
                model.setProcessSerialNumber(processSerialNumber);
                model.setProcessInstanceId(processInstanceId);
                model.setWordType(wordType);
                model.setTaskId(taskId);
                model.setUpdateDate(sdf.format(new Date()));
                model.setFileStoreId(y9FileStore.getId());
                return Y9Result.success(model);
            }
            DocumentWordModel model = new DocumentWordModel();
            model.setId(Y9IdGenerator.genId());
            model.setFileType(fileType);
            model.setFileName(wordType + fileType);
            model.setFileSize(y9FileStore.getDisplayFileSize());
            model.setUserId(userId);
            model.setUserName(person.getName());
            model.setType(type);
            model.setSaveDate(sdf.format(new Date()));
            model.setProcessSerialNumber(processSerialNumber);
            model.setProcessInstanceId(processInstanceId);
            model.setWordType(wordType);
            model.setTaskId(taskId);
            model.setUpdateDate(sdf.format(new Date()));
            model.setFileStoreId(y9FileStore.getId());
            return documentWordApi.saveWord(tenantId, model);
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return Y9Result.failure("上传正文失败");
    }
}
