package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ReceiveDeptAndPersonApi;
import net.risesoft.model.platform.Person;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ReceiveDeptAndPersonApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/receiveDeptAndPerson")
public interface ReceiveDeptAndPersonApiClient extends ReceiveDeptAndPersonApi {

    /**
     * 根据name模糊搜索收发单位
     *
     * @param tenantId 租户id
     * @param name 搜索名称
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/findByDeptNameLike")
    List<Map<String, Object>> findByDeptNameLike(@RequestParam("tenantId") String tenantId,
        @RequestParam("name") String name);

    /**
     * 获取所有收发单位
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping("/getReceiveDeptTree")
    List<Map<String, Object>> getReceiveDeptTree(@RequestParam("tenantId") String tenantId);

    /**
     * 
     * Description: 获取所有收发单位
     * 
     * @param tenantId
     * @param orgUnitId
     * @param name
     * @return
     */
    @Override
    @GetMapping("/getReceiveDeptTreeById")
    List<Map<String, Object>> getReceiveDeptTreeById(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("name") String name);

    /**
     * 
     * Description: 根据收发单位id,获取人员集合
     * 
     * @param tenantId
     * @param deptId
     * @return
     */
    @Override
    @GetMapping("/getSendReceiveByDeptId")
    public List<Person> getSendReceiveByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId);

    /**
     * 根据人员id,获取对应的收发单位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getSendReceiveByUserId")
    public List<Map<String, Object>> getSendReceiveByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);
}
