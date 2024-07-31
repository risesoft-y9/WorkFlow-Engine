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
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<List<LinkInfoModel>>} 通用请求返回对象 - data 是事项绑定链接
     * @since 9.6.6
     */
    @GetMapping("/getItemLinkList")
    Y9Result<List<LinkInfoModel>> getItemLinkList(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("itemId") @NotBlank String itemId);

}
