package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.PrintLogModel;
import net.risesoft.pojo.Y9Result;

/**
 * 打印日志接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface PrintLogApi {

    /**
     * 获取打印日志列表
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程实例ID
     * @return Y9Result<List<PrintLogModel>>
     */
    @GetMapping("/getPrintLogList")
    Y9Result<List<PrintLogModel>> getPrintLogList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存打印日志
     *
     * @param tenantId 租户ID
     * @param printLog 打印日志
     */
    @PostMapping(value = "/savePrintLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> savePrintLog(@RequestParam("tenantId") String tenantId, @RequestBody PrintLogModel printLog);
}
