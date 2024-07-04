package net.risesoft.api.itemadmin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 书签绑定接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BookMarkBindApi {

    /**
     * 根据模板和流程序列号查询模板的书签对应的值
     *
     * @param tenantId 租户id
     * @param wordTemplateId 模板id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map < String, Object>>} 通用请求返回对象 - data 是书签对应的值
     */
    @GetMapping("/getBookMarkData")
    Y9Result<Map<String, Object>> getBookMarkData(@RequestParam("tenantId") String tenantId,
        @RequestParam("wordTemplateId") String wordTemplateId,
        @RequestParam("processSerialNumber") String processSerialNumber);
}
