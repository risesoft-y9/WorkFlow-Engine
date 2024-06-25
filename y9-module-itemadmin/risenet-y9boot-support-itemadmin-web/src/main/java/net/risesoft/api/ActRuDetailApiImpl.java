package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/actRuDetail")
public class ActRuDetailApiImpl implements ActRuDetailApi {

    @Autowired
    private ActRuDetailService actRuDetailService;

    @Override
    @PostMapping(value = "/endByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> endByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.endByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/endByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> endByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.endByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    @Override
    @GetMapping(value = "/findByProcessInstanceIdAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(String tenantId, String processInstanceId,
        int status) {
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
        return Y9Result.success(modelList);
    }

    @Override
    @GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ActRuDetail> actRuDetailList = actRuDetailService.findByProcessSerialNumber(processSerialNumber);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model = null;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    @Override
    @GetMapping(value = "/findByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<ActRuDetailModel> findByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber,
        String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail =
            actRuDetailService.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        ActRuDetailModel model = new ActRuDetailModel();
        Y9BeanUtil.copyProperties(actRuDetail, model);
        return Y9Result.success(model);
    }

    @Override
    @GetMapping(value = "/findByProcessSerialNumberAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(String tenantId,
        String processSerialNumber, int status) {
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
        return Y9Result.success(modelList);
    }

    @Override
    @PostMapping(value = "/recoveryByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> recoveryByProcessInstanceId(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.recoveryByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/removeByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> removeByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/removeByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> removeByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/removeByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> removeByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber,
        String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveOrUpdate(String tenantId, @RequestBody ActRuDetailModel actRuDetailModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail = new ActRuDetail();
        Y9BeanUtil.copyProperties(actRuDetailModel, actRuDetail);
        actRuDetailService.saveOrUpdate(actRuDetail);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/syncByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> syncByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.syncByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }
}
