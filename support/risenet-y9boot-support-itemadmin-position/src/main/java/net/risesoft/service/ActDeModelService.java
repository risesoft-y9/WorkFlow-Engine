package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ActDeModel;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
public interface ActDeModelService {

    /**
     * 删除模型
     *
     * @param modelId
     */
    void deleteModel(String modelId);

    /**
     * 获取模型信息
     *
     * @param modelId
     * @return
     */
    ActDeModel getModel(String modelId);

    /**
     * 获取模型列表
     *
     * @return
     */
    List<ActDeModel> getModelList();

    /**
     * 保存模型
     *
     * @param actDeModel
     */
    void saveModel(ActDeModel actDeModel);

}
