package net.risesoft.service.impl;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.CompressFileReader;
import net.risesoft.utils.KkFileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;


@Service
public class CompressFilePreviewImpl implements FilePreview {

    private final FileHandlerService fileHandlerService;
    private final CompressFileReader compressFileReader;
    private final OtherFilePreviewImpl otherFilePreview;
    private static final String Rar_PASSWORD_MSG = "password";

    public CompressFilePreviewImpl(FileHandlerService fileHandlerService, CompressFileReader compressFileReader, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.compressFileReader = compressFileReader;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        String filePassword = fileAttribute.getFilePassword();
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        String fileTree = null;
        // 判断文件名是否存在(redis缓存读取)
        if (forceUpdatedCache || !StringUtils.hasText(fileHandlerService.getConvertedFile(fileName)) || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            }
            String filePath = response.getContent();
            try {
                fileTree = compressFileReader.unRar(filePath, filePassword, fileName);
            } catch (Exception e) {
                Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
                for (Throwable throwable : throwableArray) {
                    if (throwable instanceof IOException || throwable instanceof EncryptedDocumentException) {
                        if (e.getMessage().toLowerCase().contains(Rar_PASSWORD_MSG)) {
                            model.addAttribute("needFilePassword", true);
                            return EXEL_FILE_PREVIEW_PAGE;
                        }
                    }
                }
            }
            if (!ObjectUtils.isEmpty(fileTree)) {
                //是否保留压缩包源文件
                if (ConfigConstants.getDeleteSourceFile()) {
                    KkFileUtils.deleteFileByPath(filePath);
                }
                if (ConfigConstants.isCacheEnabled()) {
                    // 加入缓存
                    fileHandlerService.addConvertedFile(fileName, fileTree);
                }
            } else {
                return otherFilePreview.notSupportedFile(model, fileAttribute, "压缩文件密码错误! 压缩文件损坏!  压缩文件类型不受支持!");
            }
        } else {
            fileTree = fileHandlerService.getConvertedFile(fileName);
        }
        model.addAttribute("fileName", fileName);
        model.addAttribute("fileTree", fileTree);
        return COMPRESS_FILE_PREVIEW_PAGE;
    }
}
