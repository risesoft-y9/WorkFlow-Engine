package net.risesoft.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.FileAttribute;
import net.risesoft.model.FileType;
import net.risesoft.service.cache.CacheService;

/**
 * Content :消费队列中的转换文件
 */
@Slf4j
@Service
public class FileConvertQueueTask {

    private final FilePreviewFactory previewFactory;
    private final CacheService cacheService;
    private final FileHandlerService fileHandlerService;

    public FileConvertQueueTask(
        FilePreviewFactory previewFactory,
        CacheService cacheService,
        FileHandlerService fileHandlerService) {
        this.previewFactory = previewFactory;
        this.cacheService = cacheService;
        this.fileHandlerService = fileHandlerService;
    }

    @PostConstruct
    public void startTask() {
        new Thread(new ConvertTask(previewFactory, cacheService, fileHandlerService)).start();
        LOGGER.info("队列处理文件转换任务启动完成 ");
    }

    static class ConvertTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(ConvertTask.class);
        private final FilePreviewFactory previewFactory;
        private final CacheService cacheService;
        private final FileHandlerService fileHandlerService;

        public ConvertTask(
            FilePreviewFactory previewFactory,
            CacheService cacheService,
            FileHandlerService fileHandlerService) {
            this.previewFactory = previewFactory;
            this.cacheService = cacheService;
            this.fileHandlerService = fileHandlerService;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                String url = null;
                try {
                    url = cacheService.takeQueueTask();
                    if (url != null) {
                        FileAttribute fileAttribute = fileHandlerService.getFileAttribute(url, null);
                        FileType fileType = fileAttribute.getType();
                        logger.info("正在处理预览转换任务，url：{}，预览类型：{}", url, fileType);
                        if (isNeedConvert(fileType)) {
                            FilePreview filePreview = previewFactory.get(fileAttribute);
                            filePreview.filePreviewHandle(url, new ExtendedModelMap(), fileAttribute);
                        } else {
                            logger.info("预览类型无需处理，url：{}，预览类型：{}", url, fileType);
                        }
                    }
                } catch (Exception e) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                        ex.printStackTrace();
                    }
                    logger.info("处理预览转换任务异常，url：{}", url, e);
                }
            }
        }

        public boolean isNeedConvert(FileType fileType) {
            return fileType.equals(FileType.COMPRESS) || fileType.equals(FileType.OFFICE)
                || fileType.equals(FileType.CAD);

        }
    }

}
