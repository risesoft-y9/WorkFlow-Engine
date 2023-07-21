package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/processParam")
public class ProcessParamApiImpl implements ProcessParamApi {

	@Autowired
	private ProcessParamService processParamService;

	@Override
	@PostMapping(value = "/deleteByPprocessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteByPprocessInstanceId(String tenantId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		processParamService.deleteByPprocessInstanceId(processInstanceId);
	}

	@Override
	@GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProcessParamModel findByProcessInstanceId(String tenantId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
		ProcessParamModel pp = null;
		if (null != processParam) {
			pp = new ProcessParamModel();
			Y9BeanUtil.copyProperties(processParam, pp);
		}
		return pp;
	}

	@Override
	@GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProcessParamModel findByProcessSerialNumber(String tenantId, String processSerialNumber) {
		Y9LoginUserHolder.setTenantId(tenantId);
		ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
		ProcessParamModel pp = null;
		if (null != processParam) {
			pp = new ProcessParamModel();
			Y9BeanUtil.copyProperties(processParam, pp);
		}
		return pp;
	}

	@Override
	@PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveOrUpdate(String tenantId,@RequestBody ProcessParamModel processParam) {
		Y9LoginUserHolder.setTenantId(tenantId);
		ProcessParam pp = new ProcessParam();
		Y9BeanUtil.copyProperties(processParam, pp);
		processParamService.saveOrUpdate(pp);
	}

	@Override
	@PostMapping(value = "/updateCustomItem", produces = MediaType.APPLICATION_JSON_VALUE)
	public void updateCustomItem(String tenantId, String processSerialNumber, boolean b) {
		Y9LoginUserHolder.setTenantId(tenantId);
		processParamService.updateCustomItem(processSerialNumber, b);
	}
}
