package net.risesoft.controller.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.service.SyncYearTableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 生成租户年度表结构
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping("/services/rest/yearTable")
public class SyncYearTableRestController {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "jdbcTemplate4Public")
    private JdbcTemplate jdbcTemplate4Public;

    @Autowired
    private SyncYearTableService syncYearTableService;

    /**
     * 生成所有租户年度表结构（租用了事项管理的租户）
     *
     * @param year
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/syncYearTable4AllTenant")
    public void syncYearTable4AllTenant(String year, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> list = jdbcTemplate4Public.queryForList("select id from rs_common_tenant", String.class);
        for (String tenantId : list) {
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT" + "	count(t.ID)" + " FROM" + "	rs_common_tenant_system t"
                + " LEFT JOIN rs_common_system s on t.SYSTEMID = s.ID" + " WHERE" + "	t.TENANTID = '" + tenantId + "'"
                + " and s.SYSTEMNAME = 'itemAdmin'";
            int count = jdbcTemplate4Public.queryForObject(sql, Integer.class);
            if (count > 0) {
                Map<String, Object> m = syncYearTableService.syncYearTable(year);
                map.put(tenantId, m);
            } else {
                Map<String, Object> m = new HashMap<String, Object>(16);
                m.put("msg", "该租户未租用事项管理系统");
                map.put(tenantId, m);
            }
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 生成租户年度表结构
     *
     * @param tenantId
     * @param year
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/syncYearTable4Tenant")
    public void syncYearTable4Tenant(String tenantId, String year, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        map = syncYearTableService.syncYearTable(year);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
