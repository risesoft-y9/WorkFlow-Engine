package net.risesoft.service;

import java.io.OutputStream;

/**
 * @author : qinman
 * @date : 2025-03-05
 * @since 9.6.8
 **/
public interface ExcelHandlerService {

    void export(OutputStream outStream, String[] processSerialNumbers);

}
