package net.risesoft.api.itemadmin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.LinkInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项链接接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@Validated
public interface ItemLinkApi {

    /**
     * 获取有权限的事项绑定链接
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<List<LinkInfoModel>>} 通用请求返回对象 - data 是事项绑定链接
     * @since 9.6.6
     */
    @GetMapping("/getItemLinkList")
    Y9Result<List<LinkInfoModel>> getItemLinkList(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("itemId") @NotBlank String itemId);

    /**
     * 获取有权限的节点绑定链接
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 流程节点id
     * @return {@code Y9Result<LinkInfoModel>} 通用请求返回对象 - data 是节点绑定链接
     * @since 9.6.6
     */
    @GetMapping("/getItemNodeLinkList")
    Y9Result<LinkInfoModel> getItemNodeLinkList(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("itemId") @NotBlank String itemId,
        @RequestParam("processDefinitionId") @NotBlank String processDefinitionId,
        @RequestParam("taskDefKey") @NotBlank String taskDefKey);

}
