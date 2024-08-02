package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 我的关注接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeFollowApi {

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是关注数量
     * @since 9.6.6
     */
    @GetMapping("/countByProcessInstanceId")
    Y9Result<Integer> countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 取消关注
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceIds 流程实例id列表
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delOfficeFollow")
    Y9Result<Object> delOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceIds") String processInstanceIds);

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessInstanceId")
    Y9Result<Object> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取我的关注数量
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是我的关注数量
     * @since 9.6.6
     */
    @GetMapping("/getFollowCount")
    Y9Result<Integer> getFollowCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 根据系统名称获取关注列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param systemName 系统名称
     * @param searchName 搜索词
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeFollowModel>} 通用分页请求返回对象 - rows 是关注模型信息
     * @since 9.6.6
     */
    @GetMapping("/getFollowListBySystemName")
    Y9Page<OfficeFollowModel> getFollowListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("systemName") String systemName,
        @RequestParam(value = "searchName", required = false) String searchName, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 获取关注列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param searchName 搜索词
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeFollowModel>} 通用分页请求返回对象 - rows 是关注模型信息
     * @since 9.6.6
     */
    @GetMapping("/getOfficeFollowList")
    Y9Page<OfficeFollowModel> getOfficeFollowList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "searchName", required = false) String searchName, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 保存办件关注信息
     *
     * @param tenantId 租户id
     * @param officeFollowModel 关注信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOfficeFollow", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOfficeFollow(@RequestParam("tenantId") String tenantId,
        @RequestBody OfficeFollowModel officeFollowModel);

    /**
     * 更新标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateTitle")
    Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);

}
