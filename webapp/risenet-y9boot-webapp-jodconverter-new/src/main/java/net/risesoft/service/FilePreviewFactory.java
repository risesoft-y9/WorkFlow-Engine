package net.risesoft.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.risesoft.model.FileAttribute;

@Service
public class FilePreviewFactory {

    private final ApplicationContext context;

    public FilePreviewFactory(ApplicationContext context) {
        this.context = context;
    }

    public FilePreview get(FileAttribute fileAttribute) {
        return context.getBean(fileAttribute.getType().getInstanceName(), FilePreview.class);
    }
}
