package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.SendButton;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SendButtonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/sendButton")
public class SendButtonRestController {

    private final SendButtonService sendButtonService;

    /**
     * 判断customId是否已经存在
     *
     * @param customId 定义key
     * @return
     */
    @RequestMapping(value = "/checkCustomId", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Boolean> checkCustomId(@RequestParam(required = true) String customId) {
        boolean b = sendButtonService.checkCustomId(customId);
        return Y9Result.success(b, "获取成功");
    }

    /**
     * 获取发送按钮
     *
     * @param id 按钮id
     * @return
     */
    @RequestMapping(value = "/getSendButton", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<SendButton> getSendButton(@RequestParam(required = true) String id) {
        SendButton sendButton = sendButtonService.findOne(id);
        return Y9Result.success(sendButton, "获取成功");
    }

    /**
     * 获取发送按钮列表
     *
     * @return
     */
    @RequestMapping(value = "/getSendButtonList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<SendButton>> getSendButtonList() {
        List<SendButton> sendButtonList = sendButtonService.findAll();
        return Y9Result.success(sendButtonList, "获取成功");
    }

    /**
     * 删除按钮
     *
     * @param sendButtonIds 按钮id
     * @return
     */
    @RequestMapping(value = "/removeSendButtons", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeSendButtons(@RequestParam(required = true) String[] sendButtonIds) {
        sendButtonService.removeSendButtons(sendButtonIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存发送按钮
     *
     * @param sendButton 按钮信息
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(SendButton sendButton) {
        sendButtonService.saveOrUpdate(sendButton);
        return Y9Result.successMsg("保存成功");
    }
}
