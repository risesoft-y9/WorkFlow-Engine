package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.pojo.Y9Result;

/**
 * 正文接口管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DocumentWordApi {

    /**
     * 根据流程编号和正文类别查询正文信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param wordType 正文类别
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @GetMapping("/findByProcessSerialNumberAndWordType")
    Y9Result<List<DocumentWordModel>> findByProcessSerialNumberAndWordType(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("wordType") String wordType);

    /**
     * 根据流程编号和正文类别查询历史正文信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param wordType 正文类别
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @GetMapping("/findHisByProcessSerialNumberAndWordType")
    Y9Result<List<DocumentWordModel>> findHisByProcessSerialNumberAndWordType(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("wordType") String wordType);

    /**
     * 根据id查询历史正文信息
     *
     * @param tenantId 租户id
     * @param id 正文id
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @GetMapping("/findHisWordById")
    Y9Result<DocumentWordModel> findHisWordById(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id);

    /**
     * 根据id查询正文信息
     *
     * @param tenantId 租户id
     * @param id 正文id
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @GetMapping("/findWordById")
    Y9Result<DocumentWordModel> findWordById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取正文权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param wordType 正文类型
     * @return {@code Y9Result<Boolean>}
     */
    @GetMapping("/getPermissionWord")
    Y9Result<Boolean> getPermissionWord(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey,
        @RequestParam("wordType") String wordType);

    /**
     * 替换正文
     *
     * @param tenantId 租户id
     * @param documentWordModel 正文实体
     * @param oldId 原正文id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>}
     */
    @PostMapping(value = "/replaceWord", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> replaceWord(@RequestParam("tenantId") String tenantId,
        @RequestBody DocumentWordModel documentWordModel, @RequestParam("oldId") String oldId,
        @RequestParam("taskId") String taskId);

    /**
     * 保存正文信息
     *
     * @param tenantId 租户id
     * @param documentWordModel 正文信息
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @PostMapping(value = "/saveWord", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<DocumentWordModel> saveWord(@RequestParam("tenantId") String tenantId,
        @RequestBody DocumentWordModel documentWordModel);
}
