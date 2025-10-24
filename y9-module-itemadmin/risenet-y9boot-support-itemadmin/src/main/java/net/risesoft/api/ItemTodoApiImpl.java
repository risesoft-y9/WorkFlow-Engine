package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
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

import net.risesoft.api.itemadmin.worklist.ItemTodoApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
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

/**
 * 待办接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@Slf4j
@RequestMapping(value = "/services/rest/itemTodo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemTodoApiImpl implements ItemTodoApi {

    private static final String COMMON_SQL = "SELECT T.* FROM FF_ACT_RU_DETAIL T ";
    private final ItemPageService itemPageService;
    private final ActRuDetailService actRuDetailService;
    private final Y9TableService y9TableService;
    private final JdbcTemplate jdbcTemplate;

    public ItemTodoApiImpl(
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
        return Y9Result.success(this.actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId,
            ActRuDetailStatusEnum.TODO));
    }

    /**
     * 根据用户id查询待办数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data 是待办任务数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserId(@RequestParam String tenantId, @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(this.actRuDetailService.countByAssigneeAndStatus(userId, ActRuDetailStatusEnum.TODO));
    }

    /**
     * 根据用户id和系统名称查询待办列表(以发送时间排序)
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
        Page<ActRuDetail> ardPage = this.actRuDetailService.pageBySystemNameAndAssigneeAndStatus(systemName, userId,
            ActRuDetailStatusEnum.TODO, rows, page, sort);
        List<ActRuDetailModel> modelList = ItemAdminModelConvertUtil.convertActRuDetailsToModels(ardPage.getContent());
        return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
    }

    /**
     * 根据用户id和系统名称查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param taskDefKey 任务key
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> findByUserIdAndSystemNameAndTaskDefKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam String taskDefKey,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        Page<ActRuDetail> ardPage;
        if (StringUtils.isNotBlank(taskDefKey)) {
            ardPage = this.actRuDetailService.pageBySystemNameAndAssigneeAndStatusAndTaskDefKey(systemName, userId,
                ActRuDetailStatusEnum.TODO, taskDefKey, rows, page, sort);
        } else {
            ardPage = this.actRuDetailService.pageBySystemNameAndAssigneeAndStatus(systemName, userId,
                ActRuDetailStatusEnum.TODO, rows, page, sort);
        }
        List<ActRuDetailModel> modelList = ItemAdminModelConvertUtil.convertActRuDetailsToModels(ardPage.getContent());
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
            ardPage =
                this.actRuDetailService.pageByAssigneeAndStatus(userId, ActRuDetailStatusEnum.TODO, rows, page, sort);
            List<ActRuDetailModel> modelList =
                ItemAdminModelConvertUtil.convertActRuDetailsToModels(ardPage.getContent());
            return Y9Page.success(page, ardPage.getTotalPages(), ardPage.getTotalElements(), modelList);
        } else {
            // 使用参数化查询构建条件
            String processParamSql = "LEFT JOIN FF_PROCESS_PARAM P ON T.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER ";
            StringBuilder whereSql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            params.add(userId);
            CommonUtils.buildQueryConditions(queryParamModel, whereSql, params);
            String sql = COMMON_SQL + processParamSql + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + whereSql
                + " AND T.ASSIGNEE = ? ORDER BY T.CREATETIME DESC";
            String countSql = "SELECT COUNT(T.ID) FROM FF_ACT_RU_DETAIL T " + processParamSql
                + " WHERE T.ASSIGNEE= ? AND T.STATUS=0 AND T.DELETED = FALSE " + whereSql;
            Object[] args = params.toArray(new Object[0]);
            ItemPage<ActRuDetailModel> ardModelPage = this.itemPageService.page(sql, args,
                new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
            return Y9Page.success(page, ardModelPage.getTotalpages(), ardModelPage.getTotal(), ardModelPage.getRows());
        }
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
        if (searchMap == null) {
            searchMap = new HashMap<>();
        }
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1);
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        // 构建搜索条件
        buildSearchConditions(searchMap, assigneeNameSql, signSql, params);
        String sql =
            COMMON_SQL + innerSql + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?"
                + whereSql + assigneeNameSql + signSql + " ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql;

        Object[] args = params.toArray();
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }

    @Override
    @SuppressWarnings({"deprecation", "java:S2077"})
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemNameAndTaskDefKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam(required = false) String taskDefKey,
        @RequestBody(required = false) String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        if (StringUtils.isNotBlank(searchMapStr)) {
            Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
            if (searchMap != null) {
                List<String> sqlList = y9TableService.getSql(searchMap);
                innerSql = sqlList.get(0);
                whereSql = sqlList.get(1);
                buildCommonSearchConditions(searchMap, assigneeNameSql, signSql, params);
            }
        }
        StringBuilder taskDefKeySql = new StringBuilder();
        buildTaskDefKeyCondition(taskDefKey, taskDefKeySql, params);
        String sql =
            COMMON_SQL + innerSql + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?"
                + whereSql + assigneeNameSql + signSql + taskDefKeySql + " ORDER BY T.CREATETIME DESC";
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper<>(ActRuDetailModel.class));
        return Y9Result.success(content);
    }

    private void buildCommonSearchConditions(Map<String, Object> searchMap, StringBuilder assigneeNameSql,
        StringBuilder signSql, List<Object> params) {
        // 处理办理人姓名搜索条件
        if (null != searchMap.get(ItemConsts.ASSIGNEENAME_KEY)) {
            assigneeNameSql.append(" AND INSTR(T.ASSIGNEENAME, ?) > 0 ");
            params.add(searchMap.get(ItemConsts.ASSIGNEENAME_KEY).toString());
        }
        // 处理会签状态搜索条件
        boolean sign = null != searchMap.get("sign");
        boolean noSign = null != searchMap.get(ItemConsts.NOSIGN_KEY);
        if (sign || noSign) {
            if (sign && noSign) {
                // 同时包含已会签和未会签
                signSql.append(" AND T.SIGNSTATUS >= 0");
            } else {
                // 只包含已会签或未会签
                signSql.append(" AND T.SIGNSTATUS = ?");
                params
                    .add(sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
            }
        }
    }

    private void buildTaskDefKeyCondition(String taskDefKey, StringBuilder taskDefKeySql, List<Object> params) {
        if (StringUtils.isNotBlank(taskDefKey)) {
            taskDefKeySql.append(" AND T.taskDefKey=?");
            params.add(taskDefKey);
        }
    }

    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName4Other(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        if (searchMap == null) {
            searchMap = new HashMap<>();
        }
        String numberSql = getSafeStringFromMap(searchMap);
        if (StringUtils.isNotBlank(searchMapStr)) {
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            buildSearchConditions(searchMap, assigneeNameSql, signSql, params);
        }
        if (StringUtils.isBlank(innerSql)) {
            innerSql = "INNER JOIN Y9_FORM_FW FW ON T.PROCESSSERIALNUMBER = FW.GUID";
        }
        // 使用参数化查询防止SQL注入
        String sql =
            COMMON_SQL + innerSql + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?"
                + whereSql + assigneeNameSql + signSql + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql
                + ") ORDER BY T.CREATETIME DESC";

        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql + ")";

        Object[] args = params.toArray();
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }

    private String getSafeStringFromMap(Map<String, Object> map) {
        Object value = map.get("numberSql");
        return value != null ? value.toString() : "";
    }

    private void buildSearchConditions(Map<String, Object> searchMap, StringBuilder assigneeNameSql,
        StringBuilder signSql, List<Object> params) {
        if (null != searchMap.get(ItemConsts.ASSIGNEENAME_KEY)) {
            assigneeNameSql.append(" AND INSTR(T.ASSIGNEENAME, ?) > 0 ");
            params.add(searchMap.get(ItemConsts.ASSIGNEENAME_KEY).toString());
        }
        boolean sign = null != searchMap.get("sign");
        boolean noSign = null != searchMap.get(ItemConsts.NOSIGN_KEY);
        if (sign || noSign) {
            if (sign && noSign) {
                signSql.append(" AND T.SIGNSTATUS>=0");
            } else {
                signSql.append(" AND T.SIGNSTATUS=?");
                params
                    .add(sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
            }
        }
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName4Other(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody(required = false) String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        String COMMON_SQL = searchMap.get("COMMON_SQL") != null ? searchMap.get("COMMON_SQL").toString() : "";
        String numberSql = searchMap.get("numberSql") != null ? searchMap.get("numberSql").toString() : "";
        if (StringUtils.isNotBlank(searchMapStr)) {
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            buildSearchConditions(searchMap, assigneeNameSql, signSql, params);
        }
        if (StringUtils.isBlank(innerSql)) {
            innerSql = "INNER JOIN Y9_FORM_FW FW ON T.PROCESSSERIALNUMBER = FW.GUID";
        }
        String sql =
            COMMON_SQL + innerSql + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?"
                + whereSql + assigneeNameSql + signSql + COMMON_SQL + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql
                + ")  ORDER BY T.CREATETIME DESC";

        Object[] args = params.toArray();
        List<ActRuDetailModel> content = jdbcTemplate.queryForList(sql, ActRuDetailModel.class, args);
        return Y9Result.success(content);
    }

    /**
     * 根据用户id和系统名称、表名称、搜索集合查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param taskDefKey 任务key
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemNameAndTaskDefKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam(required = false) String taskDefKey,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1);
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(systemName);
        params.add(userId);
        buildSearchConditions(searchMap, assigneeNameSql, signSql, params);
        StringBuilder taskDefKeySql = new StringBuilder();
        if (StringUtils.isNotBlank(taskDefKey)) {
            taskDefKeySql.append(" AND T.taskDefKey=?");
            params.add(taskDefKey);
        }
        String sql =
            COMMON_SQL + innerSql + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?"
                + whereSql + assigneeNameSql + signSql + taskDefKeySql + " ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + taskDefKeySql;
        Object[] args = params.toArray();
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }
}
