package net.risesoft.api.itemadmin.position;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeFollow4PositionApi {

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return int
     */
    @GetMapping("/countByProcessInstanceId")
    Y9Result<Integer> countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 取消关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceIds 流程实例ids
     * @return Map&lt;String, Object&gt;
     */
    @PostMapping("/delOfficeFollow")
    Y9Result<Object> delOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceIds") String processInstanceIds);

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return
     */
    @PostMapping("/deleteByProcessInstanceId")
    Y9Result<Object> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取我的关注数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @GetMapping("/getFollowCount")
    Y9Result<Integer> getFollowCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 根据系统名获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @param searchName 搜索内容
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/getFollowListBySystemName")
    Y9Page<OfficeFollowModel> getFollowListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName,
        @RequestParam("searchName") String searchName, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索内容
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getOfficeFollowList")
    Y9Page<OfficeFollowModel> getOfficeFollowList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 保存办件关注信息
     *
     * @param tenantId 租户id
     * @param officeFollow 办件关注信息
     * @return Map&lt;String, Object&gt;
     */
    @PostMapping(value = "/saveOfficeFollow", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestBody OfficeFollowModel officeFollow);

    /**
     * 更新标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @return
     */
    @PostMapping("/updateTitle")
    Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);

}
