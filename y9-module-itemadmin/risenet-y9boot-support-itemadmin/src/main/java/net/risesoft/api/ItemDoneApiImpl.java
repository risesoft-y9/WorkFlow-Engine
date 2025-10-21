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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.worklist.ItemDoneApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.ActRuDetail;
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
 * 办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/itemDone", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemDoneApiImpl implements ItemDoneApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final JdbcTemplate jdbcTemplate;

    public ItemDoneApiImpl(
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
     * 根据用户id和系统名称查询办结数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是办结任务数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = this.actRuDetailService.countBySystemNameAndAssignee(systemName, userId);
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称查询办结列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是监控办结列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.LASTTIME_KEY);
        Y9LoginUserHolder.setTenantId(tenantId);
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndEnded(systemName, true, page, rows, sort);
        List<ActRuDetailModel> modelList = convertActRuDetailsToModels(ardPage.getContent());
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据用户id和系统名称查询当前人办结列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.LASTTIME_KEY);
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndAssigneeAndEnded(systemName, userId, true, rows, page, sort);
        List<ActRuDetailModel> modelList = convertActRuDetailsToModels(ardPage.getContent());
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 将 ActRuDetail 列表转换为 ActRuDetailModel 列表
     */
    private List<ActRuDetailModel> convertActRuDetailsToModels(List<ActRuDetail> actRuDetails) {
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : actRuDetails) {
            ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        return modelList;
    }

    /**
     * 根据科室id和系统名称查询当前人的在办列表
     *
     * @param tenantId 租户id
     * @param deptId 科室id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findByDeptIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String deptId, @RequestParam("isBureau") boolean isBureau, @RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.LASTTIME_KEY);
        Page<ActRuDetail> ardPage = this.actRuDetailService.pageBySystemNameAndDeptIdAndEnded(systemName, deptId,
            isBureau, true, rows, page, sort);
        List<ActRuDetailModel> modelList = convertActRuDetailsToModels(ardPage.getContent());
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据系统名称、表名称、搜索内容查询办结列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是监控办结列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql =
            "SELECT A.* FROM ( SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME = ? " + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        String countSql =
            "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME = ?" + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + " ) A WHERE A.RS_NUM = 1) ALIAS";

        Object[] args = {systemName};
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListBySystemName(@RequestParam String tenantId,
        @RequestParam String systemName, @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC ) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME = ? " + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";

        Object[] args = {systemName};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class), args);
        return Y9Result.success(content);
    }

    /**
     * 构建搜索SQL组件（可复用的通用方法）
     */
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
     * 根据用户id、系统名称、表名称、搜索内容查询当前人办结列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam String tableName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql0 = "LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
        StringBuilder whereSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        params.add(systemName);
        params.add(userId);
        if (StringUtils.isNotBlank(searchMapStr)) {
            Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
            assert searchMap != null;
            for (String columnName : searchMap.keySet()) {
                whereSql.append("AND INSTR(F.").append(columnName.toUpperCase()).append(",?) > 0 ");
                params.add(searchMap.get(columnName).toString());
            }
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.ENDED = TRUE AND T.DELETED = FALSE AND T.PLACEONFILE = FALSE " + whereSql
            + " AND T.SYSTEMNAME = ?" + " AND T.ASSIGNEE = ? ORDER BY T.LASTTIME DESC";
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.ENDED = TRUE AND T.DELETED = FALSE AND T.PLACEONFILE = FALSE "
            + whereSql;
        Object[] args = params.toArray();
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询当前人办结列表
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByDeptIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String deptId, @RequestParam boolean isBureau, @RequestParam String systemName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        // 复用统一的SQL构建逻辑，避免代码重复
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER ( PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC ) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME= ? AND T."
                + (isBureau ? ItemConsts.BUREAUID_KEY : ItemConsts.DEPTID_KEY) + "= ? " + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM =1";
        String countSql =
            "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME =? AND T."
                + (isBureau ? ItemConsts.BUREAUID_KEY : ItemConsts.DEPTID_KEY) + " = ? " + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + ") A WHERE A.RS_NUM = 1) ALIAS";
        Object[] args = {systemName, deptId};
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByDeptIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String deptId, @RequestParam boolean isBureau, @RequestParam String systemName,
        @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SqlComponents sqlComponents = buildSearchSqlComponents(searchMapStr);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sqlComponents.innerSql + sqlComponents.assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = TRUE AND T.SYSTEMNAME=? AND T."
                + (isBureau ? ItemConsts.BUREAUID_KEY : ItemConsts.DEPTID_KEY) + " =? " + sqlComponents.whereSql
                + sqlComponents.assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM= 1";
        Object[] args = {systemName, deptId};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class), args);
        return Y9Result.success(content);
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