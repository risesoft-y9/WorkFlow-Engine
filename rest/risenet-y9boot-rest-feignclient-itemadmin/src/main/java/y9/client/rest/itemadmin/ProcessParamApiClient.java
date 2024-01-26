package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.model.itemadmin.ProcessParamModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ProcessParamApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/processParam")
public interface ProcessParamApiClient extends ProcessParamApi {

    /**
     * 根据流程实例id删除流程变量
     *
     * @param tenantId
     * @param processInstanceId
     */
    @Override
    @PostMapping("/deleteByPprocessInstanceId")
    void deleteByPprocessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 根据流程实例查找流程数据
     * 
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    ProcessParamModel findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 根据流程序列号查找流程数据
     * 
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumber")
    ProcessParamModel findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存或更新流程数据
     *
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestBody ProcessParamModel processParam);

    /**
     * 更新定制流程状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param b 状态
     */
    @Override
    @PostMapping("/updateCustomItem")
    void updateCustomItem(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("b") boolean b);
}
