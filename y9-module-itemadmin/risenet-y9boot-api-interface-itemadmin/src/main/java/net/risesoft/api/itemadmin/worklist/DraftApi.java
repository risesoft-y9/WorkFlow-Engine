package net.risesoft.api.itemadmin.worklist;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 草稿列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DraftApi {

    /**
     * 根据系统名称计数
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param systemName 系统id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/countBySystemName")
    Y9Result<Integer> countBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("systemName") String systemName);

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteDraft")
    Y9Result<Object> deleteDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDeleteDraftCount")
    Y9Result<Integer> getDeleteDraftCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程编号获取草稿
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<DraftModel>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDraftByProcessSerialNumber")
    Y9Result<DraftModel> getDraftByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDraftCount")
    Y9Result<Integer> getDraftCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId);

    /**
     * 获取草稿列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @param itemId 事项id
     * @param delFlag 是否删除
     * @return {@code Y9Page<Map<String, Object>>} 通用请求返回对象 - rows 是草稿列表
     * @since 9.6.6
     */
    @GetMapping("/getDraftList")
    Y9Page<Map<String, Object>> getDraftList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("page") int page, @RequestParam("rows") int rows,
        @RequestParam(value = "title", required = false) String title, @RequestParam("itemId") String itemId,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 获取系统名称对应的草稿列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @param systemName 系统名称
     * @param delFlag 是否删除
     * @return {@code Y9Page<DraftModel>} 通用请求返回对象 - rows 是草稿情数据
     * @since 9.6.6
     */
    @GetMapping("/getDraftListBySystemName")
    Y9Page<DraftModel> getDraftListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("page") int page, @RequestParam("rows") int rows,
        @RequestParam(value = "title", required = false) String title, @RequestParam("systemName") String systemName,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 编辑草稿
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data 是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/openDraft")
    Y9Result<OpenDataModel> openDraft(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("mobile") boolean mobile);

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/reduction")
    Y9Result<Object> reduction(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeDraft")
    Y9Result<Object> removeDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 保存草稿
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 编号
     * @param level 紧急程度
     * @param title 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveDraft")
    Y9Result<Object> saveDraft(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam(value = "number", required = false) String number,
        @RequestParam(value = "level", required = false) String level, @RequestParam("title") String title);

}
