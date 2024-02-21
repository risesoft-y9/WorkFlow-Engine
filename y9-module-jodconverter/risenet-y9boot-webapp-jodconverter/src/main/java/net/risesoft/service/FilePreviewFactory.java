package net.risesoft.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.risesoft.model.FileAttribute;
import net.risesoft.utils.FileUtils;

/**
 * Created by kl on 2018/1/17. Content :
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class FilePreviewFactory {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    ApplicationContext context;

    public FilePreview get(String url) {
        Map<String, FilePreview> filePreviewMap = context.getBeansOfType(FilePreview.class);
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        return filePreviewMap.get(fileAttribute.getType().getInstanceName());
    }
}
