package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.PrintLogApi;
import net.risesoft.entity.PrintLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.itemadmin.PrintLogModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.PrintLogService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 打印日志接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/printLog", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrintLogApiImpl implements PrintLogApi {

    private final PrintLogService printLogService;

    private final ProcessParamService processParamService;

    /**
     * 获取打印日志列表
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程实例ID
     * @return Y9Result<List<PrintLogModel>>
     */
    @Override
    public Y9Result<List<PrintLogModel>> getPrintLogList(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<PrintLog> printLogList = printLogService.getPrintLogList(processSerialNumber);
        List<PrintLogModel> list = new ArrayList<>();
        for (PrintLog printLog : printLogList) {
            PrintLogModel printLogModel = new PrintLogModel();
            Y9BeanUtil.copyProperties(printLog, printLogModel);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            if (null != processParam) {
                printLogModel.setTitle(processParam.getTitle());
            }
            list.add(printLogModel);
        }
        return Y9Result.success(list);
    }

    /**
     * 保存打印日志
     *
     * @param tenantId 租户ID
     * @param printLog 打印日志
     */
    @Override
    public Y9Result<Object> savePrintLog(@RequestParam String tenantId, @RequestBody PrintLogModel printLog) {
        Y9LoginUserHolder.setTenantId(tenantId);
        PrintLog log = new PrintLog();
        Y9BeanUtil.copyProperties(printLog, log);
        printLogService.savePrintLog(log);
        return Y9Result.success();
    }
}
