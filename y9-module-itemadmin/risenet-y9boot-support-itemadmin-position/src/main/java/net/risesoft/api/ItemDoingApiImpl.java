package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
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
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemDoing")
public class ItemDoingApiImpl implements ItemDoingApi {

    private final ItemPageService itemPageService;

    private final ActRuDetailService actRuDetailService;

    /**
     * 根据系统名称查询在办数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return int
     */
    @Override
    @GetMapping(value = "/countByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 1);
    }

    /**
     * 根据系统名称查询在办列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return ItemPage<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/findBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> findBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql =
            "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.STATUS = 0 AND T.DELETED = FALSE AND T.SYSTEMNAME = ? ORDER BY T.LASTTIME DESC) A WHERE A.RS_NUM=1";
        String countSql =
            "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE ";
        Object[] args = new Object[1];
        args[0] = systemName;
        return itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args,
            page, rows);
    }

    /**
     * 根据用户id和系统名称查询在办列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return ItemPage<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/findByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage =
            actRuDetailService.findBySystemNameAndAssigneeAndStatus(systemName, userId, 1, rows, page, sort);
        List<ActRuDetail> ardList = ardPage.getContent();
        ActRuDetailModel actRuDetailModel;
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : ardList) {
            actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        return ItemPage.<ActRuDetailModel>builder().rows(modelList).currpage(page).size(rows)
            .totalpages(ardPage.getTotalPages()).total(ardPage.getTotalElements()).build();
    }

    /**
     * 根据系统名称、表名称、搜索内容查询在办列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return ItemPage<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/searchBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> searchBySystemName(@RequestParam String tenantId, @RequestParam String systemName,
        String tableName, String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
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
        String sql =
            "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T "
                + sql0 + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + sql1 + " AND T.SYSTEMNAME = ?  ORDER BY "
                + orderBy + ") A WHERE A.RS_NUM=1";
        String countSql = "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE " + sql1;
        Object[] args = new Object[1];
        args[0] = systemName;
        return itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args,
            page, rows);
    }

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询在办列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return ItemPage<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/searchByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName, @RequestParam String tableName,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
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
        return itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args,
            page, rows);
    }

}
