package net.risesoft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;

/**
 * Office工具类
 */
public class OfficeUtils {

    private static final String POI_INVALID_PASSWORD_MSG = "password";

    /**
     * 判断office（word,excel,ppt）文件是否受密码保护
     *
     * @param path office文件路径
     * @return 是否受密码保护
     */
    public static boolean isPwdProtected(String path) {
        try (InputStream propStream = Files.newInputStream(Paths.get(path))) {
            ExtractorFactory.createExtractor(propStream);
        } catch (Exception e) {
            // 统一处理所有异常，检查是否包含密码相关的错误信息
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains(POI_INVALID_PASSWORD_MSG)) {
                return true;
            }
            // 检查嵌套异常
            Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
            for (Throwable throwable : throwableArray) {
                if ((throwable instanceof IOException || throwable instanceof EncryptedDocumentException)
                    && throwable.getMessage() != null
                    && throwable.getMessage().toLowerCase().contains(POI_INVALID_PASSWORD_MSG)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断office文件是否可打开（兼容）
     *
     * @param path office文件路径
     * @param password 文件密码
     * @return 是否可打开（兼容）
     */
    public static synchronized boolean isCompatible(String path, String password) {
        InputStream propStream = null;
        try {
            propStream = Files.newInputStream(Paths.get(path));
            Biff8EncryptionKey.setCurrentUserPassword(password);
            ExtractorFactory.createExtractor(propStream);
        } catch (Exception e) {
            return false;
        } finally {
            Biff8EncryptionKey.setCurrentUserPassword(null);
            if (propStream != null) {// 如果文件输入流不是null
                try {
                    propStream.close();// 关闭文件输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}