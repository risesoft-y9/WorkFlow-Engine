package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/reminder")

public class ReminderRestController {

    @Autowired
    private ReminderApi reminderApi;

    @Autowired
    private FlowableReminderService flowableReminderService;

    /**
     * 删除催办信息
     *
     * @param ids 催办id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteList(@RequestParam(required = true) String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderApi.deleteList(tenantId, ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取我的任务催办信息
     *
     * @param taskId 任务id
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/reminderMeList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getReminderList(@RequestParam(required = true) String taskId, @RequestParam(required = true) int rows, @RequestParam(required = true) int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = reminderApi.findByTaskId(tenantId, taskId, page, rows);
        return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
    }

    /**
     * 获取催办列表信息
     *
     * @param type 类型
     * @param processInstanceId 流程实例id
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/reminderList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> myReminder(@RequestParam(required = true) String type, @RequestParam(required = true) String processInstanceId, @RequestParam(required = true) int rows, @RequestParam(required = true) int page) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        if ("my".equals(type)) {
            map = reminderApi.findBySenderIdAndProcessInstanceIdAndActive(tenantId, userId, processInstanceId, page, rows);
        } else {
            map = reminderApi.findByProcessInstanceId(tenantId, processInstanceId, page, rows);
        }
        return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
    }

    /**
     * 保存催办信息
     *
     * @param processInstanceId 流程实例id
     * @param taskIds 催办任务ids
     * @param msgContent 催办信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveReminder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveReminder(@RequestParam(required = true) String processInstanceId, @RequestParam(required = true) String[] taskIds, @RequestParam(required = true) String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getPositionId();
        try {
            Map<String, Object> map = new HashMap<>(16);
            map = reminderApi.saveReminder(tenantId, userId, processInstanceId, taskIds, msgContent);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 设置阅读时间
     *
     * @param ids 催办id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setReadTime", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> setReadTime(@RequestParam(required = true) String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderApi.setReadTime(tenantId, ids);
        return Y9Result.successMsg("操作成功");
    }

    /**
     * 获取催办任务
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/taskList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> taskList(@RequestParam(required = true) String processInstanceId, @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        return flowableReminderService.findTaskListByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 更新催办信息
     *
     * @param id 催办is
     * @param msgContent 催办信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateReminder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> updateReminder(@RequestParam(required = true) String id, @RequestParam(required = true) String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Map<String, Object> map = new HashMap<>(16);
            map = reminderApi.updateReminder(tenantId, id, msgContent);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
