package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class MapUtil {

    /**
     * 遍历map，获取key
     *
     * @param map
     */
    public static List<String> getKey(Map<String, String> map) {
        List<String> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(key);
        }
        return list;
    }
}
