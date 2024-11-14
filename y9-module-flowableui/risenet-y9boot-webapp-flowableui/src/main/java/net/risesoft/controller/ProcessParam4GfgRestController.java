package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 自定义流程变量
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/processParam/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessParam4GfgRestController {

    private final ProcessParamService processParamService;

    /**
     * 保存流程变量
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @param number 编号
     * @param level 紧急程度
     * @param customItem 是否定制流程
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String theTaskKey, @RequestParam(required = false) String processInstanceId,
        @RequestParam @NotBlank String documentTitle, @RequestParam(required = false) String number,
        @RequestParam(required = false) String level, @RequestParam(required = false) Boolean customItem) {
        return processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, documentTitle, number,
            level, customItem,theTaskKey);
    }
}
