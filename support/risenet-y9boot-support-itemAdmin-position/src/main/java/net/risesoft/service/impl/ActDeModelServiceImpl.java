package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ActDeModel;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ActDeModelRepository;
import net.risesoft.service.ActDeModelService;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@Transactional(readOnly = true)
@Service(value = "actDeModelService")
public class ActDeModelServiceImpl implements ActDeModelService {

	@Autowired
	private ActDeModelRepository actDeModelRepository;

	@Transactional(readOnly = false)
	@Override
	public void deleteModel(String modelId) {
		actDeModelRepository.deleteById(modelId);
	}

	@Override
	public ActDeModel getModel(String modelId) {
		return actDeModelRepository.findById(modelId).orElse(null);
	}

	@Override
	public List<ActDeModel> getModelList() {
		return actDeModelRepository.findAllByOrderByLastUpdatedDesc();
	}

	@Override
	public void saveModel(ActDeModel actDeModel) {
		ActDeModel model = actDeModelRepository.findByModelKey(actDeModel.getModelKey());
		if (model != null) {
			model.setDescription(actDeModel.getDescription());
			model.setLastUpdated(actDeModel.getLastUpdated());
			model.setLastUpdatedBy(actDeModel.getLastUpdatedBy());
			model.setModelByte(actDeModel.getModelByte());
			model.setName(actDeModel.getName());
			actDeModelRepository.save(model);
			return;
		}
		actDeModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
		actDeModelRepository.save(actDeModel);
	}

}