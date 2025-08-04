package net.risesoft.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.service.ExportService;
import net.risesoft.util.ExportRequest;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9.util.mime.MediaTypeUtils;

/**
 * 待办，在办，办结列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/export", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExportRestController {

    private final ExportService exportService;

    private final ServletContext servletContext;

    /**
     * 选择导出
     *
     * @param processSerialNumbers 流程序列号
     * @param columns 列属性
     * @param response 响应
     */
    @FlowableLog(operationName = "选择导出", operationType = FlowableOperationTypeEnum.EXPORT)
    @PostMapping(value = "/select")
    public void select(@RequestParam String[] processSerialNumbers, @RequestParam String[] columns,
        @RequestParam String itemBox, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            String filename = "导出" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setContentType(MediaTypeUtils.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));
            exportService.select(outStream, processSerialNumbers, columns, itemBox);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出全部
     *
     * @param columns 列属性
     * @param response 响应
     */
    @FlowableLog(operationName = "导出全部", operationType = FlowableOperationTypeEnum.EXPORT)
    @PostMapping(value = "/all")
    public void all(@RequestParam String itemId, @RequestParam String itemBox, @RequestParam String[] columns,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String searchMapStr,
        HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            String filename = "导出" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setContentType(MediaTypeUtils.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));
            Long start = System.currentTimeMillis();
            exportService.all(outStream, itemId, itemBox, columns, taskDefKey, searchMapStr);
            Long end = System.currentTimeMillis();
            System.out.println("导出全部耗时(秒):" + (end - start) / 1000);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出传签件
     *
     * @param exportRequest 导出的请求
     * @param response 响应
     */
    @FlowableLog(operationName = "导出传签件", operationType = FlowableOperationTypeEnum.EXPORT)
    @PostMapping(value = "/dc")
    public void dc(@RequestBody ExportRequest exportRequest, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            String filename = "导出" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setContentType(MediaTypeUtils.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));
            Long start = System.currentTimeMillis();
            exportService.dc(outStream, exportRequest.getProcessSerialNumbers(), exportRequest.getColumns(),
                exportRequest.getQueryParamModel());
            Long end = System.currentTimeMillis();
            System.out.println("导出全部耗时(秒):" + (end - start) / 1000);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}