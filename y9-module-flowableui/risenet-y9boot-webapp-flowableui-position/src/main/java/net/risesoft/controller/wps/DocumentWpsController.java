package net.risesoft.controller.wps;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentWpsApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.TaoHongService;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import cn.wps.yun.ApiException;
import cn.wps.yun.api.AppFilesApi;
import cn.wps.yun.api.UserOrgApi;
import cn.wps.yun.api.YunApi;
import cn.wps.yun.model.CreateEmptyRequest;
import cn.wps.yun.model.EmptyFile;
import cn.wps.yun.model.FileContent;
import cn.wps.yun.model.FileEditor;
import cn.wps.yun.model.FilePermissionCreateRequest;
import cn.wps.yun.model.FilePreview;
import cn.wps.yun.model.FilePrivilege;
import cn.wps.yun.model.FilePrivileges;
import cn.wps.yun.model.Grantee;
import cn.wps.yun.model.Identity;
import cn.wps.yun.model.Scope;
import cn.wps.yun.model.UploadConflictBehavior;
import cn.wps.yun.model.UploadMethod;
import cn.wps.yun.model.UploadTransactionCreateRequest;
import cn.wps.yun.model.UploadTransactionPatchResponse;
import cn.wps.yun.model.User;
import cn.wps.yun.model.WebofficeEditorGetUrlRequest;

@RestController
@RequestMapping("/docWps")
@Slf4j
@Validated
@RequiredArgsConstructor
public class DocumentWpsController {

    /**
     * 云文档路径
     */
    private static final String yunWpsBasePath = "http://yun.test.cn";
    /**
     * 应用id
     */
    private static final String yunWpsAppId = "4a1291d0-b753-4c2b-0000-000000000005";
    /**
     * 应用密码
     */
    private static final String yunWpsAppSecret = "u5x7yWKFjsSB";
    /**
     * 回调地址
     */
    private static final String yunWpsRedirectUri = "https://www.risesoft.net/";
    /**
     * APP权限
     */
    private static final String yunWpsAppScope = "App.Files.Read App.Files.ReadWrite";
    /**
     * 人员权限
     */
    private static final String yunWpsUserScope = "User.Profile.Read";
    /**
     * 人员账号
     */
    private static final String yunWpsUserName = "test1";
    /**
     * 密码
     */
    private static final String yunWpsUserPassword = "Aa123456";
    /**
     * 云文档下载路径
     */
    private static final String yunWpsDownloadPath = "http://yun.test.cn/minio";
    /**
     * 卷标识
     */
    private static final String volume = "workspace";
    /**
     * 文件标识，当值为\"root\"时表示根文件夹。
     */
    private static final String root = "root";
    /**
     * 云文档路径
     */
    private static final String yunWpsBasePath4Graph = "http://yun.test.cn/graph";

    public static void main(String[] args) throws Exception {
        String destDocx = "C:\\Users\\10858\\Desktop\\套红.docx";
        String content = "C:\\Users\\10858\\Desktop\\工作流相关文档.docx";
        TaoHongService taoHongService = new TaoHongService();
        taoHongService.word2RedDocument(content, destDocx);
    }

    private final Draft4PositionApi draft4PositionApi;
    private final OrgUnitApi orgUnitApi;
    private final ProcessParamApi processParamApi;
    private final DocumentWpsApi documentWpsApi;
    private final Y9FileStoreService y9FileStoreService;

    private final TransactionWordApi transactionWordApi;

    /**
     * 下载正文
     *
     * @param id 正文id
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            DocumentWpsModel documentWps = documentWpsApi.findById(tenantId, id).getData();
            String title = documentWps.getFileName();
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
            HttpURLConnection conn;
            try {
                AppFilesApi apiInstance =
                    new AppFilesApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret, yunWpsAppScope);
                FileContent result =
                    apiInstance.appGetFileContent(documentWps.getVolumeId(), documentWps.getFileId(), null);
                LOGGER.debug("result:{}", result);
                URL url = new URL(yunWpsDownloadPath + result.getUrl());
                conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(3 * 1000);
                IOUtils.copy(conn.getInputStream(), out);
                out.flush();
                out.close();
            } catch (Exception e) {
                LOGGER.error("下载正文异常", e);
            }
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    private String genRealFileName(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return Y9IdGenerator.genId(IdType.SNOWFLAKE) + "." + extension;
    }

    @RequestMapping(value = "/getDocument")
    public Map<String, Object> getDocument(@RequestParam String processSerialNumber, @RequestParam String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("fileUrl", "");
        map.put("y9FileStoreId", "");
        try {
            String y9FileStoreId =
                transactionWordApi.openDocument(tenantId, userId, processSerialNumber, itemId).getData();
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileUrl = y9FileStore.getUrl();
            map.put("y9FileStoreId", y9FileStoreId);
            map.put("fileUrl", fileUrl);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取正文异常", e);
        }
        return map;
    }

    /**
     * 选择套红
     *
     * @return String
     */
    @RequestMapping(value = "/openTaoHong")
    public String openTaoHong(Model model) {
        OrgUnit currentBureau = orgUnitApi
            .getBureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
        model.addAttribute("currentBureauGuid", currentBureau.getId());
        model.addAttribute("tenantId", Y9LoginUserHolder.getTenantId());
        model.addAttribute("userId", Y9LoginUserHolder.getUserInfo().getPersonId());
        return "intranet/taohongWps";
    }

