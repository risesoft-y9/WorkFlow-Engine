package net.risesoft.service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
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

@Component
public class CompressFileReader {
    private static final String FILE_DIR = ConfigConstants.getFileDir();
    private final FileHandlerService fileHandlerService;

    public CompressFileReader(FileHandlerService fileHandlerService) {
        this.fileHandlerService = fileHandlerService;
    }

    public String unRar(String filePath, String filePassword, String fileName, FileAttribute fileAttribute)
        throws Exception {
        List<String> imgUrls = new ArrayList<>();
        String baseUrl = BaseUrlFilter.getBaseUrl();
        String packagePath = "_";

        String folderName = getFolderName(filePath, fileAttribute);
        Path folderPath = Paths.get(FILE_DIR, folderName + packagePath);
        Files.createDirectories(folderPath);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
            IInArchive inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile))) {

            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                processArchiveItem(item, folderPath, filePassword, imgUrls, fileName, packagePath, baseUrl);
            }
            fileHandlerService.putImgCache(fileName + packagePath, imgUrls);
        } catch (Exception e) {
            throw new Exception("Error processing RAR file: " + e.getMessage(), e);
        }
        return folderName + packagePath;
    }

    private String getFolderName(String filePath, FileAttribute fileAttribute) {
        String folderName = filePath.replace(FILE_DIR, ""); // 修复压缩包 多重目录获取路径错误
        if (fileAttribute != null && fileAttribute.isCompressFile()) {
            folderName = "_decompression" + folderName;
        }
        return folderName;
    }

    private void processArchiveItem(ISimpleInArchiveItem item, Path folderPath, String filePassword,
        List<String> imgUrls, String fileName, String packagePath, String baseUrl) throws Exception {
        if (!item.isFolder()) {
            final Path filePathInsideArchive = getFilePathInsideArchive(item, folderPath);
            ExtractOperationResult result = extractItemToFile(item, filePathInsideArchive, filePassword);

            if (result != ExtractOperationResult.OK) {
                handleExtractionError(result);
            }

            FileType type = FileType.typeFromUrl(filePathInsideArchive.toString());
            if (type.equals(FileType.PICTURE)) {
                addImageUrl(imgUrls, baseUrl, fileName, packagePath, folderPath, filePathInsideArchive);
            }
        }
    }

    private void handleExtractionError(ExtractOperationResult result) throws Exception {
        if (ExtractOperationResult.WRONG_PASSWORD.equals(result)) {
            throw new Exception("Password");
        } else {
            throw new Exception("Failed to extract RAR file.");
        }
    }

    private ExtractOperationResult extractItemToFile(ISimpleInArchiveItem item, Path filePathInsideArchive,
        String filePassword) throws SevenZipException {
        return item.extractSlow(data -> {
            try (OutputStream out =
                new BufferedOutputStream(new FileOutputStream(filePathInsideArchive.toFile(), true))) {
                out.write(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data.length;
        }, filePassword);
    }

    private void addImageUrl(List<String> imgUrls, String baseUrl, String fileName, String packagePath, Path folderPath,
        Path filePathInsideArchive) {
        String relativePath = folderPath.relativize(filePathInsideArchive).toString().replace("\\", "/");
        String encodedUrl =
            baseUrl + URLEncoder.encode(fileName + packagePath + "/" + relativePath, StandardCharsets.UTF_8);
        imgUrls.add(encodedUrl);
    }

    private Path getFilePathInsideArchive(ISimpleInArchiveItem item, Path folderPath)
        throws SevenZipException, UnsupportedEncodingException {
        String insideFileName = RarUtils.getUtf8String(item.getPath());
        if (RarUtils.isMessyCode(insideFileName)) {
            insideFileName = new String(item.getPath().getBytes(StandardCharsets.ISO_8859_1), "gbk");
        }

        // 正规化路径并验证是否安全
        Path normalizedPath = folderPath.resolve(insideFileName).normalize();
        if (!normalizedPath.startsWith(folderPath)) {
            throw new SecurityException("Unsafe path detected: " + insideFileName);
        }

        try {
            Files.createDirectories(normalizedPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + normalizedPath.getParent(), e);
        }
        return normalizedPath;
    }

}