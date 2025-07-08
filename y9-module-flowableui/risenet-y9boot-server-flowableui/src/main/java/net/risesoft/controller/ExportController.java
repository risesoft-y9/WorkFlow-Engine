package net.risesoft.controller;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.view.ItemViewConfApi;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 导出列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/export", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ExportController {

    private final ItemViewConfApi itemViewConfApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    /**
     * 导出数据至Excel文件
     *
     * @param itemId 事项id
     * @param fileName 文件名
     */
    @SuppressWarnings("resource")
    @GetMapping(value = "/exportExcel")
    public void exportExcel(@RequestParam String itemId, @RequestParam String fileName,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName,
        @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime,
        HttpServletResponse response) {
        try {
            List<ItemViewConfModel> itemViewConfList =
                itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, "export").getData();
            String tableName = itemViewConfList.get(0).getTableName();
            String sql = "SELECT Y.* FROM " + tableName + " AS Y,ff_process_param AS f "
                + "WHERE y.guid = f.PROCESSSERIALNUMBER AND f.PROCESSINSTANCEID IS NOT NULL";

            // 条件查询????
            String whereStr = "";
            if (StringUtils.isNotBlank(deptName)) {
                whereStr += " AND DEPTNAME like '%" + deptName + "%'";
            }
            if (StringUtils.isNotBlank(userName)) {
                whereStr += " AND username like '%" + userName + "%'";
            }
            if (StringUtils.isNotBlank(startTime)) {
                whereStr += " AND leaveStartTime >= '" + startTime + "'";
            }
            if (StringUtils.isNotBlank(endTime)) {
                whereStr += " AND leaveStartTime <= '" + endTime + "-31'";
            }
            String endStr = " GROUP BY LEAVETYPE,USERNAME ORDER BY USERNAME ";

            LOGGER.debug("****************************************sql={}", sql);
            List<Map<String, Object>> list = jdbcTemplate4Tenant.queryForList(sql);

            HSSFWorkbook wb = new HSSFWorkbook();
            List<Map<String, Object>> headers = new ArrayList<>();
            for (ItemViewConfModel itemViewConfModel : itemViewConfList) {
                Map<String, Object> header = new HashMap<>();
                header.put("disPlayName", itemViewConfModel.getDisPlayName());
                header.put("columnName", itemViewConfModel.getColumnName());
                headers.add(header);
            }

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
                titleCell.setCellValue(headers.get(i).get("disPlayName").toString());
                // 设置样式
                titleCell.setCellStyle(cellStyle);
            }
            for (Map<String, Object> map : list) {
                Row row = sheet.createRow(rowIndex++); // 增加行数
                for (int i = 0; i < headers.size(); i++) {
                    String columnName = headers.get(i).get("columnName").toString();
                    row.createCell(i).setCellValue(map.get(columnName).toString());
                    sheet.autoSizeColumn(i);
                }
            }
            response.setHeader("Content-Disposition", "attachment;filename="
                + new String((fileName + ".xlsx").getBytes(StandardCharsets.UTF_8), "iso8859-1"));
            response.setContentType("text/html;charset=utf-8");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("导出Excel失败", e);
        }
    }

    /**
     * 获取列表
     *
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getList")
    public Y9Result<Map<String, Object>> getList(@RequestParam String itemId,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName,
        @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        try {
            List<ItemViewConfModel> itemViewConfList =
                itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, "export").getData();
            String tableName = itemViewConfList.get(0).getTableName();
            String sql = "SELECT Y.* FROM " + tableName + " AS Y,ff_process_param AS f "
                + "WHERE y.guid = f.PROCESSSERIALNUMBER AND f.PROCESSINSTANCEID IS NOT NULL";

            // 条件查询????

            List<Map<String, Object>> list = jdbcTemplate4Tenant.queryForList(sql);
            Map<String, Object> map = new HashMap<>();
            map.put("list", list);
            map.put("itemViewConfList", itemViewConfList);
            return Y9Result.success(map, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Result.failure("获取列表失败");
    }
}
