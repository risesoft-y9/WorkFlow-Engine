package net.risesoft.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;

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
