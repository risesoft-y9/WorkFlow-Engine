package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DataCenterApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.model.Person;
import net.risesoft.service.DataCenterService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/dataCenter")
public class DataCenterApiImpl implements DataCenterApi {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private DataCenterService dataCenterService;

    @Override
    @PostMapping(value = "/saveToDateCenter", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveToDateCenter(String processInstanceId, String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPersonById(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        boolean b = dataCenterService.saveToDateCenter(processInstanceId);
        return b;
    }

}
