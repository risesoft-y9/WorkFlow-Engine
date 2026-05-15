package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

public interface DocumentHandleService {

    Y9Result<String> sign4Batch(String[] taskIdAndProcessSerialNumbers);

}
