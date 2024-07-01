package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = "/services/rest/opinion4Position")
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
     * @return Y9Result<Boolean>
     */
    @Override
    public Y9Result<Boolean> checkSignOpinion(String tenantId, String userId, String processSerialNumber,
        String taskId) {
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
     * @return Y9Result<Integer>
     */
    @Override
    public Y9Result<Integer> countOpinionHistory(String tenantId, String processSerialNumber, String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = opinionService.countOpinionHistory(processSerialNumber, opinionFrameMark);
        return Y9Result.success(count);
    }

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @Override
    public Y9Result<Object> delete(String tenantId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        opinionService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List<ItemOpinionFrameBindModel>>
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(String tenantId, String itemId,
        String processDefinitionId) {
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
     * @return Y9Result<OpinionModel>
     */
    @Override
    public Y9Result<OpinionModel> getById(String tenantId, String id) {
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
     * @return Y9Result<List<OpinionHistoryModel>>
     */
    @Override
    public Y9Result<List<OpinionHistoryModel>> opinionHistoryList(String tenantId, String processSerialNumber,
        String opinionFrameMark) {
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
     * @return Y9Result<List<OpinionListModel>>
     */
    @Override
    public Y9Result<List<OpinionListModel>> personCommentList(String tenantId, String userId,
        String processSerialNumber, String taskId, String itembox, String opinionFrameMark, String itemId,
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
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @Override
    public Y9Result<Object> save(String tenantId, @RequestBody OpinionModel opinionModel) throws Exception {
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
     * @return Y9Result<OpinionModel>
     * @throws Exception Exception
     */
    @Override
    public Y9Result<OpinionModel> saveOrUpdate(String tenantId, String userId, String positionId,
        @RequestBody OpinionModel opinionModel) throws Exception {
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
}
