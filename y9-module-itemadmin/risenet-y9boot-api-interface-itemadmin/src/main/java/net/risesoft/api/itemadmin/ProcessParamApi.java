package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流程变量
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessParamApi {

    /**
     * 根据流程实例id删除流程变量
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/deleteByPprocessInstanceId")
    Y9Result<Object> deleteByPprocessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 根据流程实例查找流程数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 流程数据对象
     */
    @GetMapping("/findByProcessInstanceId")
    Y9Result<ProcessParamModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 根据流程序列号查找流程数据
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 流程数据对象
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<ProcessParamModel> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存或更新流程数据
     *
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessParamModel processParam);

    /**
     * 更新定制流程状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param isCustomItem 是否定制流程
     * @return{@code Y9Result<Object>} 通用请求返回对象
     *
     * @since 9.6.6
     */
    @PostMapping("/updateCustomItem")
    Y9Result<Object> updateCustomItem(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("isCustomItem") boolean isCustomItem);
}
