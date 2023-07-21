/**
 * @Project Name:risenet-sp-approve
 * @File Name: XSSCheck.java
 * @Package Name: net.risesoft.util
 * @Company:北京有生博大软件有限公司（深圳分公司）
 * @Copyright (c) 2016,RiseSoft All Rights Reserved.
 * @date 2016年11月26日 下午2:42:06
 */
package net.risesoft.util;

/**
 * @ClassName: XSSCheck.java
 * @Description: 过滤用户输入要保护应用程序免遭跨站点脚本编制的攻击，请通过将敏感字符转换为其对 应的字符实体来清理 HTML。这些是 HTML 敏感字符：< > " ' % ; ) ( & +
 *               以下示例通过将敏感字符转换为其对应的字符实体，来过滤指定字符串：
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class XssCheckUtil {
    public static String filter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                case '%':
                    result.append("&#37;");
                    break;
                case ';':
                    result.append("&#59;");
                    break;
                case '(':
                    result.append("&#40;");
                    break;
                case ')':
                    result.append("&#41;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '+':
                    result.append("&#43;");
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }
        return result.toString();
    }
}
