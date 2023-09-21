package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ActDeModelApi;
import net.risesoft.model.itemadmin.ActDeModel;
import net.risesoft.service.ActDeModelService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

import jakarta.validation.constraints.NotBlank;

/**
 * 流程设计模型接口
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@RestController
@RequestMapping(value = "/services/rest/actDeModel", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActDeModelApiImpl implements ActDeModelApi {

	@Autowired
	private ActDeModelService actDeModelService;

	@Override
	public void deleteModel(String tenantId, String modelId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		actDeModelService.deleteModel(modelId);
	}

	@Override
	public ActDeModel getModel(String tenantId, @NotBlank String modelId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		net.risesoft.entity.ActDeModel actDeModel = actDeModelService.getModel(modelId);
		ActDeModel model = new ActDeModel();
		Y9BeanUtil.copyProperties(actDeModel, model);
		return model;
	}

	@Override
	public List<ActDeModel> getModelList(String tenantId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		List<ActDeModel> res_list = new ArrayList<>();
		List<net.risesoft.entity.ActDeModel> list = actDeModelService.getModelList();
		for (net.risesoft.entity.ActDeModel actDeModel : list) {
			ActDeModel model = new ActDeModel();
			Y9BeanUtil.copyProperties(actDeModel, model);
			res_list.add(model);
		}
		return res_list;
	}

	@Override
	public void saveModel(@NotBlank String tenantId, ActDeModel newModel) {
		Y9LoginUserHolder.setTenantId(tenantId);
		net.risesoft.entity.ActDeModel actDeModel = new net.risesoft.entity.ActDeModel();
		Y9BeanUtil.copyProperties(newModel, actDeModel);
		actDeModelService.saveModel(actDeModel);

	}

}
