package net.risesoft.service;

import java.io.OutputStream;

import net.risesoft.model.itemadmin.QueryParamModel;

/**
 * @author qinman
 */
public interface ExportService {

    void select(OutputStream outStream, String[] processSerialNumbers, String[] columns, String itemBox);

    void all(OutputStream outStream, String itemId, String itemBox, String[] columns, String taskDefKey,
        String searchMapStr);

    void dc(OutputStream outStream, String[] processSerialNumbers, String[] columns, QueryParamModel queryParamModel);
}
