package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;

/**
 * flv文件预览处理实现
 **/
@Service
public class FlvFilePreviewImpl implements FilePreview {

    private final MediaFilePreviewImpl mediaFilePreview;

    public FlvFilePreviewImpl(MediaFilePreviewImpl mediaFilePreview) {
        this.mediaFilePreview = mediaFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        mediaFilePreview.filePreviewHandle(url, model, fileAttribute);
        return FLV_FILE_PREVIEW_PAGE;
    }
}
