package net.risesoft.controller.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.util.form.DbMetaDataUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/y9form/tableField")
public class TableFieldRestController {

    @Autowired
    private Y9TableFieldService y9TableFieldService;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant") private JdbcTemplate jdbcTemplate4Tenant;

    /**
     * 删除表字段定义
     *
     * @param id 字段id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delete(@RequestParam(required = true) String id) {
        Map<String, Object> map = y9TableFieldService.delete(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取表字段定义列表
     *
     * @param tableId 表id
     * @return
     */
    @RequestMapping(value = "/getTableFieldList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<Map<String, Object>> getTableFieldList(@RequestParam(required = true) String tableId) {
        Map<String, Object> map = y9TableFieldService.getFieldList(tableId);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取新增或修改表字段信息
     *
     * @param id 字段id
     * @param tableId 表id
     * @return
     */
    @RequestMapping(value = "/newOrModifyField", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModifyField(@RequestParam(required = false) String id,
        @RequestParam(required = true) String tableId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("tableId", tableId);
        Y9TableField field = new Y9TableField();
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
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            List<Map<String, Object>> list = dbMetaDataUtil.listTypes(connection);
            map.put("typeList", list);
            String databaseName = dbMetaDataUtil.getDatabaseDialectName(connection);
            map.put("databaseName", databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("获取失败");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存或更新表字段定义
     *
     * @param field 字段信息
     * @return
     */
    @RequestMapping(value = "/saveField", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveField(Y9TableField field) {
        Map<String, Object> map = y9TableFieldService.saveOrUpdate(field);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

}
