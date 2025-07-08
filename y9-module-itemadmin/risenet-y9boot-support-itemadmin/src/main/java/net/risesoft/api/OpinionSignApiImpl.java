package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.opinion.OpinionSignApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.opinion.OpinionSign;
import net.risesoft.model.itemadmin.OpinionSignListModel;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OpinionSignService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 会签意见接口
 *
 * @author qinman
 * @date 2024/12/16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/opinionSign", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionSignApiImpl implements OpinionSignApi {

    private final OpinionSignService opinionSignService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    /**
     * 验证当前taskId任务节点是否已经签写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是是否已经签写意见
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> checkSignOpinion(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        // Boolean result = opinionSignService.checkSignOpinion(processSerialNumber, taskId);
        return Y9Result.success(true);
    }

    /**
     * 删除意见数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delete(@RequestParam String tenantId, @RequestParam String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        opinionSignService.deleteById(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据id获取意见数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionSignModel> getById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OpinionSign opinionSign = opinionSignService.findById(id);
        OpinionSignModel model = new OpinionSignModel();
        if (opinionSign != null) {
            Y9BeanUtil.copyProperties(opinionSign, model);
            return Y9Result.success(model);
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 获取个人意见列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<List<OpinionListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OpinionSignListModel>> personCommentList(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String positionId, @RequestParam String processSerialNumber,
        @RequestParam String signDeptDetailId, @RequestParam String itembox,
        @RequestParam(required = false) String taskId, @RequestParam String opinionFrameMark) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setOrgUnitId(positionId);
        List<OpinionSignListModel> opinionList =
            opinionSignService.list(processSerialNumber, signDeptDetailId, itembox, taskId, opinionFrameMark);
        return Y9Result.success(opinionList);
    }

    /**
     * 保存或更新意见信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param opinionSignModel 意见信息
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionSignModel> saveOrUpdate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String orgUnitId, @RequestBody OpinionSignModel opinionSignModel) throws Exception {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        OpinionSign opinionSign = new OpinionSign();
        Y9BeanUtil.copyProperties(opinionSignModel, opinionSign);
        opinionSign = opinionSignService.saveOrUpdate(opinionSign);
        Y9BeanUtil.copyProperties(opinionSign, opinionSignModel);
        return Y9Result.success(opinionSignModel);
    }
}
