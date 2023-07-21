package net.risesoft.service;

import net.risesoft.entity.ErrorLog;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ErrorLogService {

    /**
     * 保存错误日志
     * 
     * @param errorLog
     */
    void saveErrorLog(ErrorLog errorLog);
}
