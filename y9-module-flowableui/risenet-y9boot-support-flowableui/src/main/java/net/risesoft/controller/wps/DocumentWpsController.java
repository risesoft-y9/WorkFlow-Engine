package net.risesoft.controller.wps;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.documentword.DocumentWpsApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.impl.TaoHongServiceImpl;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.util.Y9DownloadUtil;
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
    private static final String YUN_WPS_BASE_PATH = "https://yun.test.cn";
    /**
     * 应用id
     */
    private static final String YUN_WPS_APP_ID = "4a1291d0-b753-4c2b-0000-000000000005";
    /**
     * 应用密码
     */
    private static final String YUN_WPS_APP_SECRET = "u5x7yWKFjsSB";
    /**
     * 回调地址
     */
    private static final String YUN_WPS_REDIRECT_URI = "https://www.risesoft.net/";
    /**
     * APP权限
     */
    private static final String YUN_WPS_APP_SCOPE = "App.Files.Read App.Files.ReadWrite";
    /**
     * 人员权限
     */
    private static final String YUN_WPS_USER_SCOPE = "User.Profile.Read";
    /**
     * 人员账号
     */
    private static final String YUN_WPS_USER_NAME = "test1";
    /**
     * 密码
     */
    private static final String YUN_WPS_USER_PD = "Aa123456";
    /**
     * 云文档下载路径
     */
    private static final String YUN_WPS_DOWNLOAD_PATH = "https://yun.test.cn/minio";
    /**
     * 卷标识
     */
    private static final String VOLUME = "workspace";
    /**
     * 文件标识，当值为\"root\"时表示根文件夹。
     */
    private static final String ROOT = "root";
    /**
     * 云文档路径
     */
    private static final String YUN_WPS_BASE_PATH_GRAPH = "https://yun.test.cn/graph";
    private static final String DOC_URL_KEY = "docUrl";
    private static final String DOCX_KEY = ".docx";
    private final DraftApi draftApi;
    private final ProcessParamApi processParamApi;
    private final DocumentWpsApi documentWpsApi;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9WordApi y9WordApi;

    public static void main(String[] args) throws Exception {
        String destDocx = "C:\\Users\\10858\\Desktop\\套红.docx";
        String content = "C:\\Users\\10858\\Desktop\\工作流相关文档.docx";
        TaoHongServiceImpl taoHongService = new TaoHongServiceImpl();
        taoHongService.word2RedDocument(content, destDocx);
    }

    /**
     * 创建并初始化WebOffice编辑器URL请求对象
     *
     * @param user 用户信息
     * @return WebofficeEditorGetUrlRequest 对象
     */
    private WebofficeEditorGetUrlRequest createWebofficeEditorRequest(User user) {
        WebofficeEditorGetUrlRequest request = new WebofficeEditorGetUrlRequest();
        request.setWrite("1");
        request.setExtUserid(user.getId());
        request.setExtUsername(user.getDisplayName());
        request.setWatermarkText("");
        request.setAccountSync("1");
        request.setHistory("0");
        return request;
    }

    /**
     * 下载正文
     *
     * @param id 正文id
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            DocumentWpsModel documentWps = documentWpsApi.findById(tenantId, id).getData();
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, documentWps.getFileName());
            OutputStream out = response.getOutputStream();
            HttpURLConnection conn;
            try {
                AppFilesApi apiInstance =
                    new AppFilesApi(YUN_WPS_BASE_PATH_GRAPH, YUN_WPS_APP_ID, YUN_WPS_APP_SECRET, YUN_WPS_APP_SCOPE);
                FileContent result =
                    apiInstance.appGetFileContent(documentWps.getVolumeId(), documentWps.getFileId(), null);
                LOGGER.debug("下载正文,文件内容获取result:{}", result);
                URL url = new URL(YUN_WPS_DOWNLOAD_PATH + result.getUrl());
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

    @GetMapping(value = "/getDocument")
    public Map<String, Object> getDocument(@RequestParam String processSerialNumber, @RequestParam String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("fileUrl", "");
        map.put("y9FileStoreId", "");
        try {
            String y9FileStoreId = y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, "").getData();
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

    @PostMapping(value = "/saveWps")
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
    @GetMapping("/showWps")
    public String showWord(@RequestParam String processSerialNumber, @RequestParam String processInstanceId,
        @RequestParam String itembox, Model model) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            // 初始化模型属性
            initializeModelAttributes(model, itembox, processInstanceId, processSerialNumber, userId, tenantId);
            // 登录WPS并获取用户信息
            String wpsSid = loginWpsAndGetSid();
            User user = getUserProfile(wpsSid);
            // 获取WPS API实例
            AppFilesApi apiInstance = getWpsApiInstance();
            // 查找现有文档
            DocumentWpsModel documentWps =
                documentWpsApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (documentWps != null) {
                // 处理已存在的文档
                handleExistingDocument(documentWps, itembox, apiInstance, user, wpsSid, model);
            } else {
                // 创建新文档
                DocumentWpsModel newDocument = createNewDocument(tenantId, processSerialNumber, processInstanceId,
                    userId, apiInstance, user, model);
                model.addAttribute("id", newDocument.getId());
            }
        } catch (Exception e) {
            model.addAttribute(DOC_URL_KEY, "发生异常");
            LOGGER.error("发生异常", e);
        }

        return "intranet/webOfficeWps";
    }

    /**
     * 获取文档标题
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例ID
     * @return 文档标题
     */
    private String getDocumentTitle(String tenantId, String processSerialNumber, String processInstanceId) {
        try {
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                return model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                return processModel.getTitle();
            }
        } catch (Exception e) {
            LOGGER.warn("获取文档标题失败，使用默认标题", e);
            return "正文";
        }
    }

    /**
     * 处理已存在的WPS文档
     *
     * @param documentWps WPS文档模型
     * @param itembox 文档箱类型
     * @param apiInstance WPS文件API实例
     * @param result0 用户信息
     * @param wpsSid WPS会话ID
     * @param model Spring Model对象
     */
    private void handleExistingDocument(DocumentWpsModel documentWps, String itembox, AppFilesApi apiInstance,
        User result0, String wpsSid, Model model) {
        try {
            if (isPreviewMode(itembox)) {
                handlePreviewMode(documentWps, apiInstance, wpsSid, model);
            } else {
                handleEditMode(documentWps, apiInstance, result0, model);
            }

            // 公共属性设置
            model.addAttribute("hasContent", documentWps.getHasContent());
            model.addAttribute("id", documentWps.getId());

            // 获取下载URL
            try {
                FileContent result =
                    apiInstance.appGetFileContent(documentWps.getVolumeId(), documentWps.getFileId(), null);
                LOGGER.debug("获取wps文件内容，结果result:{}", result);
                model.addAttribute("downloadUrl", YUN_WPS_DOWNLOAD_PATH + result.getUrl());
            } catch (Exception e) {
                LOGGER.warn("Exception when calling AppFilesApi#appGetFileContent", e);
            }
        } catch (Exception e) {
            LOGGER.error("处理已存在文档异常", e);
        }
    }

    /**
     * 判断是否为预览模式
     *
     * @param itembox 文档箱类型
     * @return 是否为预览模式
     */
    private boolean isPreviewMode(String itembox) {
        return itembox.equals(ItemBoxTypeEnum.TODO.getValue()) || itembox.equals(ItemBoxTypeEnum.DRAFT.getValue());
    }

    /**
     * 处理预览模式
     *
     * @param documentWps WPS文档模型
     * @param apiInstance WPS文件API实例
     * @param wpsSid WPS会话ID
     * @param model Spring Model对象
     */
    private void handlePreviewMode(DocumentWpsModel documentWps, AppFilesApi apiInstance, String wpsSid, Model model) {
        try {
            FilePreview result = apiInstance.appGetFilePreview(documentWps.getVolumeId(), documentWps.getFileId(), "",
                "", true, true, "", "", wpsSid, "", "");
            LOGGER.debug("获取wps文件预览信息result:{}", result);
            model.addAttribute(DOC_URL_KEY, result.getUrl());
        } catch (Exception e) {
            LOGGER.warn("Exception when calling AppFilesApi#appGetFileContent", e);
        }
    }

    /**
     * 处理编辑模式
     *
     * @param documentWps WPS文档模型
     * @param apiInstance WPS文件API实例
     * @param result0 用户信息
     * @param model Spring Model对象
     */
    private void handleEditMode(DocumentWpsModel documentWps, AppFilesApi apiInstance, User result0, Model model) {
        try {
            WebofficeEditorGetUrlRequest body1 = createWebofficeEditorRequest(result0);
            FileEditor result1 =
                apiInstance.appGetFileEditor(documentWps.getVolumeId(), documentWps.getFileId(), body1);
            String docUrl = result1.getUrl();
            model.addAttribute(DOC_URL_KEY, docUrl);
        } catch (ApiException e) {
            LOGGER.warn("Exception when calling AppFilesApi#appGetFileEditor", e);
        }
    }

    /**
     * 创建新的WPS文档
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例ID
     * @param userId 用户ID
     * @param apiInstance WPS文件API实例
     * @param result0 用户信息
     * @param model Spring Model对象
     * @return 创建的文档模型
     */
    private DocumentWpsModel createNewDocument(String tenantId, String processSerialNumber, String processInstanceId,
        String userId, AppFilesApi apiInstance, User result0, Model model) {
        try {
            String documentTitle = getDocumentTitle(tenantId, processSerialNumber, processInstanceId);
            documentTitle = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";

            // 创建空文件
            CreateEmptyRequest body = new CreateEmptyRequest();
            body.setFileName(documentTitle + DOCX_KEY);
            EmptyFile result = apiInstance.appCreateEmpty(VOLUME, ROOT, body);
            LOGGER.debug("获取空文件结果result:{}", result);
            // 设置文件权限
            setFilePermissions(result0);
            // 获取编辑URL
            WebofficeEditorGetUrlRequest body1 = createWebofficeEditorRequest(result0);

            FileEditor result1 = apiInstance.appGetFileEditor(result.getVolumeId(), result.getId(), body1);
            String docUrl = result1.getUrl();
            model.addAttribute(DOC_URL_KEY, docUrl);
            LOGGER.debug("result1:{}", result1);

            // 保存文档信息
            DocumentWpsModel documentWps = new DocumentWpsModel();
            documentWps.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            documentWps.setFileId(result.getId());
            documentWps.setFileName(documentTitle + DOCX_KEY);
            documentWps.setFileType("docx");
            documentWps.setHasContent("0");
            documentWps.setIstaohong("0");
            documentWps.setProcessInstanceId(processInstanceId);
            documentWps.setProcessSerialNumber(processSerialNumber);
            documentWps.setSaveDate(Y9DateTimeUtils.formatCurrentDateTime());
            documentWps.setTenantId(tenantId);
            documentWps.setUserId(userId);
            documentWps.setVolumeId(result.getVolumeId());

            documentWpsApi.saveDocumentWps(tenantId, documentWps);

            return documentWps;
        } catch (Exception e) {
            LOGGER.error("创建新文档异常", e);
            throw new RuntimeException("创建新文档失败", e);
        }
    }

    /**
     * 设置文件权限
     *
     * @param result0 用户信息
     */
    private void setFilePermissions(User result0) {
        try {
            FilePermissionCreateRequest body0 = new FilePermissionCreateRequest();
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
            // 注释掉的权限设置代码可以根据需要启用
            // apiInstance.appCreateFilePermission(result.getVolumeId(), result.getId(), body0);
        } catch (Exception e) {
            LOGGER.warn("设置文件权限异常", e);
        }
    }

    /**
     * 初始化模型属性
     */
    private void initializeModelAttributes(Model model, String itembox, String processInstanceId,
        String processSerialNumber, String userId, String tenantId) {
        model.addAttribute(DOC_URL_KEY, "");
        model.addAttribute("itembox", itembox);
        model.addAttribute("hasContent", "0");
        model.addAttribute("processInstanceId", processInstanceId);
        model.addAttribute("processSerialNumber", processSerialNumber);
        model.addAttribute("id", "");
        model.addAttribute("userId", userId);
        model.addAttribute("tenantId", tenantId);
    }

    /**
     * 登录WPS并获取会话ID
     */
    private String loginWpsAndGetSid() throws Exception {
        String wpsSid = new YunApi(YUN_WPS_BASE_PATH).yunLogin(YUN_WPS_USER_NAME, YUN_WPS_USER_PD);
        LOGGER.debug("wpsSid:{}", wpsSid);
        return wpsSid;
    }

    /**
     * 获取用户配置信息
     */
    private User getUserProfile(String wpsSid) throws Exception {
        UserOrgApi apiInstance0 = new UserOrgApi(YUN_WPS_BASE_PATH_GRAPH, YUN_WPS_APP_ID, YUN_WPS_APP_SECRET,
            YUN_WPS_REDIRECT_URI, YUN_WPS_USER_SCOPE, wpsSid);
        User result0 = apiInstance0.userGetProfile();
        LOGGER.debug("User:{}", result0);
        return result0;
    }

    /**
     * 获取WPS API实例
     */
    private AppFilesApi getWpsApiInstance() {
        return new AppFilesApi(YUN_WPS_BASE_PATH_GRAPH, YUN_WPS_APP_ID, YUN_WPS_APP_SECRET, YUN_WPS_APP_SCOPE);
    }

    /**
     * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param file 文件
     * @return Map
     */
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber, @RequestParam String processInstanceId,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            String tmpdir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tmpdir, genRealFileName(file.getOriginalFilename()));
            file.transferTo(tempFile);

            AppFilesApi appFilesApi =
                new AppFilesApi(YUN_WPS_BASE_PATH_GRAPH, YUN_WPS_APP_ID, YUN_WPS_APP_SECRET, YUN_WPS_APP_SCOPE);

            UploadTransactionCreateRequest uploadRequest = new UploadTransactionCreateRequest();
            uploadRequest.setFileName(this.genRealFileName(file.getOriginalFilename()));
            uploadRequest.setFileSize(tempFile.length());
            uploadRequest.setUploadMethod(UploadMethod.POST);
            uploadRequest.setFileNameConflictBehavior(UploadConflictBehavior.RENAME);
            uploadRequest.setFilePath(tempFile.getAbsolutePath());
            UploadTransactionPatchResponse uploadResponse =
                appFilesApi.appCreateUploadTransaction(VOLUME, ROOT, uploadRequest);
            try {
                Files.delete(tempFile.toPath());
            } catch (IOException e) {
                LOGGER.error("删除临时文件失败: {}", tempFile.getAbsolutePath(), e);
            }
            String documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = processModel.getTitle();
                processInstanceId = processModel.getProcessInstanceId();
            }
            documentTitle = StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文";

            cn.wps.yun.model.File File = uploadResponse.getFile();
            DocumentWpsModel documentWps = new DocumentWpsModel();
            documentWps.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            documentWps.setFileId(File.getId());
            documentWps.setFileName(documentTitle + DOCX_KEY);
            documentWps.setFileType("docx");
            documentWps.setHasContent("1");
            documentWps.setIstaohong("0");
            documentWps.setProcessInstanceId(processInstanceId);
            documentWps.setProcessSerialNumber(processSerialNumber);
            documentWps.setSaveDate(Y9DateTimeUtils.formatCurrentDateTime());
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
