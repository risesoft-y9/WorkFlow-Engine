package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Validated
public interface QueryListApi {

    /**
     * 获取综合查询列表
     *
     * @param tenantId 租户id
     * @param userId 岗位id
     * @param systemName 系统名称
     * @param state 状态
     * @param createDate 开始日期
     * @param tableName 表名称
     * @param searchMapStr 搜索条件
     * @param page 页面
     * @param rows 条数
     * @return ItemPage&lt;ActRuDetailModel&gt;
     */
    @GetMapping("/getQueryList")
    ItemPage<ActRuDetailModel> getQueryList(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("userId") @NotBlank String userId, @RequestParam("systemName") @NotBlank String systemName, @RequestParam("state") String state, @RequestParam("createDate") String createDate,
        @RequestParam("tableName") String tableName, @RequestParam("searchMapStr") String searchMapStr, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

}
