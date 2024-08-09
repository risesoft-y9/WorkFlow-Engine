package net.risesoft.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

@Configuration
@EnableScheduling
@Slf4j
public class DeletePrintPdfFileUtil {

    Calendar calendar = Calendar.getInstance();

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public DeletePrintPdfFileUtil() {

    }

    /**
     * 定时任务，定时删除打印转换生成的pdf文件
     */
    @Scheduled(cron = "0 0 */5 * * ?") // 每5个小时执行一次
    public void deletePdf() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            LOGGER.info("******************定时任务删除pdf文件开始：{}******************", sdf.format(date));
            calendar.clear();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, -5);// 当前时间减去5个小时，当前时间的前五个小时
            long timeInMillis = calendar.getTimeInMillis();

            String pdfPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "formToPDF";
            LOGGER.info("********************pdfPath：{}********************", pdfPath);

            File realFile = new File(pdfPath);
            if (realFile.exists() && realFile.isDirectory()) {
                File[] subfiles = realFile.listFiles();
                if (subfiles != null) {
                    for (File file : subfiles) {
                        if (file.getName().contains(".pdf")) {
                            long lastModified = file.lastModified();// pdf最后更新时间
                            if (lastModified < timeInMillis) {// 删除当前时间前5个小时的pdf文件
                                file.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("定时任务删除pdf文件异常：{}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
    public void updateTaskEndTime() {// kingbase办结时出现.截转数据时最后一个历史任务时间为null，历程不显示
        Date date = new Date();
        try {
            Y9LoginUserHolder.setTenantId("11111111-1111-1111-1111-111111111113");
            LOGGER.info("***************定时任务updateTaskEndTime:" + date);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
            String year = sdf1.format(date);
            String sql = "SELECT * FROM act_hi_taskinst_" + year + " WHERE END_TIME_ IS NULL";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() > 0) {
                sql = "UPDATE act_hi_taskinst_" + year + " t SET t.END_TIME_ = ( SELECT"
                    + " p.STARTTIME FROM  ff_processtrack AS p WHERE"
                    + " t.PROC_INST_ID_ = p.PROCESSINSTANCEID and p.TASKDEFNAME = '办结')"
                    + " WHERE t.END_TIME_ IS NULL ";
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            LOGGER.error("定时任务updateTaskEndTime异常：{}", e.getMessage());
        }
    }

}
