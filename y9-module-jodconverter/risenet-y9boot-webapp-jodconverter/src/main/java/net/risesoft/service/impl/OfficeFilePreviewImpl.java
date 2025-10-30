package net.risesoft.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.jodconverter.core.office.OfficeException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.service.OfficeToPdfService;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.KkFileUtils;
import net.risesoft.utils.OfficeUtils;
import net.risesoft.utils.WebUtils;
import net.risesoft.web.filter.BaseUrlFilter;

/**
 * Content :处理office文件
 */
@Slf4j
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    public static final String OFFICE_PREVIEW_TYPE_IMAGE = "image";
    public static final String OFFICE_PREVIEW_TYPE_ALL_IMAGES = "allImages";
    public static final String NEEDFILE_PASSWORD = "needFilePassword";
    private static final String OFFICE_PASSWORD_MSG = "password";
    private final FileHandlerService fileHandlerService;
    private final OfficeToPdfService officeToPdfService;
    private final OtherFilePreviewImpl otherFilePreview;

    public OfficeFilePreviewImpl(
        FileHandlerService fileHandlerService,
        OfficeToPdfService officeToPdfService,
        OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.officeToPdfService = officeToPdfService;
        this.otherFilePreview = otherFilePreview;
    }

    static String getPreviewType(Model model, FileAttribute fileAttribute, String officePreviewType, String pdfName,
        String outFilePath, FileHandlerService fileHandlerService, String officePreviewTypeImage,
        OtherFilePreviewImpl otherFilePreview) {
        String suffix = fileAttribute.getSuffix();
        boolean isPPT = suffix.equalsIgnoreCase("ppt") || suffix.equalsIgnoreCase("pptx");
        List<String> imageUrls = convertPdfToImages(outFilePath, pdfName, fileAttribute, fileHandlerService, model);

        // 如果imageUrls为null，说明在convertPdfToImages中已经设置了相应的model属性并返回了页面
        if (imageUrls == null) {
            return EXEL_FILE_PREVIEW_PAGE; // 已在convertPdfToImages中处理了model属性设置
        }

        if (imageUrls.isEmpty()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "office转图片异常，请联系管理员");
        }

        model.addAttribute("imgUrls", imageUrls);
        model.addAttribute("currentUrl", imageUrls.get(0));
        if (officePreviewTypeImage.equals(officePreviewType)) {
            // PPT 图片模式使用专用预览页面
            return (isPPT ? PPT_FILE_PREVIEW_PAGE : OFFICE_PICTURE_FILE_PREVIEW_PAGE);
        } else {
            return PICTURE_FILE_PREVIEW_PAGE;
        }
    }

    /**
     * 转换PDF文件为图片
     */
    private static List<String> convertPdfToImages(String outFilePath, String pdfName, FileAttribute fileAttribute,
        FileHandlerService fileHandlerService, Model model) {
        try {
            return fileHandlerService.pdf2jpg(outFilePath, outFilePath, pdfName, fileAttribute);
        } catch (Exception e) {
            return handleConversionException(e, model);
        }
    }

    /**
     * 处理转换异常
     */
    private static List<String> handleConversionException(Exception e, Model model) {
        Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
        for (Throwable throwable : throwableArray) {
            if (throwable instanceof IOException || throwable instanceof EncryptedDocumentException) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains(OFFICE_PASSWORD_MSG)) {
                    model.addAttribute(NEEDFILE_PASSWORD, true);
                    return null; // 密码缺失，需用户输入
                }
            }
        }
        // 其他异常情况，记录日志并返回空列表表示转换失败
        LOGGER.error("PDF转图片失败", e);
        return List.of(); // 明确返回空列表而非null，便于上层判断
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        // 预览Type，参数传了就取参数的，没传取系统默认
        String officePreviewType = fileAttribute.getOfficePreviewType();
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        boolean isHtmlView = fileAttribute.isHtmlView();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();

        // 处理特殊格式的Web预览
        String specialPreviewPage = handleSpecialWebPreview(officePreviewType, suffix, url, model);
        if (specialPreviewPage != null) {
            return specialPreviewPage;
        }

        // 处理文件转换和缓存
        if (shouldProcessFile(fileAttribute, cacheName)) {
            String processResult = processAndCacheFile(model, fileAttribute, fileName, cacheName, outFilePath);
            if (processResult != null) {
                return processResult;
            }
        }

        // 处理图片预览模式
        String baseUrl = BaseUrlFilter.getBaseUrl();
        if (!isHtmlView && baseUrl != null && isImagePreviewMode(officePreviewType)) {
            return getPreviewType(model, fileAttribute, officePreviewType, cacheName, outFilePath, fileHandlerService,
                OFFICE_PREVIEW_TYPE_IMAGE, otherFilePreview);
        }

        // 默认PDF预览
        model.addAttribute("pdfUrl", WebUtils.encodeFileName(cacheName));
        return isHtmlView ? EXEL_FILE_PREVIEW_PAGE : PDF_FILE_PREVIEW_PAGE;
    }

    /**
     * 处理特殊格式的Web预览
     */
    private String handleSpecialWebPreview(String officePreviewType, String suffix, String url, Model model) {
        if (!officePreviewType.equalsIgnoreCase("html")) {
            if (ConfigConstants.getOfficeTypeWeb().equalsIgnoreCase("web")) {
                if (suffix.equalsIgnoreCase("xlsx")) {
                    model.addAttribute("pdfUrl", KkFileUtils.htmlEscape(url));
                    return XLSX_FILE_PREVIEW_PAGE;
                }
                if (suffix.equalsIgnoreCase("csv")) {
                    model.addAttribute("csvUrl", KkFileUtils.htmlEscape(url));
                    return CSV_FILE_PREVIEW_PAGE;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否需要处理文件
     */
    private boolean shouldProcessFile(FileAttribute fileAttribute, String cacheName) {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        return forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(cacheName)
            || !ConfigConstants.isCacheEnabled();
    }

    /**
     * 处理文件转换和缓存
     */
    private String processAndCacheFile(Model model, FileAttribute fileAttribute, String fileName, String cacheName,
        String outFilePath) {
        ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
        if (response.isFailure()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
        }

        String filePath = response.getContent();
        return handleFileConversion(model, fileAttribute, filePath, cacheName, outFilePath);
    }

    /**
     * 处理文件转换
     */
    private String handleFileConversion(Model model, FileAttribute fileAttribute, String filePath, String cacheName,
        String outFilePath) {
        boolean isPwdProtectedOffice = OfficeUtils.isPwdProtected(filePath);
        String filePassword = fileAttribute.getFilePassword();

        // 检查密码保护文件
        if (isPwdProtectedOffice && !StringUtils.hasLength(filePassword)) {
            model.addAttribute(NEEDFILE_PASSWORD, true);
            return EXEL_FILE_PREVIEW_PAGE;
        }

        // 执行文件转换
        if (StringUtils.hasText(outFilePath)) {
            try {
                officeToPdfService.openOfficeToPDF(filePath, outFilePath, fileAttribute);
            } catch (OfficeException e) {
                return handleConversionException(model, fileAttribute, filePath, filePassword, isPwdProtectedOffice);
            }

            // 转换后处理
            postProcessConvertedFile(fileAttribute, filePath, outFilePath, cacheName, isPwdProtectedOffice);
        }

        return null;
    }

    /**
     * 处理转换异常
     */
    private String handleConversionException(Model model, FileAttribute fileAttribute, String filePath,
        String filePassword, boolean isPwdProtectedOffice) {
        if (isPwdProtectedOffice && !OfficeUtils.isCompatible(filePath, filePassword)) {
            // 加密文件密码错误，提示重新输入
            model.addAttribute(NEEDFILE_PASSWORD, true);
            model.addAttribute("filePasswordError", true);
            return EXEL_FILE_PREVIEW_PAGE;
        }
        return otherFilePreview.notSupportedFile(model, fileAttribute, "抱歉，该文件版本不兼容，文件版本错误。");
    }

    /**
     * 转换后处理
     */
    private void postProcessConvertedFile(FileAttribute fileAttribute, String filePath, String outFilePath,
        String cacheName, boolean isPwdProtectedOffice) {
        boolean isHtmlView = fileAttribute.isHtmlView();
        boolean userToken = fileAttribute.getUsePasswordCache();

        if (isHtmlView) {
            // 对转换后的文件进行操作(改变编码方式)
            fileHandlerService.doActionConvertedFile(outFilePath);
        }

        // 是否保留OFFICE源文件
        if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
            KkFileUtils.deleteFileByPath(filePath);
        }

        if (userToken || !isPwdProtectedOffice) {
            // 加入缓存
            fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
        }
    }

    /**
     * 判断是否为图片预览模式
     */
    private boolean isImagePreviewMode(String officePreviewType) {
        return OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)
            || OFFICE_PREVIEW_TYPE_ALL_IMAGES.equals(officePreviewType);
    }
}
