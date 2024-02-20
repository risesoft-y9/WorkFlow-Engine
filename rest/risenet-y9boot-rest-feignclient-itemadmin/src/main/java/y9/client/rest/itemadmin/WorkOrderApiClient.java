package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.model.itemadmin.WorkOrderModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "WorkOrderApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/workOrder")
public interface WorkOrderApiClient extends WorkOrderApi {

    /**
     * 改变工单状态
     *
     * @param processSerialNumber
     * @param state
     * @param processInstanceId
     * @param resultFeedback
     * @return
     */
    @Override
    @PostMapping("/changeWorkOrderState")
    Map<String, Object> changeWorkOrderState(@RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("state") String state, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("resultFeedback") String resultFeedback);

    /**
     * 删除草稿
     *
     * @param processSerialNumber
     * @return
     */
    @Override
    @PostMapping("/deleteDraft")
    Map<String, Object> deleteDraft(@RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取系统工单草稿
     *
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/draftlist")
    Map<String, Object> draftlist(@RequestParam("userId") String userId, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 获取工单信息
     *
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumber")
    WorkOrderModel findByProcessSerialNumber(@RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取管理员工单计数
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/getAdminCount")
    Map<String, Object> getAdminCount();

    /**
     * 获取系统管理员未处理计数
     *
     * @return
     */
    @Override
    @GetMapping("/getAdminTodoCount")
    int getAdminTodoCount();

    /**
     * 获取个人工单计数
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/getCount")
    Map<String, Object> getCount(@RequestParam("userId") String userId);

    /**
     * 保存工单信息
     *
     * @param workOrderModel
     * @return
     */
    @Override
    @PostMapping(value = "/saveWorkOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> saveWorkOrder(@RequestBody WorkOrderModel workOrderModel);

    /**
     * 获取管理员工单列表
     *
     * @param searchTerm
     * @param handleType
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/workOrderAdminList")
    Map<String, Object> workOrderAdminList(@RequestParam("searchTerm") String searchTerm,
        @RequestParam("handleType") String handleType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 获取工单列表
     *
     * @param userId
     * @param searchTerm
     * @param handleType
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/workOrderList")
    Map<String, Object> workOrderList(@RequestParam("userId") String userId,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("handleType") String handleType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

}
