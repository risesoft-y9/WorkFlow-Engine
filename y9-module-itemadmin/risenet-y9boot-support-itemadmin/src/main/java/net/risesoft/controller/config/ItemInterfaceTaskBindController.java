package net.risesoft.controller.config;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemInterfaceTaskBind;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemInterfaceTaskBindRepository;
import net.risesoft.service.config.ItemInterfaceTaskBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/interfaceTaskBind", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String interfaceId, @RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        itemInterfaceTaskBindService.copyBind(itemId, interfaceId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List<FlowElementModel>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<FlowElementModel>> getBpmList(@RequestParam String itemId, @RequestParam String interfaceId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<FlowElementModel> list = processDefinitionApi.listUserTask(tenantId, processDefinitionId).getData();
        for (FlowElementModel feModel : list) {
            String elementKey = feModel.getElementKey();
            ItemInterfaceTaskBind bind =
                itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(
                    elementKey, itemId, processDefinitionId, interfaceId);
            feModel.setBind(false);
            if (bind != null) {
                feModel.setBind(true);
                feModel.setCondition(bind.getExecuteCondition());
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
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(@RequestParam String interfaceId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, String elementKey, @RequestParam String condition) {
        itemInterfaceTaskBindService.saveBind(itemId, interfaceId, processDefinitionId, elementKey, condition);
        return Y9Result.successMsg("保存成功");
    }
}
