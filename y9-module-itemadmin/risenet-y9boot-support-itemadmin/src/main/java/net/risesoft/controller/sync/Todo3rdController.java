package net.risesoft.controller.sync;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Todo3rd;
import net.risesoft.util.TodoResponse;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 同步未办结件到数据中心
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class Todo3rdController {

    @PostMapping(value = "/add")
    public TodoResponse add(@RequestParam @NotBlank String timestamp, @RequestParam @NotBlank String token,
        @RequestParam @NotBlank String app, @RequestParam @NotBlank String key, @RequestParam @NotBlank String vcode,
        @RequestBody String json) {
        Todo3rd todo3rd = Y9JsonUtil.readValue(json, Todo3rd.class);
        assert todo3rd != null;
        LOGGER.info("timestamp：{}", timestamp);
        LOGGER.info("token:{}", token);
        LOGGER.info("app:{}", app);
        LOGGER.info("key：{}", key);
        LOGGER.info("vcode：{}", vcode);
        LOGGER.error("接收待办：{}:{}", todo3rd.getReceiveUserName(), todo3rd.getTitle());
        return TodoResponse.builder().code(200).type("success").message("保存成功").token(token).build();
    }

    @PutMapping(value = "/update/{vendorId}")
    public TodoResponse update(@PathVariable String vendorId, @RequestParam @NotBlank String timestamp,
        @RequestParam @NotBlank String token, @RequestParam @NotBlank String app, @RequestParam @NotBlank String key,
        @RequestParam @NotBlank String vcode, @RequestBody String json) {
        Todo3rd todo3rd = Y9JsonUtil.readValue(json, Todo3rd.class);
        assert todo3rd != null;
        LOGGER.info("vendorId：{}", vendorId);
        LOGGER.info("timestamp：{}", timestamp);
        LOGGER.info("token:{}", token);
        LOGGER.info("app:{}", app);
        LOGGER.info("key：{}", key);
        LOGGER.info("vcode：{}", vcode);
        LOGGER.error("更新待办：{}:{}", todo3rd.getReceiveUserName(), todo3rd.getTitle());
        return TodoResponse.builder().code(200).type("success").message("保存成功").token(token).build();
    }

    @DeleteMapping(value = "/delete/{vendorId}")
    public TodoResponse delete(@PathVariable String vendorId, @RequestParam @NotBlank String timestamp,
        @RequestParam @NotBlank String token, @RequestParam @NotBlank String app, @RequestParam @NotBlank String key,
        @RequestParam @NotBlank String vcode) {
        LOGGER.info("vendorId：{}", vendorId);
        LOGGER.info("timestamp：{}", timestamp);
        LOGGER.info("token:{}", token);
        LOGGER.info("app:{}", app);
        LOGGER.info("key：{}", key);
        LOGGER.info("vcode：{}", vcode);
        LOGGER.error("删除待办：{}", vendorId);
        return TodoResponse.builder().code(200).type("success").message("删除成功").token(token).build();
    }
}
