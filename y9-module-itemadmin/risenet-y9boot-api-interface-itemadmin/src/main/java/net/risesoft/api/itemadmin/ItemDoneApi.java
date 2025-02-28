package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemDoneApi {

    /**
     * 根据用户id和系统名称查询办结数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是办结任务数量
     * @since 9.6.6
     */
    @GetMapping("/countByUserIdAndSystemName")
    Y9Result<Integer> countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName);

    /**
     * 根据系统名称查询办结列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是监控办结列表
     * @since 9.6.6
     */
    @GetMapping("/findBySystemName")
    Y9Page<ActRuDetailModel> findBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据用户id和系统名称查询办结列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @GetMapping("/findByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据科室id和系统名称查询办结列表
     *
     * @param tenantId 租户id
     * @param deptId 科室id
     * @param isBureau 是否是部门
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByDeptIdAndSystemName")
    Y9Page<ActRuDetailModel> findByDeptIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId, @RequestParam("isBureau") boolean isBureau,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统名称、搜索内容查询办结列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是监控办结列表
     * @since 9.6.6
     */
    @PostMapping("/searchBySystemName")
    Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName,
        @RequestBody String searchMapStr, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询办结列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @PostMapping("/searchByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam(value = "tableName") String tableName, @RequestBody String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据用户id、系统名称、表名称、搜索内容查询办结列表
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @param systemName 系统名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 - rows 是个人办结列表
     * @since 9.6.6
     */
    @PostMapping("/searchByDeptIdAndSystemName")
    Y9Page<ActRuDetailModel> searchByDeptIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId, @RequestParam("isBureau") boolean isBureau,
        @RequestParam("systemName") String systemName, @RequestBody String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);
}
