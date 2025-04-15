package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FileHandlerService;
import net.risesoft.utils.KkFileUtils;

/**
 * Content :图片文件处理
 */
@Service
public class PictureFilePreviewImpl extends CommonPreviewImpl {

    private final FileHandlerService fileHandlerService;

    public PictureFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        super(fileHandlerService, otherFilePreview);
        this.fileHandlerService = fileHandlerService;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        url = KkFileUtils.htmlEscape(url);
        List<String> imgUrls = new ArrayList<>();
        imgUrls.add(url);
        String compressFileKey = fileAttribute.getCompressFileKey();
        List<String> zipImgUrls = fileHandlerService.getImgCache(compressFileKey);
        if (!CollectionUtils.isEmpty(zipImgUrls)) {
            imgUrls.addAll(zipImgUrls);
        }
        // 不是http开头，浏览器不能直接访问，需下载到本地
        super.filePreviewHandle(url, model, fileAttribute);
        model.addAttribute("imgUrls", imgUrls);
        return PICTURE_FILE_PREVIEW_PAGE;
    }
}
