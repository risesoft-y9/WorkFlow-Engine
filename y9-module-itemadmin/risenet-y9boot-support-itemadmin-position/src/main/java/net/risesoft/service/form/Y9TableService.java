package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9Table;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9TableService {

    /**
     * Description: 添加数据库表
     *
     * @param tableName
     * @param systemName
     * @param systemCnName
     * @return
     */
    Y9Result<Object> addDataBaseTable(String tableName, String systemName, String systemCnName);

    /**
     * 根据表PK创建相应的表
     *
     * @param table
     * @param listMap
     * @return
     */
    Y9Result<Object> buildTable(Y9Table table, List<Map<String, Object>> listMap);

    /**
     * 根据id删除业务表
     *
     * @param id
     * @return
     */
    Y9Result<Object> delete(String id);

    /**
     * 根据id获取业务表信息
     *
     * @param id
     * @return
     */
    Y9Table findById(String id);

    /**
     * Description:
     *
     * @param tableName
     * @return
     */
    Y9Table findByTableName(String tableName);

    /**
     * 获取所有表名称
     *
     * @return
     */
    String getAlltableName();

    /**
     * Description: 获取数据库表
     *
     * @param name
     * @return
     */
    Map<String, Object> getAllTables(String name);

    /**
     * 获取所有表
     *
     * @return
     */
    List<Y9Table> listAllTable();

    /**
     * 获取应用列表
     *
     * @return
     */
    List<Map<String, Object>> listApps();

    /**
     * 根据表类型查找所有表
     *
     * @param tableType
     * @return
     */
    List<Y9Table> listByTableType(Integer tableType);

    /**
     * Description: 根据应用id分页获取业务表列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Y9Table> pageTables(String systemName, int page, int rows);

    /**
     * Description:
     *
     * @param table
     * @return
     * @throws Exception
     */
    Y9Table saveOrUpdate(Y9Table table) throws Exception;

    /**
     * Description: 修改表结构，增加字段
     *
     * @param table
     * @param listMap
     * @param type
     * @return
     */
    Y9Result<Object> updateTable(Y9Table table, List<Map<String, Object>> listMap, String type);

}
