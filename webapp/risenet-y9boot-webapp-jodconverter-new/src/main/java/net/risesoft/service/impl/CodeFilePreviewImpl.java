package net.risesoft.service.impl;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * @author kl (http://kailing.pub)
 * @since 2021/2/18
 */
@Component
public class CodeFilePreviewImpl implements FilePreview {

    private final SimTextFilePreviewImpl filePreviewHandle;

    public CodeFilePreviewImpl(SimTextFilePreviewImpl filePreviewHandle) {
        this.filePreviewHandle = filePreviewHandle;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        filePreviewHandle.filePreviewHandle(url, model, fileAttribute);
        return CODE_FILE_PREVIEW_PAGE;
    }
}
