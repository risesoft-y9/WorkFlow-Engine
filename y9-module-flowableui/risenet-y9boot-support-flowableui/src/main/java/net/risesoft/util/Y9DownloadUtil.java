package net.risesoft.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9DownloadUtil {

    private static final Pattern p = Pattern.compile("\\s*");

    /**
     * 设置文件下载响应头
     *
     * @param response HttpServletResponse对象
     * @param request HttpServletRequest对象
     * @param title 文件标题
     */
    public static void setDownloadResponseHeaders(HttpServletResponse response, HttpServletRequest request,
        String title) {
        String userAgent = request.getHeader("User-Agent");
        String encodedTitle = encodeFileName(title, userAgent);

        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=\"" + encodedTitle + "\"");
        response.setContentType("application/octet-stream");
        // 设置字符编码
        if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
            response.setHeader("Content-type", "text/html;charset=GBK");
        } else {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
        }
    }

    /**
     * 根据浏览器类型对文件名进行编码
     *
     * @param fileName 原始文件名
     * @param userAgent 用户代理字符串
     * @return 编码后的文件名
     */
    public static String encodeFileName(String fileName, String userAgent) {
        try {
            fileName = replaceSpecialStr(fileName);
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                return new String(fileName.getBytes("gb2312"), "ISO8859-1");
            } else if (userAgent.contains("Firefox")) {
                return "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8))))
                    + "?=";
            } else {
                String encodedName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                return StringUtils.replace(encodedName, "+", "%20"); // 替换空格
            }
        } catch (Exception e) {
            LOGGER.error("文件名编码异常", e);
            return fileName;
        }
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str 字符串
     * @return String 去除空格后的字符串
     */
    public static String replaceSpecialStr(String str) {
        if (str == null) {
            return "";
        }
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 提取文件名（去除路径）
     *
     * @param originalFileName 原始文件名
     * @return 处理后的文件名
     */
    public static String extractFileName(String originalFileName) {
        if (StringUtils.isBlank(originalFileName)) {
            return "";
        }
        String fileName = originalFileName;
        // 处理不同操作系统的路径分隔符
        if (fileName.contains(File.separator)) {
            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        }
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        return fileName;
    }

    /**
     * 验证文件类型是否有效(仅针对正文)
     *
     * @param fileType 文件类型
     * @return 是否有效
     */
    public static boolean isValidFileType(String fileType) {
        return fileType == null || (!fileType.equals(".doc") && !fileType.equals(".docx") && !fileType.equals(".pdf")
            && !fileType.equals(".tif") && !fileType.equals(".ofd"));
    }

}
