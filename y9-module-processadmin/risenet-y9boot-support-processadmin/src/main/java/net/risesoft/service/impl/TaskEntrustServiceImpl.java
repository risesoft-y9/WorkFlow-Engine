package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.EntrustApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.service.TaskEntrustService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.Y9Properties;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "taskEntrustService")
public class TaskEntrustServiceImpl implements TaskEntrustService {

    private final  EntrustApi entrustManager;

    private final  TaskService taskService;

    private final  ProcessParamApi processParamManager;

    private final  Y9Properties y9Conf;

    /**
     * 出差委托
     */
    @Override
    public DelegateTask entrust(final DelegateTask task, final Map<String, Object> vars) {
        try {
            Boolean entrustSwitch = y9Conf.getApp().getProcessAdmin().getEntrustSwitch();
            if (entrustSwitch == null || !entrustSwitch) {
                LOGGER.info(
                    "######################出差委托开关已关闭,如需出差委托请更改配置文件:y9.app.processAdmin.entrustSwitch######################");
                return task;
            }
            String tenantId = (String)vars.get("tenantId");
            String processSerialNumber = (String)vars.get("processSerialNumber");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            String itemId = processParamModel.getItemId();
            String assigneeId = task.getAssignee();
            String taskId = task.getId();
            String currentTime = sdf.format(new Date());
            EntrustModel entrust = null;
            String sended = processParamModel.getSended();
            if (StringUtils.isBlank(sended) || UtilConsts.FALSE.equals(sended)) {
                // 第一步新建产生的任务，即使有出差委托，也不进行委托
                return task;
            }
            try {
                entrust = entrustManager.findOneByOwnerIdAndItemIdAndTime(tenantId, assigneeId, itemId, currentTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != entrust && StringUtils.isNotEmpty(entrust.getId())) {
                String entrustPersonId = entrust.getAssigneeId();
                /**
                 * 设置任务的办理人为原办理人的委托对象
                 */
                taskService.setAssignee(taskId, entrustPersonId);
                /**
                 * 设置任务的办理人为任务的拥有者
                 */
                /**
                 * 委托成功后，改变流程变量和任务变量里面的users
                 */
                @SuppressWarnings("unchecked")
                List<String> users = (List<String>)vars.get(SysVariables.USERS);
                List<String> usersTemp = new ArrayList<>();
                for (String user : users) {
                    if (user.equals(assigneeId)) {
                        usersTemp.add(entrustPersonId);
                    } else {
                        usersTemp.add(user);
                    }
                }
                entrustManager.saveEntrustDetail(tenantId, task.getProcessInstanceId(), taskId, assigneeId,
                    entrustPersonId);
                taskService.setVariable(taskId, SysVariables.USERS, usersTemp);
                taskService.setVariableLocal(taskId, SysVariables.USERS, usersTemp);
                LOGGER.info("{}委托任务taskId[{}]给{}成功!", assigneeId, taskId, entrustPersonId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

}
