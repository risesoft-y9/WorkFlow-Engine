package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.Opinion;
import net.risesoft.model.itemadmin.OpinionHistoryModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OpinionService {

    /**
     * Description: 检查当前taskId任务节点是否已经签写意见
     *
     * @param processSerialNumber
     * @param taskId
     * @return
     */
    Boolean checkSignOpinion(String processSerialNumber, String taskId);

    /**
     * Description: 把老的意见框里面的意见复制到新的意见框里面去,如果老的意见框Id等于all,则把老的流程的所有的意见框的意见按照时间升序复制到新流程的制定的意见框中能
     *
     * @param oldProcessSerialNumber
     * @param oldOpinionFrameMark
     * @param newProcessSerialNumber
     * @param newOpinionFrameMark
     * @param newProcessInstanceId
     * @param newTaskId
     * @throws Exception
     */
    void copy(String oldProcessSerialNumber, String oldOpinionFrameMark, String newProcessSerialNumber, String newOpinionFrameMark, String newProcessInstanceId, String newTaskId) throws Exception;

    /**
     * 获取意见历史记录数量
     *
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return
     */
    int countOpinionHistory(String processSerialNumber, String opinionFrameMark);

    /**
     * 根据id删除意见
     *
     * @param id
     */
    void delete(String id);

    /**
     * 根据processSerialNumber查找所有意见
     *
     * @param processSerialNumber
     * @return
     */
    List<Opinion> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据processSerialNumber查找意见，用于未启动流程发送前的是否填写意见校验
     *
     * @param processSerialNumber
     * @return
     */
    int findByProcSerialNumber(String processSerialNumber);

    /**
     * Description:
     *
     * @param processSerialNumber
     * @param taskId
     * @param opinionFrameId
     * @param userId
     * @return
     */
    Opinion findByPsnsAndTaskIdAndOfidAndUserId(String processSerialNumber, String taskId, String opinionFrameId, String userId);

    /**
     * 根据任务id获取意见
     *
     * @param taskId
     * @return
     */
    List<Opinion> findByTaskId(String taskId);

    List<Opinion> findByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId);

    /**
     * Description: 获取自定义历程意见
     *
     * @param taskId
     * @param processTrackId
     * @return
     */
    List<Opinion> findByTaskIdAndProcessTrackId(String taskId, String processTrackId);

    /**
     * 历程时显示的意见,显示的是当前taskId的个人意见和领导意见，暂时不现实部门意见。
     *
     * @param taskId
     * @param userId
     * @return
     */
    List<Opinion> findByTaskIdAndUserIdAndProcessTrackIdIsNull(String taskId, String userId);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    Opinion findOne(String id);

    /**
     * Description:
     *
     * @param processSerialNumber
     * @param category
     * @param userId
     * @return
     */
    Integer getCount4Personal(String processSerialNumber, String category, String userId);

    /**
     * Description:
     *
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param userId
     * @return
     */
    Integer getCount4Personal(String processSerialNumber, String taskId, String category, String userId);

    /**
     * 根据taskId获取意见数量
     *
     * @param taskId
     * @return
     */
    int getCountByTaskId(String taskId);

    /**
     * 获取意见历史记录
     *
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return
     */
    List<OpinionHistoryModel> opinionHistoryList(String processSerialNumber, String opinionFrameMark);

    /**
     * Description:
     *
     * @param processSerialNumber
     * @param taskId
     * @param itembox
     * @param opinionFrameMark
     * @param itemId
     * @param taskDefinitionKey
     * @param activitiUser
     * @return
     */
    List<Map<String, Object>> personCommentList(String processSerialNumber, String taskId, String itembox, String opinionFrameMark, String itemId, String taskDefinitionKey, String activitiUser);

    /**
     * 保存多条意见
     *
     * @param entities
     */
    void save(List<Opinion> entities);

    /**
     * 保存意见
     *
     * @param entity
     */
    void save(Opinion entity);

    /**
     * Description: 保存意见
     *
     * @param entity
     * @return
     */
    Opinion saveOrUpdate(Opinion entity);

    /**
     * 如果用户在启动流程之前先保存了意见，这时意见数据表中之前保存的数据的taskId和processInstanceId仍为空，
     * 此时需要根据processSerialNumber查询数据并给taskId和processInstanceId赋值 注意，该方法只用于启动流程时
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param taskId
     */
    void update(String processSerialNumber, String processInstanceId, String taskId);
}
