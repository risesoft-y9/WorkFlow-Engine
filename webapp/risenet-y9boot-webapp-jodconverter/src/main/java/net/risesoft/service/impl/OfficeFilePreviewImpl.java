package net.risesoft.service.impl;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.FileUtils;
import net.risesoft.utils.OfficeToPdf;

/**
 * Created by kl on 2018/1/17. Content :处理office文件
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    DownloadUtils downloadUtils;

    @Autowired
    private OfficeToPdf officeToPdf;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator;
        String sourcefile = filePath + "previewFile" + File.separator + fileName;
        if (!new File(sourcefile).exists()) {
            ReturnResponse<String> response = downloadUtils.downLoad(decodedUrl, suffix, fileName);
            if (0 != response.getCode()) {
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            sourcefile = response.getContent();
        }
        String outFilePath = filePath + "previewFile" + File.separator + pdfName;
        if (StringUtils.hasText(outFilePath)) {
            officeToPdf.openOfficeToPdf(sourcefile, outFilePath);
            if (isHtml) {
                // 对转换后的文件进行操作(改变编码方式)
                fileUtils.doActionConvertedFile(outFilePath);
            }
        }
        model.addAttribute("fileName", pdfName);
        return isHtml ? "html" : "pdf";
    }
}
