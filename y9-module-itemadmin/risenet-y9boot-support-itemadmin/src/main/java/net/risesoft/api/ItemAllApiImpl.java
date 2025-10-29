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

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.worklist.ItemAllApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.service.util.ItemPageService;
import net.risesoft.util.CommonUtils;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 所有本人经手的件接口
 *
 * @author qinman
 * @date 2024/12/19
 */
@Slf4j
@RestController
@RequestMapping(value = "/services/rest/itemAll", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemAllApiImpl implements ItemAllApi {

    private static final String COMMON_SQL = "SELECT T.* FROM FF_ACT_RU_DETAIL T ";
    private static final String WHERE_DELETED_SYSTEMNAME_SQL = " WHERE T.DELETED = FALSE AND T.SYSTEMNAME = ? ";
    private final ItemPageService itemPageService;
    private final ActRuDetailService actRuDetailService;
    private final Y9TableService y9TableService;
    private final JdbcTemplate jdbcTemplate;

    public ItemAllApiImpl(
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
     * 根据用户id和系统名称查询待办数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data 是待办任务数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(
            actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, ActRuDetailStatusEnum.TODO));
    }

    /**
     * 根据用户id和系统名称查询(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        Page<ActRuDetail> ardPage =
            actRuDetailService.pageBySystemNameAndAssignee(systemName, userId, rows, page, sort);
        return convertToY9Page(ardPage, page);
    }

    @Override
    public Y9Page<ActRuDetailModel> findBySystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        Page<ActRuDetail> ardPage = actRuDetailService.pageBySystemName(systemName, rows, page, sort);
        return convertToY9Page(ardPage, page);
    }

    /**
     * 将 Page<ActRuDetail> 转换为 Y9Page<ActRuDetailModel>
     */
    private Y9Page<ActRuDetailModel> convertToY9Page(Page<ActRuDetail> ardPage, int page) {
        List<ActRuDetail> ardList = ardPage.getContent();
        List<ActRuDetailModel> modelList = ItemAdminModelConvertUtil.convertActRuDetailsToModels(ardList);
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据用户id查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param queryParamModel 查询参数
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.8
     */
    @Override
    public Y9Page<ActRuDetailModel> findByUserId(@RequestParam String tenantId, @RequestParam String userId,
        QueryParamModel queryParamModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        int page = queryParamModel.getPage(), rows = queryParamModel.getRows();
        Page<ActRuDetail> ardPage;
        boolean isEmpty = CommonUtils.checkObjAllFieldsIsNull(queryParamModel);
        if (isEmpty) {
            ardPage = actRuDetailService.pageByAssigneeAndStatus(userId, ActRuDetailStatusEnum.TODO, rows, page, sort);
        } else {
            String processParamSql = "LEFT JOIN FF_PROCESS_PARAM F ON T.PROCESSSERIALNUMBER = F.PROCESSSERIALNUMBER ";
            StringBuilder whereSql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            params.add(userId);
            CommonUtils.buildQueryConditions(queryParamModel, whereSql, params);
            String sql = COMMON_SQL + processParamSql + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + whereSql
                + " AND T.ASSIGNEE = ? ORDER BY T.CREATETIME DESC";
            String countSql = "SELECT COUNT(T.ID) FROM FF_ACT_RU_DETAIL T " + processParamSql
                + " WHERE T.ASSIGNEE= ? AND T.STATUS=0 AND T.DELETED = FALSE " + whereSql;
            Object[] args = params.toArray();
            ItemPage<ActRuDetailModel> ardModelPage = itemPageService.page(sql, args,
                new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
            return Y9Page.success(page, ardModelPage.getTotalpages(), ardModelPage.getTotal(), ardModelPage.getRows());
        }
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
     * 根据用户id和系统名称、表名称、搜索集合查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestBody String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql = COMMON_SQL + innerSql + assigneeNameInnerSql
            + " WHERE T.DELETED = FALSE AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? " + whereSql + assigneeNameWhereSql
            + " ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql + assigneeNameInnerSql
            + " WHERE T.DELETED = FALSE AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? " + whereSql + assigneeNameWhereSql;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> ardPage = itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }

    @Override
    public Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName, @RequestBody String searchMapStr, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + WHERE_DELETED_SYSTEMNAME_SQL + whereSql + assigneeNameWhereSql
                + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        String countSql =
            "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER () OVER ( PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC ) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + WHERE_DELETED_SYSTEMNAME_SQL + whereSql + assigneeNameWhereSql
                + " ) A WHERE A.RS_NUM = 1) ALIAS";
        Object[] args = {systemName};
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListBySystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestBody String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM   FROM FF_ACT_RU_DETAIL T "
                + innerSql + assigneeNameInnerSql + WHERE_DELETED_SYSTEMNAME_SQL + whereSql + assigneeNameWhereSql
                + " ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        Object[] args = {systemName};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class), args);
        return Y9Result.success(content);
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody(required = false) String searchMapStr) {
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
        String sql = COMMON_SQL + innerSql + assigneeNameInnerSql
            + " WHERE T.DELETED = FALSE AND T.ASSIGNEE = ? AND T.SYSTEMNAME = ?  " + whereSql + assigneeNameWhereSql
            + " ORDER BY T.CREATETIME DESC";
        Object[] args = {userId, systemName};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class), args);
        return Y9Result.success(content);
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchByProcessSerialNumbers(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String[] processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (processSerialNumbers == null || processSerialNumbers.length == 0) {
            return Y9Result.success(new ArrayList<>());
        }
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < processSerialNumbers.length; i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }
        String sql =
            "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.PROCESSSERIALNUMBER IN ("
                + placeholders + ") ORDER BY T.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        List<ActRuDetailModel> content = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ActRuDetailModel.class),
            (Object[])processSerialNumbers);
        return Y9Result.success(content);
    }
}
