package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentWordApi;
import net.risesoft.entity.DocumentHistoryWord;
import net.risesoft.entity.DocumentWord;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.DocumentHistoryWordRepository;
import net.risesoft.service.DocumentHisWordService;
import net.risesoft.service.DocumentWordService;
import net.risesoft.service.config.ItemWordConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 正文接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/docWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentWordApiImpl implements DocumentWordApi {

    private final DocumentHistoryWordRepository documentHistoryWordRepository;

    private final DocumentWordService documentWordService;

    private final ItemWordConfService itemWordConfService;

    private final DocumentHisWordService documentHisWordService;

    /**
     * 根据流程编号和正文类型查询正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param wordType 正文类型
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @Override
    public Y9Result<List<DocumentWordModel>> findByProcessSerialNumberAndWordType(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String wordType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<DocumentWordModel> list =
            documentWordService.findByProcessSerialNumberAndWordType(processSerialNumber, wordType);
        return Y9Result.success(list);
    }

    /**
     * 根据流程编号和正文类型查询历史正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param wordType 正文类型
     * @return {@code Y9Result<List<DocumentWordModel>>}
     */
    @Override
    public Y9Result<List<DocumentWordModel>> findHisByProcessSerialNumberAndWordType(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String wordType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<DocumentHistoryWord> list = documentHistoryWordRepository
            .findByProcessSerialNumberAndWordTypeOrderByUpdateDateAsc(processSerialNumber, wordType);
        List<DocumentWordModel> resultList = new ArrayList<>();
        for (DocumentHistoryWord documentWord : list) {
            DocumentWordModel documentWordModel = new DocumentWordModel();
            Y9BeanUtil.copyProperties(documentWord, documentWordModel);
            resultList.add(documentWordModel);
        }
        return Y9Result.success(resultList);
    }

    /**
     * 根据id查询历史正文
     *
     * @param tenantId 租户id
     * @param id 主键
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @Override
    public Y9Result<DocumentWordModel> findHisWordById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentHistoryWord documentWord = documentHisWordService.findWordById(id);
        DocumentWordModel documentWordModel = null;
        if (documentWord != null) {
            documentWordModel = new DocumentWordModel();
            Y9BeanUtil.copyProperties(documentWord, documentWordModel);
        }
        return Y9Result.success(documentWordModel);
    }

    /**
     * 根据id查询正文
     *
     * @param tenantId 租户id
     * @param id 主键
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @Override
    public Y9Result<DocumentWordModel> findWordById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWord documentWord = documentWordService.findWordById(id);
        DocumentWordModel documentWordModel = null;
        if (documentWord != null) {
            documentWordModel = new DocumentWordModel();
            Y9BeanUtil.copyProperties(documentWord, documentWordModel);
        }
        return Y9Result.success(documentWordModel);
    }

    /**
     * 根据流程定义id和任务key获取正文权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param wordType 正文类型
     * @return {@code Y9Result<Boolean>}
     */
    @Override
    public Y9Result<Boolean> getPermissionWord(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String wordType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(
            itemWordConfService.getPermissionWord(positionId, itemId, processDefinitionId, taskDefKey, wordType));
    }

    /**
     * 替换正文
     *
     * @param tenantId 租户id
     * @param documentWordModel 正文实体
     * @param oldId 原正文id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>}
     */
    @Override
    public Y9Result<Object> replaceWord(@RequestParam String tenantId, @RequestBody DocumentWordModel documentWordModel,
        @RequestParam String oldId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWord documentWord = new DocumentWord();
        Y9BeanUtil.copyProperties(documentWordModel, documentWord);
        documentWordService.replaceWord(documentWord, oldId, taskId);
        return Y9Result.success();
    }

    /**
     * 保存正文
     *
     * @param tenantId 租户id
     * @param documentWordModel 正文实体
     * @return {@code Y9Result<DocumentWordModel>}
     */
    @Override
    public Y9Result<DocumentWordModel> saveWord(@RequestParam String tenantId,
        @RequestBody DocumentWordModel documentWordModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWord documentWord = new DocumentWord();
        Y9BeanUtil.copyProperties(documentWordModel, documentWord);
        DocumentWord newWord = documentWordService.saveWord(documentWord);
        DocumentWordModel documentWordModelResult = new DocumentWordModel();
        Y9BeanUtil.copyProperties(newWord, documentWordModelResult);
        return Y9Result.success(documentWordModelResult);
    }

}
