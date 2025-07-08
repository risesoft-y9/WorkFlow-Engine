package net.risesoft.api.itemadmin.entrust;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.pojo.Y9Result;

/**
 * 出差委托接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface EntrustApi {

    /**
     * 删除委托
     *
     * @param tenantId 租户id
     * @param id 委托id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteEntrust")
    Y9Result<Object> deleteEntrust(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取委托列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getEntrustList")
    Y9Result<List<EntrustModel>> getEntrustList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 获取我的委托列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getMyEntrustList")
    Y9Result<List<EntrustModel>> getMyEntrustList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 保存或更新委托
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param entrustModel 实体类（EntrustModel）
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestBody EntrustModel entrustModel);
}
