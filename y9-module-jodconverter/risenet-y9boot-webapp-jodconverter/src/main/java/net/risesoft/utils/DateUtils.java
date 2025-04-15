package net.risesoft.utils;

import java.time.Instant;

public class DateUtils {
    /**
     * 获取当前时间的秒级时间戳
     * 
     * @return
     */
    public static long getCurrentSecond() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 计算当前时间与指定时间的秒级时间戳差值
     * 
     * @param datetime 指定时间
     * @return 差值
     */
    public static long calculateCurrentTimeDifference(long datetime) {
        return getCurrentSecond() - datetime;
    }
}
