package net.risesoft.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 上传预览文件到文件系统
 *
 * @author Think
 *
 */
public class UploadUtils {

    /**
     * 上传解压文件到文件系统
     *
     * @param file
     * @param fileName
     * @return
     */
    public String upload(byte[] file, String fileName) {
        String url = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String fullPath = Y9FileStore.buildFullPath("jodconverter", "file", sdf.format(new Date()));
            Y9FileStoreService y9FileStoreService = Y9Context.getBean(Y9FileStoreService.class);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            url = y9FileStore.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 上传预览文件到文件系统
     *
     * @param file
     * @return
     */
    public String upload(File file) {
        String url = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String fileName = FilenameUtils.getName(file.getPath());
            String fullPath = Y9FileStore.buildFullPath("jodconverter", "file", sdf.format(new Date()));
            Y9FileStoreService y9FileStoreService = Y9Context.getBean(Y9FileStoreService.class);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            url = y9FileStore.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 上传预览文件到文件系统
     *
     * @param file
     * @param fileName
     * @return
     */
    public String upload(MultipartFile file, String fileName) {
        String url = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String fullPath = Y9FileStore.buildFullPath("jodconverter", "file", sdf.format(new Date()));
            Y9FileStoreService y9FileStoreService = Y9Context.getBean(Y9FileStoreService.class);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            url = Y9Context.getProperty("y9.common.jodconverterBaseUrl") + y9FileStore.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
