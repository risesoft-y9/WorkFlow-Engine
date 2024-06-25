package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.QueryListApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.service.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 综合查询接口
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/queryList", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueryListApiImpl implements QueryListApi {

    private final ItemPageService itemPageService;

    /**
     * 综合搜索
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
     * @return ItemPage<ActRuDetailModel>
     */
    @Override
    public ItemPage<ActRuDetailModel> getQueryList(String tenantId, String userId, String systemName, String state,
        String createDate, String tableName, String searchMapStr, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String sql0 = "";
            StringBuilder sql1 = new StringBuilder();
            if (StringUtils.isNotBlank(searchMapStr)) {// 表单搜索
                boolean query = false;
                sql0 = " LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
                List<Map<String, Object>> list = Y9JsonUtil.readListOfMap(searchMapStr, String.class, Object.class);
                assert list != null;
                for (Map<String, Object> map : list) {
                    if (map.get("value") != null && !map.get("value").toString().isEmpty()) {// value有值
                        query = true;
                        String queryType = map.get("queryType").toString();
                        String value = map.get("value").toString();
                        String columnName = map.get("columnName").toString();
                        // select，radio类型搜索用=
                        switch (queryType) {
                            case "select":
                            case "radio":
                                sql1.append(" AND F.").append(columnName.toUpperCase()).append(" = '").append(value)
                                    .append("' ");
                                break;
                            case "checkbox": {// 多选框搜索
                                String[] values = value.split(",");
                                if (values.length == 1) {// 单个值
                                    sql1.append(" AND INSTR(F.").append(columnName.toUpperCase()).append(",'")
                                        .append(values[0]).append("') > 0 ");
                                } else {
                                    StringBuilder sql2 = new StringBuilder();
                                    for (String val : values) {// 多个值
                                        if (sql2.toString().isEmpty()) {
                                            sql2.append(" AND ( INSTR(F.").append(columnName.toUpperCase()).append(",'")
                                                .append(val).append("') > 0 ");
                                        } else {
                                            sql2.append(" OR INSTR(F.").append(columnName.toUpperCase()).append(",'")
                                                .append(val).append("') > 0 ");
                                        }
                                    }
                                    sql2.append(" ) ");
                                    sql1.append(sql2);
                                }
                                break;
                            }
                            case "date": {// 日期搜索
                                String[] values = value.split(",");
                                sql1.append(" AND F.").append(columnName.toUpperCase()).append(" >= '")
                                    .append(values[0]).append("' ");
                                sql1.append(" AND F.").append(columnName.toUpperCase()).append(" < '").append(values[1])
                                    .append(" 23:59:59' ");
                                break;
                            }
                            default:
                                sql1.append(" AND INSTR(F.").append(columnName.toUpperCase()).append(",'").append(value)
                                    .append("') > 0 ");
                                break;
                        }
                    }
                }
                if (!query) {
                    sql0 = "";
                    sql1 = new StringBuilder();
                }
            }
            String stateSql = "";
            if (StringUtils.isNotBlank(state)) {// 状态搜索
                switch (state) {
                    case "todo":
                        stateSql = " and T.STATUS = 0 AND T.ENDED = FALSE ";
                        break;
                    case "doing":
                        stateSql = " and T.STATUS = 1 AND T.ENDED = FALSE ";
                        break;
                    case "done":
                        stateSql = " and T.ENDED = TRUE ";
                        break;
                }
            }
            String dateSql = "";
            if (StringUtils.isNotBlank(createDate)) {// 时间搜索
                String startDate = createDate.split(",")[0];
                String endDate = createDate.split(",")[1];
                dateSql = " and T.STARTTIME > '" + startDate + "' and T.STARTTIME < '" + endDate + " 23:59:59' ";
            }

            String orderBy = " T.STARTTIME DESC";
            String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.DELETED = FALSE " + stateSql + dateSql
                + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? " + sql1 + " ORDER BY " + orderBy;
            System.out.println(sql);

            String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.SYSTEMNAME= ? " + stateSql
                + dateSql + " AND T.ASSIGNEE= ? AND T.DELETED = FALSE " + sql1;
            Object[] args = new Object[2];
            args[0] = systemName;
            args[1] = userId;
            return itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args,
                page, rows);
        } catch (Exception e) {
            LOGGER.error("查询列表失败", e);
        }
        return null;
    }

}