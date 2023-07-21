package net.risesoft.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableScheduling
@Slf4j
public class DeletePrintPdfFileUtil {

    Calendar calendar = Calendar.getInstance();

    public DeletePrintPdfFileUtil() {

    }

    /**
     * 定时任务，定时删除打印转换生成的pdf文件,每5个小时执行一次
     */
    @Scheduled(cron = "0 0 */5 * * ?")
    public void deletePdf() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            LOGGER.info("******************定时任务删除pdf文件开始：" + sdf.format(date) + "******************");
            calendar.clear();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, -5);
            long timeInMillis = calendar.getTimeInMillis();

            String pdfPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "formToPDF";
            LOGGER.info("********************pdfPath：" + pdfPath + "********************");

            File realFile = new File(pdfPath);
            if (realFile.exists() && realFile.isDirectory()) {
                File[] subfiles = realFile.listFiles();
                for (File file : subfiles) {
                    if (file.getName().contains(".pdf")) {
                        long lastModified = file.lastModified();
                        if (lastModified < timeInMillis) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
