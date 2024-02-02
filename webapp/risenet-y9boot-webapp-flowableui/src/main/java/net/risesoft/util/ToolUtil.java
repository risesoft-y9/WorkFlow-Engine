package net.risesoft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class ToolUtil {

    private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     * 
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Matcher m = p.matcher(str);
            repl = m.replaceAll("").trim();
        }
        return repl;
    }
}
