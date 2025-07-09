package net.risesoft.api.itemadmin.worklist;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;

/**
 * 综合查询
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
public interface QueryListApi {

    /**
     * 综合搜索
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
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - data 是综合查询列表
     * @since 9.6.6
     */
    @PostMapping("/getQueryList")
    Y9Page<ActRuDetailModel> getQueryList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "createDate", required = false) String createDate,
        @RequestParam(value = "tableName", required = false) String tableName, @RequestBody String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

}
