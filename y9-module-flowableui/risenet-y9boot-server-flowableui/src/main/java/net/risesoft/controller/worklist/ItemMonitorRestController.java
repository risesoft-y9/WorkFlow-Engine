package net.risesoft.controller.worklist;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Page;
import net.risesoft.service.ItemMonitorService;

/**
 * 监控办件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/itemMonitor", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemMonitorRestController {

    private final ItemMonitorService itemMonitorService;

    /**
     * 所有历史件
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/pageAllList")
    public Y9Page<Map<String, Object>> pageAllList(@RequestParam @NotBlank String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return itemMonitorService.pageAllList(itemId, page, rows);
    }
}
