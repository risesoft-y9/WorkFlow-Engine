package net.risesoft.api;

import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 待办接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/itemTodo")
public class ItemTodoApiImpl implements ItemTodoApi {

    @Autowired
    private ItemPageService itemPageService;

    @Autowired
    private ActRuDetailService actRuDetailService;

    /**
     * 根据用户id和系统名称查询待办数量
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/countByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndSystemName(String tenantId, String userId, String systemName) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 0);
    }

    /**
     * 根据用户id和系统名称查询待办列表
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/findByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> findByUserIdAndSystemName(String tenantId, String userId, String systemName,
        Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<ActRuDetail> ardPage =
            actRuDetailService.findBySystemNameAndAssigneeAndStatus(systemName, userId, 0, rows, page, sort);
        List<ActRuDetail> ardList = ardPage.getContent();
        ActRuDetailModel actRuDetailModel = null;
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : ardList) {
            actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        ItemPage<ActRuDetailModel> pageList = ItemPage.<ActRuDetailModel>builder().rows(modelList).currpage(page)
            .size(rows).totalpages(ardPage.getTotalPages()).total(ardPage.getTotalElements()).build();
        return pageList;
    }

    /**
     * 根据用户id和系统名称、表名称、搜索集合查询待办列表
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/searchByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String tableName, String searchMapStr, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql0 = "LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
        String sql1 = "";
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        for (String columnName : searchMap.keySet()) {
            sql1 += "AND INSTR(F." + columnName.toUpperCase() + ",'" + searchMap.get(columnName).toString() + "') > 0 ";
        }

        String orderBy = "T.CREATETIME DESC";
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + sql1
            + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? ORDER BY " + orderBy;
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0
            + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.STATUS=0 AND T.DELETED = FALSE " + sql1;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args,
            new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return pageList;
    }

}
