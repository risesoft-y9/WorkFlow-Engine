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
@Builder
@AllArgsConstructor
public class TodoResponse {
    /**
     * 返回代码值
     */
    private int code;
    /**
     * 枚举型，包括：success、error、warning
     */
    private String type;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 请求服务时传递的唯一标识UUID
     */
    private String token;
    /**
     * 返回数据内容
     */
    private String data;
}
