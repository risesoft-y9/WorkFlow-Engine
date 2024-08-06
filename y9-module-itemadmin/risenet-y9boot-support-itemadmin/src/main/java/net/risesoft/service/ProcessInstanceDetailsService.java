package net.risesoft.service;

import java.util.Date;

import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ProcessInstanceDetailsService {

    /**
     * 删除协作状态
     *
     * @param processInstanceId
     * @return
     */
    boolean deleteProcessInstance(String processInstanceId);

    /**
     * 获取协作状态列表
     *
     * @param userId
     * @param title
     * @param page
     * @param rows
     * @return
     */
    Y9Page<ProcessCooperationModel> pageByUserIdAndTitle(String userId, String title, int page, int rows);

    /**
     * 保存协作状态
     *
     * @param model
     */
    boolean save(ProcessInstanceDetailsModel model);

    /**
     * 更新协作状态
     *
     * @param processInstanceId
     * @param taskId
     * @param itembox
     * @param endTime
     * @return
     */
    boolean updateProcessInstanceDetails(String processInstanceId, String taskId, String itembox, Date endTime);

}
