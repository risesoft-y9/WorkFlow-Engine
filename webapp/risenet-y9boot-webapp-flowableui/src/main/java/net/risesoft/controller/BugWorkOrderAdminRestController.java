package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.WorkOrderHandleTypeEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.WorkOrderModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/workOrderAdmin")
public class BugWorkOrderAdminRestController {

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private WorkOrderApi workOrderManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private TodoTaskApi todoTaskApi;

    @Value("${y9.app.flowable.tenantId}")
    private String myTenantId;

    @Autowired
    private PersonRoleApi personRoleApi;

    /**
     * 工单分配发送
     *
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     * @param processDefinitionKey 流程定义id
     * @param processSerialNumber 流程编号
     * @param userChoice 选择人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param level 紧急程度
     * @param number 文件编号
     * @param documentTitle 文件标题
     * @param isSendSms 是否发送短信
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/forwarding", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> forwarding(@RequestParam(required = true) String itemId,
        @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = true) String processDefinitionKey,
        @RequestParam(required = true) String processSerialNumber, @RequestParam(required = true) String userChoice,
        @RequestParam(required = false) String sponsorGuid, @RequestParam(required = true) String routeToTaskId,
        @RequestParam(required = false) String level, @RequestParam(required = false) String number,
        @RequestParam(required = false) String documentTitle, @RequestParam(required = false) String isSendSms,
        @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent) {
        try {
            WorkOrderModel workOrderModel = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            if (!WorkOrderHandleTypeEnum.UNHANDLE.getValue().equals(workOrderModel.getHandleType())) {
                return Y9Result.failure("改件已被办理，请刷新页面");
            }
            Map<String, Object> variables = new HashMap<String, Object>(16);
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            ProcessParamModel pp = new ProcessParamModel();
            pp.setIsSendSms(isSendSms);
            pp.setIsShuMing(isShuMing);
            pp.setSmsContent(smsContent);
            pp.setSmsPersonId("");
            pp.setDeptIds("");
            pp.setBureauIds("");
            pp.setCustomLevel(level);
            pp.setCustomNumber(number);
            pp.setItemId(itemId);
            pp.setItemName(item.getName());
            pp.setProcessInstanceId(processInstanceId);
            pp.setProcessSerialNumber(processSerialNumber);
            pp.setSystemName(item.getSystemName());
            pp.setSystemCnName(item.getSysLevel());
            pp.setTitle(documentTitle);
            pp.setSponsorGuid(processParamModel != null ? processParamModel.getSponsorGuid() : "");
            pp.setSended(processParamModel != null ? processParamModel.getSended() : "");
            pp.setStartor(processParamModel != null ? processParamModel.getStartor() : "");
            pp.setStartorName(processParamModel != null ? processParamModel.getStartorName() : "");
            pp.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            StringBuffer searchTerm = new StringBuffer();
            searchTerm.append(documentTitle).append("|").append(number).append("|").append(level).append("|")
                .append(item.getName());
            pp.setSearchTerm(searchTerm.toString());
            processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), pp);
            Map<String,
                Object> map = documentManager.saveAndForwarding(Y9LoginUserHolder.getTenantId(),
                    Y9LoginUserHolder.getPersonId(), processInstanceId, "", "", itemId, processSerialNumber,
                    processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                processInstanceId = (String)map.get("processInstanceId");
                workOrderManager.changeWorkOrderState(processSerialNumber, "2", processInstanceId, "");
                todoTaskApi.deleteByProcessInstanceId(myTenantId, processSerialNumber);
                return Y9Result.successMsg("发送成功");
            } else {
                return Y9Result.failure((String)map.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");

    }

    /**
     * 统一待办打开系统工单
     *
     * @param itemId
     * @param processSerialNumber
     * @param model
     * @return
     */
    @RequestMapping(value = "/todoIndex")
    public String todoIndex(@RequestParam(required = false) String itemId,
        @RequestParam(required = false) String processSerialNumber, Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", Y9LoginUserHolder.getUserInfo().getName());
        model.addAttribute("type", "fromWorkOrder");
        model.addAttribute("processSerialNumber", processSerialNumber);
        model.addAttribute("itemId", itemId);
        model.addAttribute("tenantManager", userInfo.isGlobalManager());
        boolean workOrderManage = personRoleApi.hasRole(tenantId, "itemAdmin", "", "系统工单管理员", userId).getData();
        model.addAttribute("workOrderManage", workOrderManage);
        ItemModel itemModel = itemManager.getByItemId(Y9LoginUserHolder.getTenantId(), itemId);
        if (itemModel != null && itemModel.getId() != null) {
            model.addAttribute("itemName", itemModel.getName());
            model.addAttribute("itemModel", itemModel);
        }
        return "main/newIndex";
    }

    /**
     * 办结工单
     *
     * @param processSerialNumber 编号
     * @param resultFeedback 反馈结果
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workOrderFinish", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> workOrderFinish(@RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String resultFeedback) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        try {
            WorkOrderModel workOrderModel = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            if (!workOrderModel.getHandleType().equals(WorkOrderHandleTypeEnum.UNHANDLE.getValue())) {
                return Y9Result.failure("改件已被办理，请刷新页面");
            }
            resMap = workOrderManager.changeWorkOrderState(processSerialNumber, "3", "", resultFeedback);
            if ((boolean)resMap.get(UtilConsts.SUCCESS)) {
                todoTaskApi.deleteByProcessInstanceId(myTenantId, processSerialNumber);
                return Y9Result.successMsg("办结成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办结失败");
    }

}
