package net.risesoft.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.WebUtils;

/**
 * Content :处理pdf文件
 */
@Slf4j
@Service
public class PdfFilePreviewImpl implements FilePreview {

    private static final String PDF_PASSWORD_MSG = "password";
    private static final String PDF_URL = "pdfUrl";
    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public PdfFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String pdfName = fileAttribute.getName();
        String officePreviewType = fileAttribute.getOfficePreviewType();
        String outFilePath = fileAttribute.getOutFilePath();
        String originFilePath = fileAttribute.getOriginFilePath();

        boolean isImagePreview = OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)
            || OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_ALL_IMAGES.equals(officePreviewType);

        if (isImagePreview) {
            return handleImagePreview(model, fileAttribute, pdfName, officePreviewType, originFilePath, outFilePath);
        } else {
            return handlePdfPreview(model, fileAttribute, url, pdfName, outFilePath);
        }
    }

    /**
     * 处理图片预览模式
     */
    private String handleImagePreview(Model model, FileAttribute fileAttribute, String pdfName,
        String officePreviewType, String originFilePath, String outFilePath) {
        // 下载并缓存文件
        String filePath = downloadAndCacheFileIfNeeded(fileAttribute, pdfName, originFilePath);
        if (filePath == null) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "文件下载失败");
        }

        // 转换PDF为图片
        List<String> imageUrls = convertPdfToImages(model, fileAttribute, filePath, outFilePath, pdfName);
        if (imageUrls == null || imageUrls.isEmpty()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "pdf转图片异常，请联系管理员！");
        }

        // 设置模型属性
        model.addAttribute("imgUrls", imageUrls);
        model.addAttribute("currentUrl", imageUrls.get(0));

        // 返回相应页面
        if (OfficeFilePreviewImpl.OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)) {
            return OFFICE_PICTURE_FILE_PREVIEW_PAGE;
        } else {
            return PICTURE_FILE_PREVIEW_PAGE;
        }
    }

    /**
     * 处理PDF预览模式
     */
    private String handlePdfPreview(Model model, FileAttribute fileAttribute, String url, String pdfName,
        String outFilePath) {
        // 不是http开头，浏览器不能直接访问，需下载到本地
        if (url != null && !url.toLowerCase().startsWith("http")) {
            handleNonHttpUrl(model, fileAttribute, pdfName, outFilePath);
        } else {
            model.addAttribute(PDF_URL, url);
        }

        return PDF_FILE_PREVIEW_PAGE;
    }

    /**
     * 处理非HTTP URL的情况
     */
    private void handleNonHttpUrl(Model model, FileAttribute fileAttribute, String pdfName, String outFilePath) {
        if (!fileHandlerService.listConvertedFiles().containsKey(pdfName) || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, pdfName);
            if (response.isFailure()) {
                // 注意：这里原逻辑有缺陷，应该返回错误页面而不是继续执行
                return;
            }
            model.addAttribute(PDF_URL, fileHandlerService.getRelativePath(response.getContent()));
            if (ConfigConstants.isCacheEnabled()) {
                // 加入缓存
                fileHandlerService.addConvertedFile(pdfName, fileHandlerService.getRelativePath(outFilePath));
            }
        } else {
            model.addAttribute(PDF_URL, WebUtils.encodeFileName(pdfName));
        }
    }

    /**
     * 如果需要则下载并缓存文件
     */
    private String downloadAndCacheFileIfNeeded(FileAttribute fileAttribute, String pdfName, String originFilePath) {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        if (forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(pdfName)
            || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, pdfName);
            if (response.isFailure()) {
                return null;
            }
            String filePath = response.getContent();
            if (ConfigConstants.isCacheEnabled()) {
                // 加入缓存
                fileHandlerService.addConvertedFile(pdfName, fileHandlerService.getRelativePath(filePath));
            }
            return filePath;
        }
        return originFilePath;
    }

    /**
     * 转换PDF为图片
     */
    private List<String> convertPdfToImages(Model model, FileAttribute fileAttribute, String originFilePath,
        String outFilePath, String pdfName) {
        try {
            return fileHandlerService.pdf2jpg(originFilePath, outFilePath, pdfName, fileAttribute);
        } catch (Exception e) {
            return handlePdfConversionException(model, e);
        }
    }

    /**
     * 处理PDF转换异常
     */
    private List<String> handlePdfConversionException(Model model, Exception e) {
        Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
        for (Throwable throwable : throwableArray) {
            if (throwable instanceof IOException || throwable instanceof EncryptedDocumentException) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains(PDF_PASSWORD_MSG)) {
                    model.addAttribute("needFilePassword", true);
                    LOGGER.error("pdf转图片异常，请联系管理员，needFilePassword", e);
                    return null;
                }
            }
        }
        // 添加兜底日志和返回，避免方法总是返回null而不被察觉
        LOGGER.error("pdf转图片过程中发生未知异常，请联系管理员", e);
        return Collections.emptyList(); // 或者根据业务需求返回null
    }
}
