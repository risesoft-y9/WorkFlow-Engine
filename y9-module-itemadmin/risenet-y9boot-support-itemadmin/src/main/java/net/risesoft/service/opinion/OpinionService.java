package net.risesoft.service.opinion;

import java.util.List;

import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.itemadmin.OpinionFrameModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OpinionService {

    /**
     * 检查当前taskId任务节点是否已经签写意见
     *
     * @param processSerialNumber 流程实例编号
     * @param taskId 任务ID
     * @return Boolean
     */
    Boolean checkSignOpinion(String processSerialNumber, String taskId);

    /**
     * 获取意见历史记录数量
     *
     * @param processSerialNumber 流程序列号
     * @param opinionFrameMark 意见框标识
     * @return int
     */
    int countOpinionHistory(String processSerialNumber, String opinionFrameMark);

    /**
     * 根据id删除意见
     *
     * @param id 意见id
     */
    void delete(String id);

    /**
     * 根据processSerialNumber查找意见，用于未启动流程发送前的是否填写意见校验
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    int findByProcSerialNumber(String processSerialNumber);

    /**
     *
     * @param id 意见id
     * @return
     */
    Opinion getById(String id);

    /**
     * 根据taskId获取意见数量
     *
     * @param taskId
     * @return
     */
    int getCountByTaskId(String taskId);

    /**
     * 根据processSerialNumber查找所有意见
     *
     * @param processSerialNumber 流程序列号
     * @return List<Opinion>
     */
    List<Opinion> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据任务id获取意见
     *
     * @param taskId
     * @return
     */
    List<Opinion> listByTaskId(String taskId);

    List<Opinion> listByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId);

    /**
     * 获取自定义历程意见
     *
     * @param taskId
     * @param processTrackId
     * @return
     */
    List<Opinion> listByTaskIdAndProcessTrackId(String taskId, String processTrackId);

    /**
     * 获取意见历史记录
     *
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return List<OpinionHistoryModel>
     */
    List<OpinionHistoryModel> listOpinionHistory(String processSerialNumber, String opinionFrameMark);

    /**
     * 
     *
     * @param processSerialNumber
     * @param taskId
     * @param itembox
     * @param opinionFrameMark
     * @param itemId
     * @param taskDefinitionKey
     * @return
     */
    List<OpinionListModel> listPersonComment(String processSerialNumber, String taskId, String itembox,
        String opinionFrameMark, String itemId, String taskDefinitionKey);

    /**
     *
     *
     * @param processSerialNumber
     * @param taskId
     * @param itembox
     * @param opinionFrameMark
     * @param itemId
     * @param taskDefinitionKey
     * @return
     */
    OpinionFrameModel listPersonCommentNew(String processSerialNumber, String taskId, String itembox,
        String opinionFrameMark, String itemId, String taskDefinitionKey);

    /**
     * 保存多条意见
     *
     * @param entities 待保存的实体
     */
    void save(List<Opinion> entities);

    /**
     * 保存意见
     *
     * @param entity 待保存的实体
     */
    void save(Opinion entity);

    /**
     * 保存意见
     *
     * @param entity 待保存的实体
     * @return Opinion
     */
    Opinion saveOrUpdate(Opinion entity);

    /**
     * 如果用户在启动流程之前先保存了意见，这时意见数据表中之前保存的数据的taskId和processInstanceId仍为空，
     * 此时需要根据processSerialNumber查询数据并给taskId和processInstanceId赋值 注意，该方法只用于启动流程时
     *
     * @param processSerialNumber 流程序列号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     */
    void update(String processSerialNumber, String processInstanceId, String taskId);

    /**
     * 更新意见
     *
     * @param id 意见id
     * @param content 意见内容
     */
    void updateOpinion(String id, String content);
}