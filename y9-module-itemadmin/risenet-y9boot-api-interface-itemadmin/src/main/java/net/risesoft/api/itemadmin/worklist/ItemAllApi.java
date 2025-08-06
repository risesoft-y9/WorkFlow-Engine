package net.risesoft.api.itemadmin.worklist;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 所有本人经手的件接口
 *
 * @author qinman
 * @date 2024/12/19
 */
public interface ItemAllApi {

    /**
     * 根据用户id和系统名称查询待办数量 #TODO
     * 
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data 是待办任务数量
     * @since 9.6.6
     */
    @GetMapping("/countByUserIdAndSystemName")
    Y9Result<Integer> countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName);

    /**
     * 根据用户id和系统名称查询待办列表(以发送时间排序)#TODO
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param queryParamModel 查询参数
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @PostMapping("/findByUserId")
    Y9Page<ActRuDetailModel> findByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestBody QueryParamModel queryParamModel);

    /**
     * 根据用户id和系统名称查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @GetMapping("/findByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    @GetMapping("/findBySystemName")
    Y9Page<ActRuDetailModel> findBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据用户id和系统名称、表名称、搜索集合查询待办列表(以发送时间排序)#TODO
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @PostMapping("/searchByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody String searchMapStr, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    @PostMapping("/searchBySystemName")
    Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody String searchMapStr, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    @PostMapping("/searchListBySystemName")
    Y9Result<List<ActRuDetailModel>> searchListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody String searchMapStr);

    @PostMapping("/searchListByUserIdAndSystemName")
    Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody(required = false) String searchMapStr);

    /**
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumbers 流程序列号数组
     * @return {@code List<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @PostMapping("/searchByProcessSerialNumbers")
    Y9Result<List<ActRuDetailModel>> searchByProcessSerialNumbers(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumbers") String[] processSerialNumbers);

}
