package net.risesoft.controller;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Result;

/**
 * 人事办件统计
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/leaveCount")
@Slf4j
public class LeaveCountController {

    @Qualifier("y9TenantDataSource")
    private final DataSource y9TenantDS;

    private JdbcTemplate jdbcTemplate4Tenant = null;

    /**
     * 请假统计
     *
     * @param leaveType 请假类型
     * @param userName 人员名称
     * @param deptName 部门名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/countList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> countList(@RequestParam(required = false) String leaveType, @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName, @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime) {
        try {
            List<Map<String, Object>> list;
            String sql = "SELECT USERNAME,DEPTNAME,LEAVETYPE,SUM(leaveDuration) AS leaveDuration,CASE WHEN LEAVETYPE = '事假' OR LEAVETYPE = '病假' OR LEAVETYPE = '哺乳假' OR LEAVETYPE = '调休' OR LEAVETYPE = '公出' THEN '小时' WHEN LEAVETYPE = '转正申请' OR LEAVETYPE = '入职申请'"
                + " OR LEAVETYPE = '离职申请' THEN '位' ELSE '天' END AS danwei FROM y9_form_qingjiabanjian AS Y,ff_process_param AS f WHERE y.guid = f.PROCESSSERIALNUMBER AND f.PROCESSINSTANCEID IS NOT NULL AND f.COMPLETER IS NOT NULL ";

            String whereStr = "";
            if (StringUtils.isNotBlank(deptName)) {
                whereStr += " AND DEPTNAME like '%" + deptName + "%'";
            }
            if (StringUtils.isNotBlank(userName)) {
                whereStr += " AND username like '%" + userName + "%'";
            }
            if (StringUtils.isNotBlank(leaveType)) {
                whereStr += " AND LEAVETYPE = '" + leaveType + "'";
            }
            if (StringUtils.isNotBlank(startTime)) {
                whereStr += " AND leaveStartTime >= '" + startTime + "'";
            }
            if (StringUtils.isNotBlank(endTime)) {
                whereStr += " AND leaveStartTime <= '" + endTime + "-31'";
            }

            String endStr = " GROUP BY LEAVETYPE,USERNAME ORDER BY USERNAME ";
            sql = sql + whereStr + endStr;
            LOGGER.debug("****************************************sql={}", sql);
            list = jdbcTemplate4Tenant.queryForList(sql);
            return Y9Result.success(list, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Result.failure("获取列表失败");
    }

    /**
     * 导出Excel
     *
     * @param leaveType 请假类型
     * @param userName 人员名称
     * @param deptName 部门名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @SuppressWarnings("resource")
    @RequestMapping(value = "/exportExcel", method = RequestMethod.GET, produces = "application/json")
    public void exportExcel(@RequestParam(required = false) String leaveType, @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime,
        HttpServletResponse response) {
        try {
            List<Map<String, Object>> list;
            String sql = "SELECT USERNAME,DEPTNAME,LEAVETYPE,SUM(leaveDuration) AS leaveDuration, CASE WHEN LEAVETYPE = '事假' OR LEAVETYPE = '病假' OR LEAVETYPE = '哺乳假' OR LEAVETYPE = '调休' OR LEAVETYPE = '公出' THEN '小时' WHEN LEAVETYPE = '转正申请' OR LEAVETYPE = '入职申请'"
                + " OR LEAVETYPE = '离职申请' THEN '位' ELSE '天' END AS danwei FROM y9_form_qingjiabanjian AS Y,ff_process_param AS f " + "WHERE y.guid = f.PROCESSSERIALNUMBER AND f.PROCESSINSTANCEID IS NOT NULL AND f.COMPLETER IS NOT NULL ";

            String whereStr = "";
            if (StringUtils.isNotBlank(deptName)) {
                whereStr += " AND DEPTNAME like '%" + deptName + "%'";
            }
            if (StringUtils.isNotBlank(userName)) {
                whereStr += " AND username like '%" + userName + "%'";
            }
            if (StringUtils.isNotBlank(leaveType)) {
                whereStr += " AND LEAVETYPE = '" + leaveType + "'";
            }
            if (StringUtils.isNotBlank(startTime)) {
                whereStr += " AND leaveStartTime >= '" + startTime + "'";
            }
            if (StringUtils.isNotBlank(endTime)) {
                whereStr += " AND leaveStartTime <= '" + endTime + "-31'";
            }

            String endStr = " GROUP BY LEAVETYPE,USERNAME ORDER BY USERNAME ";
            sql = sql + whereStr + endStr;
            LOGGER.debug("****************************************sql={}", sql);
            list = jdbcTemplate4Tenant.queryForList(sql);

            HSSFWorkbook wb = new HSSFWorkbook();
            List<String> headers = new ArrayList<>();
            headers.add("姓名");
            headers.add("部门");
            headers.add("请假类型");
            headers.add("合计");
            headers.add("单位");

            int rowIndex = 0; // 定义行的初始值
            Sheet sheet = wb.createSheet(); // 创建sheet页
            // 设置标题字体样式
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);// 加粗
            font.setFontHeightInPoints((short)14);// 设置字体大小
            cellStyle.setFont(font);
            // 创建标题行
            Row titleRow = sheet.createRow(rowIndex++);
            // 将标题信息填进单元格
            for (int i = 0; i < headers.size(); i++) {
                // 创建标题的单元格
                Cell titleCell = titleRow.createCell(i);
                // 填充标题数值
                titleCell.setCellValue(headers.get(i));
                // 设置样式
                titleCell.setCellStyle(cellStyle);
            }
            for (Map<String, Object> map : list) {
                Row row = sheet.createRow(rowIndex++); // 增加行数
                row.createCell(0).setCellValue(map.get("USERNAME").toString());
                sheet.autoSizeColumn(0);
                row.createCell(1).setCellValue(map.get("DEPTNAME").toString());
                sheet.autoSizeColumn(1);
                row.createCell(2).setCellValue(map.get("LEAVETYPE").toString());
                sheet.autoSizeColumn(2);
                row.createCell(3).setCellValue(map.get("leaveDuration").toString());
                sheet.autoSizeColumn(3);
                row.createCell(4).setCellValue(map.get("danwei").toString());
                sheet.autoSizeColumn(4);
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("请假统计.xlsx").getBytes(StandardCharsets.UTF_8), "iso8859-1"));
            response.setContentType("text/html;charset=utf-8");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("导出Excel失败", e);
        }
    }

    @PostConstruct
    public void init() {
        this.jdbcTemplate4Tenant = new JdbcTemplate(this.y9TenantDS);
    }
}
