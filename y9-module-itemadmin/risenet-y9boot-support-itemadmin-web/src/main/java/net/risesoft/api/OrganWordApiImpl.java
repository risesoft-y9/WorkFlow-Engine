package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.service.OrganWordService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/organWord")
public class OrganWordApiImpl implements OrganWordApi {

    @Autowired
    private OrganWordService organWordService;

    @Autowired
    private PersonApi personManager;

    @Override
    @GetMapping(value = "/checkNumberStr", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer checkNumberStr(String tenantId, String userId, String characterValue, String custom, Integer year,
        Integer numberTemp, String itemId, Integer common, String processSerialNumber) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Integer status = organWordService.checkNumberStr(characterValue, custom, year, numberTemp, itemId, common,
            processSerialNumber);
        return status;
    }

    @Override
    @GetMapping(value = "/exist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> exist(String tenantId, String userId, String custom, String processSerialNumber,
        String processInstanceId, String itembox) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = organWordService.exist(custom, processSerialNumber, processInstanceId, itembox);
        return map;
    }

    @Override
    @GetMapping(value = "/findByCustom", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findByCustom(String tenantId, String userId, String custom, String itemId,
        String processDefinitionId, String taskDefKey) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap =
            organWordService.findByCustom(itemId, processDefinitionId, taskDefKey, custom);
        return listMap;
    }

    @Override
    @GetMapping(value = "/getNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getNumber(String tenantId, String userId, String custom, String characterValue,
        Integer year, Integer common, String itemId) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = organWordService.getNumber(custom, characterValue, year, common, itemId);
        return map;
    }

    @Override
    @GetMapping(value = "/getNumberOnly", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer getNumberOnly(String tenantId, String userId, String custom, String characterValue, Integer year,
        Integer common, String itemId) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Integer number = organWordService.getNumberOnly(custom, characterValue, year, common, itemId);
        return number;
    }
}
