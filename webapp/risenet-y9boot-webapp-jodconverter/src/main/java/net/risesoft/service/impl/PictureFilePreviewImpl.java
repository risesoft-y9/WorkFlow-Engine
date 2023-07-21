package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.google.common.collect.Lists;

import net.risesoft.service.FilePreview;
import net.risesoft.utils.FileUtils;

/**
 * Created by kl on 2018/1/17. Content :图片文件处理
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class PictureFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Override
    public String filePreviewHandle(String url, Model model) {
        List<String> imgUrls = Lists.newArrayList(url);
        model.addAttribute("imgurls", imgUrls);
        model.addAttribute("currentUrl", url);
        return "picture";
    }
}
