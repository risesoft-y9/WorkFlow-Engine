package net.risesoft.controller.gfg;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ExcelHandlerService;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.y9.Y9LoginUserHolder;
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
@RequestMapping(value = "/vue/todo/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class Todo4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    private final ItemViewConfApi itemViewConfApi;

    private final ServletContext servletContext;

    private final ExcelHandlerService excelHandlerService;

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param searchMapStr 查询参数
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.todoList(itemId, searchMapStr, page, rows);
    }

    @PostMapping(value = "/todoList4CancelNumber")
    public Y9Page<Map<String, Object>> todoList4CancelNumber(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList4CancelNumber(itemId, searchMapStr, page, rows);
    }

    @PostMapping(value = "/todoList4Other")
    public Y9Page<Map<String, Object>> todoList4Other(@RequestParam String itemId, @RequestParam String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList4Other(itemId, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/todoList4TaskDefKey")
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(@RequestParam String itemId,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList4TaskDefKey(itemId, taskDefKey, searchMapStr, page, rows);
    }

    /**
     * 获取待办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/todoViewConf")
    public Y9Result<List<ItemViewConfModel>> todoViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.TODO.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/allTodoList")
    public Y9Page<Map<String, Object>> allTodoList(@Valid QueryParamModel queryParamModel) {
        return workList4GfgService.allTodoList(queryParamModel);
    }

    /**
     * 导出列表
     *
     * @param processSerialNumbers 流程序列号
     * @param columns 列属性
     * @param response 响应
     */
    @RiseLog(operationName = "导出", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/exportSelect")
    public void exportSelect(@RequestParam String[] processSerialNumbers, @RequestParam String[] columns,
        HttpServletResponse response) {
        Arrays.stream(columns).forEach(System.out::println);
        try (OutputStream outStream = response.getOutputStream()) {
            String filename = "待办导出" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setContentType(MediaTypeUtils.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));
            workList4GfgService.exportSelect(outStream, processSerialNumbers, columns);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}