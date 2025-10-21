package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.worklist.ItemHaveDoneApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.service.util.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 已办（包含在办和办结）接口
 *
 * @author qinman
 * @date 2024/12/18
 */
@Validated
@RestController
@RequestMapping(value = "/services/rest/itemHaveDone", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemHaveDoneApiImpl implements ItemHaveDoneApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final JdbcTemplate jdbcTemplate;

    public ItemHaveDoneApiImpl(
        ItemPageService itemPageService,
        ActRuDetailService actRuDetailService,
        Y9TableService y9TableService,
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate) {
        this.itemPageService = itemPageService;
        this.actRuDetailService = actRuDetailService;
        this.y9TableService = y9TableService;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据系统名称查询当前人的已办（包含在办和办结）数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是在办任务数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count =
            actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, ActRuDetailStatusEnum.DOING);
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称查询已办（包含在办和办结）列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql =
            "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.STATUS = 0 AND T.DELETED = FALSE AND T.SYSTEMNAME = ? ORDER BY T.LASTTIME DESC) A WHERE A.RS_NUM=1";
        String countSql =
            "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE ";
        Object[] args = {systemName};
        ItemPage<ActRuDetailModel> itemPage = itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    /**
     * 根据用户id和系统名称查询当前人的已办（包含在办和办结）列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage =
            actRuDetailService.pageBySystemNameAndAssigneeAndStatusEquals1(systemName, userId, rows, page, sort);
        List<ActRuDetail> ardList = ardPage.getContent();
        ActRuDetailModel actRuDetailModel;
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : ardList) {
            actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据系统名称、表名称、搜索内容查询已办（包含在办和办结）列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestParam String tableName, @RequestBody String searchMapStr, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String safeTableName = validateAndSanitizeTableName(tableName);
        String sql0 = "LEFT JOIN " + safeTableName + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
        StringBuilder whereSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        if (StringUtils.isNotBlank(searchMapStr)) {
            Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
            assert searchMap != null;
            for (String columnName : searchMap.keySet()) {
                whereSql.append("AND INSTR(F.").append(columnName.toUpperCase()).append(",?) > 0 ");
                params.add(searchMap.get(columnName).toString());
            }
        }
        String orderBy = "T.LASTTIME DESC";
        String sql =
            "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sql0 + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + whereSql + " AND T.SYSTEMNAME = ?  ORDER BY "
                + orderBy + ") A WHERE A.RS_NUM=1";
        String countSql = "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE " + whereSql;
        Object[] args = params.toArray();
        ItemPage<ActRuDetailModel> itemPage = itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    /**
     * 验证并清理表名，防止SQL注入
     */
    private String validateAndSanitizeTableName(String tableName) {
        // 只允许字母、数字和下划线
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tableName.toUpperCase();
    }

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询当前人的已办（包含在办和办结）列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @SuppressWarnings("java:S2077")
    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestBody String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 1 AND T.SYSTEMNAME =? AND T.ASSIGNEE = ? "
            + sqlComponents.whereSql + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC";
        String countSql =
            "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.STATUS =1 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? "
                + sqlComponents.whereSql + sqlComponents.assigneeNameWhereSql;
        Object[] args = {systemName, userId};
        ItemPage<ActRuDetailModel> itemPage = itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
            + " WHERE T.DELETED =FALSE AND T.STATUS = 1 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? "
            + sqlComponents.whereSql + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC";
        Object[] args = {systemName, userId};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class), args);
        return Y9Result.success(content);
    }

    private SqlComponents buildSearchSqlComponents(String searchMapStr) {
        String innerSql = "", whereSql = "", assigneeNameInnerSql = "", assigneeNameWhereSql = "";
        if (StringUtils.isNotBlank(searchMapStr)) {
            Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
            assert searchMap != null;
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            assigneeNameInnerSql = sqlList.get(2);
            assigneeNameWhereSql = sqlList.get(3);
        }
        return new SqlComponents(innerSql, whereSql, assigneeNameInnerSql, assigneeNameWhereSql);
    }

    /**
     * SQL组件数据类
     */
    private static class SqlComponents {
        final String innerSql;
        final String whereSql;
        final String assigneeNameInnerSql;
        final String assigneeNameWhereSql;

        SqlComponents(String innerSql, String whereSql, String assigneeNameInnerSql, String assigneeNameWhereSql) {
            this.innerSql = innerSql;
            this.whereSql = whereSql;
            this.assigneeNameInnerSql = assigneeNameInnerSql;
            this.assigneeNameWhereSql = assigneeNameWhereSql;
        }
    }
}
