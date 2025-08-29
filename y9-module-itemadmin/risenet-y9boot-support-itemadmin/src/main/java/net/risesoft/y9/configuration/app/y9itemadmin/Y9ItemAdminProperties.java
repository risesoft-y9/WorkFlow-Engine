package net.risesoft.y9.configuration.app.y9itemadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.item-admin", ignoreInvalidFields = true)
public class Y9ItemAdminProperties {

    /** 从统一待办打开办件的前缀地址 */
    public static String todoTaskUrlPrefix = "https://vue.youshengyun.com/flowableUI/todoIndex";

    /** 意见排序方式，设置为true,则按照岗位的orderedPath排序 */
    public static boolean opinionOrderBy = false;

    /** 统一待办开关 */
    public static boolean todoSwitch = true;

    /** 消息提醒开关 **/
    public static boolean msgSwitch = false;

    /** 短信通知开关 **/
    public static boolean smsSwitch = false;

    /** 意见常用语 **/
    private String defaultOpinion = "知悉。,同意。,已阅。";

}
