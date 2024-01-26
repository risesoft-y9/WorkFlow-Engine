package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OptionClassApi {

    /**
     * 获取数据字典列表
     *
     * @param tenantId 租户id
     * @param type 字典类型
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getOptionValueList(String tenantId, String type);

}
