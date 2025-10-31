package net.risesoft.utils;

import static net.risesoft.utils.KkFileUtils.isFtpUrl;
import static net.risesoft.utils.KkFileUtils.isHttpUrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.ReturnResponse;

@Slf4j
public class DownloadUtils {

    private static final String FILEDIR = ConfigConstants.getFileDir();
    private static final String URL_PARAM_FTP_USERNAME = "ftp.username";
    private static final String URL_PARAM_FTP_PASSWORD = "ftp.password";
    private static final String URL_PARAM_FTP_CONTROL_ENCODING = "ftp.control.encoding";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param fileAttribute fileAttribute
     * @param fileName 文件名
     * @return 本地文件绝对路径
     */
    public static ReturnResponse<String> downLoad(FileAttribute fileAttribute, String fileName) {
        // 忽略ssl证书
        String urlStr = prepareUrl(fileAttribute);
        ReturnResponse<String> response = new ReturnResponse<>(0, "下载成功!!!", "");
        String realPath = getRelFilePath(fileName, fileAttribute);
        // 验证文件路径合法性
        ReturnResponse<String> validationResponse = validateFilePath(realPath, urlStr);
        if (validationResponse != null) {
            return validationResponse;
        }
        // 处理压缩文件
        if (fileAttribute.isCompressFile()) {
            return handleCompressFile(fileName, response);
        }
        // 检查文件是否已存在且不需要强制更新
        if (KkFileUtils.isExist(realPath) && !fileAttribute.forceUpdatedCache()) {
            return createSuccessResponse(realPath, fileName, response);
        }
        // 执行文件下载
        return executeFileDownload(fileAttribute, urlStr, realPath, fileName, response);
    }

    /**
     * 准备URL字符串
     */
    private static String prepareUrl(FileAttribute fileAttribute) {
        try {
            // SslUtils.ignoreSsl();
            return fileAttribute.getUrl().replace("+", "%20").replace(" ", "%20");
        } catch (Exception e) {
            LOGGER.error("忽略SSL证书异常:", e);
            return fileAttribute.getUrl();
        }
    }

    /**
     * 验证文件路径合法性
     */
    private static ReturnResponse<String> validateFilePath(String realPath, String urlStr) {
        // 判断是否非法地址
        if (KkFileUtils.isIllegalFileName(realPath)) {
            return new ReturnResponse<>(1, "下载失败:文件名不合法!" + urlStr, null);
        }

        if (!KkFileUtils.isAllowedUpload(realPath)) {
            return new ReturnResponse<>(1, "下载失败:不支持的类型!" + urlStr, null);
        }
        return null;
    }

    /**
     * 处理压缩文件
     */
    private static ReturnResponse<String> handleCompressFile(String fileName, ReturnResponse<String> response) {
        response.setContent(FILEDIR + fileName);
        response.setMsg(fileName);
        return response;
    }

    /**
     * 创建成功响应
     */
    private static ReturnResponse<String> createSuccessResponse(String realPath, String fileName,
        ReturnResponse<String> response) {
        response.setContent(realPath);
        response.setMsg(fileName);
        return response;
    }

    /**
     * 执行文件下载
     */
    private static ReturnResponse<String> executeFileDownload(FileAttribute fileAttribute, String urlStr,
        String realPath, String fileName, ReturnResponse<String> response) {
        try {
            URL url = WebUtils.normalizedURL(urlStr);
            if (!fileAttribute.getSkipDownLoad()) {
                if (isHttpUrl(url)) {
                    downloadHttpFile(url, realPath, fileAttribute, response);
                } else if (isFtpUrl(url)) {
                    downloadFtpFile(realPath, fileAttribute);
                } else {
                    response.setCode(1);
                    response.setMsg("url不能识别url" + urlStr);
                }
            }
            response.setContent(realPath);
            response.setMsg(fileName);
            return response;
        } catch (Exception e) {
            return handleDownloadException(e, urlStr, response);
        }
    }

    /**
     * 下载HTTP文件
     */
    private static void downloadHttpFile(URL url, String realPath, FileAttribute fileAttribute,
        ReturnResponse<String> response) {
        File realFile = new File(realPath);
        configureHttpClient();

        RequestCallback requestCallback = request -> {
            request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            String proxyAuthorization = fileAttribute.getKkProxyAuthorization();
            if (StringUtils.hasText(proxyAuthorization)) {
                Map<String, String> proxyAuthorizationMap = mapper.readValue(proxyAuthorization, Map.class);
                proxyAuthorizationMap.forEach((key, value) -> request.getHeaders().set(key, value));
            }
        };

        try {
            restTemplate.execute(url.toURI(), HttpMethod.GET, requestCallback, fileResponse -> {
                FileUtils.copyToFile(fileResponse.getBody(), realFile);
                return null;
            });
        } catch (Exception e) {
            response.setCode(1);
            response.setContent(null);
            response.setMsg("下载失败:" + e);
        }
    }

    /**
     * 配置HTTP客户端
     */
    private static void configureHttpClient() {
        factory.setConnectionRequestTimeout(2000); // 设置超时时间
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(72000);
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new DefaultRedirectStrategy()).build();
        factory.setHttpClient(httpClient); // 加入重定向方法
        restTemplate.setRequestFactory(factory);
    }

    /**
     * 下载FTP文件
     */
    private static void downloadFtpFile(String realPath, FileAttribute fileAttribute) throws IOException {
        String ftpUsername = WebUtils.getUrlParameterReg(fileAttribute.getUrl(), URL_PARAM_FTP_USERNAME);
        String ftpPassword = WebUtils.getUrlParameterReg(fileAttribute.getUrl(), URL_PARAM_FTP_PASSWORD);
        String ftpControlEncoding = WebUtils.getUrlParameterReg(fileAttribute.getUrl(), URL_PARAM_FTP_CONTROL_ENCODING);
        FtpUtils.download(fileAttribute.getUrl(), realPath, ftpUsername, ftpPassword, ftpControlEncoding);
    }

    /**
     * 处理下载异常
     */
    private static ReturnResponse<String> handleDownloadException(Exception e, String urlStr,
        ReturnResponse<String> response) {
        LOGGER.error("文件下载失败，url：{}", urlStr);
        response.setCode(1);
        response.setContent(null);
        if (e instanceof FileNotFoundException) {
            response.setMsg("文件不存在!!!");
        } else {
            response.setMsg(e.getMessage());
        }
        return response;
    }

    /**
     * 获取真实文件绝对路径
     *
     * @param fileName 文件名
     * @return 文件路径
     */
    private static String getRelFilePath(String fileName, FileAttribute fileAttribute) {
        String type = fileAttribute.getSuffix();
        if (null == fileName) {
            UUID uuid = UUID.randomUUID();
            fileName = uuid + "." + type;
        } else { // 文件后缀不一致时，以type为准(针对simText【将类txt文件转为txt】)
            fileName = fileName.replace(fileName.substring(fileName.lastIndexOf(".") + 1), type);
        }

        String realPath = FILEDIR + fileName;
        File dirFile = new File(FILEDIR);
        if (!dirFile.exists() && !dirFile.mkdirs()) {
            LOGGER.error("创建目录【{}】失败,可能是权限不够，请检查", FILEDIR);
        }
        return realPath;
    }

}
