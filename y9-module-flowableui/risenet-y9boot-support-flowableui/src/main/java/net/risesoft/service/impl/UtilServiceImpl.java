package net.risesoft.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
public class UtilServiceImpl implements UtilService {

    private final ChaoSongApi chaoSongApi;

    private final SpeakInfoApi speakInfoApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeFollowApi officeFollowApi;

    @Override
    public void setPublicData(Map<String, Object> mapTemp, String processInstanceId, List<TaskModel> taskList,
        ItemBoxTypeEnum itemBoxTypeEnum) {
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = Y9LoginUserHolder.getPersonId(),
            positionId = Y9LoginUserHolder.getPositionId();
        int chaosongNum = 0, speakInfoNum = 0, countFollow = 0;
        boolean isReminder = false;
        RemindInstanceModel remindInstanceModel = null;
        switch (itemBoxTypeEnum) {
            case TODO:
            case DOING:
                chaosongNum =
                    chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                speakInfoNum = speakInfoApi.getNotReadCount(tenantId, personId, processInstanceId).getData();
                remindInstanceModel =
                    remindInstanceApi.getRemindInstance(tenantId, personId, processInstanceId).getData();
                countFollow =
                    officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                isReminder = String.valueOf(taskList.get(0).getPriority()).contains("8");
                break;
            case DONE:
                countFollow =
                    officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                chaosongNum =
                    chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                break;
            case MONITOR_DOING:
                isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                break;
            case QUERY:
                countFollow =
                    officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                break;
        }
        mapTemp.put("chaosongNum", chaosongNum);
        mapTemp.put("speakInfoNum", speakInfoNum);
        mapTemp.put("remindSetting", remindInstanceModel != null);
        mapTemp.put("follow", countFollow > 0);
        mapTemp.put("isReminder", isReminder);
    }
}