package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SendButton;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SendButtonService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/sendButton", produces = MediaType.APPLICATION_JSON_VALUE)
public class SendButtonRestController {

    private final SendButtonService sendButtonService;

    /**
     * 判断customId是否已经存在
     *
     * @param customId 定义key
     * @return
     */
    @GetMapping(value = "/checkCustomId")
    public Y9Result<Boolean> checkCustomId(@RequestParam String customId) {
        boolean b = sendButtonService.checkCustomId(customId);
        return Y9Result.success(b, "获取成功");
    }

    /**
     * 获取发送按钮
     *
     * @param id 按钮id
     * @return
     */
    @GetMapping(value = "/getSendButton")
    public Y9Result<SendButton> getSendButton(@RequestParam String id) {
        SendButton sendButton = sendButtonService.getById(id);
        return Y9Result.success(sendButton, "获取成功");
    }

    /**
     * 获取发送按钮列表
     *
     * @return
     */
    @GetMapping(value = "/getSendButtonList")
    public Y9Result<List<SendButton>> getSendButtonList() {
        List<SendButton> sendButtonList = sendButtonService.listAll();
        return Y9Result.success(sendButtonList, "获取成功");
    }

    /**
     * 删除按钮
     *
     * @param sendButtonIds 按钮id
     * @return
     */
    @PostMapping(value = "/removeSendButtons")
    public Y9Result<String> removeSendButtons(@RequestParam String[] sendButtonIds) {
        sendButtonService.removeSendButtons(sendButtonIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存发送按钮
     *
     * @param sendButton 按钮信息
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(SendButton sendButton) {
        sendButtonService.saveOrUpdate(sendButton);
        return Y9Result.successMsg("保存成功");
    }
}
