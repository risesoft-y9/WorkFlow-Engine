package net.risesoft.util;

import lombok.Data;
import lombok.Getter;

import net.risesoft.model.itemadmin.QueryParamModel;

/**
 * @author qinman
 */
@Getter
@Data
public class ExportRequest {
    private String[] processSerialNumbers;
    private String[] columns;
    private QueryParamModel queryParamModel;
}
