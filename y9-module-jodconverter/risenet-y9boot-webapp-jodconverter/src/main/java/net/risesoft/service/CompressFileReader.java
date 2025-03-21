package net.risesoft.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.FileType;
import net.risesoft.utils.RarUtils;
import net.risesoft.web.filter.BaseUrlFilter;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

@Slf4j
@Component
public class CompressFileReader {
    private final FileHandlerService fileHandlerService;

    public CompressFileReader(FileHandlerService fileHandlerService) {
        this.fileHandlerService = fileHandlerService;
    }

    public String unRar(String paths, String passWord, String fileName) throws Exception {
        List<String> imgUrls = new ArrayList<>();
        String baseUrl = BaseUrlFilter.getBaseUrl();
        String archiveFileName = fileHandlerService.getFileNameFromPath(paths);
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(paths, "r");
            SevenZip.initSevenZipFromPlatformJAR();
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            String folderName = paths.substring(paths.lastIndexOf(File.separator) + 1);
            String extractPath = paths.substring(0, paths.lastIndexOf(folderName));
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            final String[] str = {null};
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (!item.isFolder()) {
                    ExtractOperationResult result;
                    result = item.extractSlow(data -> {
                        try {
                            str[0] = RarUtils.getUtf8String(item.getPath());
                            if (RarUtils.isMessyCode(str[0])) {
                                str[0] = new String(item.getPath().getBytes(StandardCharsets.ISO_8859_1), "gbk");
                            }
                            str[0] = str[0].replace("\\", File.separator); // Linux 下路径错误
                            String str1 = str[0].substring(0, str[0].lastIndexOf(File.separator) + 1);
                            File file = new File(extractPath, folderName + "_" + File.separator + str1);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            OutputStream out =
                                new FileOutputStream(extractPath + folderName + "_" + File.separator + str[0], true);
                            IOUtils.write(data, out);
                            out.close();
                        } catch (Exception e) {
                            LOGGER.error("解压失败！", e);
                        }
                        return data.length;
                    }, passWord);
                    if (result == ExtractOperationResult.OK) {
                        FileType type = FileType.typeFromUrl(str[0]);
                        if (type.equals(FileType.PICTURE)) {
                            imgUrls.add(baseUrl + folderName + "_/" + str[0].replace("\\", "/"));
                        }
                        fileHandlerService.putImgCache(fileName, imgUrls);
                    } else {
                        return null;
                    }
                }
            }
            return archiveFileName + "_";
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    LOGGER.error("Error closing archive: ", e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing file: ", e);
                }
            }
        }
    }

}
