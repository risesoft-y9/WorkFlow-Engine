package net.risesoft.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
import net.risesoft.service.ItemWorkDayService;
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
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemTodo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemTodoApiImpl implements ItemTodoApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final PositionApi positionApi;

    private final ItemWorkDayService itemWorkDayService;

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
                processParamSql = "LEFT JOIN FF_PROCESS_PARAM F ON T.PROCESSSERIALNUMBER = F.PROCESSSERIALNUMBER ";
            StringBuilder sql1 = new StringBuilder();
            Object object = queryParamModel;
            Class queryParamModelClazz = object.getClass();
            Field[] fields = queryParamModelClazz.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if ("serialVersionUID".equals(f.getName()) || "page".equals(f.getName())
                    || "rows".equals(f.getName())) {
                    continue;
                }
                Object fieldValue;
                try {
                    fieldValue = f.get(object);
                    if (null != fieldValue) {
                        if ("systemName".equals(f.getName())) {
                            systemNameSql = StringUtils.isBlank(queryParamModel.getSystemName()) ? ""
                                : "AND T.SYSTEMNAME = '" + fieldValue + "' ";
                        } else {
                            sql1.append("AND INSTR(F.").append(f.getName().toUpperCase()).append(",'")
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
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (!"serialVersionUID".equals(f.getName()) && !"page".equals(f.getName())
                    && !"rows".equals(f.getName()) && f.get(object) != null
                    && StringUtils.isNotBlank(f.get(object).toString())) {
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
        Position position = positionApi.get(tenantId, userId).getData();
        StringBuilder innerSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        boolean haveAssigneeName = null != searchMap.get("assigneeName");
        if (haveAssigneeName && !position.getName().contains(String.valueOf(searchMap.get("assigneeName")))) {
            return Y9Page.success(page, 0, 0, List.of(), "获取列表成功");
        }
        List<String> tableAliasList = new ArrayList<>();
        for (String key : searchMap.keySet()) {
            if (key.contains(".")) {
                String[] aliasColumnNameType = key.split("\\.");
                String alias = aliasColumnNameType[0];
                if (!tableAliasList.contains(alias)) {
                    tableAliasList.add(alias);
                    Y9Table y9Table = this.y9TableService.findByTableAlias(alias);
                    if (null == y9Table) {
                        return Y9Page.failure(page, rows, 0, null, "别名" + alias + "对应的表不存在", 0);
                    }
                    innerSql.append("INNER JOIN ").append(y9Table.getTableName().toUpperCase()).append(" ")
                        .append(alias.toUpperCase()).append(" ON T.PROCESSSERIALNUMBER = ").append(alias.toUpperCase())
                        .append(".GUID ");
                }
                switch (aliasColumnNameType.length) {
                    case 2:
                        if ("dbsx".equalsIgnoreCase(aliasColumnNameType[1])) {
                            int days = Integer.parseInt(searchMap.get(key).toString());
                            List<String> start_end = itemWorkDayService.getDb(days);
                            whereSql.append(" AND ").append(key.toUpperCase()).append(" >='").append(start_end.get(0))
                                .append("' AND ").append(key.toUpperCase()).append(" <='").append(start_end.get(1))
                                .append("'");
                        } else {
                            whereSql.append("AND INSTR(").append(key.toUpperCase()).append(",'")
                                .append(searchMap.get(key).toString()).append("') > 0 ");
                        }
                        break;
                    case 3:
                        String aliasColumnName = aliasColumnNameType[0] + "." + aliasColumnNameType[1];
                        String type = aliasColumnNameType[2];
                        if ("equal".equals(type)) {
                            whereSql.append(" AND ").append(aliasColumnName.toUpperCase()).append("='")
                                .append(searchMap.get(key).toString()).append("' ");
                        } else if ("date".equals(type)) {
                            ArrayList<String> list = (ArrayList<String>)searchMap.get(key);
                            whereSql.append(" AND ").append(aliasColumnName.toUpperCase()).append(" >='")
                                .append(list.get(0)).append("' AND ").append(aliasColumnName.toUpperCase())
                                .append(" <='").append(list.get(1)).append("'");
                        }
                        break;
                }
            }
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql + " WHERE T.STATUS = 0 AND T.DELETED = FALSE "
            + whereSql + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.STATUS=0 AND T.DELETED = FALSE " + whereSql;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
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
        StringBuilder innerSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> tableAliasList = new ArrayList<>();
        for (String key : searchMap.keySet()) {
            if (key.contains(".")) {
                String[] aliasAndColumnName = key.split("\\.");
                String alias = aliasAndColumnName[0];
                if (null != searchMap.get(key) && StringUtils.isNotBlank(searchMap.get(key).toString())) {
                    whereSql.append("AND INSTR(").append(key.toUpperCase()).append(",'")
                        .append(searchMap.get(key).toString()).append("') > 0 ");
                } else {
                    whereSql.append("AND (").append(key.toUpperCase()).append("= '' OR ").append(key.toUpperCase())
                        .append(" IS NULL)");
                }
                if (!tableAliasList.contains(alias)) {
                    tableAliasList.add(alias);
                    Y9Table y9Table = this.y9TableService.findByTableAlias(alias);
                    if (null == y9Table) {
                        return Y9Page.failure(page, rows, 0, null, "别名" + alias + "对应的表不存在", 0);
                    }
                    innerSql.append("INNER JOIN ").append(y9Table.getTableName().toUpperCase()).append(" ")
                        .append(alias.toUpperCase()).append(" ON T.PROCESSSERIALNUMBER = ").append(alias.toUpperCase())
                        .append(".GUID ");
                }
            }
        }
        Object[] args;
        String taskDefKeySql = "";
        if (StringUtils.isNotBlank(taskDefKey)) {
            args = new Object[3];
            args[0] = systemName;
            args[1] = taskDefKey;
            args[2] = userId;
            taskDefKeySql = "AND T.TASKDEFKEY = ?";
        } else {
            args = new Object[2];
            args[0] = systemName;
            args[1] = userId;
        }
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql + " WHERE T.STATUS = 0 AND T.DELETED = FALSE "
            + whereSql + " AND T.SYSTEMNAME = ? " + taskDefKeySql + " AND T.ASSIGNEE = ?ORDER BY T.CREATETIME DESC";
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + innerSql
            + " WHERE T.SYSTEMNAME= ? " + taskDefKeySql + " AND T.ASSIGNEE= ?  AND T.STATUS=0 AND T.DELETED = FALSE "
            + whereSql;
        ItemPage<ActRuDetailModel> ardPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardPage.getTotalpages(), ardPage.getTotal(), ardPage.getRows());
    }
}
