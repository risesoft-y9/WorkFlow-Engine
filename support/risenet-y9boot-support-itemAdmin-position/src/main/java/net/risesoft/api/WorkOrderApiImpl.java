package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.entity.WorkOrderEntity;
import net.risesoft.model.itemadmin.WorkOrderModel;
import net.risesoft.service.WorkOrderService;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 系统工单接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/workOrder")
public class WorkOrderApiImpl implements WorkOrderApi {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 改变工单状态
     *
     * @param processSerialNumber 流程编号
     * @param state 状态
     * @param processInstanceId 流程实例id
     * @param resultFeedback 结果反馈
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/changeWorkOrderState", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> changeWorkOrderState(String processSerialNumber, String state, String processInstanceId, String resultFeedback) {
        return workOrderService.changeWorkOrderState(processSerialNumber, state, processInstanceId, resultFeedback);
    }

    /**
     * 删除草稿
     *
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/deleteDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteDraft(String processSerialNumber) {
        return workOrderService.deleteDraft(processSerialNumber);
    }

    /**
     * 获取系统工单草稿
     *
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/draftlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> draftlist(String userId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = workOrderService.draftlist(userId, searchTerm, page, rows);
        List<WorkOrderModel> resList = new ArrayList<WorkOrderModel>();
        List<WorkOrderEntity> list = (List<WorkOrderEntity>)map.get("rows");
        for (WorkOrderEntity workOrderEntity : list) {
            WorkOrderModel workOrderModel = new WorkOrderModel();
            Y9BeanUtil.copyProperties(workOrderEntity, workOrderModel);
            resList.add(workOrderModel);
        }
        map.put("rows", resList);
        return map;
    }

    /**
     * 获取工单信息
     *
     * @param processSerialNumber 流程编号
     * @return WorkOrderModel
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkOrderModel findByProcessSerialNumber(String processSerialNumber) {
        WorkOrderEntity workOrder = workOrderService.findByProcessSerialNumber(processSerialNumber);
        WorkOrderModel workOrderModel = new WorkOrderModel();
        if (workOrder != null) {
            Y9BeanUtil.copyProperties(workOrder, workOrderModel);
        }
        return workOrderModel;
    }

    /**
     * 获取管理员工单计数
     *
     * @param userId 人员id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getAdminCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAdminCount() {
        return workOrderService.getAdminCount();
    }

    /**
     * 获取系统管理员未处理计数
     *
     * @return int
     */
    @Override
    @GetMapping(value = "/getAdminTodoCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getAdminTodoCount() {
        return workOrderService.getAdminTodoCount();
    }

    /**
     * 获取个人工单计数
     *
     * @param userId 人员id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCount(String userId) {
        return workOrderService.getCount(userId);
    }

    /**
     * 保存工单信息
     *
     * @param workOrderModel 工单信息
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/saveWorkOrder", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveWorkOrder(@RequestBody WorkOrderModel workOrderModel) {
        WorkOrderEntity workOrder = new WorkOrderEntity();
        Y9BeanUtil.copyProperties(workOrderModel, workOrder);
        return workOrderService.saveWorkOrder(workOrder);
    }

    /**
     * 获取管理员工单列表
     *
     * @param searchTerm 搜索词
     * @param handleType 处理状态
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/workOrderAdminList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> workOrderAdminList(String searchTerm, String handleType, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = workOrderService.workOrderAdminList(searchTerm, handleType, page, rows);
        List<WorkOrderModel> resList = new ArrayList<WorkOrderModel>();
        List<WorkOrderEntity> list = (List<WorkOrderEntity>)map.get("rows");
        for (WorkOrderEntity workOrderEntity : list) {
            WorkOrderModel workOrderModel = new WorkOrderModel();
            Y9BeanUtil.copyProperties(workOrderEntity, workOrderModel);
            resList.add(workOrderModel);
        }
        map.put("rows", resList);
        return map;
    }

    /**
     * 获取工单列表
     *
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param handleType 处理状态
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/workOrderList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> workOrderList(String userId, String searchTerm, String handleType, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = workOrderService.workOrderList(userId, searchTerm, handleType, page, rows);
        List<WorkOrderModel> resList = new ArrayList<WorkOrderModel>();
        List<WorkOrderEntity> list = (List<WorkOrderEntity>)map.get("rows");
        for (WorkOrderEntity workOrderEntity : list) {
            WorkOrderModel workOrderModel = new WorkOrderModel();
            Y9BeanUtil.copyProperties(workOrderEntity, workOrderModel);
            resList.add(workOrderModel);
        }
        map.put("rows", resList);
        return map;
    }

}
