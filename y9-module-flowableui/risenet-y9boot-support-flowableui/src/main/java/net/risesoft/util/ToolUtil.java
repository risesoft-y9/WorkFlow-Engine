package net.risesoft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class ToolUtil {

    private static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str 字符串
     * @return String 去除空格后的字符串
     */
    public static String replaceSpecialStr(String str) {
        if (str == null) {
            return "";
        }
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}