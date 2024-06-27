package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 在办任务管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemDoingApi {

    /**
     * 查询在办任务数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return int
     * @throws Exception Exception
     */
    @GetMapping("/countByUserIdAndSystemName")
    Y9Result<Integer> countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(监控在办)
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return ItemPage&lt;ActRuDetailModel&gt;
     * @throws Exception Exception
     */
    @GetMapping("/findBySystemName")
    Y9Page<ActRuDetailModel> findBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(个人在办)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return ItemPage&lt;ActRuDetailModel&gt;
     * @throws Exception Exception
     */
    @GetMapping("/findByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(监控在办)
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return ItemPage&lt;ActRuDetailModel&gt;
     * @throws Exception Exception
     */
    @GetMapping("/searchBySystemName")
    Y9Page<ActRuDetailModel> searchBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("tableName") String tableName,
        @RequestParam("searchMapStr") String searchMapStr, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(个人在办搜索)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param tableName 表名称
     * @param searchMapStr 搜索内容
     * @param page page
     * @param rows rows
     * @return ItemPage&lt;ActRuDetailModel&gt;
     * @throws Exception Exception
     */
    @GetMapping("/searchByUserIdAndSystemName")
    Y9Page<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("tableName") String tableName, @RequestParam("searchMapStr") String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;
}
