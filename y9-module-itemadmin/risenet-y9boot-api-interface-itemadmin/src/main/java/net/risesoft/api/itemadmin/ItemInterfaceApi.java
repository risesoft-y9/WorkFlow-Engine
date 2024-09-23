package net.risesoft.api.itemadmin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.model.itemadmin.TaskTimeConfModel;
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
     * 获取事项接口信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param processDefinitionId 流程定义id
     * @param condition 执行条件
     * @return {@code Y9Result<List<InterfaceModel>>} 通用请求返回对象 - data 是接口绑定列表
     * @since 9.6.6
     */
    @GetMapping("/getInterface")
    Y9Result<List<InterfaceModel>> getInterface(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("itemId") @NotBlank String itemId,
        @RequestParam(value = "taskKey", required = false) String taskKey,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("condition") String condition);

    /**
     * 根据事项id获取绑定接口
     * 
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<List<InterfaceModel>>} 通用请求返回对象 - data 是接口绑定列表
     * @since 9.6.6
     */
    @GetMapping("/getInterfaceList")
    Y9Result<List<InterfaceModel>> getInterfaceList(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("itemId") @NotBlank String itemId);

    /**
     * 获取事项接口参数信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @return {@code Y9Result<List<InterfaceParamsModel>>} 通用请求返回对象 - data 是接口绑定参数列表
     * @since 9.6.6
     */
    @GetMapping("/getInterfaceParams")
    Y9Result<List<InterfaceParamsModel>> getInterfaceParams(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("itemId") @NotBlank String itemId, @RequestParam("interfaceId") @NotBlank String interfaceId);

    /**
     * 获取任务时间配置信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskKey 任务key
     * @return {@code Y9Result<TaskTimeConfModel>} 通用请求返回对象 - data 是任务时间配置信息
     * @since 9.6.6
     */
    @GetMapping("/getTaskTimeConf")
    Y9Result<TaskTimeConfModel> getTaskTimeConf(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("processDefinitionId") @NotBlank String processDefinitionId,
        @RequestParam("itemId") @NotBlank String itemId,
        @RequestParam(value = "taskKey", required = false) String taskKey);

}
