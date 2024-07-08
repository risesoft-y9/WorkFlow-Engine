package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 我的关注接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/officeFollow4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class OfficeFollowApiImpl implements OfficeFollow4PositionApi {

    private final OfficeFollowService officeFollowService;

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是关注数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByProcessInstanceId(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int count = officeFollowService.countByProcessInstanceId(processInstanceId);
        return Y9Result.success(count);
    }

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.deleteByProcessInstanceId(processInstanceId);
        return Y9Result.successMsg("删除关注成功");
    }

    /**
     * 取消关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceIds 流程实例id列表
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delOfficeFollow(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        officeFollowService.delOfficeFollow(processInstanceIds);
        return Y9Result.successMsg("取消关注成功");
    }

    /**
     * 获取我的关注数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是我的关注数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getFollowCount(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int count = officeFollowService.getFollowCount();
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @param searchName 搜索词
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeFollowModel>} 通用分页请求返回对象 - rows 是关注模型信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeFollowModel> getFollowListBySystemName(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String systemName, String searchName, @RequestParam int page,
        @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.getFollowListBySystemName(systemName, searchName, page, rows);
    }

    /**
     * 获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索词
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeFollowModel>} 通用分页请求返回对象 - rows 是关注模型信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeFollowModel> getOfficeFollowList(@RequestParam String tenantId, @RequestParam String positionId,
        String searchName, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.getOfficeFollowList(searchName, page, rows);
    }

    /**
     * 保存办件关注信息
     *
     * @param tenantId 租户id
     * @param officeFollowModel 关注信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOfficeFollow(@RequestParam String tenantId,
        @RequestBody OfficeFollowModel officeFollowModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeFollow officeFollow = new OfficeFollow();
        if (null != officeFollowModel) {
            Y9BeanUtil.copyProperties(officeFollowModel, officeFollow);
        }
        officeFollowService.saveOfficeFollow(officeFollow);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 更新标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateTitle(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.updateTitle(processInstanceId, documentTitle);
        return Y9Result.successMsg("更新成功");
    }
}
