package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;

/**
 * svg 图片文件处理
 *
 */
@Service
public class SvgFilePreviewImpl implements FilePreview {

    private final CommonPreviewImpl commonPreview;

    public SvgFilePreviewImpl(CommonPreviewImpl commonPreview) {
        this.commonPreview = commonPreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        commonPreview.filePreviewHandle(url, model, fileAttribute);
        return SVG_FILE_PREVIEW_PAGE;
    }
}
