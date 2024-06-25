package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.RemindInstance;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/remindInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class RemindInstanceApiImpl implements RemindInstanceApi {

    @Autowired
    private RemindInstanceService remindInstanceService;

    @Autowired
    private PersonApi personManager;

    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstance(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list = remindInstanceService.findRemindInstance(processInstanceId);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String tenantId,
        String processInstanceId, String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndArriveTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String tenantId,
        String processInstanceId, String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndRemindType(String tenantId,
        String processInstanceId, String remindType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndRemindType(processInstanceId, remindType);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndTaskId(String tenantId,
        String processInstanceId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndTaskId(processInstanceId, taskId);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    @Override
    public Y9Result<RemindInstanceModel> getRemindInstance(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        RemindInstance remindInstance = remindInstanceService.getRemindInstance(processInstanceId);
        RemindInstanceModel remindInstanceModel = null;
        if (remindInstance != null) {
            remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
        }
        return Y9Result.success(remindInstanceModel);
    }

    @Override
    public Y9Result<String> saveRemindInstance(String tenantId, String userId, String processInstanceId, String taskIds,
        Boolean process, String arriveTaskKey, String completeTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        return remindInstanceService.saveRemindInstance(processInstanceId, taskIds, process, arriveTaskKey,
            completeTaskKey);
    }

}
