package net.risesoft.controller.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

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
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequestMapping(value = "/vue/y9form/table", produces = MediaType.APPLICATION_JSON_VALUE)
public class TableRestController {
    private final JdbcTemplate jdbcTemplate4Tenant;

    private final Y9TableService y9TableService;

    public TableRestController(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        Y9TableService y9TableService) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9TableService = y9TableService;
    }

    /**
     * 添加数据库表
     *
     * @param tableName 表名称
     * @param systemName 系统名称
     * @param systemCnName 系统中文名称
     * @return
     */
    @PostMapping(value = "/addDataBaseTable")
    public Y9Result<Object> addDataBaseTable(@RequestParam String tableName, @RequestParam String systemName,
        @RequestParam String systemCnName) {
        return y9TableService.addDataBaseTable(tableName, systemName, systemCnName);
    }

    /**
     * 新生成表，创建数据库表
     *
     * @param tables 表信息
     * @param fields 字段信息
     * @return
     */
    @PostMapping(value = "/buildTable")
    public Y9Result<Object> buildTable(@RequestParam String tables, @RequestParam String fields) {
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        return y9TableService.buildTable(table, listMap);
    }

    /**
     * 数据库是否存在该表
     *
     * @param tableName 表名
     * @return
     */
    @GetMapping(value = "/checkTableExist")
    public Y9Result<String> checkTableExist(@RequestParam String tableName) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        try {
            boolean msg =
                dbMetaDataUtil.checkTableExist(Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource()), tableName);
            return Y9Result.success(msg ? "exist" : "isNotExist", "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取数据库表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取数据库表
     *
     * @param name 表名
     * @return
     */
    @GetMapping(value = "/getAllTables")
    public Y9Result<Map<String, Object>> getAllTables(@RequestParam(required = false) String name) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list = y9TableService.getAllTables(name);
        String tableNames = y9TableService.getAlltableName();
        map.put("rows", list);
        map.put("tableNames", tableNames);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取app分类
     *
     * @return
     */
    @GetMapping(value = "/getAppList")
    public Y9Result<List<Map<String, Object>>> getAppList() {
        List<Map<String, Object>> list = y9TableService.listApps();
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
    @GetMapping(value = "/getTables")
    public Y9Page<Y9Table> getTables(@RequestParam(required = false) String systemName, @RequestParam int page,
        @RequestParam int rows) {
        return y9TableService.pageTables(systemName, page, rows);
    }

    /**
     * 获取新增或修改表数据
     *
     * @param id 表id
     * @return
     */
    @GetMapping(value = "/newOrModifyTable")
    public Y9Result<Map<String, Object>> newOrModifyTable(@RequestParam(required = false) String id) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Map<String, Object> map = new HashMap<>(16);
        try {
            String databaseName =
                dbMetaDataUtil.getDatabaseDialectName(Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource()));
            map.put("databaseName", databaseName);
            if (StringUtils.isNotBlank(id) && !UtilConsts.NULL.equals(id)) {
                Y9Table y9Table = y9TableService.findById(id);
                map.put("y9Table", y9Table);
            }
            String tableNames = y9TableService.getAlltableName();
            map.put("tableNames", tableNames);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取数据库表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 删除业务表
     *
     * @param ids 表ids
     * @return
     */
    @PostMapping(value = "/removeTable")
    public Y9Result<Object> removeTable(@RequestParam String ids) {
        return y9TableService.delete(ids);
    }

    /**
     * 保存业务表
     *
     * @param tables 表信息
     * @param fields 字段信息
     * @return
     */
    @PostMapping(value = "/saveTable")
    public Y9Result<Object> saveTable(@RequestParam(required = false) String tables,
        @RequestParam(required = false) String fields) {
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        return y9TableService.updateTable(table, listMap, "save");
    }

    /**
     * 修改表结构，增加字段
     *
     * @param tables 表信息
     * @param fields 字段信息
     * @return Y9Result<String>
     */
    @PostMapping(value = "/updateTable")
    public Y9Result<Object> updateTable(@RequestParam String tables, @RequestParam String fields) {
        Y9Table table = Y9JsonUtil.readValue(tables, Y9Table.class);
        List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fields, String.class, Object.class);
        return y9TableService.updateTable(table, listMap, "");
    }

}
