package net.risesoft.controller.gfg;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkList4GfgService;

/**
 * 所有件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/all/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class All4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    /**
     * 获取所有本人经手件的列表
     *
     * @param itemId 事项id
     * @param searchMapStr 查询参数
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "办理列表")
    @PostMapping(value = "/allList")
    public Y9Page<Map<String, Object>> allList(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.allList(itemId, searchMapStr, false, page, rows);
    }

    /**
     * 获取全委列表
     *
     * @param itemId 事项id
     * @param searchMapStr 查询参数
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控所有件列表", logLevel = FlowableLogLevelEnum.ADMIN)
    @PostMapping(value = "/allList4Org")
    public Y9Page<Map<String, Object>> allList4Org(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.allList(itemId, searchMapStr, true, page, rows);
    }
}