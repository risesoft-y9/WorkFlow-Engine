package net.risesoft.service.impl;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.CompressFileReader;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.KkFileUtils;

/**
 * Content :处理压缩包文件
 */
@Service
public class CompressFilePreviewImpl implements FilePreview {

    private static final String RAR_PASSWORD_MSG = "password";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CompressFilePreviewImpl.class);
    private final FileHandlerService fileHandlerService;
    private final CompressFileReader compressFileReader;
    private final OtherFilePreviewImpl otherFilePreview;

    public CompressFilePreviewImpl(
        FileHandlerService fileHandlerService,
        CompressFileReader compressFileReader,
        OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.compressFileReader = compressFileReader;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        String filePassword = fileAttribute.getFilePassword();

        // 获取或生成文件树
        String fileTree = getFileTree(model, fileAttribute, fileName, filePassword);
        if (fileTree == null) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "该压缩包文件无法处理!");
        }

        // 设置模型属性并返回视图
        model.addAttribute("fileName", fileName);
        model.addAttribute("fileTree", fileTree);
        return COMPRESS_FILE_PREVIEW_PAGE;
    }

    /**
     * 获取文件树（从缓存或重新生成）
     */
    private String getFileTree(Model model, FileAttribute fileAttribute, String fileName, String filePassword) {
        if (shouldRegenerateFileTree(fileAttribute, fileName)) {
            return generateFileTree(model, fileAttribute, fileName, filePassword);
        } else {
            return fileHandlerService.getConvertedFile(fileName);
        }
    }

    /**
     * 判断是否需要重新生成文件树
     */
    private boolean shouldRegenerateFileTree(FileAttribute fileAttribute, String fileName) {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        return forceUpdatedCache || !StringUtils.hasText(fileHandlerService.getConvertedFile(fileName))
            || !ConfigConstants.isCacheEnabled();
    }

    /**
     * 生成文件树
     */
    private String generateFileTree(Model model, FileAttribute fileAttribute, String fileName, String filePassword) {
        ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
        if (response.isFailure()) {
            return handleDownloadFailure(model, fileAttribute, response);
        }

        String filePath = response.getContent();
        String fileTree = processCompressFile(model, filePath, filePassword, fileName, fileAttribute);

        if (!ObjectUtils.isEmpty(fileTree)) {
            handlePostProcessing(fileAttribute, filePath, fileName, fileTree);
        }

        return fileTree;
    }

    /**
     * 处理下载失败情况
     */
    private String handleDownloadFailure(Model model, FileAttribute fileAttribute, ReturnResponse<String> response) {
        return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
    }

    /**
     * 处理压缩文件
     */
    private String processCompressFile(Model model, String filePath, String filePassword, String fileName,
        FileAttribute fileAttribute) {
        try {
            return compressFileReader.unRar(filePath, filePassword, fileName, fileAttribute);
        } catch (Exception e) {
            return handleCompressException(model, e);
        }
    }

    /**
     * 处理压缩文件异常
     */
    private String handleCompressException(Model model, Exception e) {
        if (e.getMessage().toLowerCase().contains(RAR_PASSWORD_MSG)) {
            model.addAttribute("needFilePassword", true);
            return EXEL_FILE_PREVIEW_PAGE;
        } else {
            logger.error("Error processing RAR file: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 处理后续操作（文件清理和缓存）
     */
    private void handlePostProcessing(FileAttribute fileAttribute, String filePath, String fileName, String fileTree) {
        // 是否保留压缩包源文件
        if (!fileAttribute.isCompressFile() && ConfigConstants.getDeleteSourceFile()) {
            KkFileUtils.deleteFileByPath(filePath);
        }

        if (ConfigConstants.isCacheEnabled()) {
            // 加入缓存
            fileHandlerService.addConvertedFile(fileName, fileTree);
        }
    }
}
