package net.risesoft.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.InterfaceRequestParams;
import net.risesoft.entity.InterfaceResponseParams;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class InterfaceExportData implements Serializable {

    private static final long serialVersionUID = 7669425535268773230L;

    /**
     * 接口信息
     */
    private InterfaceInfo interfaceInfo;

    /**
     * 请求参数列表
     */
    private List<InterfaceRequestParams> requestParamsList;

    /**
     * 响应参数列表
     */
    private List<InterfaceResponseParams> responseParamsList;

}
