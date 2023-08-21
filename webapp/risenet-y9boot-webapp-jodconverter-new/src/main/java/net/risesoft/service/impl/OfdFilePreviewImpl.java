package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;

/**
 * ofd 图片文件处理
 *
 */
@Service
public class OfdFilePreviewImpl implements FilePreview {

    private final CommonPreviewImpl commonPreview;

    public OfdFilePreviewImpl(CommonPreviewImpl commonPreview) {
        this.commonPreview = commonPreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        commonPreview.filePreviewHandle(url, model, fileAttribute);
        return OFD_FILE_PREVIEW_PAGE;
    }
}
