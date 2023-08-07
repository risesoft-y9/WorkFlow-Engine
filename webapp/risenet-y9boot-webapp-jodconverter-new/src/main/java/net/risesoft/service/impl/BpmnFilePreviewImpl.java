package net.risesoft.service.impl;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;


@Component
public class BpmnFilePreviewImpl implements FilePreview {

    private final CommonPreviewImpl commonPreview;

    public BpmnFilePreviewImpl(CommonPreviewImpl commonPreview) {
        this.commonPreview = commonPreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        commonPreview.filePreviewHandle(url, model, fileAttribute);
        model.addAttribute("fileName", fileAttribute.getName());
        return FilePreview.BPMN_FILE_PREVIEW_PAGE;
    }
}
