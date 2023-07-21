package net.risesoft.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/officeDoneInfo")
public class OfficeDoneInfoApiImpl implements OfficeDoneInfoApi {

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Override
    @GetMapping(value = "/countByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countByItemId(itemId);
    }

    @Override
    @GetMapping(value = "/countByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserId(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countByUserId(userId, itemId);
    }

    @Override
    @GetMapping(value = "/countDoingByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public long countDoingByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countDoingByItemId(itemId);
    }

    @Override
    @PostMapping(value = "/deleteOfficeDoneInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteOfficeDoneInfo(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.deleteOfficeDoneInfo(processInstanceId);
    }

    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfficeDoneInfoModel findByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        OfficeDoneInfoModel officeDoneInfoModel = null;
        if (officeDoneInfo != null) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
        }
        return officeDoneInfoModel;
    }

    @Override
    @PostMapping(value = "/saveOfficeDone", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOfficeDone(String tenantId, @RequestBody OfficeDoneInfoModel info) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeInfo = new OfficeDoneInfo();
        Y9BeanUtil.copyProperties(info, officeInfo);
        officeDoneInfoService.saveOfficeDone(officeInfo);
    }

    @Override
    @GetMapping(value = "/searchAllByDeptId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllByDeptId(String tenantId, String deptId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllByDeptId(deptId, title, itemId, userName, state, year, page, rows);
    }

    @Override
    @GetMapping(value = "/searchAllByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllByUserId(String tenantId, String userId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllByUserId(userId, title, itemId, userName, state, year, page, rows);
    }

    @Override
    @GetMapping(value = "/searchAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllList(searchName, itemId, userName, state, year, page, rows);
    }

    @Override
    @GetMapping(value = "/searchByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchByItemId(String tenantId, String title, String itemId, String state, String startdate, String enddate, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByItemId(title, itemId, state, startdate, enddate, page, rows);
    }

    @Override
    @GetMapping(value = "/searchByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchByUserId(String tenantId, String userId, String title, String itemId, String startdate, String enddate, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByUserId(userId, title, itemId, startdate, enddate, page, rows);
    }

}
