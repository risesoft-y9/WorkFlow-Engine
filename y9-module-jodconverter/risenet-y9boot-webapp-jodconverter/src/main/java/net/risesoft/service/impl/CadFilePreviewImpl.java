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
        String officePreviewType = fileAttribute.getOfficePreviewType() == null ? ConfigConstants.getOfficePreviewType()
            : fileAttribute.getOfficePreviewType();
        String baseUrl = BaseUrlFilter.getBaseUrl();
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        String fileName = fileAttribute.getName();
        String cadPreviewType = ConfigConstants.getCadPreviewType();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();
        // 判断之前是否已转换过，如果转换过，直接返回，否则执行转换
        if (forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(cacheName)
            || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            }
            String filePath = response.getContent();
            String imageUrls = null;
            if (StringUtils.hasText(outFilePath)) {
                try {
                    imageUrls = fileHandlerService.cadToPdf(filePath, outFilePath, cadPreviewType, fileAttribute);
                } catch (Exception e) {
                    LOGGER.error("cad 转 pdf 异常！", e);
                }
                if (imageUrls == null) {
                    return otherFilePreview.notSupportedFile(model, fileAttribute, "CAD转换异常，请联系管理员");
                }
                // 是否保留CAD源文件
                if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
                    KkFileUtils.deleteFileByPath(filePath);
                }
                if (ConfigConstants.isCacheEnabled()) {
                    // 加入缓存
                    fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
                }
            }
        }
        cacheName = WebUtils.encodeFileName(cacheName);
        if ("tif".equalsIgnoreCase(cadPreviewType)) {
            model.addAttribute("currentUrl", cacheName);
            return TIFF_FILE_PREVIEW_PAGE;
        } else if ("svg".equalsIgnoreCase(cadPreviewType)) {
            model.addAttribute("currentUrl", cacheName);
            return SVG_FILE_PREVIEW_PAGE;
        }
        if (baseUrl != null && (OFFICE_PREVIEW_TYPE_IMAGE.equals(officePreviewType)
            || OFFICE_PREVIEW_TYPE_ALL_IMAGES.equals(officePreviewType))) {
            return getPreviewType(model, fileAttribute, officePreviewType, cacheName, outFilePath, fileHandlerService,
                OFFICE_PREVIEW_TYPE_IMAGE, otherFilePreview);
        }
        model.addAttribute("pdfUrl", cacheName);
        return PDF_FILE_PREVIEW_PAGE;
    }
}
