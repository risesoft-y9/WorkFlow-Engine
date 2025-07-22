package net.risesoft.y9.configuration.app.flowble;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.flowable", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9FlowableProperties {

    private String monitorManageRoleName = "监控管理员角色";

    private String repositionrManageRoleName = "重定向角色";

    private String fawenManageRoleName = "发文角色";

    private String shouwenManageRoleName = "收文角色";

    private String leaveManageRoleName = "人事统计角色";

}
