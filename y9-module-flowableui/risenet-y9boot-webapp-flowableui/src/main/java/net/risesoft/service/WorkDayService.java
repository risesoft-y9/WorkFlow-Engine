package net.risesoft.service;

import net.risesoft.model.LightColorModel;

import java.util.Date;

public interface WorkDayService {

    int getDay(Date startDate, Date endDate);

    LightColorModel getLightColor(Date startDate, Date endDate);
}
