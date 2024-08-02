package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ReceiveOrgUnit;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;

/**
 * 收发单位管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ReceiveDeptAndPersonApi {

    /**
     * 根据name模糊搜索收发单位
     *
     * @param tenantId 租户id
     * @param name 搜索名称
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @GetMapping("/findByDeptNameLike")
    Y9Result<List<ReceiveOrgUnit>> findByDeptNameLike(@RequestParam("tenantId") String tenantId,
        @RequestParam("name") String name);

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @GetMapping("/getReceiveDeptTree")
    Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTree(@RequestParam("tenantId") String tenantId);

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @param orgUnitId 单位Id
     * @param name 名称
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @GetMapping("/getReceiveDeptTreeById")
    Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTreeById(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam(value = "name", required = false) String name);

    /**
     * 根据收发单位id,获取人员集合
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员集合
     * @since 9.6.6
     */
    @GetMapping("/getSendReceiveByDeptId")
    Y9Result<List<OrgUnit>> getSendReceiveByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId);

    /**
     * 根据id,获取对应的收发单位
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @GetMapping("/getSendReceiveByUserId")
    Y9Result<List<ReceiveOrgUnit>> getSendReceiveByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);
}
