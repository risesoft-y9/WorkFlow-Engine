package net.risesoft.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.annotations.Comment;

import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class CommentUtil {
    private static final Pattern pattern = Pattern.compile("\r\n|\r|\n|\n\r");
    private static String comment;

    public static String[] getComment() {
        comment = Y9Context.getProperty("y9.app.itemAdmin.comment");
        comment = unicodeToCn(comment);
        return comment.split(SysVariables.COMMA);
    }

    public static String replaceEnter2Br(String oldStr) {
        Matcher matcher = pattern.matcher(oldStr);
        String newstr = matcher.replaceAll("<br>");
        return newstr;
    }

    public static String unicodeToCn(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                boolean b =
                    (i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U'));
                if (b) {
                    try {
                        retBuf.append((char)Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                } else {
                    retBuf.append(unicodeStr.charAt(i));
                }
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    public static List<Map<String, Object>> getEntityFieldList(Class<?> entityClass) {
        List<Map<String, Object>> list_map = new ArrayList<>();

        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            Map<String, Object> map = new HashMap<>(16);
            // 打印字段名称
            System.out.println("字段名称: " + field.getName());
            // 获取字段上的 @Comment 注解
            Comment comment = field.getAnnotation(Comment.class);
            // 获取字段类型
            String fieldType = field.getType().getSimpleName();
            if (comment != null) {
                // 打印注解值
                System.out.println("注解值: " + comment.value());
                map.put("fieldName", field.getName());
                map.put("fieldType", fieldType);
                map.put("comment", comment.value());
                list_map.add(map);
            } else {
                System.out.println("没有注解");
            }
            // 获取字段值
            field.setAccessible(true); // 允许访问 private 字段
        }
        return list_map;
    }
}