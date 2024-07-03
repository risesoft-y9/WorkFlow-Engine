package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 待办任务
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemTodoApi {

    /**
     * 查询待办任务数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data 是待办任务数量
     * @throws Exception Exception
     */
    @GetMapping("/countByUserIdAndSystemName")
    Y9Result<Integer> countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName)
        throws Exception;

    /**
     * 查询待办任务，以发送时间排序
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @throws Exception Exception
     */
    @GetMapping("/findByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询待办任务，以发送时间排序
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @throws Exception Exception
     */
    @GetMapping("/searchByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "tableName") String tableName, @RequestParam(value = "searchMapStr") String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

}
