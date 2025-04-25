package net.risesoft.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用type判断是否成功执行。
 *
 * @author : qinman
 * @date : 2025-04-24
 * @since 9.6.8
 **/
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class TodoParam {
    /**
     * 时间戳，年月日时分秒毫秒，全部需要补足0，例如：20200301110308005
     */
    private String timestamp;
    /**
     * 请求唯一标识，使用UUID，例如：f0d09ac102634e2593a0fe12bc0e601c
     */
    private String token;
    /**
     * 请求应用的唯一标识;
     */
    private String app;
    /**
     * 双方系统约定的密钥
     */
    private String key;

    /**
     * 校验码，使用timestamp+app+token+key进行MD5加密后的校验码，例如：03adfb3cfb8929bed284773fbc7fe406
     */
    private String vcode;
}
