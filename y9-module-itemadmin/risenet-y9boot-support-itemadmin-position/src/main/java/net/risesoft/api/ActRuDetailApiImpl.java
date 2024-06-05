package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 流转信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/actRuDetail")
public class ActRuDetailApiImpl implements ActRuDetailApi {

    private final ActRuDetailService actRuDetailService;

    /**
     * 根据流程实例id标记流程为办结
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return Boolean
     */
    @Override
    @PostMapping(value = "/endByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean endByProcessInstanceId(@RequestParam @NotBlank String tenantId,
                                          @RequestParam @NotBlank String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.endByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据流程编号标记流程为办结
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @return Boolean
     */
    @Override
    @PostMapping(value = "/endByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean endByProcessSerialNumber(@RequestParam @NotBlank String tenantId,
                                            @RequestParam @NotBlank String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.endByProcessSerialNumber(processSerialNumber);
    }

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @param status            0为待办，1位在办
     * @return List<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceIdAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActRuDetailModel> findByProcessInstanceIdAndStatus(@RequestParam @NotBlank String tenantId,
                                                                   @RequestParam @NotBlank String processInstanceId, int status) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ActRuDetail> actRuDetailList =
                actRuDetailService.findByProcessInstanceIdAndStatus(processInstanceId, status);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model = null;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return modelList;
    }

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @return List<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActRuDetailModel> findByProcessSerialNumber(@RequestParam @NotBlank String tenantId,
                                                            @RequestParam @NotBlank String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ActRuDetail> actRuDetailList = actRuDetailService.findByProcessSerialNumber(processSerialNumber);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model = null;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return modelList;
    }

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param assignee            办理人id
     * @return ActRuDetailModel
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ActRuDetailModel findByProcessSerialNumberAndAssignee(@RequestParam @NotBlank String tenantId,
                                                                 @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail =
                actRuDetailService.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        ActRuDetailModel model = new ActRuDetailModel();
        Y9BeanUtil.copyProperties(actRuDetail, model);
        return model;
    }

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param status              0为待办，1位在办
     * @return List<ActRuDetailModel>
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumberAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActRuDetailModel> findByProcessSerialNumberAndStatus(@RequestParam @NotBlank String tenantId,
                                                                     @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotNull int status) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ActRuDetail> actRuDetailList =
                actRuDetailService.findByProcessSerialNumberAndStatus(processSerialNumber, status);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model = null;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return modelList;
    }

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/recoveryByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean recoveryByProcessInstanceId(@RequestParam @NotBlank String tenantId,
                                               @RequestParam @NotBlank String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.recoveryByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据流程实例id删除整个流程的办件详情
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/removeByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeByProcessInstanceId(@RequestParam @NotBlank String tenantId,
                                             @RequestParam @NotBlank String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.removeByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据流程编号删除整个流程的办件详情
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @return boolean
     */
    @Override
    @PostMapping(value = "/removeByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeByProcessSerialNumber(@RequestParam @NotBlank String tenantId,
                                               @RequestParam @NotBlank String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.removeByProcessSerialNumber(processSerialNumber);
    }

    /**
     * 删除某个参与人的办件详情
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param assignee            办理人id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/removeByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeByProcessSerialNumberAndAssignee(@RequestParam @NotBlank String tenantId,
                                                          @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.removeByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
    }

    /**
     * 保存或者更新
     *
     * @param tenantId         租户id
     * @param actRuDetailModel 详情对象
     * @return boolean
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveOrUpdate(@RequestParam @NotBlank String tenantId, @RequestBody ActRuDetailModel actRuDetailModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail = new ActRuDetail();
        Y9BeanUtil.copyProperties(actRuDetailModel, actRuDetail);
        return actRuDetailService.saveOrUpdate(actRuDetail);
    }

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/syncByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean syncByProcessInstanceId(@RequestParam @NotBlank String tenantId,@RequestParam @NotBlank String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return actRuDetailService.syncByProcessInstanceId(processInstanceId);
    }
}
