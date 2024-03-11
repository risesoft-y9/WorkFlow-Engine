package y9.client.rest.itemadmin.position;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemRole4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemRole4Position")
public interface ItemRole4PositionApiClient extends ItemRole4PositionApi {

    /**
     * 
     * Description: 根据id获取发送选人
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param id
     * @param principalType
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findCsUser")
    public List<Map<String, Object>> findCsUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("id") String id, @RequestParam("principalType") Integer principalType,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 查询委办局下的部门
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param principalType
     * @return
     */
    @Override
    @GetMapping("/findCsUserBureau")
    public List<Map<String, Object>> findCsUserBureau(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("principalType") Integer principalType);

    /**
     * 
     * Description: 抄送搜索
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findCsUserSearch")
    public List<Map<String, Object>> findCsUserSearch(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("name") String name, @RequestParam("principalType") Integer principalType,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 获取发送人
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param principalType
     * @param id
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findPermUser")
    public List<Map<String, Object>> findPermUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("principalType") Integer principalType,
        @RequestParam("id") String id, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 发送选人搜索
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param name
     * @param principalType
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findPermUserByName")
    public List<Map<String, Object>> findPermUserByName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("name") String name, @RequestParam("principalType") Integer principalType,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 获取发送人（收发单位）
     * 
     * @param tenantId
     * @param positionId
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findPermUserSendReceive")
    public List<Map<String, Object>> findPermUserSendReceive(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("id") String id);

    /**
     * 获取组织机构树
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 组织架构id
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @param name 人员名称
     *
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getOrgTree")
    public List<Map<String, Object>> getOrgTree(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("id") String id,
        @RequestParam("treeType") OrgTreeTypeEnum treeType, @RequestParam("name") String name);
}
