package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/processInstance")
public class ProcessInstanceVueController {

    private static String TYPE_DELETE = "1";
    private static String TYPE_REJECT = "2";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private PositionApi positionApi;

    /**
     * 删除流程实例 现在有两种情况要删除流程实例： 1.用户直接删除：此时只需有processInstanceId即可，不需要reason 2.用户拒收：此时不止需有processInstanceId，还需填写拒收原因
     *
     * @param processInstanceId 要删除的流程实例Id
     * @param type 删除类型，1：用户直接删除，2：用户拒收
     * @param reason 删除原因，这里除了保存删除原因外，当用于删除的时候删除人也暂时保存在这里
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delete(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) String type, @RequestParam(required = false) String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        // 删除人不一定是当前正在处理流程的人员
        String assignee = userInfo.getPersonId();
        // 用户直接删除时，设置用户删除原因
        if (TYPE_DELETE.equals(type)) {
            if (StringUtils.isBlank(reason)) {
                reason = "delete:删除流程实例";
            } else {
                // 为防止reason中出现英文顿号，这里将它们替换成中文顿号
                reason.replace(SysVariables.COLON, "：");
                // 为防止reason中出现英文逗号，这里将它们替换成中文逗号
                reason.replace(SysVariables.COMMA, "，");
                reason = "delete:" + reason;
            }
            // 当删除流程实例的时候，保存删除人的guid
            reason = reason + SysVariables.COMMA + "operator" + SysVariables.COLON + assignee;
        }
        // 用户拒收时，设置用户拒收原因
        if (TYPE_REJECT.equals(type)) {
            reason = "reject:" + reason;
        }
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取流程实例列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/runningList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Map<String, Object>> runningList(@RequestParam(required = false) String processInstanceId,
        @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        long totalCount = 0;
        List<ProcessInstance> processInstanceList = null;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .orderByStartTime().desc().listPage((page - 1) * rows, rows);
        }
        Position position = null;
        OrgUnit orgUnit = null;
        Map<String, Object> map = null;
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            map = new HashMap<>(16);
            map.put("processInstanceId", processInstanceId);
            map.put("processDefinitionId", processInstance.getProcessDefinitionId());
            map.put("processDefinitionName", processInstance.getProcessDefinitionName());
            map.put("startTime",
                processInstance.getStartTime() == null ? "" : sdf.format(processInstance.getStartTime()));
            try {
                map.put("activityName",
                    runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByActivityInstanceStartTime().desc().list().get(0).getActivityName());
                map.put("suspended", processInstance.isSuspended());
                map.put("startUserName", "无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    String[] userIdAndDeptId = processInstance.getStartUserId().split(":");
                    if (userIdAndDeptId.length == 1) {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        orgUnit = orgUnitManager.getParent(tenantId, position.getId()).getData();
                        if (null != position) {
                            map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                        }
                    } else {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        if (null != position) {
                            orgUnit = orgUnitManager
                                .getOrgUnit(tenantId, processInstance.getStartUserId().split(":")[1]).getData();
                            if (null == orgUnit) {
                                map.put("startUserName", position.getName());
                            } else {
                                map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            items.add(map);
        }
        int totalpages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalpages, totalCount, items, "获取列表成功");
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processInstanceId 流程实例
     * @return
     */
    @RequestMapping(value = "/switchSuspendOrActive", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> switchSuspendOrActive(@RequestParam String state, @RequestParam String processInstanceId) {
        if (ItemProcessStateTypeEnum.ACTIVE.equals(state)) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            return Y9Result.successMsg("已激活ID为[" + processInstanceId + "]的流程实例。");
        } else if (ItemProcessStateTypeEnum.SUSPEND.equals(state)) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            return Y9Result.successMsg("已挂起ID为[" + processInstanceId + "]的流程实例。");
        }
        return Y9Result.failure("操作失败");
    }
}
