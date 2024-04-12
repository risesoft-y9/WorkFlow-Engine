package net.risesoft.api;

import java.util.List;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.ProcessDataCopyApi;
import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * 流程定义数据复制接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/processDataCopy")
public class ProcessDataCopyApiImpl implements ProcessDataCopyApi {

    @Autowired
    ModelService modelService;

    @Autowired
    RepositoryService repositoryService;

    /**
     * 复制拷贝流程定义数据
     *
     * @param sourceTenantId 源租户id
     * @param targetTenantId 目标租户id
     * @param modelKey 定义key
     * @return
     */
    @Override
    @PostMapping(value = "/copyModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public void copyModel(@RequestParam String sourceTenantId, @RequestParam String targetTenantId, @RequestParam String modelKey) throws Exception {
        try {
            /**
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
            /**
             * 切换租户
             */
            FlowableTenantInfoHolder.setTenantId(targetTenantId);
            /**
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
                /**
                 * 复制流程
                 */
                sourceModel.setId(null);
                sourceModel.setTenantId(targetTenantId);
                Model modelData = modelService.createModel(sourceModel, "管理员");
                /**
                 * 部署流程
                 */
                BpmnModel bpmnModel = modelService.getBpmnModel(modelData);
                byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
                String processName = modelData.getName() + ".bpmn20.xml";
                repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("ProcessDataCopyApi copyModel error");
        }

    }

}
