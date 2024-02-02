package net.risesoft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class CommentUtil {
    private static Pattern pattern = Pattern.compile("\r\n|\r|\n|\n\r");
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
}