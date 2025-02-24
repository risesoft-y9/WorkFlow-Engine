package net.risesoft.controller.gfg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.PrintLogApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.PrintLogModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发文单排版信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/printLog", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class PrintLog4GfgController {

    private final PrintLogApi printLogApi;

    /**
     * 获取打印日志列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<PrintLogModel>>
     */
    @GetMapping(value = "/getPrintLogList")
    public Y9Result<List<PrintLogModel>> getPrintLogList(@RequestParam String processSerialNumber) {
        return printLogApi.getPrintLogList(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

    /**
     * 保存打印日志
     *
     * @param processSerialNumber 流程编号
     * @param actionContent 操作内容
     * @param actionType 操作类型
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/savePrintLog")
    public Y9Result<Object> savePrintLog(@RequestParam String processSerialNumber, @RequestParam String actionContent,
        @RequestParam String actionType, HttpServletRequest request) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintLogModel printLog = new PrintLogModel();
        printLog.setProcessSerialNumber(processSerialNumber);
        printLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        printLog.setPrintTime(sdf.format(new Date()));
        printLog.setUserId(Y9LoginUserHolder.getPersonId());
        printLog.setUserName(Y9LoginUserHolder.getUserInfo().getName());
        printLog.setActionContent(actionContent);
        printLog.setActionType(actionType);
        printLog.setIp(Y9Context.getIpAddr(request));
        printLog.setDeptId(Y9LoginUserHolder.getUserInfo().getParentId());
        return printLogApi.savePrintLog(Y9LoginUserHolder.getTenantId(), printLog);
    }

}
