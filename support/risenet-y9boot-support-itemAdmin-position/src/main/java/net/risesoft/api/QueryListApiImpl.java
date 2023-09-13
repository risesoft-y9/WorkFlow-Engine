package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.QueryListApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.service.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

@RestController
@RequestMapping(value = "/services/rest/queryList")
public class QueryListApiImpl implements QueryListApi {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    @GetMapping(value = "/getQueryList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> getQueryList(String tenantId, String userId, String systemName, String state, String createDate, String tableName, String searchMapStr, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String sql0 = "";
            String sql1 = "";
            if (StringUtils.isNotBlank(searchMapStr)) {// 表单搜索
                Boolean query = false;
                sql0 = " LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
                List<Map<String, Object>> list = Y9JsonUtil.readListOfMap(searchMapStr, String.class, Object.class);
                for (Map<String, Object> map : list) {
                    if (map.get("value") != null && !map.get("value").toString().equals("")) {// value有值
                        query = true;
                        String queryType = map.get("queryType").toString();
                        String value = map.get("value").toString();
                        String columnName = map.get("columnName").toString();
                        // select，radio类型搜索用=
                        if (queryType.equals("select") || queryType.equals("radio")) {
                            sql1 += " AND F." + columnName.toUpperCase() + " = '" + value + "' ";
                        } else if (queryType.equals("checkbox")) {// 多选框搜索
                            String[] values = value.split(",");
                            if (values.length == 1) {// 单个值
                                sql1 += " AND INSTR(F." + columnName.toUpperCase() + ",'" + values[0] + "') > 0 ";
                            } else {
                                String sql2 = "";
                                for (String val : values) {// 多个值
                                    if (sql2.equals("")) {
                                        sql2 += " AND ( INSTR(F." + columnName.toUpperCase() + ",'" + val + "') > 0 ";
                                    } else {
                                        sql2 += " OR INSTR(F." + columnName.toUpperCase() + ",'" + val + "') > 0 ";
                                    }
                                }
                                sql2 += " ) ";
                                sql1 += sql2;
                            }
                        } else if (queryType.equals("date")) {// 日期搜索
                            String[] values = value.split(",");
                            sql1 += " AND F." + columnName.toUpperCase() + " >= '" + values[0] + "' ";
                            sql1 += " AND F." + columnName.toUpperCase() + " < '" + values[1] + " 23:59:59' ";
                        } else {
                            sql1 += " AND INSTR(F." + columnName.toUpperCase() + ",'" + value + "') > 0 ";
                        }
                    }
                }
                if (!query) {
                    sql0 = "";
                    sql1 = "";
                }
            }
            String stateSql = "";
            if (StringUtils.isNotBlank(state)) {// 状态搜索
                if (state.equals("todo")) {
                    stateSql = " and T.STATUS = 0 AND T.ENDED = FALSE ";
                } else if (state.equals("doing")) {
                    stateSql = " and T.STATUS = 1 AND T.ENDED = FALSE ";
                } else if (state.equals("done")) {
                    stateSql = " and T.ENDED = TRUE ";
                }
            }
            String dateSql = "";
            if (StringUtils.isNotBlank(createDate)) {// 时间搜索
                String startDate = createDate.split(",")[0];
                String endDate = createDate.split(",")[1];
                dateSql = " and T.STARTTIME > '" + startDate + "' and T.STARTTIME < '" + endDate + " 23:59:59' ";
            }

            String orderBy = " T.STARTTIME DESC";
            String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.DELETED = FALSE " + stateSql + dateSql + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? " + sql1 + " ORDER BY " + orderBy;
            System.out.println(sql);

            String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.SYSTEMNAME= ? " + stateSql + dateSql + " AND T.ASSIGNEE= ? AND T.DELETED = FALSE " + sql1;
            Object[] args = new Object[2];
            args[0] = systemName;
            args[1] = userId;
            ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
            return pageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}