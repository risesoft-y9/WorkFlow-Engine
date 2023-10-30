package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.Person;
import net.risesoft.service.DocumentService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.VariableApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/document")
public class DocumentApiImpl implements DocumentApi {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private VariableApiClient variableManager;

    @Override
    @GetMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> add(String tenantId, String userId, String itemId, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.add(itemId, mobile, returnMap);
        return returnMap;
    }

    @Override
    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void complete(String tenantId, String userId, String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        documentService.complete(taskId);
    }

    @Override
    @GetMapping(value = "/docUserChoise", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> docUserChoise(String tenantId, String userId, String itemId, String processDefinitionKey,
        String processDefinitionId, String taskId, String routeToTask, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.docUserChoise(itemId, processDefinitionKey, processDefinitionId, taskId,
            routeToTask, processInstanceId);
        return returnMap;
    }

    @Override
    @GetMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> edit(String tenantId, String userId, String itembox, String taskId,
        String processInstanceId, String itemId, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.edit(itembox, taskId, processInstanceId, itemId, mobile);
        return returnMap;
    }

    @Override
    @PostMapping(value = "/forwardingSendReceive", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> forwardingSendReceive(String tenantId, String userId, String taskId, String userChoice,
        String routeToTaskId, @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        variableManager.setVariables(tenantId, taskId, variables);
        map = documentService.forwardingSendReceive(taskId, userChoice, routeToTaskId);
        return map;
    }

    @Override
    @PostMapping(value = "/saveAndForwarding", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwarding(String tenantId, String userId, String processInstanceId,
        String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            map = documentService.saveAndForwarding(itemId, processSerialNumber, processDefinitionKey, userChoice,
                sponsorGuid, routeToTaskId, variables);
        } else {
            if (!variables.isEmpty()) {
                variableManager.setVariables(tenantId, taskId, variables);
            }
            map = documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
        return map;
    }

    @Override
    @PostMapping(value = "/saveAndForwardingByTaskKey", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwardingByTaskKey(String tenantId, String userId, String processInstanceId,
        String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, String startRouteToTaskId,
        @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            map = documentService.saveAndForwardingByTaskKey(itemId, processSerialNumber, processDefinitionKey,
                userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
        } else {
            variableManager.setVariables(tenantId, taskId, variables);
            map = documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
        return map;
    }

    @Override
    @GetMapping(value = "/signTaskConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> signTaskConfig(String tenantId, String userId, String itemId, String processDefinitionId,
        String taskDefinitionKey, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            documentService.signTaskConfig(itemId, processDefinitionId, taskDefinitionKey, processSerialNumber);
        return map;
    }

    @Override
    @PostMapping(value = "/startProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> startProcess(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = documentService.startProcess(itemId, processSerialNumber, processDefinitionKey);
        return map;
    }

    @Override
    @PostMapping(value = "/startProcess1", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> startProcess(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey, String userIds) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            documentService.startProcess(itemId, processSerialNumber, processDefinitionKey, userIds);
        return map;
    }
}
