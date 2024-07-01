package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Entrust4PositionApi {

    /**
     * 删除委托
     *
     * @param tenantId 租户id
     * @param id 委托id
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping(value = "/deleteEntrust")
    Y9Result<Object> deleteEntrust(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id)
        throws Exception;

    /**
     * 获取委托设置列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<List<EntrustModel>>
     */
    @GetMapping(value = "/getEntrustList")
    Y9Result<List<EntrustModel>> getEntrustList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 获取当前岗被委托记录
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<List<EntrustModel>>
     */
    @GetMapping(value = "/getMyEntrustList")
    Y9Result<List<EntrustModel>> getMyEntrustList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 保存或者更新委托对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param entrustModel 实体类（EntrustModel）
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestBody EntrustModel entrustModel);
}
