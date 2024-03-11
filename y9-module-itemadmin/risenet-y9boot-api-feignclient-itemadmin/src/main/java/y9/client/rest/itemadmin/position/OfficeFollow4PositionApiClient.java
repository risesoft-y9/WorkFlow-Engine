package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.model.itemadmin.OfficeFollowModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OfficeFollow4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/officeFollow4Position")
public interface OfficeFollow4PositionApiClient extends OfficeFollow4PositionApi {

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/countByProcessInstanceId")
    public int countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId
     * @param processInstanceId
     */
    @Override
    @PostMapping("/deleteByProcessInstanceId")
    public void deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 取消关注
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceIds
     * @return
     */
    @Override
    @PostMapping("/delOfficeFollow")
    public Map<String, Object> delOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceIds") String processInstanceIds);

    /**
     * 获取我的关注数量
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    @Override
    @GetMapping("/getFollowCount")
    public int getFollowCount(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    @Override
    @GetMapping("/getFollowListBySystemName")
    public Map<String, Object> getFollowListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName,
        @RequestParam("searchName") String searchName, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取关注列表
     *
     * @param tenantId
     * @param positionId
     * @param searchName
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/getOfficeFollowList")
    public Map<String, Object> getOfficeFollowList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 保存办件关注信息
     *
     * @param tenantId
     * @param officeFollow
     * @return
     */
    @Override
    @PostMapping(value = "/saveOfficeFollow", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestBody OfficeFollowModel officeFollow);

    /**
     * 更新标题
     *
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     */
    @Override
    @PostMapping("/updateTitle")
    public void updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);

}
