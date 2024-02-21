package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;

/**
 * Content :图片文件处理
 */
@Service
public class Online3DFilePreviewImpl implements FilePreview {

    private final CommonPreviewImpl commonPreview;

    public Online3DFilePreviewImpl(CommonPreviewImpl commonPreview) {
        this.commonPreview = commonPreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        commonPreview.filePreviewHandle(url, model, fileAttribute);
        return ONLINE3D_PREVIEW_PAGE;
    }
}