    @RequestMapping(value = "/saveWps")
    public Map<String, Object> saveWps(@RequestParam String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            documentWpsApi.saveWpsContent(tenantId, processSerialNumber, "1");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("保存正文异常", e);
        }
        return map;
    }

    /**
     * 获取正文
     *
     * @return String
     */
    @RequestMapping("/showWps")
    public String showWord(@RequestParam String processSerialNumber, @RequestParam String processInstanceId,
        @RequestParam String itembox, Model model) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        String documentTitle;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // String destDocx = "C:\\Users\\10858\\Desktop\\套红.docx";
            // String content = "C:\\Users\\10858\\Desktop\\工作流相关文档.docx";
            // TaoHongService taoHongService = new TaoHongService();
            // TaoHongService.word2RedDocument(content, destDocx);

            String wpsSid = new YunApi(yunWpsBasePath).yunLogin(yunWpsUserName, yunWpsUserPassword);
            LOGGER.debug("wpsSid:{}", wpsSid);

            UserOrgApi apiInstance0 = new UserOrgApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret,
                yunWpsRedirectUri, yunWpsUserScope, wpsSid);
            User result0 = apiInstance0.userGetProfile();
            LOGGER.debug("User:{}", result0);

            AppFilesApi apiInstance =
                new AppFilesApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret, yunWpsAppScope);

            DocumentWpsModel documentWps =
                documentWpsApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            model.addAttribute("docUrl", "");
            model.addAttribute("itembox", itembox);
            model.addAttribute("hasContent", "0");
            model.addAttribute("processInstanceId", processInstanceId);
            model.addAttribute("processSerialNumber", processSerialNumber);
            model.addAttribute("id", "");
            model.addAttribute("userId", userId);
            model.addAttribute("tenantId", tenantId);
            if (documentWps != null) {// 获取文件编辑地址
                if ((itembox.equals(ItemBoxTypeEnum.TODO.getValue())
                    || itembox.equals(ItemBoxTypeEnum.DRAFT.getValue()))) {
                    String documentChallenge = ""; // String | 文档口令
                    String expiration = ""; // String | 过期时间
                    Boolean printable = true; // Boolean | 内容可打印
                    Boolean copyable = true; // Boolean | 内容可复制
                    String watermarkText = ""; // String | 水印文本
                    String watermarkImageUrl = ""; // String | 水印图片
                    String extCompanyid = ""; // String | 外部公司ID
                    String extUserid = ""; // String | 外部用户ID
                    try {
                        FilePreview result = apiInstance.appGetFilePreview(documentWps.getVolumeId(),
                            documentWps.getFileId(), documentChallenge, expiration, printable, copyable, watermarkText,
                            watermarkImageUrl, wpsSid, extCompanyid, extUserid);
                        LOGGER.debug("result:{}", result);
                        model.addAttribute("docUrl", result.getUrl());
                    } catch (Exception e) {
                        LOGGER.warn("Exception when calling AppFilesApi#appGetFileContent", e);
                    }
                } else {
                    WebofficeEditorGetUrlRequest body1 = new WebofficeEditorGetUrlRequest(); // WebofficeEditorGetUrlRequest
                    // |
                    body1.setWrite("1");
                    body1.setExtUserid(result0.getId());
                    body1.setExtUsername(result0.getDisplayName());
                    body1.setWatermarkText("");
                    body1.setAccountSync("1");
                    body1.setHistory("0");
                    try {
                        FileEditor result1 =
                            apiInstance.appGetFileEditor(documentWps.getVolumeId(), documentWps.getFileId(), body1);
                        String docUrl = result1.getUrl();
                        model.addAttribute("docUrl", docUrl);
                    } catch (ApiException e) {
                        LOGGER.warn("Exception when calling AppFilesApi#appGetFileEditor", e);
                    }
                }
                model.addAttribute("hasContent", documentWps.getHasContent());
                try {
                    FileContent result =
                        apiInstance.appGetFileContent(documentWps.getVolumeId(), documentWps.getFileId(), null);
                    LOGGER.debug("result:{}", result);
                    model.addAttribute("downloadUrl", yunWpsDownloadPath + result.getUrl());
                } catch (Exception e) {
                    LOGGER.warn("Exception when calling AppFilesApi#appGetFileContent", e);
                }
                model.addAttribute("id", documentWps.getId());
            } else {// 创建空文件，并获取文件编辑地址
                if (StringUtils.isBlank(processInstanceId)) {
                    DraftModel model1 =
                        draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    documentTitle = model1.getTitle();
                } else {
                    ProcessParamModel processModel =
                        processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
                    documentTitle = processModel.getTitle();
                    processInstanceId = processModel.getProcessInstanceId();
                }
                documentTitle = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";

                CreateEmptyRequest body = new CreateEmptyRequest(); // CreateEmptyRequest |
                body.setFileName(documentTitle + ".docx");
                EmptyFile result = apiInstance.appCreateEmpty(volume, root, body);
                LOGGER.debug("result:{}", result);

                FilePermissionCreateRequest body0 = new FilePermissionCreateRequest(); // FilePermissionCreateRequest |
                Grantee grantedTo = new Grantee();
                Identity identity = new Identity();
                FilePrivileges filePrivileges = new FilePrivileges();

                identity.setId(result0.getId());
                identity.setDisplayName(result0.getDisplayName());

                grantedTo.setUser(identity);
                grantedTo.setScope(Scope.ANONYMOUS);

                filePrivileges.add(FilePrivilege.READ);
                filePrivileges.add(FilePrivilege.UPDATE);
                filePrivileges.add(FilePrivilege.UPLOAD);
                filePrivileges.add(FilePrivilege.DOWNLOAD);

                body0.setGrantedTo(grantedTo);
                body0.setPrivileges(filePrivileges);
                // FilePermission resultFilePermission =
                // apiInstance.appCreateFilePermission(result.getVolumeId(), result.getId(),
                // body0);

                WebofficeEditorGetUrlRequest body1 = new WebofficeEditorGetUrlRequest(); // WebofficeEditorGetUrlRequest
                // |
                body1.setWrite("1");
                body1.setExtUserid(result0.getId());
                body1.setExtUsername(result0.getDisplayName());
                body1.setWatermarkText("");
                body1.setAccountSync("1");
                body1.setHistory("0");
                FileEditor result1 = apiInstance.appGetFileEditor(result.getVolumeId(), result.getId(), body1);
                String docUrl = result1.getUrl();
                model.addAttribute("docUrl", docUrl);
                LOGGER.debug("result1:{}", result1);

                documentWps = new DocumentWpsModel();
                documentWps.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                documentWps.setFileId(result.getId());
                documentWps.setFileName(documentTitle + ".docx");
                documentWps.setFileType("docx");
                documentWps.setHasContent("0");
                documentWps.setIstaohong("0");
                documentWps.setProcessInstanceId(processInstanceId);
                documentWps.setProcessSerialNumber(processSerialNumber);
                documentWps.setSaveDate(sdf.format(new Date()));
                documentWps.setTenantId(tenantId);
                documentWps.setUserId(userId);
                documentWps.setVolumeId(result.getVolumeId());
                documentWpsApi.saveDocumentWps(tenantId, documentWps);

                model.addAttribute("id", documentWps.getId());
            }
        } catch (Exception e) {
            model.addAttribute("docUrl", "发生异常");
            LOGGER.error("发生异常", e);
        }
        return "intranet/webOfficeWps";
    }

    /**
     * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param file 文件
     * @return Map
     */
    @RequestMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber, @RequestParam String processInstanceId,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            String tmpdir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tmpdir, genRealFileName(file.getOriginalFilename()));
            file.transferTo(tempFile);

            AppFilesApi appFilesApi =
                new AppFilesApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret, yunWpsAppScope);

            UploadTransactionCreateRequest uploadRequest = new UploadTransactionCreateRequest();
            uploadRequest.setFileName(this.genRealFileName(file.getOriginalFilename()));
            uploadRequest.setFileSize(tempFile.length());
            uploadRequest.setUploadMethod(UploadMethod.POST);
            uploadRequest.setFileNameConflictBehavior(UploadConflictBehavior.RENAME);
            uploadRequest.setFilePath(tempFile.getAbsolutePath());
            UploadTransactionPatchResponse uploadResponse =
                appFilesApi.appCreateUploadTransaction(volume, root, uploadRequest);
            tempFile.delete();

            String documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model =
                    draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = processModel.getTitle();
                processInstanceId = processModel.getProcessInstanceId();
            }
            documentTitle = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";

            cn.wps.yun.model.File File = uploadResponse.getFile();
            DocumentWpsModel documentWps = new DocumentWpsModel();
            documentWps.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            documentWps.setFileId(File.getId());
            documentWps.setFileName(documentTitle + ".docx");
            documentWps.setFileType("docx");
            documentWps.setHasContent("1");
            documentWps.setIstaohong("0");
            documentWps.setProcessInstanceId(processInstanceId);
            documentWps.setProcessSerialNumber(processSerialNumber);
            documentWps.setSaveDate(sdf.format(new Date()));
            documentWps.setTenantId(tenantId);
            documentWps.setUserId(userId);
            documentWps.setVolumeId(File.getVolumeId());
            documentWpsApi.saveDocumentWps(tenantId, documentWps);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "上传失败");
            LOGGER.error("上传失败", e);
        }
        return map;
    }

}
