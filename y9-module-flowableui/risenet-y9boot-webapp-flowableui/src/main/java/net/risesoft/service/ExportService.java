package net.risesoft.service;

import java.io.OutputStream;

/**
 * @author qinman
 */
public interface ExportService {

    /**
     * 获取待办列表
     *
     * @param processSerialNumbers 流程序列号数组
     * @return Y9Page<Map < String, Object>>
     */
    void select(OutputStream outStream, String[] processSerialNumbers, String[] columns, String itemBox);
}
