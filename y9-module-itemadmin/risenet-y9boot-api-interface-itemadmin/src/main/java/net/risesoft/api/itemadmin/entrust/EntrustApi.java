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
     * @param id 委托id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteEntrust")
    Y9Result<Object> deleteEntrust(@RequestParam String id);

    /**
     * 获取岗位的委托列表
     * 
     * @param ownerId 委托的岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @GetMapping(value = "/findByOwnerId")
    Y9Result<List<EntrustModel>> findByOwnerId(@RequestParam String ownerId);

    /**
     * 获取岗位的被委托列表
     * 
     * @param assigneeId 被委托岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @GetMapping(value = "/findByAssigneeId")
    Y9Result<List<EntrustModel>> findByAssigneeId(@RequestParam String assigneeId);

    /**
     * 保存或更新委托
     *
     * @param entrustModel 实体类（EntrustModel）
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestBody EntrustModel entrustModel);
}
