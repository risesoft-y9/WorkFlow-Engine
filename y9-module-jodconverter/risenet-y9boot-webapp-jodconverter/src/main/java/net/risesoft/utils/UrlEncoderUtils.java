package net.risesoft.utils;

import java.net.URL;
import java.util.Base64;
import java.util.BitSet;

public class UrlEncoderUtils {
    private static final BitSet dontNeedEncoding;

    static {
        dontNeedEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set('+');
        /**
         * 这里会有误差,比如输入一个字符串 123+456,它到底是原文就是123+456还是123 456做了urlEncode后的内容呢？<br>
         * 其实问题是一样的，比如遇到123%2B456,它到底是原文即使如此，还是123+456 urlEncode后的呢？ <br>
         * 在这里，我认为只要符合urlEncode规范的，就当作已经urlEncode过了<br>
         * 毕竟这个方法的初衷就是判断string是否urlEncode过<br>
         */

        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');
    }

    /**
     * 判断str是否urlEncoder.encode过<br>
     * 经常遇到这样的情况，拿到一个URL,但是搞不清楚到底要不要encode.<Br>
     * 不做encode吧，担心出错，做encode吧，又怕重复了<Br>
     *
     * @param str
     * @return
     */
    public static boolean hasUrlEncoded(String str) {

        /**
         * 支持JAVA的URLEncoder.encode出来的string做判断。 即: 将' '转成'+' <br>
         * 0-9a-zA-Z保留 <br>
         * '-'，'_'，'.'，'*'保留 <br>
         * 其他字符转成%XX的格式，X是16进制的大写字符，范围是[0-9A-F]
         */
        boolean needEncode = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (dontNeedEncoding.get(c)) {
                continue;
            }
            if (c == '%' && (i + 2) < str.length()) {
                // 判断是否符合urlEncode规范
                char c1 = str.charAt(++i);
                char c2 = str.charAt(++i);
                if (isDigit16Char(c1) && isDigit16Char(c2)) {
                    continue;
                }
            }
            // 其他字符，肯定需要urlEncode
            needEncode = true;
            break;
        }

        return !needEncode;
    }

    /**
     * 判断c是否是16进制的字符
     *
     * @param c
     * @return
     */
    private static boolean isDigit16Char(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
    }

    /**
     * 验证链接是否经过Base64编码
     * 
     * @param base64Str 带验证的url
     * @return
     */
    public static boolean isBase64EncodedUrl(String base64Str) {
        try {
            String decodedStr = decodeBase64String(base64Str);
            return isValidUrl(decodedStr);
        } catch (IllegalArgumentException e) { // Base64解码时可能抛出此异常，如果输入不是有效的Base64编码字符串。
            return false;
        }
    }

    /**
     * 解密url
     * 
     * @param base64Str 带验证的url
     * @return
     */
    public static String decodeBase64String(String base64Str) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
        return new String(decodedBytes);
    }

    /**
     * 验证是否是一个有效的URL
     * 
     * @param urlString 带验证的url
     * @return
     */
    public static boolean isValidUrl(String urlString) {
        try {
            // 这将抛出异常如果urlString不是一个有效的URL
            new URL(urlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}