package net.risesoft.controller.form;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.util.form.DbMetaDataUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequestMapping(value = "/vue/y9form/tableField")
public class TableFieldRestController {

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final Y9TableFieldService y9TableFieldService;

    public TableFieldRestController(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant, Y9TableFieldService y9TableFieldService) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9TableFieldService = y9TableFieldService;
    }

    /**
     * 删除表字段定义
     *
     * @param id 字段id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam String id) {
        Map<String, Object> map = y9TableFieldService.delete(id);
        if ((boolean) map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String) map.get("msg"));
        }
        return Y9Result.failure((String) map.get("msg"));
    }

    /**
     * 获取表字段定义列表
     *
     * @param tableId 表id
     * @return Y9Result<Map<String, Object>>
     */
    @RequestMapping(value = "/getTableFieldList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTableFieldList(@RequestParam String tableId) {
        Map<String, Object> map = y9TableFieldService.getFieldList(tableId);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取新增或修改表字段信息
     *
     * @param id      字段id
     * @param tableId 表id
     * @return Y9Result<Map<String, Object>>
     */
    @RequestMapping(value = "/newOrModifyField", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModifyField(@RequestParam(required = false) String id,
                                                          @RequestParam String tableId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableId", tableId);
        Y9TableField field;
        if (StringUtils.isNotBlank(id) && !UtilConsts.NULL.equals(id)) {
            field = y9TableFieldService.findById(id);
            if (field != null) {
                String type = field.getFieldType().substring(0, field.getFieldType().indexOf("("));
                field.setFieldType(type);
            }
            map.put("id", id);
            map.put("field", field);
        }
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        try {
            connection = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource()).getConnection();
            List<Map<String, Object>> list = dbMetaDataUtil.listTypes(connection);
            map.put("typeList", list);
            String databaseName = dbMetaDataUtil.getDatabaseDialectName(connection);
            map.put("databaseName", databaseName);
        } catch (Exception e) {
            LOGGER.error("获取数据库类型失败", e);
            return Y9Result.failure("获取失败");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.error("关闭数据库连接失败", e);
            }
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存或更新表字段定义
     *
     * @param field 字段信息
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveField", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveField(Y9TableField field) {
        Map<String, Object> map = y9TableFieldService.saveOrUpdate(field);
        if ((boolean) map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String) map.get("msg"));
        }
        return Y9Result.failure((String) map.get("msg"));
    }

}
