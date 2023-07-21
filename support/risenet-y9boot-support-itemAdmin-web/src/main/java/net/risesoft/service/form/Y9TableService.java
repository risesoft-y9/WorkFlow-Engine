package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9Table;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
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
    public Map<String, Object> addDataBaseTable(String tableName, String systemName, String systemCnName);

    /**
     * 根据表PK创建相应的表
     * 
     * @param tableId
     * @param listMap
     * @return
     */
    Map<String, Object> buildTable(Y9Table table, List<Map<String, Object>> listMap);

    /**
     * 根据id删除业务表
     * 
     * @param id
     * @return
     */
    public Map<String, Object> delete(String id);

    /**
     * 根据id获取业务表信息
     * 
     * @param id
     * @return
     */
    public Y9Table findById(String id);

    /**
     * Description:
     * 
     * @param tableName
     * @return
     */
    public Y9Table findByTableName(String tableName);

    /**
     * 根据表类型查找所有表
     * 
     * @param tableType
     * @return
     */
    List<Y9Table> findByTableType(Integer tableType);

    /**
     * 获取所有表
     * 
     * @return
     */
    public List<Y9Table> getAllTable();

    /**
     * 获取所有表名称
     * 
     * @return
     */
    public String getAlltableName();

    /**
     * Description: 获取数据库表
     * 
     * @param name
     * @return
     */
    public Map<String, Object> getAllTables(String name);

    /**
     * 获取应用列表
     * 
     * @return
     */
    public List<Map<String, Object>> getAppList();

    /**
     * Description: 根据应用id分页获取业务表列表
     * 
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> getTables(String systemName, int page, int rows);

    /**
     * Description: 保存业务表信息
     * 
     * @param table
     * @return
     * @throws Exception
     */
    public Y9Table saveOrUpdate(Y9Table table) throws Exception;

    /**
     * Description: 修改表结构，增加字段
     * 
     * @param table
     * @param listMap
     * @param type
     * @return
     */
    public Map<String, Object> updateTable(Y9Table table, List<Map<String, Object>> listMap, String type);

}
