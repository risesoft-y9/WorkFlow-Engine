package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.service.FilePreview;
import net.risesoft.utils.FileUtils;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class MediaFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Override
    public String filePreviewHandle(String url, Model model) {
        model.addAttribute("mediaUrl", url);
        return "media";
    }

}
