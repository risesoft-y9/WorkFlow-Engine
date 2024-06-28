package net.risesoft.api.itemadmin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项接口绑定
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@Validated
public interface ItemInterfaceApi {

    /**
     * 获取任务绑定接口
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param processDefinitionId 流程定义id
     * @param condition 执行条件
     * @return Y9Result<List<InterfaceModel>>
     */
    @GetMapping("/getInterface")
    Y9Result<List<InterfaceModel>> getInterface(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("itemId") @NotBlank String itemId, @RequestParam("taskKey") String taskKey,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("condition") String condition);

    /**
     * 获取接口绑定参数，包括请求参数和响应参数
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @return Y9Result<List<InterfaceParamsModel>>
     */
    @GetMapping("/getInterfaceParams")
    Y9Result<List<InterfaceParamsModel>> getInterfaceParams(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("itemId") @NotBlank String itemId, @RequestParam("interfaceId") @NotBlank String interfaceId);
}
