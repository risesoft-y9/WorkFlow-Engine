package net.risesoft.api.itemadmin;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActDeModel;

/**
 * 流程设计模型接口
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@Validated
public interface ActDeModelApi {

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     */
    @PostMapping("/deleteModel")
    public void deleteModel(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("modelId") @NotBlank String modelId);

    /**
     * 获取模型信息
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return
     */
    @GetMapping("/getModel")
    public ActDeModel getModel(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("modelId") @NotBlank String modelId);

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return
     */
    @GetMapping("/getModelList")
    public List<ActDeModel> getModelList(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 保存模型
     *
     * @param tenantId 租户id
     * @param newModel 模型信息
     * @return
     */
    public void saveModel(@RequestParam("tenantId") @NotBlank String tenantId, @RequestBody ActDeModel newModel);

}
