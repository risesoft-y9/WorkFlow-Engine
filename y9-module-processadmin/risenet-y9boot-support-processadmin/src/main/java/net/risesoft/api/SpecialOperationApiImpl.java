package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.OperationService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 退回，收回，重定向，特殊办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/specialOperation")
public class SpecialOperationApiImpl implements SpecialOperationApi {

    private final OperationService operationService;

    private final PersonApi personManager;

    private final PositionApi positionManager;

    /**
     * 重定向
     *
     * @param tenantId            租户id
     * @param userId              人员id
     * @param taskId              任务id
     * @param targetTaskDefineKey 任务key
     * @param users               人员id集合
     * @param reason              重定向原因
     * @param sponsorGuid         主办人id
     */
    @Override
    @PostMapping(value = "/reposition", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId, @RequestParam String targetTaskDefineKey, @RequestBody List<String> users, String reason, String sponsorGuid) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.reposition(taskId, targetTaskDefineKey, users, reason, sponsorGuid);
    }

    /**
     * 重定向(岗位)
     *
     * @param tenantId           租户id
     * @param positionId         岗位id
     * @param taskId             任务id
     * @param repositionToTaskId 任务key
     * @param userChoice         岗位id集合
     * @param reason             重定向原因
     * @param sponsorGuid        主办人id
     */
    @Override
    @PostMapping(value = "/reposition4Position", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId, @RequestParam String repositionToTaskId, @RequestParam("userChoice") List<String> userChoice, @RequestParam String reason, @RequestParam String sponsorGuid) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.reposition4Position(taskId, repositionToTaskId, userChoice, reason, sponsorGuid);
    }

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId   人员id
     * @param taskId   任务id
     * @param reason   退回的原因
     */
    @Override
    @PostMapping(value = "/rollBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack(@RequestParam String tenantId, @RequestParam String userId, String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.rollBack(taskId, reason);
    }

    /**
     * 退回（岗位）
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param reason     退回的原因
     */
    @Override
    @PostMapping(value = "/rollBack4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.rollBack4Position(taskId, reason);
    }

    /**
     * 发回给发送人
     *
     * @param tenantId 租户id
     * @param userId   人员id
     * @param taskId   任务id
     */
    @Override
    @PostMapping(value = "/rollbackToSender", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.rollbackToSender(taskId);
    }

    /**
     * 发回给发送人/岗位
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @Override
    @PostMapping(value = "/rollbackToSender4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.rollbackToSender4Position(taskId);
    }

    /**
     * 返回拟稿人
     *
     * @param tenantId 租户id
     * @param userId   人员id
     * @param taskId   任务id
     * @param reason   原因
     */
    @Override
    @PostMapping(value = "/rollbackToStartor", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.rollbackToStartor(taskId, reason);
    }

    /**
     * 返回拟稿人/岗位
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param reason     原因
     */
    @Override
    @PostMapping(value = "/rollbackToStartor4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.rollbackToStartor4Position(taskId, reason);
    }

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId   人员id
     * @param taskId   任务id
     * @param reason   原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/specialComplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId, @RequestParam String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.specialComplete(taskId, reason);
    }

    /**
     * 特殊办结/岗位
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param reason     原因
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/specialComplete4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId, @RequestParam String reason) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.specialComplete4Position(taskId, reason);
    }

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId   人员id
     * @param taskId   任务id
     * @param reason   收回的原因
     */
    @Override
    @PostMapping(value = "/takeBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeBack(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        operationService.takeBack(taskId, reason);
    }

    /**
     * 收回(岗位)
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param reason     收回的原因
     */
    @Override
    @PostMapping(value = "/takeBack4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeBack4Position(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String taskId, @RequestParam String reason) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        operationService.takeBack4Position(taskId, reason);
    }
}
