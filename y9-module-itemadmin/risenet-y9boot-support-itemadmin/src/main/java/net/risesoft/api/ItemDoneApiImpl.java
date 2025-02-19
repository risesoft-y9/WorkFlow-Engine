package net.risesoft.api;

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

import net.risesoft.api.itemadmin.ItemDoneApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.form.Y9Table;
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
 * 办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemDone", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemDoneApiImpl implements ItemDoneApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

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
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Y9LoginUserHolder.setTenantId(tenantId);
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndEnded(systemName, true, page, rows, sort);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ardPage.getContent().forEach(ard -> {
            ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(ard, actRuDetailModel);
            modelList.add(actRuDetailModel);
        });
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
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndAssigneeAndEnded(systemName, userId, true, rows, page, sort);
        List<ActRuDetail> ardList = ardPage.getContent();
        ActRuDetailModel actRuDetailModel;
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : ardList) {
            actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        ItemPage<ActRuDetailModel> itemPage = ItemPage.<ActRuDetailModel>builder().rows(modelList).currpage(page)
            .size(rows).totalpages(ardPage.getTotalPages()).total(ardPage.getTotalElements()).build();
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
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
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage =
            this.actRuDetailService.pageBySystemNameAndDeptIdAndEnded(systemName, deptId, isBureau, true, rows, page,
                sort);
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
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> tableAliasList = new ArrayList<>();
        StringBuilder innerSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
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
        String orderBySql = " ORDER BY T.STARTTIME DESC";
        String sql =
            "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql
                + " WHERE T.SYSTEMNAME = ? AND T.ENDED = TRUE AND  T.DELETED = FALSE " + whereSql
                + " GROUP BY T.PROCESSSERIALNUMBER ";
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") ALIAS";
        sql += orderBySql;
        Object[] args = new Object[1];
        args[0] = systemName;
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
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
        StringBuilder sql1 = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        for (String columnName : searchMap.keySet()) {
            sql1.append("AND INSTR(F.").append(columnName.toUpperCase()).append(",'")
                .append(searchMap.get(columnName).toString()).append("') > 0 ");
        }
        String orderBy = "T.LASTTIME DESC";
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.ENDED = TRUE AND T.DELETED = FALSE AND T.PLACEONFILE = FALSE " + sql1 + " AND T.SYSTEMNAME = ?"
            + " AND T.ASSIGNEE = ? ORDER BY " + orderBy;
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.ENDED = TRUE AND T.DELETED = FALSE AND T.PLACEONFILE = FALSE "
            + sql1;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> itemPage = this.itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return Y9Page.success(itemPage.getCurrpage(), itemPage.getTotalpages(), itemPage.getTotal(),
            itemPage.getRows());
    }
}