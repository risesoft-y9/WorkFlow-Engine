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
 * Created by kl on 2018/1/17. Content :处理pdf文件
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class PdfFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, fileName);
        if (0 != response.getCode()) {
            model.addAttribute("msg", response.getMsg());
            return "fileNotSupported";
        }
        model.addAttribute("fileName", response.getMsg());
        return "pdf";
    }
}
