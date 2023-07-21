package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.FileUtils;
import net.risesoft.utils.ZipReader;

/**
 * 处理压缩包文件
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class CompressFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Autowired
    ZipReader zipReader;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        String suffix = fileAttribute.getSuffix();
        String fileTree = null;
        ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, fileName);
        if (0 != response.getCode()) {
            model.addAttribute("msg", response.getMsg());
            return "fileNotSupported";
        }
        String filePath = response.getContent();
        String zip = "zip", jar = "jar", gzip = "gzip", rar = "rar";
        if (zip.equalsIgnoreCase(suffix) || jar.equalsIgnoreCase(suffix) || gzip.equalsIgnoreCase(suffix)) {
            fileTree = zipReader.readZipFile(filePath, fileName);
        } else if (rar.equalsIgnoreCase(suffix)) {
            fileTree = zipReader.unRar(filePath, fileName);
        }
        if (null != fileTree) {
            model.addAttribute("fileTree", fileTree);
            return "compress";
        } else {
            model.addAttribute("msg", "压缩文件类型不受支持，尝试在压缩的时候选择RAR4格式");
            return "fileNotSupported";
        }
    }
}
