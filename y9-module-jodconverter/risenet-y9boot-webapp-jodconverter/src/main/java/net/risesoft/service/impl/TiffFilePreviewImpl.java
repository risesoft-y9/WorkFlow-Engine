package net.risesoft.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.ConvertPicUtil;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.KkFileUtils;
import net.risesoft.utils.WebUtils;

/**
 * tiff 图片文件处理
 *
 */
@Service
public class TiffFilePreviewImpl implements FilePreview {

    private static final String IMAURLS = "imgUrls";
    private static final String CURRENTURL = "currentUrl";
    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public TiffFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        String tifPreviewType = ConfigConstants.getTifPreviewType();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();

        // 处理转换预览类型
        if (isSupportedPreviewType(tifPreviewType)) {
            return handleConversionPreview(model, fileAttribute, fileName, tifPreviewType, cacheName, outFilePath);
        }

        // 处理直接预览
        return handleDirectPreview(model, fileAttribute, url, fileName, outFilePath, forceUpdatedCache);
    }

    /**
     * 判断是否为支持的预览类型
     */
    private boolean isSupportedPreviewType(String tifPreviewType) {
        return "jpg".equalsIgnoreCase(tifPreviewType) || "pdf".equalsIgnoreCase(tifPreviewType);
    }

    /**
     * 处理需要转换的预览
     */
    private String handleConversionPreview(Model model, FileAttribute fileAttribute, String fileName,
        String tifPreviewType, String cacheName, String outFilePath) {
        if (shouldConvertFile(fileAttribute, cacheName)) {
            return performFileConversion(model, fileAttribute, fileName, tifPreviewType, cacheName, outFilePath);
        } else {
            return serveFromCache(model, tifPreviewType, cacheName);
        }
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
     * 执行文件转换
     */
    private String performFileConversion(Model model, FileAttribute fileAttribute, String fileName,
        String tifPreviewType, String cacheName, String outFilePath) {
        ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
        if (response.isFailure()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
        }

        String filePath = response.getContent();

        if ("pdf".equalsIgnoreCase(tifPreviewType)) {
            return convertToPdf(model, fileAttribute, filePath, outFilePath, cacheName);
        } else {
            return convertToJpg(model, fileAttribute, filePath, outFilePath, cacheName);
        }
    }

    /**
     * 转换为PDF格式
     */
    private String convertToPdf(Model model, FileAttribute fileAttribute, String filePath, String outFilePath,
        String cacheName) {
        try {
            ConvertPicUtil.convertJpg2Pdf(filePath, outFilePath);
            handlePostConversion(fileAttribute, cacheName, outFilePath);
            model.addAttribute("pdfUrl", WebUtils.encodeFileName(cacheName));
            return PDF_FILE_PREVIEW_PAGE;
        } catch (Exception e) {
            return handleConversionException(model, fileAttribute, e);
        }
    }

    /**
     * 转换为JPG格式
     */
    private String convertToJpg(Model model, FileAttribute fileAttribute, String filePath, String outFilePath,
        String cacheName) {
        try {
            List<String> listPic2Jpg =
                ConvertPicUtil.convertTif2Jpg(filePath, outFilePath, fileAttribute.forceUpdatedCache());
            // 是否保留源文件,转换失败保留源文件,转换成功删除源文件
            if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
                KkFileUtils.deleteFileByPath(filePath);
            }
            if (ConfigConstants.isCacheEnabled()) {
                // 加入缓存
                fileHandlerService.putImgCache(cacheName, listPic2Jpg);
                fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
            }
            model.addAttribute(IMAURLS, listPic2Jpg);
            model.addAttribute(CURRENTURL, listPic2Jpg.get(0));
            return PICTURE_FILE_PREVIEW_PAGE;
        } catch (Exception e) {
            return handleConversionException(model, fileAttribute, e);
        }
    }

    /**
     * 处理转换后操作
     */
    private void handlePostConversion(FileAttribute fileAttribute, String cacheName, String outFilePath) {
        // 是否保留TIFF源文件
        if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
            // KkFileUtils.deleteFileByPath(filePath);
        }
        if (ConfigConstants.isCacheEnabled()) {
            // 加入缓存
            fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
        }
    }

    /**
     * 处理转换异常
     */
    private String handleConversionException(Model model, FileAttribute fileAttribute, Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("Bad endianness tag (not 0x4949 or 0x4d4d)")) {
            model.addAttribute(IMAURLS, model.getAttribute("url")); // 假设url在model中
            model.addAttribute(CURRENTURL, model.getAttribute("url"));
            return FilePreview.PICTURE_FILE_PREVIEW_PAGE;
        } else {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "TIF转JPG异常，请联系系统管理员!");
        }
    }

    /**
     * 从缓存提供服务
     */
    private String serveFromCache(Model model, String tifPreviewType, String cacheName) {
        if ("pdf".equalsIgnoreCase(tifPreviewType)) {
            model.addAttribute("pdfUrl", WebUtils.encodeFileName(cacheName));
            return PDF_FILE_PREVIEW_PAGE;
        } else if ("jpg".equalsIgnoreCase(tifPreviewType)) {
            List<String> imgCache = fileHandlerService.getImgCache(cacheName);
            if (imgCache != null && !imgCache.isEmpty()) {
                model.addAttribute(IMAURLS, imgCache);
                model.addAttribute(CURRENTURL, imgCache.get(0));
            }
            return PICTURE_FILE_PREVIEW_PAGE;
        }
        return PICTURE_FILE_PREVIEW_PAGE;
    }

    /**
     * 处理直接预览
     */
    private String handleDirectPreview(Model model, FileAttribute fileAttribute, String url, String fileName,
        String outFilePath, boolean forceUpdatedCache) {
        // 不是http开头，浏览器不能直接访问，需下载到本地
        if (url != null && !url.toLowerCase().startsWith("http")) {
            return handleNonHttpUrl(model, fileAttribute, fileName, outFilePath, forceUpdatedCache);
        }

        model.addAttribute(CURRENTURL, url);
        return TIFF_FILE_PREVIEW_PAGE;
    }

    /**
     * 处理非HTTP URL
     */
    private String handleNonHttpUrl(Model model, FileAttribute fileAttribute, String fileName, String outFilePath,
        boolean forceUpdatedCache) {
        if (forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(fileName)
            || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            }
            model.addAttribute(CURRENTURL, fileHandlerService.getRelativePath(response.getContent()));
            if (ConfigConstants.isCacheEnabled()) {
                // 加入缓存
                fileHandlerService.addConvertedFile(fileName, fileHandlerService.getRelativePath(outFilePath));
            }
        } else {
            model.addAttribute(CURRENTURL, WebUtils.encodeFileName(fileName));
        }
        return TIFF_FILE_PREVIEW_PAGE;
    }

}
