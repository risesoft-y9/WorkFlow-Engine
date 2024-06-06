package net.risesoft.util.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组件类型工具类
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public class ComponentTypeUtil {

    public ComponentTypeUtil() {}

    public List<Map<String, Object>> getComponentType(String elementType) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        elementType = elementType == null ? "" : elementType;
        switch (elementType) {
            case "text":
                map.put("text", "textbox");
                map.put("value", "textbox");
                list.add(map);

                map = new HashMap<>(16);
                map.put("text", "numberbox");
                map.put("value", "numberbox");
                list.add(map);

                map = new HashMap<>(16);
                map.put("text", "datebox");
                map.put("value", "datebox");
                list.add(map);

                map = new HashMap<>(16);
                map.put("text", "datetimebox");
                map.put("value", "datetimebox");
                list.add(map);
                break;
            case "textarea":
                map.put("text", "textbox");
                map.put("value", "textbox");
                list.add(map);
                break;
            case "checkbox":
                map.put("text", "checkbox");
                map.put("value", "checkbox");
                list.add(map);
                break;
            case "radio":
                map.put("text", "radiobutton");
                map.put("value", "radiobutton");
                list.add(map);
                break;
            case "select":
                map.put("text", "combobox");
                map.put("value", "combobox");
                list.add(map);
                break;
            default:
                return list;
        }
        return list;
    }

}
