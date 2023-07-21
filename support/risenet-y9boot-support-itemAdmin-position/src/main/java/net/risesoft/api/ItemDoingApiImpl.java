package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

@RestController
@RequestMapping(value = "/services/rest/itemDoing")
public class ItemDoingApiImpl implements ItemDoingApi {

    @Autowired
    private ItemPageService itemPageService;

    @Autowired
    private ActRuDetailService actRuDetailService;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Override
    @GetMapping(value = "/countByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndSystemName(String tenantId, String userId, String systemName) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.countBySystemNameAndAssigneeAndStatus(systemName, userId, 1);
    }

    @Override
    @GetMapping(value = "/findBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> findBySystemName(String tenantId, String systemName, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId  or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql = "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.STATUS = 0 AND T.DELETED = FALSE AND T.SYSTEMNAME = ? ORDER BY T.LASTTIME DESC) A WHERE A.RS_NUM=1";
        String countSql = "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE ";
        Object[] args = new Object[1];
        args[0] = systemName;
        ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return pageList;
    }

    @Override
    @GetMapping(value = "/findByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> findByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        Sort sort = Sort.by(Sort.Direction.DESC, "lastTime");
        Page<ActRuDetail> ardPage = actRuDetailService.findBySystemNameAndAssigneeAndStatus(systemName, userId, 1, rows, page, sort);
        List<ActRuDetail> ardList = ardPage.getContent();
        ActRuDetailModel actRuDetailModel = null;
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : ardList) {
            actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        ItemPage<ActRuDetailModel> pageList = ItemPage.<ActRuDetailModel>builder().rows(modelList).currpage(page).size(rows).totalpages(ardPage.getTotalPages()).total(ardPage.getTotalElements()).build();
        return pageList;
    }

    @Override
    @GetMapping(value = "/searchBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> searchBySystemName(String tenantId, String systemName, String tableName, String searchMapStr, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(systemName)) {
            throw new Exception("tenantId  or systemName is null !");
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql0 = "LEFT JOIN " + tableName.toUpperCase() + " F ON T.PROCESSSERIALNUMBER = F.GUID ";
        String sql1 = "";
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        for (String columnName : searchMap.keySet()) {
            sql1 += "AND INSTR(F." + columnName.toUpperCase() + ",'" + searchMap.get(columnName).toString() + "') > 0 ";
        }

        String orderBy = "T.LASTTIME DESC";

        String sql = "SELECT A.* FROM ( SELECT T.*, ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER) AS RS_NUM FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.STATUS = 0 AND T.DELETED = FALSE " + sql1 + " AND T.SYSTEMNAME = ?  ORDER BY " + orderBy + ") A WHERE A.RS_NUM=1";
        String countSql = "SELECT COUNT(DISTINCT T.PROCESSSERIALNUMBER) FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.SYSTEMNAME= ? AND T.STATUS=0 AND T.DELETED = FALSE " + sql1;
        Object[] args = new Object[1];
        args[0] = systemName;
        ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return pageList;
    }

    @Override
    @GetMapping(value = "/searchByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(String tenantId, String userId, String systemName, String tableName, String searchMapStr, Integer page, Integer rows) throws Exception {
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
        String orderBy = "T.LASTTIME DESC";
        String sql = "SELECT T.* FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.STATUS = 1 AND T.ENDED = FALSE AND T.DELETED = FALSE " + sql1 + " AND T.SYSTEMNAME = ? AND T.ASSIGNEE = ? ORDER BY " + orderBy;
        String countSql = "SELECT COUNT(ID) FROM FF_ACT_RU_DETAIL T " + sql0 + " WHERE T.SYSTEMNAME= ? AND T.ASSIGNEE= ? AND T.STATUS=1 AND T.ENDED = FALSE AND T.DELETED = FALSE " + sql1;
        Object[] args = new Object[2];
        args[0] = systemName;
        args[1] = userId;
        ItemPage<ActRuDetailModel> pageList = itemPageService.page(sql, args, new BeanPropertyRowMapper<>(ActRuDetailModel.class), countSql, args, page, rows);
        return pageList;
    }

}
