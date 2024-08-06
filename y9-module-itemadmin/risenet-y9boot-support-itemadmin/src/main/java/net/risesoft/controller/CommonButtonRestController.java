package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.CommonButton;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CommonButtonService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/commonButton", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonButtonRestController {

    private final CommonButtonService commonButtonService;

    /**
     * 判断customId是否已经存在
     *
     * @param customId 定义key
     * @return Y9Result<Boolean>
     */
    @GetMapping(value = "/checkCustomId")
    public Y9Result<Boolean> checkCustomId(@RequestParam String customId) {
        boolean b = commonButtonService.checkCustomId("common_" + customId);
        return Y9Result.success(b, "获取成功");
    }

    /**
     * 获取普通按钮
     *
     * @param id 按钮id
     * @return Y9Result<CommonButton>
     */
    @GetMapping(value = "/getCommonButton")
    public Y9Result<CommonButton> getCommonButton(@RequestParam String id) {
        CommonButton commonButton = commonButtonService.getById(id);
        return Y9Result.success(commonButton, "获取成功");
    }

    /**
     * 获取普通按钮列表
     *
     * @return Y9Result<List<CommonButton>>
     */
    @GetMapping(value = "/getCommonButtonList")
    public Y9Result<List<CommonButton>> getCommonButtonList() {
        List<CommonButton> list = commonButtonService.listAll();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除按钮
     *
     * @param commonButtonIds 按钮id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeCommonButtons")
    public Y9Result<String> removeCommonButtons(@RequestParam String[] commonButtonIds) {
        commonButtonService.removeCommonButtons(commonButtonIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存普通按钮
     *
     * @param commonButton 按钮信息
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(CommonButton commonButton) {
        commonButtonService.saveOrUpdate(commonButton);
        return Y9Result.successMsg("保存成功");
    }
}
