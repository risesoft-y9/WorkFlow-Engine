package net.risesoft.service;

import net.risesoft.model.FileAttribute;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


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
