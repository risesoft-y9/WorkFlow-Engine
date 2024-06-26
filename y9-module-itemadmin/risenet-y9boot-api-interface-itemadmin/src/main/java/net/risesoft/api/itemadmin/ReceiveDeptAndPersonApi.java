package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ReceiveOrgUnit;
import net.risesoft.model.platform.Person;
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
     */
    @GetMapping("/findByDeptNameLike")
    Y9Result<List<ReceiveOrgUnit>> findByDeptNameLike(@RequestParam("tenantId") String tenantId,
        @RequestParam("name") String name);

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     */
    @GetMapping("/getReceiveDeptTree")
    Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTree(@RequestParam("tenantId") String tenantId);

    /**
     *
     * Description: 获取所有收发单位
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织id
     * @param name 名称
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     */
    @GetMapping("/getReceiveDeptTreeById")
    Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTreeById(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("name") String name);

    /**
     *
     * Description: 根据收发单位id,获取人员集合
     *
     * @param tenantId 租户id
     * @param deptId 单位id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员集合
     */
    @GetMapping("/getSendReceiveByDeptId")
    Y9Result<List<Person>> getSendReceiveByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId);

    /**
     * 根据人员id,获取对应的收发单位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     */
    @GetMapping("/getSendReceiveByUserId")
    Y9Result<List<ReceiveOrgUnit>> getSendReceiveByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);
}
