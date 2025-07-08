package net.risesoft.controller.config;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.interfaceinfo.ItemInterfaceBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemInterfaceBindService;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemInterfaceBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemInterfaceBindController {

    private final ItemInterfaceBindService itemInterfaceBindService;

    /**
     * 获取绑定列表
     *
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemInterfaceBind>> getBindList(@RequestParam String itemId) {
        List<ItemInterfaceBind> list = itemInterfaceBindService.listByItemId(itemId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/removeBind")
    public Y9Result<String> removeBind(@RequestParam String id) {
        itemInterfaceBindService.removeBind(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存绑定
     *
     * @param interfaceIds 接口id
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(@RequestParam String[] interfaceIds, @RequestParam String itemId) {
        itemInterfaceBindService.saveBind(itemId, interfaceIds);
        return Y9Result.successMsg("保存成功");
    }
}
