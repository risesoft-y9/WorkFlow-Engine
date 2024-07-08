package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.entity.ErrorLog;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ErrorLogService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 错误日志记录接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/errorLog", produces = MediaType.APPLICATION_JSON_VALUE)
public class ErrorLogApiImpl implements ErrorLogApi {

    private final ErrorLogService errorLogService;

    /**
     * 保存错误日志
     *
     * @param tenantId 租户id
     * @param errorLogModel 日志信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveErrorLog(@RequestParam String tenantId, @RequestBody ErrorLogModel errorLogModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ErrorLog errorLog = new ErrorLog();
        Y9BeanUtil.copyProperties(errorLogModel, errorLog);
        errorLogService.saveErrorLog(errorLog);
        return Y9Result.success();
    }

}
