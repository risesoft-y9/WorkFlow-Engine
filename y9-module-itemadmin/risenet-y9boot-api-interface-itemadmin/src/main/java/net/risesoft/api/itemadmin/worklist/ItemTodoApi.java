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
 * 待办任务
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemTodoApi {

    /**
     * 根据用户id和系统名称查询待办数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data 是待办任务数量
     * @since 9.6.6
     */
    @GetMapping("/countByUserId")
    Y9Result<Integer> countByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据用户id和系统名称查询待办数量
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
     * 根据用户id和系统名称查询待办列表(以发送时间排序)
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

    /**
     * 根据用户id和系统名称查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param taskDefKey 任务key
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @GetMapping("/findByUserIdAndSystemNameAndTaskDefKey")
    Y9Page<ActRuDetailModel> findByUserIdAndSystemNameAndTaskDefKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据用户id和系统名称、搜索集合查询待办列表(以发送时间排序)
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

    @PostMapping("/searchListByUserIdAndSystemNameAndTaskDefKey")
    Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemNameAndTaskDefKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey,
        @RequestBody(required = false) String searchMapStr);

    @PostMapping("/searchByUserIdAndSystemName4Other")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemName4Other(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody String searchMapStr, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    @PostMapping("/searchListByUserIdAndSystemName4Other")
    Y9Result<List<ActRuDetailModel>> searchListByUserIdAndSystemName4Other(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestBody String searchMapStr);

    /**
     * 根据用户id和系统名称、搜索集合查询待办列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param taskDefKey 任务key
     * @param searchMapStr 搜索集合
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @PostMapping("/searchByUserIdAndSystemNameAndTaskDefKey")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemNameAndTaskDefKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey, @RequestBody String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

}
