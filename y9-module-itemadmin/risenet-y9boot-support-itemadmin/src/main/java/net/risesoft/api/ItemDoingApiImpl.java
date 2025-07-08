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

import net.risesoft.api.itemadmin.worklist.ItemDoingApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 在办接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequestMapping(value = "/services/rest/itemDoing", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemDoingApiImpl implements ItemDoingApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final JdbcTemplate jdbcTemplate;

    public ItemDoingApiImpl(ItemPageService itemPageService, ActRuDetailService actRuDetailService,
        Y9TableService y9TableService, @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate) {
        this.itemPageService = itemPageService;
        this.actRuDetailService = actRuDetailService;
        this.y9TableService = y9TableService;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据系统名称查询当前人的在办数量
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
        int count = this.actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 1);
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称查询在办列表
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
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Y9LoginUserHolder.setTenantId(tenantId);
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndEnded(systemName, false, page, rows, sort);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ardPage.getContent().forEach(ard -> {
            ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(ard, actRuDetailModel);
            modelList.add(actRuDetailModel);
        });
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据用户id和系统名称查询当前人的在办列表
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
            this.actRuDetailService.pageBySystemNameAndAssigneeAndStatus(systemName, userId, 1, rows, page, sort);
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
        @RequestParam String deptId, @RequestParam boolean isBureau, @RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage = this.actRuDetailService.pageBySystemNameAndDeptIdAndEnded(systemName, deptId,
            isBureau, false, rows, page, sort);
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
     * 根据系统名称、表名称、搜索内容查询在办列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ? "
                + whereSql + assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        String countSql =
            "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ?"
                + whereSql + assigneeNameWhereSql + " ) A WHERE A.RS_NUM = 1) ALIAS";
        Object[] args = {systemName};
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @Override
    public Y9Result<List<ActRuDetailModel>> searchListBySystemName(@RequestParam String tenantId,
        @RequestParam String systemName, @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
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
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ? "
                + whereSql + assigneeNameWhereSql + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        Object[] args = {systemName};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class));
        return Y9Result.success(content);
    }

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询当前人的在办列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam String tableName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql0 = "LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
        StringBuilder sql1 = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        for (String columnName : searchMap.keySet()) {
            sql1.append("AND INSTR(F.").append(columnName.toUpperCase()).append(",'")
                .append(searchMap.get(columnName).toString()).append("') > 0 ");
        }
        String orderBy = "T.LASTTIME DESC";
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.STATUS = 1 AND T.ENDED = FALSE AND T.DELETED = FALSE " + sql1
            + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? ORDER BY " + orderBy;
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.STATUS=1 AND T.ENDED = FALSE AND T.DELETED = FALSE "
            + sql1;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询当前人的在办列表
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByDeptIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String deptId, @RequestParam boolean isBureau, @RequestParam String systemName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ? AND T."
                + (isBureau ? "BUREAUID" : "DEPTID") + " = ? " + whereSql + assigneeNameWhereSql
                + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        String countSql =
            "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ? AND T."
                + (isBureau ? "BUREAUID" : "DEPTID") + " = ? " + whereSql + assigneeNameWhereSql
                + ") A WHERE A.RS_NUM = 1) ALIAS";
        Object[] args = {systemName, deptId};
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByDeptIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String deptId, @RequestParam boolean isBureau, @RequestParam String systemName,
        @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
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
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql
                + " WHERE T.DELETED = FALSE AND T.ENDED = FALSE AND T.SYSTEMNAME = ? AND T."
                + (isBureau ? "BUREAUID" : "DEPTID") + " = ? " + whereSql + assigneeNameWhereSql
                + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        Object[] args = {systemName, deptId};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class));
        return Y9Result.success(content);
    }
}
