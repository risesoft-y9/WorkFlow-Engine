package net.risesoft.api;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/officeFollow")
public class OfficeFollowApiImpl implements OfficeFollowApi {

    @Resource(name = "officeFollowService")
    private OfficeFollowService officeFollowService;

    @Autowired
    private PersonApi personManager;

    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return officeFollowService.countByProcessInstanceId(processInstanceId);
    }

    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.deleteByProcessInstanceId(processInstanceId);
    }

    @Override
    @PostMapping(value = "/delOfficeFollow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delOfficeFollow(String tenantId, String userId, String processInstanceIds) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return officeFollowService.delOfficeFollow(processInstanceIds);
    }

    @Override
    @GetMapping(value = "/getFollowCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getFollowCount(String tenantId, String userId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return officeFollowService.getFollowCount();
    }

    @Override
    @GetMapping(value = "/getOfficeFollowList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getOfficeFollowList(String tenantId, String userId, String searchName, int page,
        int rows) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return officeFollowService.getOfficeFollowList(searchName, page, rows);
    }

    @Override
    @PostMapping(value = "/saveOfficeFollow", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveOfficeFollow(String tenantId, String userId,
        @RequestBody OfficeFollowModel officeFollowModel) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        OfficeFollow officeFollow = new OfficeFollow();
        if (null != officeFollowModel) {
            Y9BeanUtil.copyProperties(officeFollowModel, officeFollow);
        }
        return officeFollowService.saveOfficeFollow(officeFollow);
    }

    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.updateTitle(processInstanceId, documentTitle);
    }
}
