package net.risesoft.api.itemadmin.documentword;

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

    @GetMapping("/copyByProcessSerialNumberAndWordType")
    Y9Result<Object> copyByProcessSerialNumberAndWordType(@RequestParam String sourceProcessSerialNumber,
        @RequestParam String targetProcessSerialNumber, @RequestParam String wordType);

    /**
     * 根据流程编号和正文类别查询正文信息
     *
     * @param processSerialNumber 流程编号
     * @param wordType 正文类别
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @GetMapping("/findByProcessSerialNumberAndWordType")
    Y9Result<List<DocumentWordModel>> findByProcessSerialNumberAndWordType(@RequestParam String processSerialNumber,
        @RequestParam String wordType);

    /**
     * 根据流程编号和正文类别查询历史正文信息
     *
     * @param processSerialNumber 流程编号
     * @param wordType 正文类别
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @GetMapping("/findHisByProcessSerialNumberAndWordType")
    Y9Result<List<DocumentWordModel>> findHisByProcessSerialNumberAndWordType(@RequestParam String processSerialNumber,
        @RequestParam String wordType);

    /**
     * 根据id查询历史正文信息
     *
     * @param id 正文id
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @GetMapping("/findHisWordById")
    Y9Result<DocumentWordModel> findHisWordById(@RequestParam String id);

    /**
     * 根据id查询正文信息
     *
     * @param id 正文id
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @GetMapping("/findWordById")
    Y9Result<DocumentWordModel> findWordById(@RequestParam String id);

    /**
     * 获取正文权限
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param wordType 正文类型
     * @return {@code Y9Result<Boolean>}
     */
    @GetMapping("/getPermissionWord")
    Y9Result<Boolean> getPermissionWord(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String wordType);

    /**
     * 替换正文
     *
     * @param documentWordModel 正文实体
     * @param oldId 原正文id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>}
     */
    @PostMapping(value = "/replaceWord", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> replaceWord(@RequestBody DocumentWordModel documentWordModel, @RequestParam String oldId,
        @RequestParam String taskId);

    /**
     * 保存正文信息
     *
     * @param documentWordModel 正文信息
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @PostMapping(value = "/saveWord", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<DocumentWordModel> saveWord(@RequestBody DocumentWordModel documentWordModel);
}
