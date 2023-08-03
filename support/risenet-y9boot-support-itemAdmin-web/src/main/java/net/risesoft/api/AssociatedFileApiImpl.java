package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.model.Person;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/associatedFile")
public class AssociatedFileApiImpl implements AssociatedFileApi {

    @Autowired
    private AssociatedFileService associatedFileService;

    @Autowired
    private PersonApi personManager;

    @Override
    @GetMapping(value = "/countAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countAssociatedFile(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return associatedFileService.countAssociatedFile(processSerialNumber);
    }

    @Override
    @PostMapping(value = "/deleteAllAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAllAssociatedFile(String tenantId, String userId, String processSerialNumber, String delIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        boolean b = associatedFileService.deleteAllAssociatedFile(processSerialNumber, delIds);
        return b;
    }

    @Override
    @PostMapping(value = "/deleteAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAssociatedFile(String tenantId, String userId, String processSerialNumber, String delId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        boolean b = associatedFileService.deleteAssociatedFile(processSerialNumber, delId);
        return b;
    }

    @Override
    @GetMapping(value = "/getAssociatedFileAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAssociatedFileAllList(String tenantId, String userId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = associatedFileService.getAssociatedFileAllList(processSerialNumber);
        return map;
    }

    @Override
    @GetMapping(value = "/getAssociatedFileList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAssociatedFileList(String tenantId, String userId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = associatedFileService.getAssociatedFileList(processSerialNumber);
        return map;
    }

    @Override
    @PostMapping(value = "/saveAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveAssociatedFile(String tenantId, String userId, String processSerialNumber,
        String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        boolean b = associatedFileService.saveAssociatedFile(processSerialNumber, processInstanceIds);
        return b;
    }
}
