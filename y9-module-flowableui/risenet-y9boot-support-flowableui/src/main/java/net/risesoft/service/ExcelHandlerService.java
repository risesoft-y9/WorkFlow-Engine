package net.risesoft.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author : qinman
 * @date : 2025-03-05
 * @since 9.6.8
 **/
public interface ExcelHandlerService {

    void export(OutputStream outStream, List<Map<String, Object>> mapList, String[] columns);

}
