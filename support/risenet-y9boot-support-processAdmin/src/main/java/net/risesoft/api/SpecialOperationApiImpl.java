package net.risesoft.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.OperationService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/specialOperation")
public class SpecialOperationApiImpl implements SpecialOperationApi {

    @Autowired
    private OperationService operationService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionManager;

    /**
     * 重定向
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param targetTaskDefineKey 任务key
     * @param users 人员id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/reposition", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition(String tenantId, String userId, String taskId, String targetTaskDefineKey,
        @RequestBody List<String> users, String reason, String sponsorGuid) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.reposition(taskId, targetTaskDefineKey, users, reason, sponsorGuid);
    }

    /**
     * 重定向(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/reposition4Position", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition4Position(String tenantId, String positionId, String taskId, String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.reposition4Position(taskId, repositionToTaskId, userChoice, reason, sponsorGuid);
    }

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack(String tenantId, String userId, String taskId, String reason) throws Exception {
        try {
            FlowableTenantInfoHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Y9LoginUserHolder.setTenantId(tenantId);
            operationService.rollBack(taskId, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退回（岗位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollBack4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack4Position(String tenantId, String positionId, String taskId, String reason) throws Exception {
        try {
            FlowableTenantInfoHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setTenantId(tenantId);
            Position position = positionManager.getPosition(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            operationService.rollBack4Position(taskId, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发回给发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollbackToSender", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender(String tenantId, String userId, String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.rollbackToSender(taskId);
    }

    /**
     * 发回给发送人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollbackToSender4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender4Position(String tenantId, String positionId, String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.rollbackToSender4Position(taskId);
    }

    /**
     * 返回拟稿人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollbackToStartor", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor(String tenantId, String userId, String taskId, String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);

        operationService.rollbackToStartor(taskId, reason);
    }

    /**
     * 返回拟稿人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/rollbackToStartor4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor4Position(String tenantId, String positionId, String taskId, String reason)
        throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.rollbackToStartor4Position(taskId, reason);
    }

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/specialComplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete(String tenantId, String userId, String taskId, String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.specialComplete(taskId, reason);
    }

    /**
     * 特殊办结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/specialComplete4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete4Position(String tenantId, String positionId, String taskId, String reason)
        throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.specialComplete4Position(taskId, reason);
    }

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/takeBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeBack(String tenantId, String userId, String taskId, String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.takeBack(taskId, reason);
    }

    /**
     * 收回(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/takeBack4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeBack4Position(String tenantId, String positionId, String taskId, String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.takeBack4Position(taskId, reason);
    }
}
