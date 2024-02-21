package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import net.risesoft.model.itemadmin.ItemPage;

@Component
public class ItemPageService {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public int count(String countSql, Map<String, Object> map) {
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        int totalSize = jdbc.queryForObject(countSql, map, Integer.class);
        return totalSize;
    }

    @SuppressWarnings("deprecation")
    public int count(String countSql, Object[] countArgs) {
        int totalSize = jdbcTemplate.queryForObject(countSql, countArgs, Integer.class);
        return totalSize;
    }

    public <T> List<T> list(String sql, Map<String, Object> sqlMap, RowMapper<T> rowMapper) {
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<T> content = jdbc.query(sql, sqlMap, rowMapper);
        return content;
    }

    @SuppressWarnings("deprecation")
    public <T> List<T> list(String sql, Object[] queryArgs, RowMapper<T> rowMapper) {
        List<T> content = jdbcTemplate.query(sql, queryArgs, rowMapper);
        return content;
    }

    public <T> ItemPage<T> page(String sql, Map<String, Object> sqlMap, RowMapper<T> rowMapper, String countSql,
        Map<String, Object> countSqlMap, int currPage, int size) {
        if (currPage <= 0) {
            currPage = 1;
        }
        if (size <= 0) {
            size = 15;
        }
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        int totalSize = jdbc.queryForObject(countSql, countSqlMap, Integer.class);
        if (totalSize == 0) {
            return ItemPage.<T>builder().rows(new ArrayList<>()).currpage(0).size(0).totalpages(0).total(0).build();
        }
        int totalPage = totalSize % size == 0 ? totalSize / size : totalSize / size + 1;
        int offset = (currPage - 1) * size;
        int limit = size;
        sql = sql + " limit " + limit + " offset " + offset;
        List<T> content = jdbc.query(sql, sqlMap, rowMapper);
        return ItemPage.<T>builder().rows(content).total(totalSize).totalpages(totalPage).currpage(currPage).size(size)
            .build();
    }

    /**
     * 返回分页对象
     * 
     * @param sql 查询sql
     * @param queryArgs 查询参数
     * @param rowMapper entity mapper
     * @param countSql 总量sql
     * @param countArgs 总量参数
     * @param currPage 当前页
     * @param size 每页大小
     * @param <T> entity
     * @return 分页对象
     */
    @SuppressWarnings("deprecation")
    public <T> ItemPage<T> page(String sql, Object[] queryArgs, RowMapper<T> rowMapper, String countSql,
        Object[] countArgs, int currPage, int size) {
        if (currPage <= 0) {
            currPage = 1;
        }
        if (size <= 0) {
            size = 15;
        }
        int totalSize = jdbcTemplate.queryForObject(countSql, countArgs, Integer.class);
        if (totalSize == 0) {
            return ItemPage.<T>builder().rows(new ArrayList<>()).currpage(0).size(0).totalpages(0).total(0).build();
        }
        int totalPage = totalSize % size == 0 ? totalSize / size : totalSize / size + 1;
        int offset = (currPage - 1) * size;
        int limit = size;
        sql = sql + " limit " + limit + " offset " + offset;
        List<T> content = jdbcTemplate.query(sql, queryArgs, rowMapper);
        return ItemPage.<T>builder().rows(content).total(totalSize).totalpages(totalPage).currpage(currPage).size(size)
            .build();
    }
}
