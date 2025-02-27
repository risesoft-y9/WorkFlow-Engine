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

import net.risesoft.api.itemadmin.ItemAllApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ActRuDetail;
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
 * 所有本人经手的件接口
 *
 * @author qinman
 * @date 2024/12/19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemAll", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemAllApiImpl implements ItemAllApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    private final Y9TableService y9TableService;

    private final PositionApi positionApi;

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
        return Y9Result.success(actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 0));
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<ActRuDetail> ardPage =
            actRuDetailService.pageBySystemNameAndAssignee(systemName, userId, rows, page, sort);
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
        boolean isEmpty = checkObjAllFieldsIsNull(queryParamModel);
        if (isEmpty) {
            ardPage = actRuDetailService.pageByAssigneeAndStatus(userId, 0, rows, page, sort);
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
        @RequestParam String userId, @RequestParam String systemName,
        @RequestBody String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        assert searchMap != null;
        List<String> sqlList = y9TableService.getSql(searchMap);
        String innerSql = sqlList.get(0), whereSql = sqlList.get(1), assigneeNameInnerSql = sqlList.get(2),
            assigneeNameWhereSql = sqlList.get(3);
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + innerSql + assigneeNameInnerSql
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

}
