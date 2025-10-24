package net.risesoft.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

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

    /**
     * 定时任务，定时删除打印转换生成的pdf文件
     */
    @Scheduled(cron = "0 0 */5 * * ?") // 每5个小时执行一次
    public void deletePdf() {
        try {
            LOGGER.info("******************定时任务删除pdf文件开始：{}******************",
                Y9DateTimeUtils.formatCurrentDateTime());
            long cutoffTime = calculateCutoffTime();
            String pdfPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "formToPDF";
            LOGGER.info("********************pdfPath：{}********************", pdfPath);
            File pdfDirectory = new File(pdfPath);
            if (pdfDirectory.exists() && pdfDirectory.isDirectory()) {
                deleteExpiredPdfFiles(pdfDirectory, cutoffTime);
            }
        } catch (Exception e) {
            LOGGER.error("定时任务删除pdf文件异常：{}", e.getMessage(), e);
        }
    }

    /**
     * 计算文件过期时间（当前时间减去5小时）
     */
    private long calculateCutoffTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -5);
        return calendar.getTimeInMillis();
    }

    /**
     * 删除过期的PDF文件
     */
    private void deleteExpiredPdfFiles(File directory, long cutoffTime) {
        File[] pdfFiles = directory.listFiles((dir, name) -> name.endsWith(".pdf"));
        if (pdfFiles != null) {
            int deletedCount = 0;
            for (File file : pdfFiles) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    } else {
                        LOGGER.warn("Failed to delete pdf file: {}", file.getAbsolutePath());
                    }
                }
            }
            LOGGER.info("定时任务删除pdf文件结束，共删除 {} 个过期文件", deletedCount);
        }
    }

    @Scheduled(cron = "0 0/3 * * * ?") // 每5分钟执行一次
    public void updateTaskEndTime() {// kingbase办结时出现.截转数据时最后一个历史任务时间为null，历程不显示
        Date date = new Date();
        Y9LoginUserHolder.setTenantId("11111111-1111-1111-1111-111111111113");
        LOGGER.info("***************定时任务updateTaskEndTime:{}", date);
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String year0 = String.valueOf((Integer.parseInt(year) - 1));// 同步去年，避免跨年办理问题
        this.updateTaskEndTime(year);
        this.updateTaskEndTime(year0);
    }

    @SuppressWarnings("java:S2077") // 表名来源于内部白名单，processInstanceId使用参数化查询，无SQL注入风险
    public void updateTaskEndTime(String year) {
        try {
            LOGGER.info("***************定时任务year:{}", year);
            String sql = "SELECT * FROM act_hi_taskinst_" + year + " WHERE END_TIME_ IS NULL";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (!list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    try {
                        String processInstanceId = map.get("PROC_INST_ID_").toString();
                        String id = map.get("ID_").toString();
                        String sql0 =
                            "select STARTTIME as startTime from ff_processtrack where PROCESSINSTANCEID = ? and TASKDEFNAME like '%办结%' order by STARTTIME desc";
                        List<Map<String, Object>> list0 = jdbcTemplate.queryForList(sql0, processInstanceId);
                        if (!list0.isEmpty()) {
                            String startTime = list0.get(0).get("startTime").toString();
                            String sql1 =
                                "update act_hi_taskinst_" + year + " set END_TIME_ = '" + startTime + "' where ID_ = ?";
                            jdbcTemplate.update(sql1, id);
                        }
                    } catch (Exception e) {
                        LOGGER.error("定时任务updateTaskEndTime异常1：{}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("***********定时任务updateTaskEndTime异常");
        }
    }
}