package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.PrintLog;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface PrintLogService {

    List<PrintLog> getPrintLogList(String processSerialNumber);

    void savePrintLog(PrintLog log);
}
