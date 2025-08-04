package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Item;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.entity.interfaceinfo.ItemInterfaceParamsBind;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.interfaceinfo.ItemInterfaceParamsBindRepository;
import net.risesoft.service.config.ItemInterfaceParamsBindService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.service.form.Y9TableService;

/**
 * @author zhangchongjie
 * @date 2024/05/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/interfaceParamsBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemInterfaceParamsBindController {

    private final ItemInterfaceParamsBindService itemInterfaceParamsBindService;

    private final ItemService itemService;

    private final Y9TableService y9TableService;

    private final Y9TableFieldService y9TableFieldService;

    private final ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    /**
     * 获取绑定信息
     *
     * @param id 绑定id
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBindInfo")
    public Y9Result<Map<String, Object>> getBindInfo(@RequestParam(required = false) String id,
        @RequestParam String itemId) {
        Map<String, Object> resMap = new HashMap<>(16);
        Item item = itemService.findById(itemId);
        Y9Page<Y9Table> pageList = y9TableService.pageTables(item.getSystemName(), 1, 500);
        List<String> tableNameList = new ArrayList<>();
        List<Y9Table> tableList = new ArrayList<>();
        List<Map<String, Object>> tableField = new ArrayList<>();
        for (Y9Table table : pageList.getRows()) {
            tableNameList.add(table.getTableName());
            tableList.add(table);
            Map<String, Object> tableFieldMap = new HashMap<>();
            tableFieldMap.put("tableName", table.getTableName());
            List<Y9TableField> fieldlist = y9TableFieldService.listByTableId(table.getId());
            tableFieldMap.put("fieldlist", fieldlist);
            tableField.add(tableFieldMap);
        }
        if (StringUtils.isNotBlank(id)) {
            ItemInterfaceParamsBind info = itemInterfaceParamsBindRepository.findById(id).orElse(null);
            resMap.put("info", info);
        }
        resMap.put("tableList", tableList);
        resMap.put("tablefield", tableField);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取绑定列表
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param type 参数类型
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemInterfaceParamsBind>> getBindList(@RequestParam String itemId,
        @RequestParam String interfaceId, @RequestParam String type) {
        List<ItemInterfaceParamsBind> list =
            itemInterfaceParamsBindService.listByItemIdAndInterfaceIdAndType(itemId, interfaceId, type);
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
        itemInterfaceParamsBindService.removeBind(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存绑定
     *
     * @param info 绑定信息
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(ItemInterfaceParamsBind info) {
        itemInterfaceParamsBindService.saveBind(info);
        return Y9Result.successMsg("保存成功");
    }

}
