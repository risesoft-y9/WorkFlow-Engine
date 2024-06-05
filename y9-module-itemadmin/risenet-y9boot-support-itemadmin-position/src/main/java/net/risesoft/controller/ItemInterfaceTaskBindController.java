package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemInterfaceTaskBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemInterfaceTaskBindRepository;
import net.risesoft.service.ItemInterfaceTaskBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/interfaceTaskBind")
public class ItemInterfaceTaskBindController {

    private final ProcessDefinitionApi processDefinitionApi;

    private final ItemInterfaceTaskBindService itemInterfaceTaskBindService;

    private final ItemInterfaceTaskBindRepository itemInterfaceTaskBindRepository;

    /**
     * 复制绑定
     *
     * @param interfaceId 接口id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam(required = true) String interfaceId, @RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        itemInterfaceTaskBindService.copyBind(itemId, interfaceId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getBpmList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String interfaceId, @RequestParam(required = true) String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionApi.getFlowElement(tenantId, processDefinitionId, false);
        for (Map<String, Object> map : list) {
            String elementKey = (String)map.get("elementKey");
            ItemInterfaceTaskBind bind = itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(elementKey, itemId, processDefinitionId, interfaceId);
            map.put("bind", false);
            if (bind != null) {
                map.put("bind", true);
                map.put("condition", bind.getExecuteCondition());
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存绑定
     *
     * @param interfaceId 接口id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param elementKey 任务路由key
     * @param condition 执行条件
     * @return
     */
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBind(@RequestParam(required = true) String interfaceId, @RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, String elementKey, @RequestParam String condition) {
        itemInterfaceTaskBindService.saveBind(itemId, interfaceId, processDefinitionId, elementKey, condition);
        return Y9Result.successMsg("保存成功");
    }
}
