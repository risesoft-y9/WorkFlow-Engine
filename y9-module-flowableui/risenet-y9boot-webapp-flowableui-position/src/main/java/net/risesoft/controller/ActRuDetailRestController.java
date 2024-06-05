package net.risesoft.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;

/**
 * 办件详情
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/actRuDetail")
public class ActRuDetailRestController {

    private final ActRuDetailService actRuDetailService;

    /**
     * 办结
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/complete")
    public Y9Result<String> complete(@RequestParam @NotBlank String processSerialNumber) {
        return actRuDetailService.complete(processSerialNumber);
    }

    /**
     * 保存流程当前用户的参与人信息
     *
     * @param itemId 事项唯一标示
     * @param processSerialNumber 流程序列号
     * @param sponsorDeptId 主办处室id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String processSerialNumber) {
        return actRuDetailService.saveOrUpdate(itemId, processSerialNumber);
    }
}
