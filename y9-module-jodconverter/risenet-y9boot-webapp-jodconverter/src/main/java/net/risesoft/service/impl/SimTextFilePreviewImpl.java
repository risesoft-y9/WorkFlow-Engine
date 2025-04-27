package net.risesoft.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.utils.EncodingDetects;
import net.risesoft.utils.KkFileUtils;

/**
 * Content :处理文本文件
 */
@Service
public class SimTextFilePreviewImpl implements FilePreview {

    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public SimTextFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        String filePath = fileAttribute.getOriginFilePath();
        if (forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(fileName)
            || !ConfigConstants.isCacheEnabled()) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            }
            filePath = response.getContent();
            if (ConfigConstants.isCacheEnabled()) {
                fileHandlerService.addConvertedFile(fileName, filePath); // 加入缓存
            }
            try {
                String fileData = HtmlUtils.htmlEscape(textData(filePath, fileName));
                model.addAttribute("textData", Base64.encodeBase64String(fileData.getBytes()));
            } catch (IOException e) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, e.getLocalizedMessage());
            }
            return TXT_FILE_PREVIEW_PAGE;
        }
        String fileData = null;
        try {
            fileData = HtmlUtils.htmlEscape(textData(filePath, fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("textData", Base64.encodeBase64String(fileData.getBytes()));
        return TXT_FILE_PREVIEW_PAGE;
    }

    private String textData(String filePath, String fileName) throws IOException {
        File file = new File(filePath);
        if (KkFileUtils.isIllegalFileName(fileName)) {
            return null;
        }
        if (!file.exists() || file.length() == 0) {
            return "";
        } else {
            String charset = EncodingDetects.getJavaEncode(filePath);
            if ("ASCII".equals(charset)) {
                charset = StandardCharsets.US_ASCII.name();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\r\n");
            }
            br.close();
            return result.toString();
        }
    }
}
