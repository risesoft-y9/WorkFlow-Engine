package net.risesoft.util;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.itemadmin.QueryParamModel;

@Slf4j
public class CommonUtils {
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!"serialVersionUID".equals(field.getName()) && !"page".equals(field.getName())
                    && !"rows".equals(field.getName()) && field.get(object) != null
                    && StringUtils.isNotBlank(field.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 构建查询条件（使用参数化查询防止SQL注入）
     */
    public static void buildQueryConditions(QueryParamModel queryParamModel, StringBuilder whereSql,
        List<Object> params) {
        Class<?> queryParamModelClazz = queryParamModel.getClass();
        Field[] fields = queryParamModelClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (shouldSkipField(field.getName())) {
                continue;
            }
            try {
                Object fieldValue = field.get(queryParamModel);
                if (fieldValue != null) {
                    appendCondition(whereSql, params, field.getName(), fieldValue);
                }
            } catch (Exception e) {
                LOGGER.error("构建查询条件异常", e);
            }
        }
    }

    /**
     * 判断是否应该跳过的字段
     */
    private static boolean shouldSkipField(String fieldName) {
        return "serialVersionUID".equals(fieldName) || "page".equals(fieldName) || "rows".equals(fieldName);
    }

    /**
     * 根据字段名添加相应的查询条件
     */
    private static void appendCondition(StringBuilder whereSql, List<Object> params, String fieldName,
        Object fieldValue) {
        if ("systemName".equals(fieldName)) {
            whereSql.append(" AND T.SYSTEMNAME = ? ");
            params.add(fieldValue);
        } else if ("bureauIds".equals(fieldName)) {
            whereSql.append(" AND P.HOSTDEPTID = ? ");
            params.add(fieldValue);
        } else {
            whereSql.append(" AND INSTR(P.").append(fieldName).append(", ?) > 0 ");
            params.add(fieldValue);
        }
    }
}
