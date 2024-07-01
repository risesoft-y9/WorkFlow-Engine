package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ErrorLogApi {

    /**
     * 保存错误日志
     *
     * @param tenantId 租户id
     * @param errorLogModel 错误日志
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveErrorLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveErrorLog(@RequestParam("tenantId") String tenantId, @RequestBody ErrorLogModel errorLogModel);
}
