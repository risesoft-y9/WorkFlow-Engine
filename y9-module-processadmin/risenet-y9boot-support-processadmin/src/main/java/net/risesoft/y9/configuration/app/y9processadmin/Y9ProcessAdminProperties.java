package net.risesoft.y9.configuration.app.y9processadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.process-admin", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9ProcessAdminProperties {

    private String baseUrl = "http://127.0.0.1:7055/processAdmin";

    private String freeFlowKey = "ziyouliucheng";

    private Boolean todoSwitch = false;// 统一待办开关

    private Boolean dataCenterSwitch = false;// 数据中心开关

    private Boolean weiXinSwitch = false;// 微信提醒开关

    private Boolean cooperationStateSwitch = false;// 协作状态开关

    private Boolean msgSwitch = false;// 消息提醒开关

    private Boolean entrustSwitch = false;

    private Boolean messagePushSwitch = false;

    private Boolean pushSwitch = false;

    private Boolean smsSwitch = false;

    private Boolean interfaceSwitch = false;// 接口调用开关

    private String weiXinUrl;

}
