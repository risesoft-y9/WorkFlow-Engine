package net.risesoft.util.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import alexh.Fluent;

/**
 * 组件属性工具类
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public class ElementPropertyUtil {

    public ElementPropertyUtil() {}

    private List<Map<String, Object>> getComboboxProp(Integer formType, HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>(16);

        map = new HashMap<String, Object>(16);
        map.put("key", "bindOption");
        map.put("name",
            "<a onclick=\"bindOption();\" href=\"javascript:void(0);\" style=\"text-decoration:none;color:blue;\" title=\"点击绑定\" >数据字典</a>");
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("id", "validType");
        map.put("key", "validType");
        map.put("name", "校验类型");
        map.put("value", "");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("url", request.getContextPath() + "/y9form/bind/validType?formType=" + formType)
                    .append("panelHeight", "300px")));
        list.add(map);

        return list;
    }

    public List<Map<String, Object>> getDateboxProp(Integer formType, HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>(16);

        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        Map<String, Object> m = new HashMap<String, Object>(16);

        m.put("text", "否");
        m.put("value", "否");
        m.put("selected", true);
        list1.add(m);

        m = new HashMap<String, Object>(16);
        m.put("text", "是");
        m.put("value", "是");
        list1.add(m);

        map = new HashMap<String, Object>(16);
        map.put("key", "required");
        map.put("name", "是否必填");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("data", list1).append("panelHeight", "auto")));
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "fieldPermission");
        map.put("name",
            "<a onclick=\"fieldPermission();\" href=\"javascript:void(0);\" style=\"text-decoration:none;color:blue;\" title=\"点击设置\" >读写权限</a>");
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "initValue");
        map.put("name", "初始值");
        map.put("value", "");
        map.put("editor", "text");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "validType");
        map.put("name", "校验类型");
        map.put("value", "");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("url", request.getContextPath() + "/y9form/bind/validType?formType=" + formType)
                    .append("data", list1).append("panelHeight", "300px")));
        list.add(map);

        if (formType == 1) {
            map = new HashMap<String, Object>(16);
            map.put("key", "missingMessage");
            map.put("name", "为空时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("key", "invalidMessage");
            map.put("name", "违反校验时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getNumberboxProp(Integer formType, HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>(16);

        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        Map<String, Object> m = new HashMap<String, Object>(16);

        m.put("text", "否");
        m.put("value", "否");
        m.put("selected", true);
        list1.add(m);

        m = new HashMap<String, Object>(16);
        m.put("text", "是");
        m.put("value", "是");
        list1.add(m);

        map = new HashMap<String, Object>(16);
        map.put("key", "required");
        map.put("name", "是否必填");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("data", list1).append("panelHeight", "auto")));
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "fieldPermission");
        map.put("name",
            "<a onclick=\"fieldPermission();\" href=\"javascript:void(0);\" style=\"text-decoration:none;color:blue;\" title=\"点击设置\" >读写权限</a>");
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "initValue");
        map.put("name", "初始值");
        map.put("value", "");
        map.put("editor", "text");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "validType");
        map.put("name", "校验类型");
        map.put("value", "");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("url", request.getContextPath() + "/y9form/bind/validType?formType=" + formType)
                    .append("data", list1).append("panelHeight", "300px")));
        list.add(map);

        if (formType == 1) {
            map = new HashMap<String, Object>(16);
            map.put("key", "missingMessage");
            map.put("name", "为空时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("key", "invalidMessage");
            map.put("name", "违反校验时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("key", "min");
            map.put("name", "最小值");
            map.put("value", "");
            map.put("editor", new Fluent.HashMap<String, Object>().append("type", "numberbox").append("options",
                new Fluent.HashMap<String, Object>().append("editable", true)));
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("key", "max");
            map.put("name", "最大值");
            map.put("value", "");
            map.put("editor", new Fluent.HashMap<String, Object>().append("type", "numberbox").append("options",
                new Fluent.HashMap<String, Object>().append("editable", true)));
            list.add(map);
        }

        return list;
    }

    public List<Map<String, Object>> getProperty(Integer formType, String componentType, HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        switch (componentType) {
            case "textbox":
                list = getTextboxProp(formType, request);
                break;
            case "numberbox":
                list = getNumberboxProp(formType, request);
                break;
            case "datebox":
                list = getDateboxProp(formType, request);
                break;
            case "datetimebox":
                list = getDateboxProp(formType, request);
                break;
            case "combobox":
                list = getComboboxProp(formType, request);
                break;
            case "checkbox":
                list = getComboboxProp(formType, request);
                break;
            case "radiobutton":
                list = getComboboxProp(formType, request);
                break;
            default:
                return list;
        }
        return list;
    }

    public List<Map<String, Object>> getTextboxProp(Integer formType, HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>(16);

        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        Map<String, Object> m = new HashMap<String, Object>(16);

        m.put("text", "否");
        m.put("value", "否");
        m.put("selected", true);
        list1.add(m);

        m = new HashMap<String, Object>(16);
        m.put("text", "是");
        m.put("value", "是");
        list1.add(m);

        map = new HashMap<String, Object>(16);
        map.put("key", "required");
        map.put("name", "是否必填");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("data", list1).append("panelHeight", "auto")));
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "fieldPermission");
        map.put("name",
            "<a onclick=\"fieldPermission();\" href=\"javascript:void(0);\" style=\"text-decoration:none;color:blue;\" title=\"点击设置\" >读写权限</a>");
        map.put("value", "");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("key", "initValue");
        map.put("name", "初始值");
        map.put("value", "");
        map.put("editor", "text");
        list.add(map);

        map = new HashMap<String, Object>(16);
        map.put("id", "validType");
        map.put("key", "validType");
        map.put("name", "校验类型");
        map.put("value", "");
        map.put("editor",
            new Fluent.HashMap<String, Object>().append("type", "combobox").append("options",
                new Fluent.HashMap<String, Object>().append("limitToList", true).append("editable", false)
                    .append("url", request.getContextPath() + "/y9form/bind/validType?formType=" + formType)
                    .append("panelHeight", "300px")));
        list.add(map);

        if (formType == 1) {
            map = new HashMap<String, Object>(16);
            map.put("key", "missingMessage");
            map.put("name", "为空时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("key", "invalidMessage");
            map.put("name", "违反校验时的提示信息");
            map.put("value", "");
            map.put("editor", "text");
            list.add(map);
        }

        return list;
    }

}
