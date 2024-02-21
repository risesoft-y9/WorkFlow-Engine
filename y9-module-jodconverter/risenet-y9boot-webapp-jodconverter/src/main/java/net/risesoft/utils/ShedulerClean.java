package net.risesoft.utils;

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
public class ShedulerClean {

    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Description:每2个小时执行一次
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void clean() {
        Date date = new Date();
        try {
            LOGGER.info("******************定时任务删除预览文件开始：{}******************", sdf.format(date));
            calendar.clear();
            calendar.setTime(date);
            // 当前时间减去2个小时，当前时间的前2个小时
            calendar.add(Calendar.HOUR, -2);
            long timeInMillis = calendar.getTimeInMillis();
            String filePath = Y9Context.getWebRootRealPath() + "static" + File.separator + "previewFile";
            File realFile = new File(filePath);
            if (realFile.exists() && realFile.isDirectory()) {
                File[] subfiles = realFile.listFiles();
                for (File file : subfiles) {
                    // 文件最后更新时间
                    long lastModified = file.lastModified();
                    // 删除当前时间前2个小时的预览文件
                    if (lastModified < timeInMillis) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
