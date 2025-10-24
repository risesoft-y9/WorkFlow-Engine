package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.worklist.QueryListApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.util.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 综合查询接口
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/queryList", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryListApiImpl implements QueryListApi {

    private final ItemPageService itemPageService;

    /**
     * 综合搜索办件列表
     *
     * @param tenantId 租户id
     * @param userId 岗位id
     * @param systemName 系统名称
     * @param state 状态
     * @param createDate 开始日期
     * @param tableName 表名称
     * @param searchMapStr 搜索条件
     * @param page 页面
     * @param rows 条数
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - data 是综合查询列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ActRuDetailModel> getQueryList(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName, String state, String createDate, String tableName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            StringBuilder joinSql = new StringBuilder();
            StringBuilder whereSql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            // 表单搜索
            if (StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(searchMapStr)) {
                buildFormSearchConditions(joinSql, whereSql, params, tableName, searchMapStr);
            }
            // 状态搜索
            if (StringUtils.isNotBlank(state)) {
                buildStateConditions(whereSql, state);
            }
            // 时间搜索
            if (StringUtils.isNotBlank(createDate)) {
                buildDateConditions(whereSql, params, createDate);
            }
            // 添加系统名称和用户ID参数
            params.add(systemName);
            params.add(userId);
            Object[] args = params.toArray();
            String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + joinSql + " WHERE T.DELETED = FALSE " + whereSql
                + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? ORDER BY T.STARTTIME DESC";
            String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + joinSql + " WHERE T.DELETED = FALSE "
                + whereSql + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ?";
            ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args,
                new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
            return Y9Page.success(page, pageList.getTotalpages(), pageList.getTotal(), pageList.getRows());
        } catch (Exception e) {
            LOGGER.error("查询列表失败", e);
        }
        return null;
    }

    /**
     * 构建表单搜索条件
     */
    private void buildFormSearchConditions(StringBuilder joinSql, StringBuilder whereSql, List<Object> params,
        String tableName, String searchMapStr) {
        List<Map<String, Object>> list = Y9JsonUtil.readListOfMap(searchMapStr, String.class, Object.class);
        if (list == null || list.isEmpty()) {
            return;
        }
        boolean hasQuery = false;
        joinSql.append(" LEFT JOIN ").append(tableName.toUpperCase()).append(" F ON T.PROCESSSERIALNUMBER = F.GUID ");
        for (Map<String, Object> map : list) {
            if (map.get(ItemConsts.VALUE_KEY) != null && !map.get(ItemConsts.VALUE_KEY).toString().isEmpty()) {
                hasQuery = true;
                String queryType = map.get("queryType").toString();
                String value = map.get(ItemConsts.VALUE_KEY).toString();
                String columnName = map.get("columnName").toString();
                buildConditionByType(whereSql, params, queryType, value, columnName);
            }
        }
        if (!hasQuery) {
            joinSql.setLength(0);
        }
    }

    /**
     * 根据查询类型构建条件
     */
    private void buildConditionByType(StringBuilder whereSql, List<Object> params, String queryType, String value,
        String columnName) {
        switch (queryType) {
            case "select":
            case "radio":
                whereSql.append(" AND F.").append(columnName.toUpperCase()).append(" = ? ");
                params.add(value);
                break;
            case "checkbox":
                buildCheckboxConditions(whereSql, params, value, columnName);
                break;
            case "date":
                buildDateRangeConditions(whereSql, params, value, columnName);
                break;
            default:
                whereSql.append(" AND INSTR(F.").append(columnName.toUpperCase()).append(", ?) > 0 ");
                params.add(value);
                break;
        }
    }

    /**
     * 构建复选框条件
     */
    private void buildCheckboxConditions(StringBuilder whereSql, List<Object> params, String value, String columnName) {
        String[] values = value.split(",");
        if (values.length == 1) {
            whereSql.append(" AND INSTR(F.").append(columnName.toUpperCase()).append(", ?) > 0 ");
            params.add(values[0]);
        } else {
            whereSql.append(" AND (");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    whereSql.append(" OR ");
                }
                whereSql.append(" INSTR(F.").append(columnName.toUpperCase()).append(", ?) > 0 ");
                params.add(values[i]);
            }
            whereSql.append(") ");
        }
    }

    /**
     * 构建日期范围条件
     */
    private void buildDateRangeConditions(StringBuilder whereSql, List<Object> params, String value,
        String columnName) {
        String[] values = value.split(",");
        whereSql.append(" AND F.").append(columnName.toUpperCase()).append(" >= ? ");
        whereSql.append(" AND F.").append(columnName.toUpperCase()).append(" <= ? ");
        params.add(values[0]);
        params.add(values[1] + " 23:59:59");
    }

    /**
     * 构建状态条件
     */
    private void buildStateConditions(StringBuilder whereSql, String state) {
        switch (state) {
            case "todo":
                whereSql.append(" AND T.STATUS = 0 AND T.ENDED = FALSE ");
                break;
            case "doing":
                whereSql.append(" AND T.STATUS = 1 AND T.ENDED = FALSE ");
                break;
            case "done":
                whereSql.append(" AND T.ENDED = TRUE ");
                break;
            default:
                LOGGER.warn("state对应的itemBox不存在！");
                break;
        }
    }

    /**
     * 构建日期条件
     */
    private void buildDateConditions(StringBuilder whereSql, List<Object> params, String createDate) {
        String[] dates = createDate.split(",");
        String startDate = dates[0];
        String endDate = dates[1];
        whereSql.append(" AND T.STARTTIME >= ? AND T.STARTTIME <= ? ");
        params.add(startDate);
        params.add(endDate + " 23:59:59");
    }
}