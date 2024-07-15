package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Opinion;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.OpinionService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 意见接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/opinion4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionApiImpl implements Opinion4PositionApi {

    private final OpinionService opinionService;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final PersonApi personManager;

    private final PositionApi positionManager;

    /**
     * 检查当前taskId任务节点是否已经签写意见
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
        Boolean result = opinionService.checkSignOpinion(processSerialNumber, taskId);
        return Y9Result.success(result);
    }

    /**
     * 获取意见框历史记录数量
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是意见框历史记录数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countOpinionHistory(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = opinionService.countOpinionHistory(processSerialNumber, opinionFrameMark);
        return Y9Result.success(count);
    }

    /**
     * 删除意见
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
        opinionService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是事项意见框绑定信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam String tenantId,
        @RequestParam String itemId, String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBindModel> list = new ArrayList<>();
        List<String> opinionFrameList = itemOpinionFrameBindService.getBindOpinionFrame(itemId, processDefinitionId);
        for (String opinionFrame : opinionFrameList) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            model.setOpinionFrameMark(opinionFrame);
            list.add(model);
        }
        return Y9Result.success(list);
    }

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionModel> getById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Opinion opinion = opinionService.findOne(id);
        OpinionModel opinionModel = new OpinionModel();
        if (opinion != null) {
            Y9BeanUtil.copyProperties(opinion, opinionModel);
        }
        return Y9Result.success(opinionModel);
    }

    /**
     * 获取意见框历史记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<List<OpinionHistoryModel>>} 通用请求返回对象 - data 是历史意见列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OpinionHistoryModel>> opinionHistoryList(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<OpinionHistoryModel> opinionHistoryModelList =
            opinionService.opinionHistoryList(processSerialNumber, opinionFrameMark);
        return Y9Result.success(opinionHistoryModelList);
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
     * @param itemId 事项id
     * @param taskDefinitionKey 任务定义key
     * @param activitiUser 人员id
     * @param orderByUser 是否根据岗位排序 1：按岗位排序号排序
     * @return {@code Y9Result<List<OpinionListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OpinionListModel>> personCommentList(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processSerialNumber, String taskId,
        @RequestParam String itembox, @RequestParam String opinionFrameMark, @RequestParam String itemId,
        String taskDefinitionKey, String activitiUser, String orderByUser) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<OpinionListModel> opinionList = opinionService.personCommentList(processSerialNumber, taskId, itembox,
            opinionFrameMark, itemId, taskDefinitionKey, activitiUser, orderByUser);
        return Y9Result.success(opinionList);
    }

    /**
     * 保存意见
     *
     * @param tenantId 租户id
     * @param opinionModel 意见信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> save(@RequestParam String tenantId, @RequestBody OpinionModel opinionModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Opinion opinion = ItemAdminModelConvertUtil.opinionModel2Opinion(opinionModel);
        opinionService.save(opinion);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param opinionModel 意见信息
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionModel> saveOrUpdate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String positionId, @RequestBody OpinionModel opinionModel) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Opinion opinion = new Opinion();
        Y9BeanUtil.copyProperties(opinionModel, opinion);
        opinion = opinionService.saveOrUpdate(opinion);
        Y9BeanUtil.copyProperties(opinion, opinionModel);
        return Y9Result.success(opinionModel);
    }

    @Override
    public Y9Result<Object> updateOpinion(String tenantId, String id, String content) {
        Y9LoginUserHolder.setTenantId(tenantId);
        opinionService.updateOpinion(id, content);
        return Y9Result.success();
    }
}
