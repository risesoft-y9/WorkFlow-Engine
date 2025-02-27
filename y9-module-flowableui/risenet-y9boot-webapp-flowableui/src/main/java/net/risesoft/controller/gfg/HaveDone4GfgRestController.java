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

import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkList4GfgService;

/**
 * 已办件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/haveDone/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class HaveDone4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    /**
     * 获取已办（包括在办、办结）列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/list")
    public Y9Page<Map<String, Object>> list(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.haveDoneList(itemId, searchMapStr, page, rows);
    }
}