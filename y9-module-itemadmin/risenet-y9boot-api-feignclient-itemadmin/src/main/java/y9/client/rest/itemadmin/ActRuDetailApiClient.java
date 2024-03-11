package y9.client.rest.itemadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ActRuDetailApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/actRuDetail")
public interface ActRuDetailApiClient extends ActRuDetailApi {

    /**
     * 标记流程为办结
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/endByProcessInstanceId")
    boolean endByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 标记流程为办结
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @PostMapping("/endByProcessSerialNumber")
    boolean endByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId
     * @param processInstanceId
     * @param status 0为待办，1位在办
     * @return
     */
    @Override
    @GetMapping("/findByProcessInstanceIdAndStatus")
    List<ActRuDetailModel> findByProcessInstanceIdAndStatus(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") int status);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumber")
    List<ActRuDetailModel> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumberAndAssignee")
    ActRuDetailModel findByProcessSerialNumberAndAssignee(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("assignee") String assignee);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @param status 0为待办，1位在办
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumberAndStatus")
    List<ActRuDetailModel> findByProcessSerialNumberAndStatus(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("status") int status);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/recoveryByProcessInstanceId")
    boolean recoveryByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/removeByProcessInstanceId")
    boolean removeByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return
     */
    @Override
    @PostMapping("/removeByProcessSerialNumber")
    boolean removeByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 
     * Description: 删除某个参与人的办件详情
     * 
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param assignee 办理人id
     * @return
     */
    @Override
    @PostMapping("/removeByProcessSerialNumberAndAssignee")
    boolean removeByProcessSerialNumberAndAssignee(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("assignee") String assignee);

    /**
     * 保存或者更新
     *
     * @param tenantId
     * @param actRuDetailModel
     * @return
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestBody ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/syncByProcessInstanceId")
    boolean syncByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);
}
