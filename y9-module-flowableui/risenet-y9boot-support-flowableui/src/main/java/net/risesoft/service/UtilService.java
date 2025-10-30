package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.ItemBoxAndTaskIdModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.processadmin.TaskModel;

public interface UtilService {

    /**
     * 设置列表公共数据
     * 
     * @param mapTemp
     * @param processInstanceId
     * @param taskList
     * @param itemBoxTypeEnum
     */
    void setPublicData(Map<String, Object> mapTemp, String processInstanceId, List<TaskModel> taskList,
        ItemBoxTypeEnum itemBoxTypeEnum);

    String getAssigneeNames(List<TaskModel> taskList, SignDeptDetailModel signDeptDetail);

    ItemBoxAndTaskIdModel getItemBoxAndTaskId(List<TaskModel> taskList);
}
