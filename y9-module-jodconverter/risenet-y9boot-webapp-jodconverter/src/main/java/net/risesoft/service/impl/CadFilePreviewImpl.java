package net.risesoft.service.impl;

import static net.risesoft.service.impl.OfficeFilePreviewImpl.getPreviewType;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.KkFileUtils;
import net.risesoft.utils.WebUtils;
import net.risesoft.web.filter.BaseUrlFilter;

@Slf4j
@Service
public class CadFilePreviewImpl implements FilePreview {

    private static final String OFFICE_PREVIEW_TYPE_IMAGE = "image";
    private static final String OFFICE_PREVIEW_TYPE_ALL_IMAGES = "allImages";

    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public CadFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        // 预览Type，参数传了就取参数的，没传取系统默认
        String officePreviewType = getOfficePreviewType(fileAttribute);
        String baseUrl = BaseUrlFilter.getBaseUrl();
        String fileName = fileAttribute.getName();
        String cadPreviewType = ConfigConstants.getCadPreviewType();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();

        // 处理文件转换
        if (shouldConvertFile(fileAttribute, cacheName)) {
            String result =
                handleFileConversion(model, fileAttribute, fileName, cadPreviewType, cacheName, outFilePath);
            if (result != null) {
                return result;
            }
        }

        // 处理文件预览
        return handleFilePreview(model, officePreviewType, baseUrl, cadPreviewType, cacheName, outFilePath,
            fileAttribute);
    }

    /**
     * 获取Office预览类型
     */
    private String getOfficePreviewType(FileAttribute fileAttribute) {
        return fileAttribute.getOfficePreviewType() == null ? ConfigConstants.getOfficePreviewType()
            : fileAttribute.getOfficePreviewType();
    }

    /**
     * 判断是否需要转换文件
     */
    private boolean shouldConvertFile(FileAttribute fileAttribute, String cacheName) {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        return forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(cacheName)
            || !ConfigConstants.isCacheEnabled();
    }

    /**
     * 处理文件转换
     */
    private String handleFileConversion(Model model, FileAttribute fileAttribute, String fileName,
        String cadPreviewType, String cacheName, String outFilePath) {
        ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
        if (response.isFailure()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
        }

        String filePath = response.getContent();
        if (!StringUtils.hasText(outFilePath)) {
            return null;
        }

        return convertCadFile(model, fileAttribute, filePath, outFilePath, cadPreviewType, cacheName);
    }

    /**
     * 转换CAD文件
     */
    private String convertCadFile(Model model, FileAttribute fileAttribute, String filePath, String outFilePath,
        String cadPreviewType, String cacheName) {
        String imageUrls;
        try {
            imageUrls = fileHandlerService.cadToPdf(filePath, outFilePath, cadPreviewType, fileAttribute);
        } catch (Exception e) {
            LOGGER.error("cad 转 pdf 异常！", e);
            return otherFilePreview.notSupportedFile(model, fileAttribute, "CAD转换异常，请联系管理员");
        }

        if (imageUrls == null) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "CAD转换异常，请联系管理员");
        }

        // 清理源文件和缓存处理
        handlePostConversion(fileAttribute, filePath, cacheName, outFilePath);
        return null;
    }

    /**
     * 处理转换后操作
     */
    private void handlePostConversion(FileAttribute fileAttribute, String filePath, String cacheName,
        String outFilePath) {
        // 是否保留CAD源文件
        if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
            KkFileUtils.deleteFileByPath(filePath);
        }

        if (ConfigConstants.isCacheEnabled()) {
            // 加入缓存
            fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
        }
    }

    /**
     * 处理文件预览
     */
    private String handleFilePreview(Model model, String officePreviewType, String baseUrl, String cadPreviewType,
        String cacheName, String outFilePath, FileAttribute fileAttribute) {
        cacheName = WebUtils.encodeFileName(cacheName);

        // 特殊格式预览
        if ("tif".equalsIgnoreCase(cadPreviewType)) {
            model.addAttribute("currentUrl", cacheName);
            return TIFF_FILE_PREVIEW_PAGE;
        } else if ("svg".equalsIgnoreCase(cadPreviewType)) {
            model.addAttribute("currentUrl", cacheName);
            return SVG_FILE_PREVIEW_PAGE;
        }

        // 图片预览模式
        if (baseUrl != null && (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)
            || OFFICE_PREVIEW_TYPE_ALL_IMAGES.equals(officePreviewType))) {
            return getPreviewType(model, fileAttribute, officePreviewType, cacheName, outFilePath, fileHandlerService,
                OFFICE_PREVIEW_TYPE_IMAGE, otherFilePreview);
        }

        // PDF预览模式
        model.addAttribute("pdfUrl", cacheName);
        return PDF_FILE_PREVIEW_PAGE;
    }
}
