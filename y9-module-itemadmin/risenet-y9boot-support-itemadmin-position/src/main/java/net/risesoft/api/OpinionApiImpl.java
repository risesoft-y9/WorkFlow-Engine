package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Opinion;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
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
     * @return Boolean
     */
    @Override
    @GetMapping(value = "/checkSignOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean checkSignOpinion(String tenantId, String userId, String processSerialNumber, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return opinionService.checkSignOpinion(processSerialNumber, taskId);
    }

    /**
     * 获取意见框历史记录数量
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return int
     */
    @Override
    @GetMapping(value = "/countOpinionHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countOpinionHistory(String tenantId, String processSerialNumber, String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return opinionService.countOpinionHistory(processSerialNumber, opinionFrameMark);
    }

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(String tenantId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        opinionService.delete(id);
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return List<String>
     */
    @Override
    @GetMapping(value = "/getBindOpinionFrame", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getBindOpinionFrame(String tenantId, String itemId, String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return itemOpinionFrameBindService.getBindOpinionFrame(itemId, processDefinitionId);
    }

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return OpinionModel
     */
    @Override
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel getById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Opinion opinion = opinionService.findOne(id);
        OpinionModel opinionModel = new OpinionModel();
        if (opinion != null) {
            Y9BeanUtil.copyProperties(opinion, opinionModel);
        }
        return opinionModel;
    }

    /**
     * 获取意见框历史记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return List<OpinionHistoryModel>
     */
    @Override
    @GetMapping(value = "/opinionHistoryList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OpinionHistoryModel> opinionHistoryList(String tenantId, String processSerialNumber,
        String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return opinionService.opinionHistoryList(processSerialNumber, opinionFrameMark);
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
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/personCommentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> personCommentList(String tenantId, String userId, String processSerialNumber,
        String taskId, String itembox, String opinionFrameMark, String itemId, String taskDefinitionKey,
        String activitiUser, String orderByUser) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return opinionService.personCommentList(processSerialNumber, taskId, itembox, opinionFrameMark, itemId,
            taskDefinitionKey, activitiUser, orderByUser);
    }

    /**
     * 保存意见
     *
     * @param tenantId 租户id
     * @param opinionModel 意见信息
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(String tenantId, @RequestBody OpinionModel opinionModel) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Opinion opinion = ItemAdminModelConvertUtil.opinionModel2Opinion(opinionModel);
        opinionService.save(opinion);
    }

    /**
     * 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param opinionModel 意见信息
     * @return OpinionModel
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel saveOrUpdate(String tenantId, String userId, String positionId,
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
        return opinionModel;
    }

}
