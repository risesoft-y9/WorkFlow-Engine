package net.risesoft.service;

import java.util.List;

import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.processadmin.TaskModel;

public interface UtilService {

    String getAssigneeNames(List<TaskModel> taskList, SignDeptDetailModel signDeptDetail);

    List<String> getItemBoxAndTaskId(List<TaskModel> taskList);
}
