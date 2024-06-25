package net.risesoft.service.impl;

import java.util.List;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.ProcessModelService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Service(value = "processModelService")
public class ProcessModelServiceImpl implements ProcessModelService {

    private final ModelService modelService;

    private final RepositoryService repositoryService;

    @Override
    public void copyModel(String sourceTenantId, String targetTenantId, String modelKey) {
        /*
         * 查找原租户中的模型
         */
        FlowableTenantInfoHolder.setTenantId(sourceTenantId);
        String modelId = null;
        List<AbstractModel> sourceModelList = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        for (AbstractModel aModel : sourceModelList) {
            if (modelKey.equals(aModel.getKey())) {
                modelId = aModel.getId();
                break;
            }
        }
        Model sourceModel = modelService.getModel(modelId);
        /*
         * 切换租户
         */
        FlowableTenantInfoHolder.setTenantId(targetTenantId);
        /*
         * 判断目标租户是否存在该流程，不存在才新增
         */
        boolean has = false;
        List<AbstractModel> targetModelList = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        for (AbstractModel aModel : targetModelList) {
            if (modelKey.equals(aModel.getKey())) {
                has = true;
                break;
            }
        }
        if (!has) {
            /*
             * 复制流程
             */
            sourceModel.setId(null);
            sourceModel.setTenantId(targetTenantId);
            Model modelData = modelService.createModel(sourceModel, "管理员");
            /*
             * 部署流程
             */
            BpmnModel bpmnModel = modelService.getBpmnModel(modelData);
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            String processName = modelData.getName() + ".bpmn20.xml";
            repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
        }
    }
}
