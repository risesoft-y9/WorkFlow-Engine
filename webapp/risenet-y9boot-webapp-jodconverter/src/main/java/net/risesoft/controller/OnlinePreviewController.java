package net.risesoft.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.risesoft.service.FilePreview;
import net.risesoft.service.FilePreviewFactory;

/**
 * 在线预览
 * 
 * @author Think
 *
 */
@Controller
@RequestMapping(value = "")
public class OnlinePreviewController {

    @Autowired
    FilePreviewFactory previewFactory;

    /**
     * 根据url获取文件内容 当pdfjs读取存在跨域问题的文件时将通过此接口读取
     *
     * @param urlPath
     * @param resp
     */
    @RequestMapping(value = "/getCorsFile")
    public void getCorsFile(String urlPath, HttpServletResponse resp) {
        InputStream inputStream = null;
        try {
            // 打开请求连接
            URL dir = new URL(urlPath);
            URLConnection connection = dir.openConnection();
            inputStream = connection.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            while (-1 != (len = inputStream.read(bs))) {
                resp.getOutputStream().write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    /**
     * 在线预览
     * 
     * @param url
     * @param model
     * @return
     */
    @RequestMapping(value = "onlinePreview", method = RequestMethod.GET)
    public String onlinePreview(String url, Model model, HttpServletRequest req) {
        req.setAttribute("fileKey", req.getParameter("fileKey"));
        FilePreview filePreview = previewFactory.get(url);
        return filePreview.filePreviewHandle(url, model);
    }

}
