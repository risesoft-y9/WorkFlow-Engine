package net.risesoft.api.itemadmin.opinion;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.OpinionSignListModel;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.pojo.Y9Result;

/**
 * 会签意见接口
 *
 * @author qinman
 * @date 2024/12/16
 */
public interface OpinionSignApi {

    /**
     * 检查当前taskId任务节点是否已经签写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param signDeptDetailId 会签部门详情id
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是是否已经签写意见
     * @since 9.6.6
     */
    @GetMapping("/checkSignOpinion")
    Y9Result<Boolean> checkSignOpinion(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("signDeptDetailId") String signDeptDetailId, @RequestParam("taskId") String taskId);

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id) throws Exception;

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<OpinionSignModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @GetMapping("/getById")
    Y9Result<OpinionSignModel> getById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取个人意见列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程序列号
     * @param signDeptDetailId 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<List<OpinionSignListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
     */
    @GetMapping("/personCommentList")
    Y9Result<List<OpinionSignListModel>> personCommentList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("signDeptDetailId") String signDeptDetailId, @RequestParam("itembox") String itembox,
        @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam("opinionFrameMark") String opinionFrameMark);

    /**
     * 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param opinionSignModel 意见信息
     * @return {@code Y9Result<OpinionSignModel>} 通用请求返回对象 - data 是意见信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<OpinionSignModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestBody OpinionSignModel opinionSignModel) throws Exception;
}
