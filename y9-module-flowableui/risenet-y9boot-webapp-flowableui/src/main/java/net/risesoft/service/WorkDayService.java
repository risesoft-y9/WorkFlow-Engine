package net.risesoft.service;

import java.util.Date;

import net.risesoft.model.itemadmin.TaskRelatedModel;

public interface WorkDayService {

    int getDay(Date startDate, Date endDate);

    TaskRelatedModel getLightColor(Date startDate, Date endDate);
}
