package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DataCenterApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
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
    public Y9Result<Object> saveToDateCenter(String processInstanceId, String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        boolean b = dataCenterService.saveToDateCenter(processInstanceId);
        if (b) {
            return Y9Result.success();
        }
        return Y9Result.failure("保存办结数据到数据中心失败");
    }

}
