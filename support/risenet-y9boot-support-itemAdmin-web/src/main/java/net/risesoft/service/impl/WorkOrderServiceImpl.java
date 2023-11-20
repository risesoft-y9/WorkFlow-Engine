package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.WorkOrderEntity;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Role;
import net.risesoft.model.todo.TodoTask;
import net.risesoft.repository.jpa.WorkOrderRepository;
import net.risesoft.service.WorkOrderService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.open.todo.TodoTaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "workOrderService")
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private PersonApi personManager;

    @Value("${y9.app.itemAdmin.tenantId}")
    private String myTenantId;

    @Value("${y9.app.itemAdmin.workOrderIndex}")
    private String workOrderIndex;

    @Value("${y9.app.itemAdmin.workOrderItemId}")
    private String workOrderItemId;

    @Autowired
    private TodoTaskApiClient todoTaskManager;

    @Autowired
    private PersonRoleApi personRoleApi;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> changeWorkOrderState(String processSerialNumber, String state, String processInstanceId,
        String resultFeedback) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            WorkOrderEntity workOrderEntity = workOrderRepository.findByGuid(processSerialNumber);
            if (workOrderEntity != null) {
                if (StringUtils.isNotBlank(processInstanceId)) {
                    workOrderEntity.setRealProcessInstanceId(processInstanceId);
                }
                if (StringUtils.isNotBlank(resultFeedback)) {
                    workOrderEntity.setResultFeedback(resultFeedback);
                }
                workOrderEntity.setHandleType(state);
                workOrderRepository.save(workOrderEntity);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> deleteDraft(String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            WorkOrderEntity workOrderEntity = workOrderRepository.findByGuid(processSerialNumber);
            if (workOrderEntity != null) {
                workOrderRepository.delete(workOrderEntity);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> draftlist(String userId, String searchTerm, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
            Page<WorkOrderEntity> pageList = workOrderRepository.findByUserIdAndHandleType(userId, "0", pageable);
            map.put("currpage", page);
            map.put("totalpages", pageList.getTotalPages());
            map.put("total", pageList.getTotalElements());
            map.put("rows", pageList.getContent());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public WorkOrderEntity findByProcessSerialNumber(String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        WorkOrderEntity workOrderEntity = workOrderRepository.findByGuid(processSerialNumber);
        return workOrderEntity;
    }

    @Override
    public Map<String, Object> getAdminCount() {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("todoCount", 0);
        map.put("doingCount", 0);
        map.put("doneCount", 0);
        try {
            List<Map<String, Object>> list = workOrderRepository.countByAdmin();
            for (Map<String, Object> m : list) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(m.get("type"))) {
                    map.put("todoCount", m.get("num"));

                } else if (new HashMap<String, String>(16).equals(m.get("type"))) {
                    map.put("doingCount", m.get("num"));

                } else if (ItemBoxTypeEnum.DONE.getValue().equals(m.get("type"))) {
                    map.put("doneCount", m.get("num"));
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public int getAdminTodoCount() {
        Y9LoginUserHolder.setTenantId(myTenantId);
        return workOrderRepository.getAdminTodoCount();
    }

    @Override
    public Map<String, Object> getCount(String userId) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("todoCount", 0);
        map.put("doingCount", 0);
        map.put("doneCount", 0);
        map.put("draftCount", 0);
        try {
            List<Map<String, Object>> list = workOrderRepository.countByUserId(userId);
            for (Map<String, Object> m : list) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(m.get("type"))) {
                    map.put("todoCount", m.get("num"));

                } else if (new HashMap<String, String>(16).equals(m.get("type"))) {
                    map.put("doingCount", m.get("num"));

                } else if (ItemBoxTypeEnum.DRAFT.getValue().equals(m.get("type"))) {
                    map.put("draftCount", m.get("num"));

                } else if (ItemBoxTypeEnum.DONE.getValue().equals(m.get("type"))) {
                    map.put("doneCount", m.get("num"));
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 提交未处理工单，保存统一待办
     *
     * @param workOrder
     * @return
     */
    @Async
    public Future<Boolean> saveTodoTask(final WorkOrderEntity workOrder) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        try {
            // FIXME
            String handleType = "1";
            if (handleType.equals(workOrder.getHandleType())) {
                // TODO
                List<Role> roleList = new ArrayList<>();
                if (roleList != null && roleList.size() > 0) {
                    Role role = roleList.get(0);
                    List<Person> personList = personRoleApi.listPersonsByRoleId(myTenantId, role.getId()).getData();
                    TodoTask todo = new TodoTask();
                    todo.setTenantId(myTenantId);
                    todo.setSystemName("systemWorkOrder");
                    todo.setSystemCnName("系统工单");
                    todo.setAppName("systemWorkOrder");
                    todo.setAppCnName("系统工单");
                    todo.setTitle(workOrder.getTitle());
                    todo.setSenderId(workOrder.getUserId());
                    todo.setSenderName(workOrder.getUserName());
                    todo.setSenderDepartmentId(workOrder.getTenantId());
                    todo.setSenderDepartmentName(workOrder.getTenantName());
                    todo.setSendTime(new Date());
                    todo.setIsNewTodo("1");
                    String urgency = "0";
                    boolean urgency0 = workOrder.getUrgency().equals("中") || workOrder.getUrgency().equals("低");
                    boolean urgency1 = workOrder.getUrgency().equals("高");
                    boolean urgency2 = workOrder.getUrgency().equals("紧急");
                    if (urgency0) {
                        urgency = "0";
                    } else if (urgency1) {
                        urgency = "1";
                    } else if (urgency2) {
                        urgency = "2";
                    }
                    todo.setUrgency(urgency);
                    todo.setDocNumber(workOrder.getNumber());
                    todo.setProcessInstanceId(workOrder.getGuid());
                    String url = workOrderIndex + "?itemId=" + workOrderItemId + "&processSerialNumber="
                        + workOrder.getGuid() + "&type=fromTodo&appName=systemWorkOrder";
                    todo.setUrl(url);
                    for (Person person : personList) {
                        // 提交的工单taskid由processSerialNumber:userId组合
                        todo.setTaskId(workOrder.getGuid() + ":" + person.getId());
                        todo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        todo.setReceiverId(person.getId());
                        todo.setReceiverName(person.getName());
                        todo.setReceiverDepartmentId(person.getParentId());
                        OrgUnit orgUnit = personManager.getParent(myTenantId, person.getId()).getData();
                        todo.setReceiverDepartmentName(orgUnit.getName());
                        todoTaskManager.saveTodoTask(myTenantId, todo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(true);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveWorkOrder(WorkOrderEntity workOrder) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            WorkOrderEntity workOrderEntity = workOrderRepository.findByGuid(workOrder.getGuid());
            if (workOrderEntity == null) {
                workOrderRepository.save(workOrder);
            } else {
                workOrder.setGuid(workOrderEntity.getGuid());
                workOrderRepository.save(workOrder);
            }
            this.saveTodoTask(workOrder);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> workOrderAdminList(String searchTerm, String handleType, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
            Page<WorkOrderEntity> pageList = workOrderRepository.findByHandleType(handleType, pageable);
            map.put("currpage", page);
            map.put("totalpages", pageList.getTotalPages());
            map.put("total", pageList.getTotalElements());
            map.put("rows", pageList.getContent());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> workOrderList(String userId, String searchTerm, String handleType, Integer page,
        Integer rows) {
        Y9LoginUserHolder.setTenantId(myTenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
            Page<WorkOrderEntity> pageList =
                workOrderRepository.findByUserIdAndHandleType(userId, handleType, pageable);
            map.put("currpage", page);
            map.put("totalpages", pageList.getTotalPages());
            map.put("total", pageList.getTotalElements());
            map.put("rows", pageList.getContent());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

}
