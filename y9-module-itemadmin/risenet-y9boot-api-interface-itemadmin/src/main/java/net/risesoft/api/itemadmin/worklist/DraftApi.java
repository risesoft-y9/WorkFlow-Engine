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
     * @param systemName 系统id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/countBySystemName")
    Y9Result<Integer> countBySystemName(@RequestParam String systemName);

    /**
     * 彻底删除草稿
     *
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteDraft")
    Y9Result<Object> deleteDraft(@RequestParam String ids);

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDeleteDraftCount")
    Y9Result<Integer> getDeleteDraftCount(@RequestParam String itemId);

    /**
     * 根据流程编号获取草稿
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<DraftModel>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDraftByProcessSerialNumber")
    Y9Result<DraftModel> getDraftByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDraftCount")
    Y9Result<Integer> getDraftCount(@RequestParam String itemId);

    /**
     * 获取草稿列表
     *
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @param itemId 事项id
     * @param delFlag 是否删除
     * @return {@code Y9Page<Map<String, Object>>} 通用请求返回对象 - rows 是草稿列表
     * @since 9.6.6
     */
    @GetMapping("/getDraftList")
    Y9Page<Map<String, Object>> getDraftList(@RequestParam int page, @RequestParam int rows,
        @RequestParam(required = false) String title, @RequestParam String itemId, @RequestParam boolean delFlag);

    /**
     * 获取系统名称对应的草稿列表
     *
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @param systemName 系统名称
     * @param delFlag 是否删除
     * @return {@code Y9Page<DraftModel>} 通用请求返回对象 - rows 是草稿情数据
     * @since 9.6.6
     */
    @GetMapping("/getDraftListBySystemName")
    Y9Page<DraftModel> getDraftListBySystemName(@RequestParam int page, @RequestParam int rows,
        @RequestParam(required = false) String title, @RequestParam String systemName, @RequestParam boolean delFlag);

    /**
     * 编辑草稿
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data 是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/openDraft")
    Y9Result<OpenDataModel> openDraft(@RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam boolean mobile);

    /**
     * 还原草稿
     *
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/reduction")
    Y9Result<Object> reduction(@RequestParam String ids);

    /**
     * 删除草稿
     *
     * @param ids 草稿ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeDraft")
    Y9Result<Object> removeDraft(@RequestParam String ids);

    /**
     * 保存草稿
     *
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
    Y9Result<Object> saveDraft(@RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey, @RequestParam(required = false) String number,
        @RequestParam(required = false) String level, @RequestParam String title);

}
