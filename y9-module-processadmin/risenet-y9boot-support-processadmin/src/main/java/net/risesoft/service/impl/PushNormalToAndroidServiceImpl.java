package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9Push;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.service.PushNormalToAndroidService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "pushNormalToAndroidService")
public class PushNormalToAndroidServiceImpl implements PushNormalToAndroidService {

    private final ProcessParamApi processParamApi;

    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    /**
     * 消息提醒
     */
    @Override
    public void pushNormalToAndroid(final DelegateTask task, final Map<String, Object> map) {
        Boolean pushSwitch = y9ProcessAdminProperties.getPushSwitch();
        if (pushSwitch == null || !pushSwitch) {
            LOGGER.info("######################消息推送提醒开关已关闭,如需推送请更改配置文件######################");
            return;
        }
        try {
            LOGGER.info("##########################消息推送提醒##########################");
            String assignee = task.getAssignee();
            String processSerialNumber = (String)map.get(SysVariables.PROCESSSERIALNUMBER);
            String tenantId = (String)map.get("tenantId");
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String title = processParamModel.getTitle();
            String itemName = processParamModel.getItemName();
            List<String> list = new ArrayList<>();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                List<Person> plist = positionApi.listPersonsByPositionId(tenantId, assignee).getData();
                for (Person p : plist) {
                    list.add(p.getId());
                }
            } else {// 人员
                list.add(assignee);
            }

            String send = processParamModel.getSended();
            // 第一步新建产生的任务，不发送提醒
            if (StringUtils.isBlank(send) || UtilConsts.FALSE.equals(send)) {
                return;
            }
            Y9Push.pushNormalMessage(list, itemName, title);
        } catch (Exception e) {
            LOGGER.warn("##########################消息推送提醒发生异常-taskId:{}##########################", task.getId(), e);
        }
    }

}
