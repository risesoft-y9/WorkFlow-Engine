package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.OrganWordApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OrganWordApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/organWord")
public interface OrganWordApiClient extends OrganWordApi {

    /**
     * 检查编号是否已经被使用了
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param characterValue 机关代字
     * @param custom 机关代字标志
     * @param year 文号年份
     * @param numberTemp numberTemp
     * @param itemId 事项id
     * @param common common
     * @param processSerialNumber 流程编号
     * @return Integer
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/checkNumberStr")
    public Integer checkNumberStr(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("characterValue") String characterValue, @RequestParam("custom") String custom, @RequestParam("year") Integer year, @RequestParam("numberTemp") Integer numberTemp,
        @RequestParam("itemId") String itemId, @RequestParam("common") Integer common, @RequestParam("processSerialNumber") String processSerialNumber) throws Exception;

    /**
     * 判断机构代字custom在某个流程实例中是否已经编号,没有编号的话就查找有权限的编号的机关代字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     *
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/exist")
    public Map<String, Object> exist(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("custom") String custom, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("itembox") String itembox) throws Exception;

    /**
     * 
     * Description: 查找有权限的机构代字
     * 
     * @param tenantId
     * @param userId
     * @param custom
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/findByCustom")
    public List<Map<String, Object>> findByCustom(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("custom") String custom, @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey) throws Exception;

    /**
     * 获取编号
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param year 文号年份
     * @param common common
     * @param itemId 事项id
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getNumber")
    public Map<String, Object> getNumber(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("custom") String custom, @RequestParam("characterValue") String characterValue, @RequestParam("year") Integer year, @RequestParam("common") Integer common,
        @RequestParam("itemId") String itemId) throws Exception;

    /**
     * 获取编号的数字
     *
     * @param tenantId
     * @param userId
     * @param custom
     * @param characterValue
     * @param year
     * @param common
     * @param itemId
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getNumberOnly")
    public Integer getNumberOnly(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("custom") String custom, @RequestParam("characterValue") String characterValue, @RequestParam("year") Integer year, @RequestParam("common") Integer common,
        @RequestParam("itemId") String itemId) throws Exception;

}
