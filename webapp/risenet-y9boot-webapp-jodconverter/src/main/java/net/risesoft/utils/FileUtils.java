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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.FileAttribute;
import net.risesoft.model.FileType;

/**
 *
 * @author yudian-it
 * @date 2017/11/13
 */
@Component
@Slf4j
public class FileUtils {

    final String REDIS_FILE_PREVIEW_PDF_KEY = "converted-preview-pdf-file";
    /**
     * 压缩包内图片文件集合
     */
    final String REDIS_FILE_PREVIEW_IMGS_KEY = "converted-preview-imgs-file";

    @Value("${converted.file.charset}")
    String charset;

    @Value("${simText}")
    String[] simText;

    @Value("${media}")
    String[] media;

    /**
     * 对转换后的文件进行操作(改变编码方式)
     *
     * @param outFilePath
     */
    public void doActionConvertedFile(String outFilePath) {
        StringBuffer sb = new StringBuffer();
        try {
            File sourceFile = new File(outFilePath);
            String os = System.getProperty("os.name");
            String encoding = "GBK";
            boolean win = os != null && os.toLowerCase().indexOf("win") > -1;
            boolean lin = os != null && os.toLowerCase().indexOf("linux") > -1;
            if (win) {
                encoding = "GBK";
            } else if (lin) {
                encoding = "UTF-8";
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(outFilePath), encoding));

            File tmpUtf8File = new File(outFilePath + ".utf8");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpUtf8File), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            sb.append("<script src=\"/jodconverter/static/js/jquery-3.0.0.min.js\" type=\"text/javascript\"></script>");
            sb.append("<script src=\"/jodconverter/static/js/excel.header.js\" type=\"text/javascript\"></script>");
            sb.append("<link rel=\"stylesheet\" href=\"http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css\">");
            bufferedWriter.write(sb.toString());
            bufferedWriter.close();
            bufferedReader.close();
            // 删除源文件
            sourceFile.delete();
            // 重命名
            tmpUtf8File.renameTo(sourceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileAttribute getFileAttribute(String url) {
        String decodedUrl = null;
        try {
            decodedUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("url解码失败");
        }
        // 路径转码
        FileType type = typeFromUrl(url);
        String suffix = suffixFromUrl(url);
        // 抽取文件并返回文件列表
        String fileName = getFileNameFromUrl(decodedUrl);
        return new FileAttribute(type, suffix, fileName, url, decodedUrl);
    }

    /**
     * 判断文件编码格式
     *
     * @param path
     * @return
     */
    public String getFileEncodeUtfgbk(String path) {
        String enc = Charset.forName("GBK").name();
        File file = new File(path);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] b = new byte[3];
            in.read(b);
            in.close();
            boolean bo = b[0] == -17 && b[1] == -69 && b[2] == -65;
            if (bo) {
                enc = Charset.forName("UTF-8").name();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug("文件编码格式为:{}", enc);
        return enc;
    }

    /**
     * 从路径中获取
     *
     * @param path 类似这种：C:\Users\yudian-it\Downloads
     * @return
     */
    public String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 从url中剥离出文件名
     *
     * @param url 格式如：http://keking.ufile.ucloud.com.cn/20171113164107_月度绩效表模板(新).xls?UCloudPublicKey=ucloudtangshd@weifenf.com14355492830001993909323&Expires=&Signature=I
     *            D1NOFtAJSPT16E6imv6JWuq0k=
     * @return
     */
    public String getFileNameFromUrl(String url) {
        // 因为url的参数中可能会存在/的情况，所以直接url.lastIndexOf("/")会有问题
        // 所以先从？处将url截断，然后运用url.lastIndexOf("/")获取文件名
        String noQueryUrl = url.substring(0, url.indexOf("?") != -1 ? url.indexOf("?") : url.length());
        String fileName = noQueryUrl.substring(noQueryUrl.lastIndexOf("/") + 1);
        return fileName;
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public String getSuffixFromFileName(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return suffix;
    }

    public List<String> listArchiveTypes() {
        List<String> list = Lists.newArrayList();
        list.add("rar");
        list.add("zip");
        list.add("jar");
        list.add("7-zip");
        list.add("tar");
        list.add("gzip");
        list.add("7z");
        return list;
    }

    public List<String> listOfficeTypes() {
        List<String> list = Lists.newArrayList();
        list.add("docx");
        // list.add("doc");//暂不支持doc文件预览
        list.add("xls");
        list.add("xlsx");
        list.add("ppt");
        list.add("pptx");
        return list;
    }

    public List<String> listPictureTypes() {
        List<String> list = Lists.newArrayList();
        list.add("jpg");
        list.add("jpeg");
        list.add("png");
        list.add("gif");
        list.add("bmp");
        list.add("ico");
        list.add("RAW");
        return list;
    }

    /**
     * 获取文件后缀
     *
     * @param url
     * @return
     */
    private String suffixFromUrl(String url) {
        String nonPramStr = url.substring(0, url.indexOf("?") != -1 ? url.indexOf("?") : url.length());
        String fileName = nonPramStr.substring(nonPramStr.lastIndexOf("/") + 1);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileType;
    }

    /**
     * 查看文件类型(防止参数中存在.点号或者其他特殊字符，所以先抽取文件名，然后再获取文件类型)
     *
     * @param url
     * @return
     */
    public FileType typeFromUrl(String url) {
        String nonPramStr = url.substring(0, url.indexOf("?") != -1 ? url.indexOf("?") : url.length());
        String fileName = nonPramStr.substring(nonPramStr.lastIndexOf("/") + 1);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (listPictureTypes().contains(fileType.toLowerCase())) {
            return FileType.picture;
        }
        if (listArchiveTypes().contains(fileType.toLowerCase())) {
            return FileType.compress;
        }
        if (listOfficeTypes().contains(fileType.toLowerCase())) {
            return FileType.office;
        }
        if (Arrays.asList(simText).contains(fileType.toLowerCase())) {
            return FileType.simText;
        }
        if (Arrays.asList(media).contains(fileType.toLowerCase())) {
            return FileType.media;
        }
        String pdf = "pdf";
        if (pdf.equalsIgnoreCase(fileType)) {
            return FileType.pdf;
        }
        return FileType.other;
    }
}
