package net.risesoft.api;

import java.lang.reflect.Field;
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

import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

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

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final JdbcTemplate jdbcTemplate;

    public ItemTodoApiImpl(ItemPageService itemPageService, ActRuDetailService actRuDetailService,
        Y9TableService y9TableService, @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate) {
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
        return Y9Result.success(this.actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 0));
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndAssigneeAndStatus(systemName, userId, 0, rows, page, sort);
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<ActRuDetail> ardPage;
        if (StringUtils.isNotBlank(taskDefKey)) {
            ardPage = this.actRuDetailService.pageBySystemNameAndAssigneeAndStatusAndTaskDefKey(systemName, userId, 0,
                taskDefKey, rows, page, sort);
        } else {
            ardPage =
                this.actRuDetailService.pageBySystemNameAndAssigneeAndStatus(systemName, userId, 0, rows, page, sort);
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        int page = queryParamModel.getPage(), rows = queryParamModel.getRows();
        Page<ActRuDetail> ardPage;
        boolean isEmpty = this.checkObjAllFieldsIsNull(queryParamModel);
        if (isEmpty) {
            ardPage = this.actRuDetailService.pageByAssigneeAndStatus(userId, 0, rows, page, sort);
        } else {
            String systemNameSql = "",
                processParamSql = "LEFT JOIN FF_PROCESS_PARAM P ON T.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER ";
            StringBuilder sql1 = new StringBuilder();
            Object object = queryParamModel;
            Class queryParamModelClazz = object.getClass();
            Field[] fields = queryParamModelClazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if ("serialVersionUID".equals(field.getName()) || "page".equals(field.getName())
                    || "rows".equals(field.getName())) {
                    continue;
                }
                Object fieldValue;
                try {
                    fieldValue = field.get(object);
                    if (null != fieldValue) {
                        if ("systemName".equals(field.getName())) {
                            systemNameSql = StringUtils.isBlank(queryParamModel.getSystemName()) ? ""
                                : "AND T.SYSTEMNAME = '" + fieldValue + "' ";
                        } else if ("bureauIds".equals(field.getName())) {
                            sql1.append(" AND P.HOSTDEPTID = '").append(fieldValue).append("' ");
                        } else {
                            sql1.append("AND INSTR(P.").append(field.getName().toUpperCase()).append(",'")
                                .append(fieldValue).append("') > 0 ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String orderBy = "T.CREATETIME DESC";
            String sql =
                "SELECT T.* FROM FF_ACT_RU_DETAIL T " + processParamSql + " WHERE T.STATUS = 0 AND T.DELETED = FALSE "
                    + sql1 + systemNameSql + " AND T.ASSIGNEE = ? ORDER BY " + orderBy;
            String countSql = "SELECT COUNT(T.ID) FROM FF_ACT_RU_DETAIL T " + processParamSql
                + " WHERE T.ASSIGNEE= ? AND T.STATUS=0 AND T.DELETED = FALSE " + sql1 + systemNameSql;
            Object[] args = new Object[1];
            args[0] = userId;
            ItemPage<ActRuDetailModel> ardModelPage = this.itemPageService.page(sql, args,
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

    public boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!"serialVersionUID".equals(field.getName()) && !"page".equals(field.getName())
                    && !"rows".equals(field.getName()) && field.get(object) != null
                    && StringUtils.isNotBlank(field.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1);
        StringBuilder assigneeNameSql = new StringBuilder();
        if (null != searchMap.get("assigneeName")) {
            assigneeNameSql.append("AND INSTR(T.ASSIGNEENAME").append(",'")
                .append(searchMap.get("assigneeName").toString()).append("') > 0 ");
        }
        boolean sign = null != searchMap.get("sign");
        boolean noSign = null != searchMap.get("noSign");
        StringBuilder signSql = new StringBuilder();
        if (sign || noSign) {
            if (sign && noSign) {
                signSql.append(" AND ").append("T.SIGNSTATUS>=0");
            } else {
                signSql.append(" AND ").append("T.SIGNSTATUS=").append(
                    sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
            }
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + " ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }

    @Override
    @SuppressWarnings("deprecation")
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemNameAndTaskDefKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam(required = false) String taskDefKey,
        @RequestBody(required = false) String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        if (StringUtils.isNotBlank(searchMapStr)) {
            Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
            assert searchMap != null;
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            if (null != searchMap.get("assigneeName")) {
                assigneeNameSql.append("AND INSTR(T.ASSIGNEENAME").append(",'")
                    .append(searchMap.get("assigneeName").toString()).append("') > 0 ");
            }
            boolean sign = null != searchMap.get("sign");
            boolean noSign = null != searchMap.get("noSign");
            if (sign || noSign) {
                if (sign && noSign) {
                    signSql.append(" AND ").append("T.SIGNSTATUS>=0");
                } else {
                    signSql.append(" AND ").append("T.SIGNSTATUS=").append(
                        sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
                }
            }
        }
        StringBuilder taskDefKeySql = new StringBuilder();
        if (StringUtils.isNotBlank(taskDefKey)) {
            taskDefKeySql.append(" AND T.taskDefKey='").append(taskDefKey).append("'");
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + taskDefKeySql + " ORDER BY T.CREATETIME DESC";
        Object[] args = {systemName, userId};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class));
        return Y9Result.success(content);
    }

    @Override
    public Y9Page<ActRuDetailModel> searchByUserIdAndSystemName4Other(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        String commonSql = searchMap.get("commonSql").toString();
        String numberSql = searchMap.get("numberSql").toString();
        if (StringUtils.isNotBlank(searchMapStr)) {
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            if (null != searchMap.get("assigneeName")) {
                assigneeNameSql.append("AND INSTR(T.ASSIGNEENAME").append(",'")
                    .append(searchMap.get("assigneeName").toString()).append("') > 0 ");
            }
            boolean sign = null != searchMap.get("sign");
            boolean noSign = null != searchMap.get("noSign");
            if (sign || noSign) {
                if (sign && noSign) {
                    signSql.append(" AND ").append("T.SIGNSTATUS>=0");
                } else {
                    signSql.append(" AND ").append("T.SIGNSTATUS=").append(
                        sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
                }
            }
        }
        if (StringUtils.isBlank(innerSql)) {
            innerSql = "INNER JOIN Y9_FORM_FW FW ON T.PROCESSSERIALNUMBER = FW.GUID";
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + commonSql + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql
            + ")  ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + commonSql + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql + ")";
        Object[] args = {systemName, userId};
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }

    @Override
    public Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName4Other(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody(required = false) String searchMapStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String innerSql = "", whereSql = "";
        StringBuilder assigneeNameSql = new StringBuilder();
        StringBuilder signSql = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        String commonSql = searchMap.get("commonSql").toString();
        String numberSql = searchMap.get("numberSql").toString();
        if (StringUtils.isNotBlank(searchMapStr)) {
            List<String> sqlList = y9TableService.getSql(searchMap);
            innerSql = sqlList.get(0);
            whereSql = sqlList.get(1);
            if (null != searchMap.get("assigneeName")) {
                assigneeNameSql.append("AND INSTR(T.ASSIGNEENAME").append(",'")
                    .append(searchMap.get("assigneeName").toString()).append("') > 0 ");
            }
            boolean sign = null != searchMap.get("sign");
            boolean noSign = null != searchMap.get("noSign");
            if (sign || noSign) {
                if (sign && noSign) {
                    signSql.append(" AND ").append("T.SIGNSTATUS>=0");
                } else {
                    signSql.append(" AND ").append("T.SIGNSTATUS=").append(
                        sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
                }
            }
        }
        if (StringUtils.isBlank(innerSql)) {
            innerSql = "INNER JOIN Y9_FORM_FW FW ON T.PROCESSSERIALNUMBER = FW.GUID";
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + commonSql + " AND T.PROCESSSERIALNUMBER NOT IN (" + numberSql
            + ")  ORDER BY T.CREATETIME DESC";
        Object[] args = {systemName, userId};
        List<ActRuDetailModel> content =
            jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class));
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
        if (null != searchMap.get("assigneeName")) {
            assigneeNameSql.append("AND INSTR(T.ASSIGNEENAME").append(",'")
                .append(searchMap.get("assigneeName").toString()).append("') > 0 ");
        }
        boolean sign = null != searchMap.get("sign");
        boolean noSign = null != searchMap.get("noSign");
        StringBuilder signSql = new StringBuilder();
        if (sign || noSign) {
            if (sign && noSign) {
                signSql.append(" AND ").append("T.SIGNSTATUS>=0");
            } else {
                signSql.append(" AND ").append("T.SIGNSTATUS=").append(
                    sign ? ActRuDetailSignStatusEnum.DONE.getValue() : ActRuDetailSignStatusEnum.TODO.getValue());
            }
        }
        StringBuilder taskDefKeySql = new StringBuilder();
        if (StringUtils.isNotBlank(taskDefKey)) {
            taskDefKeySql.append(" AND T.taskDefKey='").append(taskDefKey).append("'");
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + taskDefKeySql + " ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(*) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.DELETED = FALSE AND T.STATUS = 0 AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?" + whereSql
            + assigneeNameSql + signSql + taskDefKeySql;
        Object[] args = {systemName, userId};
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }
}
