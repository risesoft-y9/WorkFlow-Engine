package net.risesoft.controller.config;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemSystem;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemSystemService;

/**
 * @author qinman
 * @date 2026/07/09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemSystem", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemSystemController {

    private final ItemSystemService itemSystemService;

    /**
     * 删除系统
     *
     * @param id 主键
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam String id) {
        itemSystemService.deleteById(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据id获取系统
     *
     * @param id 主键
     * @return Y9Result<ItemSystem>
     */
    @GetMapping(value = "/findById")
    public Y9Result<ItemSystem> findById(@RequestParam String id) {
        ItemSystem itemSystem = itemSystemService.findById(id);
        return Y9Result.success(itemSystem, "获取成功");
    }

    /**
     * 获取所有系统列表（按排序号升序）
     *
     * @return Y9Result<List<ItemSystem>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<ItemSystem>> list() {
        List<ItemSystem> list = itemSystemService.listAll();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存或更新系统
     *
     * @param entity 系统实体
     * @return Y9Result<String>
     */
    @PostMapping(value = "/save")
    public Y9Result<String> save(ItemSystem entity) {
        itemSystemService.save(entity);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存排序
     *
     * @param ids id集合
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestBody List<String> ids) {
        itemSystemService.saveOrder(ids);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 校验系统英文名称是否已存在
     *
     * @param name 系统英文名称
     * @param id   当前记录id（编辑时传入，排除自身）
     * @return Y9Result<Boolean> true=已存在
     */
    @GetMapping(value = "/checkName")
    public Y9Result<Boolean> checkName(@RequestParam String name, @RequestParam(required = false) String id) {
        boolean exists = itemSystemService.isNameExist(name, id);
        return Y9Result.success(exists, "校验完成");
    }

    /**
     * 校验系统中文名称是否已存在
     *
     * @param cnName 系统中文名称
     * @param id     当前记录id（编辑时传入，排除自身）
     * @return Y9Result<Boolean> true=已存在
     */
    @GetMapping(value = "/checkCnName")
    public Y9Result<Boolean> checkCnName(@RequestParam String cnName, @RequestParam(required = false) String id) {
        boolean exists = itemSystemService.isCnNameExist(cnName, id);
        return Y9Result.success(exists, "校验完成");
    }
}
