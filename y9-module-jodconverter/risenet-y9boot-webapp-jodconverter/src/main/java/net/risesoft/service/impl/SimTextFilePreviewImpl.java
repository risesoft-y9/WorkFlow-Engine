package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.FileUtils;

/**
 * 处理文本文件
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class SimTextFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        String decodedUrl = fileAttribute.getDecodedUrl();
        String fileName = fileAttribute.getName();
        ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, "txt", fileName);
        if (0 != response.getCode()) {
            model.addAttribute("msg", response.getMsg());
            model.addAttribute("fileType", fileAttribute.getSuffix());
            return "fileNotSupported";
        }
        model.addAttribute("fileName", response.getMsg());
        return "txt";
    }

}
