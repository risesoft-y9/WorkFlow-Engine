package net.risesoft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping(value = "/vue/commonButton")
public class CommonButtonRestController {

    private final CommonButtonService commonButtonService;

    /**
     * 判断customId是否已经存在
     *
     * @param customId 定义key
     * @return Y9Result<Boolean>
     */
    @RequestMapping(value = "/checkCustomId", method = RequestMethod.GET, produces = "application/json")
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
    @RequestMapping(value = "/getCommonButton", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<CommonButton> getCommonButton(@RequestParam String id) {
        CommonButton commonButton = commonButtonService.findOne(id);
        return Y9Result.success(commonButton, "获取成功");
    }

    /**
     * 获取普通按钮列表
     *
     * @return Y9Result<List<CommonButton>>
     */
    @RequestMapping(value = "/getCommonButtonList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<CommonButton>> getCommonButtonList() {
        List<CommonButton> list = commonButtonService.findAll();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除按钮
     *
     * @param commonButtonIds 按钮id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/removeCommonButtons", method = RequestMethod.POST, produces = "application/json")
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
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(CommonButton commonButton) {
        commonButtonService.saveOrUpdate(commonButton);
        return Y9Result.successMsg("保存成功");
    }
}
