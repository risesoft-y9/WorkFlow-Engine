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
	public boolean endByProcessInstanceId(String tenantId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.endByProcessInstanceId(processInstanceId);
	}

	@Override
	@PostMapping(value = "/endByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean endByProcessSerialNumber(String tenantId, String processSerialNumber) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.endByProcessSerialNumber(processSerialNumber);
	}

	@Override
	@GetMapping(value = "/findByProcessInstanceIdAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ActRuDetailModel> findByProcessInstanceIdAndStatus(String tenantId, String processInstanceId, int status) {
		Y9LoginUserHolder.setTenantId(tenantId);
		List<ActRuDetail> actRuDetailList = actRuDetailService.findByProcessInstanceIdAndStatus(processInstanceId, status);
		List<ActRuDetailModel> modelList = new ArrayList<>();
		ActRuDetailModel model = null;
		for (ActRuDetail actRuDetail : actRuDetailList) {
			model = new ActRuDetailModel();
			Y9BeanUtil.copyProperties(actRuDetail, model);
			modelList.add(model);
		}
		return modelList;
	}

	@Override
	@GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ActRuDetailModel> findByProcessSerialNumber(String tenantId, String processSerialNumber) {
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

	@Override
	@GetMapping(value = "/findByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
	public ActRuDetailModel findByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee) {
		Y9LoginUserHolder.setTenantId(tenantId);
		ActRuDetail actRuDetail = actRuDetailService.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
		ActRuDetailModel model = new ActRuDetailModel();
		Y9BeanUtil.copyProperties(actRuDetail, model);
		return model;
	}

	@Override
	@GetMapping(value = "/findByProcessSerialNumberAndStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ActRuDetailModel> findByProcessSerialNumberAndStatus(String tenantId, String processSerialNumber, int status) {
		Y9LoginUserHolder.setTenantId(tenantId);
		List<ActRuDetail> actRuDetailList = actRuDetailService.findByProcessSerialNumberAndStatus(processSerialNumber, status);
		List<ActRuDetailModel> modelList = new ArrayList<>();
		ActRuDetailModel model = null;
		for (ActRuDetail actRuDetail : actRuDetailList) {
			model = new ActRuDetailModel();
			Y9BeanUtil.copyProperties(actRuDetail, model);
			modelList.add(model);
		}
		return modelList;
	}

	@Override
	@PostMapping(value = "/recoveryByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean recoveryByProcessInstanceId(String tenantId, String processSerialNumber) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.recoveryByProcessSerialNumber(processSerialNumber);
	}

	@Override
	@PostMapping(value = "/removeByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean removeByProcessInstanceId(String tenantId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.removeByProcessInstanceId(processInstanceId);
	}

	@Override
	@PostMapping(value = "/removeByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean removeByProcessSerialNumber(String tenantId, String processSerialNumber) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.removeByProcessSerialNumber(processSerialNumber);
	}

	@Override
	@PostMapping(value = "/removeByProcessSerialNumberAndAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean removeByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.removeByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
	}

	@Override
	@PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean saveOrUpdate(String tenantId,@RequestBody ActRuDetailModel actRuDetailModel) {
		Y9LoginUserHolder.setTenantId(tenantId);
		ActRuDetail actRuDetail = new ActRuDetail();
		Y9BeanUtil.copyProperties(actRuDetailModel, actRuDetail);
		return actRuDetailService.saveOrUpdate(actRuDetail);
	}

	@Override
	@PostMapping(value = "/syncByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean syncByProcessInstanceId(String tenantId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return actRuDetailService.syncByProcessInstanceId(processInstanceId);
	}
}
