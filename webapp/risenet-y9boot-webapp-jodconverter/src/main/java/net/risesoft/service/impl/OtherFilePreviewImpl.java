package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.FileUtils;

/**
 * Created by kl on 2018/1/17. Content :其他文件
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class OtherFilePreviewImpl implements FilePreview {
    @Autowired
    FileUtils fileUtils;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        model.addAttribute("fileType", fileAttribute.getSuffix());
        model.addAttribute("msg", "系统暂不支持该格式文件(" + fileAttribute.getSuffix() + ")的在线预览");
        return "fileNotSupported";
    }
}
