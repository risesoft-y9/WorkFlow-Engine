package net.risesoft.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.risesoft.model.ReturnResponse;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Component
public class DownloadUtils {

    private static String TXT = "txt";

    private static String WENHAO = "?";

    /**
     * 转换文本文件编码为utf8 探测源文件编码,探测到编码切不为utf8则进行转码
     * 
     * @param filePath 文件路径
     */
    private static void convertTextPlainFileCharsetToUtf8(String filePath, String fileType) throws IOException {
        File sourceFile = new File(filePath);
        if (sourceFile.exists() && sourceFile.isFile() && sourceFile.canRead()) {
            String encoding = null;
            try {
                FileCharsetDetector.Observer observer = FileCharsetDetector.guessFileEncoding(sourceFile);
                // 为准确探测到编码,不适用猜测的编码
                encoding = observer.isFound() ? observer.getEncoding() : null;
                // 为准确探测到编码,可以考虑使用GBK 大部分文件都是windows系统产生的
            } catch (Exception e) {
                // 编码探测失败,
                e.printStackTrace();
            }
            boolean b = (encoding != null && !"UTF-8".equals(encoding)) && !fileType.equals("java") && !fileType.equals("sql") && !fileType.equals("xml");
            if (b) {
                // 不为utf8,进行转码
                File tmpUtf8File = new File(filePath + ".utf8");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "GBK"));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpUtf8File), "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line + "\r\n");
                }
                bufferedWriter.close();
                bufferedReader.close();
                // 删除源文件
                sourceFile.delete();
                // 重命名
                tmpUtf8File.renameTo(sourceFile);
            }
        }
    }

    /**
     * 因为jodConvert2.1不支持ms2013版本的office转换，这里偷懒，尝试看改一下文件类型，让jodConvert2.1去
     * 处理ms2013，看结果如何，如果问题很大的话只能采取其他方式，如果没有问题，暂时使用该版本来转换
     * 
     * @param type
     * @return
     */
    @SuppressWarnings("unused")
    private String dealWithMs2013(String type) {
        String newType = null;
        switch (type) {
            case "docx":
                newType = "doc";
                break;
            case "xlsx":
                newType = "doc";
                break;
            case "pptx":
                newType = "ppt";
                break;
            default:
                newType = type;
                break;
        }
        return newType;
    }

    /**
     * 一开始测试的时候发现有些文件没有下载下来，而有些可以；当时也是郁闷了好一阵，但是最终还是不得解 再次测试的时候，通过前台对比url发现，原来参数中有+号特殊字符存在，但是到后之后却变成了空格，突然恍然大悟
     * 应该是转义出了问题，url转义中会把+号当成空格来计算，所以才会出现这种情况，遂想要通过整体替换空格为加号，因为url 中的参数部分是不会出现空格的，但是文件名中就不好确定了，所以只对url参数部分做替换 注:
     * 针对URLEncoder.encode(s,charset)会将空格转成+的情况需要做下面的替换工作
     * 
     * @param urlAddress
     * @param type
     * @return
     */
    public ReturnResponse<String> downLoad(String urlAddress, String type, String fileName) {
        ReturnResponse<String> response = new ReturnResponse<>(0, "下载成功!!!", "");
        URL url = null;
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        // 压缩文件打开，不需要下载
        boolean b = urlAddress.contains("/static/previewFile/");
        if (b) {
            return this.downLoadZipFile(urlAddress, type, fileName);
        }
        try {
            urlAddress = replacePlusMark(urlAddress);
            urlAddress = encodeUrlParam(urlAddress);
            // 因为tomcat不能处理'+'号，所以讲'+'号替换成'%20%'
            urlAddress = urlAddress.replaceAll("\\+", "%20");
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String fileType = FilenameUtils.getExtension(urlAddress);
        UUID uuid = UUID.randomUUID();
        if (null == fileName) {
            fileName = uuid + "." + type;
        } else { // 文件后缀不一致时，以type为准(针对simText【将类txt文件转为txt】)
            fileName = fileName.replace(fileName.substring(fileName.lastIndexOf(".") + 1), type);
        }
        String dirPath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "previewFile" + File.separator;
        String filePath = dirPath + fileName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();

            FileOutputStream os = new FileOutputStream(filePath);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            os.close();
            in.close();
            response.setContent(filePath);
            // 同样针对类txt文件，如果成功msg包含的是转换后的文件名
            response.setMsg(fileName);

            // txt转换文件编码为utf8
            if (TXT.equals(type)) {
                convertTextPlainFileCharsetToUtf8(filePath, fileType);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            response.setCode(1);
            response.setContent(null);
            if (e instanceof FileNotFoundException) {
                response.setMsg("文件不存在!!!");
            } else {
                response.setMsg(e.getMessage());
            }
            return response;
        }
    }

    /**
     * 压缩文件下的文件转换
     * 
     * @param urlAddress
     * @param type
     * @param fileName
     * @return
     */
    public ReturnResponse<String> downLoadZipFile(String urlAddress, String type, String fileName) {
        ReturnResponse<String> response = new ReturnResponse<>(0, "下载成功!!!", "");
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String oldPath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "previewFile" + File.separator + fileName;
        String fileType = FilenameUtils.getExtension(urlAddress);
        if (fileType.contains(TXT)) {
            response.setContent(oldPath);
            response.setMsg(fileName);
            return response;
        }
        UUID uuid = UUID.randomUUID();
        if (null == fileName) {
            fileName = uuid + "." + type;
        } else { // 文件后缀不一致时，以type为准(针对simText【将类txt文件转为txt】)
            fileName = fileName.replace(fileName.substring(fileName.lastIndexOf(".") + 1), type);
        }
        String dirPath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "previewFile" + File.separator;
        String filePath = dirPath + fileName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File oldFile = new File(oldPath);
            FileInputStream in = new FileInputStream(oldFile);
            FileOutputStream os = new FileOutputStream(filePath);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            os.close();
            in.close();
            response.setContent(filePath);
            // 同样针对类txt文件，如果成功msg包含的是转换后的文件名
            response.setMsg(fileName);
            // txt转换文件编码为utf8
            if (TXT.equals(type)) {
                convertTextPlainFileCharsetToUtf8(filePath, fileType);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            response.setCode(1);
            response.setContent(null);
            if (e instanceof FileNotFoundException) {
                response.setMsg("文件不存在!!!");
            } else {
                response.setMsg(e.getMessage());
            }
            return response;
        }
    }

    /**
     * 对最有一个路径进行转码
     * 
     * @param urlAddress http://192.168.2.111:8013/demo/Handle中文.zip
     * @return
     */
    private String encodeUrlParam(String urlAddress) {
        String newUrl = "";
        try {
            String path = "";
            String param = "";
            if (urlAddress.contains(WENHAO)) {
                path = urlAddress.substring(0, urlAddress.indexOf("?"));
                param = urlAddress.substring(urlAddress.indexOf("?"));
            } else {
                path = urlAddress;
            }
            String lastPath = path.substring(path.lastIndexOf("/") + 1);
            String leftPath = path.substring(0, path.lastIndexOf("/") + 1);
            String encodeLastPath = URLEncoder.encode(lastPath, "UTF-8");
            newUrl += leftPath + encodeLastPath;
            if (urlAddress.contains(WENHAO)) {
                newUrl += param;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newUrl;
    }

    /**
     * 注:可能是原来因为前端通过encodeURI来编码的，因为通过encodeURI编码+会被转成+号(亦即没有转)，
     * 而通过encodeURIComponent则会转成%2B，这样URLDecoder是可以正确处理的，所以也就没有必要在这里替换了 转换url参数部分的空格为加号(因为在url编解码的过程中出现+转为空格的情况)
     * 
     * @param urlAddress
     * @return
     */
    private String replacePlusMark(String urlAddress) {
        if (urlAddress.contains(WENHAO)) {
            String nonParamStr = urlAddress.substring(0, urlAddress.indexOf("?") + 1);
            String paramStr = urlAddress.substring(nonParamStr.length());
            return nonParamStr + paramStr.replace(" ", "+");
        }
        return urlAddress;
    }

}
