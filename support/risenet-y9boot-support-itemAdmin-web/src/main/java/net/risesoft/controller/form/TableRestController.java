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
import net.risesoft.entity.form.Y9Table;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/y9form/table")
public class TableRestController {

    @Autowired
    private Y9TableService y9TableService;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    /**
     * 添加数据库表
     *
     * @param tableName 表名称
     * @param systemName 系统名称
     * @param systemCnName 系统英文名称
     * @return
     */
    @RequestMapping(value = "/addDataBaseTable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> addDataBaseTable(@RequestParam(required = true) String tableName,
        @RequestParam(required = true) String systemName, @RequestParam(required = true) String systemCnName) {
        Map<String, Object> map = y9TableService.addDataBaseTable(tableName, systemName, systemCnName);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 新生成表，创建数据库表
     *
     * @param table 表信息
     * @param fields 字段信息
     * @return
     */
    @RequestMapping(value = "/buildTable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> buildTable(@RequestParam(required = true) String tables,
        @RequestParam(required = true) String fields) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        map = y9TableService.buildTable(table, listMap);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 数据库是否存在该表
     *
     * @param tableName 表名
     * @return
     */
    @RequestMapping(value = "/checkTableExist", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<String> checkTableExist(@RequestParam(required = true) String tableName) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            boolean msg = dbMetaDataUtil.checkTableExist(connection, tableName);
            return Y9Result.success(msg ? "exist" : "isNotExist", "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取数据库表
     *
     * @param name 表名
     * @return
     */
    @RequestMapping(value = "/getAllTables", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<Map<String, Object>> getAllTables(@RequestParam(required = false) String name) {
        Map<String, Object> map = y9TableService.getAllTables(name);
        String tableNames = y9TableService.getAlltableName();
        map.put("tableNames", tableNames);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取app分类
     *
     * @return
     */
    @RequestMapping(value = "/getAppList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getAppList() {
        List<Map<String, Object>> list = null;
        list = y9TableService.getAppList();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取业务表列表
     *
     * @param systemName 应用名称
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getTables", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Y9Table> getTables(@RequestParam(required = false) String systemName,
        @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        Map<String, Object> map = y9TableService.getTables(systemName, page, rows);
        List<Y9Table> list = (List<Y9Table>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
            Integer.parseInt(map.get("total").toString()), list, "获取列表成功");
    }

    /**
     * 获取新增或修改表数据
     *
     * @param id 表id
     * @return
     */
    @RequestMapping(value = "/newOrModifyTable", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModifyTable(@RequestParam(required = false) String id) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            String databaseName = dbMetaDataUtil.getDatabaseDialectName(connection);
            map.put("databaseName", databaseName);
            if (StringUtils.isNotBlank(id) && !UtilConsts.NULL.equals(id)) {
                Y9Table y9Table = y9TableService.findById(id);
                map.put("y9Table", y9Table);
            }
            String tableNames = y9TableService.getAlltableName();
            map.put("tableNames", tableNames);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 删除业务表
     *
     * @param ids 表ids
     * @return
     */
    @RequestMapping(value = "/removeTable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> removeTable(@RequestParam(required = true) String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = y9TableService.delete(ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存业务表
     *
     * @param tables 表信息
     * @param fields 字段信息
     * @return
     */
    @RequestMapping(value = "/saveTable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveTable(@RequestParam(required = false) String tables,
        @RequestParam(required = false) String fields) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        map = y9TableService.updateTable(table, listMap, "save");
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 修改表结构，增加字段
     *
     * @param table 表信息
     * @param fields 字段信息
     * @return
     */
    @RequestMapping(value = "/updateTable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> updateTable(@RequestParam(required = true) String tables,
        @RequestParam(required = true) String fields) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        map = y9TableService.updateTable(table, listMap, "");
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

}
