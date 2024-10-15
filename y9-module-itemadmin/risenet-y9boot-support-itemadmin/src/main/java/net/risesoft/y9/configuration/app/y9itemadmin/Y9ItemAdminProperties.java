package net.risesoft.y9.configuration.app.y9itemadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.item-admin", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9ItemAdminProperties {

    private String comment;

    private String freeFlowKey = "ziyouliucheng";

    private Boolean smsSwitch = false;

    private Boolean msgSwitch = false;// 消息提醒开关

    private Boolean weiXinSwitch = false;// 微信提醒开关

    private String weiXinUrl;

}
