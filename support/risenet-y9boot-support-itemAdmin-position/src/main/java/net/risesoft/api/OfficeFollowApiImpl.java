package net.risesoft.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.model.itemadmin.OfficeFollowModel;
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
@RequestMapping(value = "/services/rest/officeFollow4Position")
public class OfficeFollowApiImpl implements OfficeFollow4PositionApi {

    @Autowired
    private OfficeFollowService officeFollowService;

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return int
     */
    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByProcessInstanceId(String tenantId, String positionId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.countByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.deleteByProcessInstanceId(processInstanceId);
    }

    /**
     * 取消关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delOfficeFollow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delOfficeFollow(String tenantId, String positionId, String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.delOfficeFollow(processInstanceIds);
    }

    /**
     * 获取我的关注数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @Override
    @GetMapping(value = "/getFollowCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getFollowCount(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.getFollowCount();
    }

    /**
     * 获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getOfficeFollowList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getOfficeFollowList(String tenantId, String positionId, String searchName, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return officeFollowService.getOfficeFollowList(searchName, page, rows);
    }

    /**
     * 保存办件关注信息
     *
     * @param tenantId 租户id
     * @param officeFollow 关注信息
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/saveOfficeFollow", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveOfficeFollow(String tenantId, @RequestBody OfficeFollowModel officeFollowModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeFollow officeFollow = new OfficeFollow();
        if (null != officeFollowModel) {
            Y9BeanUtil.copyProperties(officeFollowModel, officeFollow);
        }
        return officeFollowService.saveOfficeFollow(officeFollow);
    }

    /**
     * 更新标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     */
    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeFollowService.updateTitle(processInstanceId, documentTitle);
    }
}
