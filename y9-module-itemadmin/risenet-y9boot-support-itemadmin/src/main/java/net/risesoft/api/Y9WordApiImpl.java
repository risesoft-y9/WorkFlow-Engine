package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.user.UserApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.Item;
import net.risesoft.entity.documentword.Y9Word;
import net.risesoft.entity.documentword.Y9WordHistory;
import net.risesoft.entity.template.ItemWordTemplateBind;
import net.risesoft.entity.template.TaoHongTemplate;
import net.risesoft.entity.template.WordTemplate;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemWordTypeEnum;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.documentword.Y9WordHistoryRepository;
import net.risesoft.repository.documentword.Y9WordRepository;
import net.risesoft.repository.template.ItemWordTemplateBindRepository;
import net.risesoft.repository.template.WordTemplateRepository;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.template.TaoHongTemplateService;
import net.risesoft.service.word.Y9WordHistoryService;
import net.risesoft.service.word.Y9WordService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.Y9FileStoreService;

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
@RequestMapping(value = "/services/rest/y9Word", produces = MediaType.APPLICATION_JSON_VALUE)
public class Y9WordApiImpl implements Y9WordApi {

    private final Y9WordRepository y9WordRepository;

    private final Y9WordService y9WordService;

    private final Y9WordHistoryService y9WordHistoryService;

    private final Y9WordHistoryRepository y9WordHistoryRepository;

    private final TaoHongTemplateService taoHongTemplateService;

    private final WordTemplateRepository wordTemplateRepository;

    private final ItemWordTemplateBindRepository wordTemplateBindRepository;

    private final PersonApi personApi;

    private final UserApi userApi;

    private final OrgUnitApi orgUnitApi;

    private final Y9FileStoreService y9FileStoreService;

    private final TaskApi taskApi;

    private final ItemService itemService;

    private final RepositoryApi repositoryApi;

    /**
     * 根据流程编号删除正文，同时删除文件历史的文件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delBatchByProcessSerialNumbers(@RequestParam String tenantId,
        @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        y9WordService.delBatchByProcessSerialNumbers(processSerialNumbers);
        y9WordHistoryService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    /**
     * 删除撤销PDF文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByIsTaoHong(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String isTaoHong) {
        List<Y9Word> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        for (Y9Word y9Word : list) {
            y9WordRepository.delete(y9Word);
            try {
                y9FileStoreService.deleteFile(y9Word.getFileStoreId());
            } catch (Exception e) {
                LOGGER.error("删除文件失败", e);
            }
        }
        if (ItemWordTypeEnum.PDF1.getValue().equals(isTaoHong)) {
            y9WordHistoryService.deleteHistoryWordByIsTaoHong(processSerialNumber, "3");
        }
        return Y9Result.success();
    }

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Y9WordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordModel> exchangeFindWordByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processSerialNumber) {
        Y9WordModel word = new Y9WordModel();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);
            List<Y9Word> list = y9WordService.listByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                Y9Word y9Word = list.get(0);
                Person user = personApi.get(tenantId, y9Word.getUserId()).getData();
                word = getY9Word(y9Word);
                word.setFileName(y9Word.getTitle() + y9Word.getFileType());
                word.setUserName(user.getName());
            }
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败（数据传输）", e);
            return Y9Result.failure("获取正文文件信息失败!");
        }
        return Y9Result.success(word);
    }

    /**
     * 根据任务id获取正文历史文件信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<Y9WordHistoryModel>} 通用请求返回对象 - data 是历史正文文件信息对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordHistoryModel> findHistoryVersionDoc(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        List<Y9WordHistory> historyWordList = y9WordHistoryService.listByTaskId(taskId);
        Y9WordHistoryModel history = new Y9WordHistoryModel();
        if (null != historyWordList && !historyWordList.isEmpty()) {
            Y9WordHistory historyWord = historyWordList.get(0);
            populateHistoryModel(history, historyWord, tenantId);
        }
        return Y9Result.success(history);
    }

    /**
     * 填充历史文档模型数据
     */
    private void populateHistoryModel(Y9WordHistoryModel history, Y9WordHistory historyWord, String tenantId) {
        history.setOpenWordOrPdf(determineOpenWordOrPdf(historyWord.getIstaohong()));
        history.setTitle(historyWord.getTitle());
        history.setFileSize(historyWord.getFileSize());
        history.setId(historyWord.getId());
        history.setProcessSerialNumber(historyWord.getProcessSerialNumber());
        history.setSaveDate(historyWord.getSaveDate());
        history.setFileStoreId(historyWord.getFileStoreId());
        history.setFileType(historyWord.getFileType());
        history.setIsTaoHong(StringUtils.isNotBlank(historyWord.getIstaohong()) ? historyWord.getIstaohong() : "");
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, historyWord.getUserId()).getData();
        history.setUserName(orgUnit != null && StringUtils.isNotBlank(orgUnit.getId()) ? orgUnit.getName() : "");
        history.setUserId(historyWord.getUserId());
    }

    /**
     * 根据套红类型确定打开方式
     */
    private String determineOpenWordOrPdf(String istaohong) {
        if (ItemWordTypeEnum.PDF.getValue().equals(istaohong) || ItemWordTypeEnum.PDF1.getValue().equals(istaohong)
            || ItemWordTypeEnum.PDF2.getValue().equals(istaohong)) {
            return "openPDF";
        }
        return ItemConsts.OPENWORD_KEY;
    }

    /**
     * 根据流程编号获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Y9WordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordModel> findWordByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Word> list = y9WordService.listByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                Y9Word y9Word = list.get(0);
                Y9WordModel word = getY9Word(y9Word);
                word.setFileName(y9Word.getTitle() + y9Word.getFileType());

                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, y9Word.getUserId()).getData();
                word.setUserName(orgUnit.getName());
                return Y9Result.success(word);
            }
        } catch (Exception e) {
            LOGGER.error("根据流程编号获取正文文件信息失败", e);
            return Y9Result.failure("获取正文文件信息失败");
        }
        return Y9Result.success(null);
    }

    private Y9WordModel getY9Word(Y9Word y9Word) {
        Y9WordModel wordModel = new Y9WordModel();
        wordModel.setId(y9Word.getId());
        wordModel.setTitle(y9Word.getTitle());
        wordModel.setFileStoreId(y9Word.getFileStoreId());
        wordModel.setFileSize(y9Word.getFileSize());
        wordModel.setFileName(y9Word.getFileName());
        wordModel.setProcessSerialNumber(y9Word.getProcessSerialNumber());
        wordModel.setSaveDate(y9Word.getSaveDate());
        wordModel.setUserId(y9Word.getUserId());
        wordModel.setIsTaoHong(y9Word.getIstaohong());
        wordModel.setFileType(y9Word.getFileType());
        return wordModel;
    }

    /**
     * 获取当前流程所有的正文文件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<Y9WordModel>>} 通用请求返回对象 - data 是正文文件信息列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Y9WordModel>> getWordList(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber) {
        List<Y9WordModel> retList = new ArrayList<>();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);
            List<Y9Word> list = y9WordRepository.findByProcessSerialNumber(processSerialNumber);
            for (Y9Word word : list) {
                Y9WordModel model = getY9Word(word);
                retList.add(model);
            }
        } catch (Exception e) {
            LOGGER.error("获取正文列表失败", e);
            return Y9Result.failure("获取正文列表失败");
        }
        return Y9Result.success(retList);
    }

    /**
     * 获取正文文件存储路径信息（用于打开正文）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param bindValue 绑定值
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     * @since 9.6.6
     */

    @Override
    public Y9Result<String> openDocument(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String itemId, String bindValue) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);

        // 尝试获取已存在的正文文件
        List<Y9Word> wordList = getExistingWordList(processSerialNumber, bindValue);
        if (!wordList.isEmpty()) {
            Y9Word y9Word = wordList.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错!!");
            }
        } else {
            // 打开事项配置的正文模板
            return openTemplateDocument(tenantId, itemId, processSerialNumber, bindValue);
        }
    }

    /**
     * 获取已存在的正文文件列表
     */
    private List<Y9Word> getExistingWordList(String processSerialNumber, String bindValue) {
        if (StringUtils.isBlank(processSerialNumber)) {
            return new ArrayList<>();
        }

        if (StringUtils.isNotBlank(bindValue) && !"null".equals(bindValue)) {
            return y9WordService.listByProcessSerialNumberAndDocCategory(processSerialNumber, bindValue);
        } else {
            return y9WordService.listByProcessSerialNumber(processSerialNumber);
        }
    }

    /**
     * 打开模板文档
     */
    private Y9Result<String> openTemplateDocument(String tenantId, String itemId, String processSerialNumber,
        String bindValue) {
        try {
            Item item = itemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = processDefinition.getId();

            WordTemplate wordTemplate = findWordTemplate(itemId, processDefinitionId, bindValue);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                return Y9Result.success(wordTemplate.getFilePath());
            } else {
                String errorMsg = buildTemplateNotFoundErrorMsg(processSerialNumber, bindValue);
                LOGGER.error(errorMsg);
                return Y9Result.failure(errorMsg);
            }
        } catch (Exception e) {
            LOGGER.error("获取模板文档失败", e);
            return Y9Result.failure("获取模板文档失败: " + e.getMessage());
        }
    }

    /**
     * 构建模板未找到的错误信息
     */
    private String buildTemplateNotFoundErrorMsg(String processSerialNumber, String bindValue) {
        if (StringUtils.isNotBlank(bindValue)) {
            return "数据库没有processSerialNumber=" + processSerialNumber + "和bindValue=" + bindValue + "绑定的正文，请联系管理员";
        } else {
            return "数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员";
        }
    }

    /**
     * 根据流程编号打开正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openDocumentByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        // 获取套红文档
        List<Y9Word> list = y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, "1");
        /*
         * 套红文档不存在,则获取未套红文档
         */
        if (list.isEmpty()) {
            list = y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, "0");
        }
        if (!list.isEmpty()) {
            Y9Word y9Word = list.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {
                return Y9Result.failure("获取fileStoreId为空，保存正文的时候出错！");
            }
        }
        return Y9Result.failure("未找到文档信息，请检查存储信息");
    }

    /**
     * 根据模板id获取套红模板数据（打开套红模板使用）
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param templateGuid 模板id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是套红文件地址
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openDocumentTemplate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String templateGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        LOGGER.debug("call /ntko/openTaoHongTemplate");
        byte[] buf;
        TaoHongTemplate taohongTemplate = taoHongTemplateService.getById(templateGuid);
        if (null != taohongTemplate) {
            buf = taohongTemplate.getTemplateContent();
            if (buf != null) {
                try {
                    return Y9Result.success(jodd.util.Base64.encodeToString(buf));
                } catch (Exception e) {
                    LOGGER.error("向jsp页面输出word二进制流错误", e);
                    return Y9Result.failure("向jsp页面输出word二进制流错误! ");
                }
            }
        } else {
            LOGGER.error("数据库没有templateGUID={}的模版，请联系管理员", templateGuid);
            return Y9Result.failure("数据库没有templateGUID=" + templateGuid + "的模版，请联系管理员");
        }
        return Y9Result.failure("未找到套红模板信息");
    }

    /**
     * 获取PDF文件存储信息（打开PDF使用）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openPdf(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        List<Y9Word> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = y9WordService.listByProcessSerialNumber(processSerialNumber);
        }
        if (!list.isEmpty()) {
            Y9Word y9Word = list.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {
                return Y9Result.failure("未获取到fileStoreId，保存正文的时候出错");
            }
        }
        return Y9Result.failure("未找到PDF文档信息");
    }

    /**
     * 获取撤销PDF后的正文文件存储信息（用于撤销PDF操作后打开正文）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openRevokePdfAfterDocument(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String isTaoHong) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        List<Y9Word> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        Y9Word y9Word;
        if (!list.isEmpty()) {
            y9Word = list.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {// 从数据库读取正文
                LOGGER.error("fileStoreId为空，保存正文出错");
                return Y9Result.failure("获取到fileStoreId为空，保存正文出错");
            }
        }
        return Y9Result.failure("未找到转换PDF前的正文文档信息");
    }

    /**
     * 获取套红文件存储信息（打开套红使用）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是当前人员的委办局GUID
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openTaoHong(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String activitiUser) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        LOGGER.debug("call /ntko/openTaoHong");
        // 当前人员的委办局GUID
        OrgUnit currentBureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), activitiUser).getData();
        return Y9Result.success(currentBureau.getId());
    }

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param docJson 正文json信息
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     * @since 9.6.6
     */
    @SuppressWarnings("unchecked")
    @Override
    public Y9Result<Boolean> importY9Word(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String docJson, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        boolean checkSave = false;
        try {
            Map<String, Object> documentMap = Y9JsonUtil.readValue(docJson, Map.class);
            assert documentMap != null;
            List<Map<String, Object>> documentList = (List<Map<String, Object>>)documentMap.get("document");
            for (Map<String, Object> dMap : documentList) {
                Y9Word tw = new Y9Word();
                tw.setId(dMap.get("id").toString());
                tw.setFileName(dMap.get("fileName").toString());
                tw.setFileStoreId(dMap.get("filePath").toString());
                tw.setFileType(dMap.get("fileType").toString());
                tw.setProcessSerialNumber(processSerialNumber);
                tw.setSaveDate(Y9DateTimeUtils.formatCurrentDateTime());
                tw.setTenantId(dMap.get("tenantId").toString());
                tw.setUserId(dMap.get("userId").toString());
                tw.setTitle(dMap.get("title") == null ? "" : dMap.get("title").toString());
                y9WordService.save(tw);
                checkSave = true;
            }
            return Y9Result.success(checkSave);
        } catch (Exception e) {
            LOGGER.error("保存公文传输转入工作流的正文信息失败", e);
            return Y9Result.failure("保存公文传输转入工作流的正文信息失败");
        }
    }

    /**
     * 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param itembox 办件状态 {@link net.risesoft.enums.ItemBoxTypeEnum}
     * @param taskId 任务id
     * @param bindValue 绑定值
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordInfo> showWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String itemId, String itembox, String taskId,
        String bindValue) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);

            Y9WordInfo wordInfo = new Y9WordInfo();
            populateWordInfoBasics(wordInfo, userInfo, processSerialNumber, itemId, itembox, taskId);

            List<Y9Word> wordList = getExistingWordList(processSerialNumber, bindValue);
            if (!wordList.isEmpty()) {
                populateWordInfoFromExistingWord(wordInfo, wordList.get(0));
            } else {
                populateWordInfoFromTemplate(wordInfo, tenantId, itemId, taskId, bindValue);
            }

            return Y9Result.success(wordInfo);
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败", e);
            return Y9Result.failure("获取正文文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 填充基本信息
     */
    private void populateWordInfoBasics(Y9WordInfo wordInfo, UserInfo person, String processSerialNumber, String itemId,
        String itembox, String taskId) {
        wordInfo.setProcessSerialNumber(processSerialNumber);
        wordInfo.setUserName(person.getName());
        wordInfo.setActivitiUser(person.getPersonId());
        wordInfo.setItemId(itemId);
        wordInfo.setItembox(itembox);
        wordInfo.setTaskId(taskId);
        wordInfo.setWordReadOnly(determineWordReadOnly(itembox));
    }

    /**
     * 确定文档是否只读
     */
    private String determineWordReadOnly(String itembox) {
        if ("".equals(itembox) || ItemBoxTypeEnum.ADD.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.TODO.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.DRAFT.getValue().equalsIgnoreCase(itembox)) {
            return "NO";
        } else if (ItemBoxTypeEnum.DONE.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.DOING.getValue().equalsIgnoreCase(itembox)) {
            return "YES";
        }
        return "NO";
    }

    /**
     * 从现有文档填充信息
     */
    private void populateWordInfoFromExistingWord(Y9WordInfo wordInfo, Y9Word word) {
        wordInfo.setFileDocumentId(word.getId());
        wordInfo.setSaveDate(word.getSaveDate());
        wordInfo.setFileType(word.getFileType());
        wordInfo.setFileStoreId(word.getFileStoreId());
        wordInfo.setUid(word.getFileStoreId());

        WordTypeInfo typeInfo = determineWordTypeInfo(word.getIstaohong());
        wordInfo.setOpenWordOrPdf(typeInfo.getOpenType());
        wordInfo.setIsTaoHong(typeInfo.getTaoHongValue());
    }

    /**
     * 确定文档类型信息
     */
    private WordTypeInfo determineWordTypeInfo(String istaohong) {
        WordTypeInfo typeInfo = new WordTypeInfo();

        if (ItemWordTypeEnum.PDF2.getValue().equals(istaohong)) {
            typeInfo.setOpenType("openWordToPDF");
            typeInfo.setTaoHongValue("4");
        } else if (ItemWordTypeEnum.PDF1.getValue().equals(istaohong)) {
            typeInfo.setOpenType("openWordToPDF");
            typeInfo.setTaoHongValue("3");
        } else if (ItemWordTypeEnum.PDF.getValue().equals(istaohong)) {
            typeInfo.setOpenType("openPDF");
            typeInfo.setTaoHongValue("2");
        } else if (ItemWordTypeEnum.WORD_RED_HEAD.getValue().equals(istaohong)) {
            typeInfo.setOpenType("openTaoHongWord");
            typeInfo.setTaoHongValue("1");
        } else {
            typeInfo.setOpenType(ItemConsts.OPENWORD_KEY);
            typeInfo.setTaoHongValue("0");
        }

        return typeInfo;
    }

    /**
     * 从模板填充信息
     */
    private void populateWordInfoFromTemplate(Y9WordInfo wordInfo, String tenantId, String itemId, String taskId,
        String bindValue) {
        try {
            String processDefinitionId = getProcessDefinitionId(tenantId, itemId, taskId);
            WordTemplate wordTemplate = findWordTemplate(itemId, processDefinitionId, bindValue);

            if (wordTemplate != null && wordTemplate.getId() != null) {
                wordInfo.setFileDocumentId(wordTemplate.getId());
                wordInfo.setOpenWordOrPdf("openWordTemplate");
                String fileName = wordTemplate.getFileName();
                String fileType = fileName.substring(fileName.lastIndexOf("."));
                wordInfo.setFileType(fileType);
            } else {
                wordInfo.setOpenWordOrPdf(ItemConsts.OPENWORD_KEY);
                wordInfo.setDocCategory(bindValue);
            }
        } catch (Exception e) {
            LOGGER.error("从模板获取文档信息失败", e);
        }
    }

    /**
     * 获取流程定义ID
     */
    private String getProcessDefinitionId(String tenantId, String itemId, String taskId) {
        if (StringUtils.isNotBlank(taskId)) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            return task.getProcessDefinitionId();
        } else {
            Item item = itemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinitionModel =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            return processDefinitionModel.getId();
        }
    }

    /**
     * 查找文档模板
     */
    private WordTemplate findWordTemplate(String itemId, String processDefinitionId, String bindValue) {
        ItemWordTemplateBind wordTemplateBind;
        if (StringUtils.isNotBlank(bindValue)) {
            wordTemplateBind = wordTemplateBindRepository.findByItemIdAndProcessDefinitionIdAndBindValue(itemId,
                processDefinitionId, bindValue);
        } else {
            wordTemplateBind =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        }

        return wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "")
            .orElse(null);
    }

    /**
     * 获取当前委办局的套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return {@code Y9Result<List<TaoHongTemplateModel>>} 通用请求返回对象 - data 是套红模板列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaoHongTemplateModel>> taoHongTemplateList(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String currentBureauGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        LOGGER.debug("call /ntko/list");
        List<TaoHongTemplateModel> retList = new ArrayList<>();
        List<TaoHongTemplate> list = taoHongTemplateService.listByBureauGuid(currentBureauGuid);
        if (list.isEmpty()) {
            TaoHongTemplateModel taoHong = new TaoHongTemplateModel();
            taoHong.setHasDocumentTemplate("0");
            retList.add(taoHong);
        } else {
            for (TaoHongTemplate taoHongTemplate : list) {
                TaoHongTemplateModel taoHong = new TaoHongTemplateModel();
                taoHong.setHasDocumentTemplate("1");
                taoHong.setTemplateGuid(taoHongTemplate.getTemplateGuid());
                taoHong.setTemplateFileName(taoHongTemplate.getTemplateFileName());
                taoHong.setTemplateType(taoHongTemplate.getTemplateType());
                retList.add(taoHong);
            }
        }
        return Y9Result.success(retList);
    }

    /**
     * 正文上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param documentTitle 文档标题
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @param docCategory 文档类别
     * @param taskId 任务id
     * @param fileSizeString 文件大小
     * @param fileStoreId 文件id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     * @since 9.6.6
     */
    @Override
    @PostMapping(value = "/uploadWord")
    public Y9Result<Boolean> uploadWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String documentTitle, @RequestParam String fileType, @RequestParam String processSerialNumber,
        String isTaoHong, String docCategory, String taskId, String fileSizeString, @RequestParam String fileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        try {
            if (StringUtils.isBlank(processSerialNumber)) {
                return Y9Result.success(false);
            }
            List<Y9Word> existingWords =
                y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
            if (existingWords.isEmpty()) {
                return handleNewWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber,
                    isTaoHong, docCategory, taskId);
            } else {
                return handleExistingWord(existingWords.get(0), fileStoreId, fileSizeString, fileType,
                    processSerialNumber, isTaoHong, docCategory, taskId, userId);
            }
        } catch (Exception e) {
            LOGGER.error("草稿箱保存正文失败", e);
            return Y9Result.failure("草稿箱保存正文失败");
        }
    }

    /**
     * 处理新正文的保存
     */
    private Y9Result<Boolean> handleNewWord(String fileStoreId, String fileSizeString, String documentTitle,
        String fileType, String processSerialNumber, String isTaoHong, String docCategory, String taskId) {
        try {
            y9WordService.saveWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber, isTaoHong,
                docCategory);
            y9WordHistoryService.save(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber,
                isTaoHong, taskId, docCategory);
            return Y9Result.success(true);
        } catch (Exception e) {
            LOGGER.error("保存新正文失败", e);
            return Y9Result.failure("保存新正文失败: " + e.getMessage());
        }
    }

    /**
     * 处理已存在正文的更新
     */
    private Y9Result<Boolean> handleExistingWord(Y9Word existingWord, String fileStoreId, String fileSizeString,
        String fileType, String processSerialNumber, String isTaoHong, String docCategory, String taskId,
        String userId) {
        // 更新文档标题
        String updatedTitle = determineDocumentTitle(existingWord);
        try {
            // 更新正文
            y9WordService.updateById(fileStoreId, fileType, updatedTitle + fileType, fileSizeString, isTaoHong, userId,
                existingWord.getId());
            // 处理历史记录
            return handleWordHistory(fileStoreId, fileSizeString, updatedTitle, fileType, processSerialNumber,
                isTaoHong, docCategory, taskId, userId);
        } catch (Exception e) {
            LOGGER.error("更新正文失败", e);
            return Y9Result.failure("更新正文失败: " + e.getMessage());
        }
    }

    /**
     * 确定文档标题
     */
    private String determineDocumentTitle(Y9Word existingWord) {
        if (StringUtils.isNotEmpty(existingWord.getTitle())) {
            return existingWord.getTitle();
        }
        return "正文";
    }

    /**
     * 处理正文历史记录
     */
    private Y9Result<Boolean> handleWordHistory(String fileStoreId, String fileSizeString, String documentTitle,
        String fileType, String processSerialNumber, String isTaoHong, String docCategory, String taskId,
        String userId) {
        List<Y9WordHistory> historyList = getHistoryList(processSerialNumber, isTaoHong, taskId);
        try {
            if (historyList.isEmpty()) {
                // 在当前任务还没有保存过正文
                y9WordHistoryService.save(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber,
                    isTaoHong, taskId, docCategory);
            } else {
                // 更新历史记录
                y9WordHistoryService.updateById(fileStoreId, fileType, documentTitle + fileType, fileSizeString,
                    isTaoHong, docCategory, userId, historyList.get(0).getId());
            }
            return Y9Result.success(true);
        } catch (Exception e) {
            LOGGER.error("处理正文历史记录失败", e);
            return Y9Result.failure("处理正文历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取历史记录列表
     */
    private List<Y9WordHistory> getHistoryList(String processSerialNumber, String isTaoHong, String taskId) {
        if (StringUtils.isNotBlank(taskId)) {
            return y9WordHistoryRepository.getByProcessSerialNumberAndIsTaoHongAndTaskId(processSerialNumber, isTaoHong,
                taskId);
        } else {
            // 流程刚启动的时候taskId为空
            return y9WordHistoryRepository.findByProcessSerialNumber(processSerialNumber);
        }
    }

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id 正文id
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordModel> wordDownload(@RequestParam String tenantId, @RequestParam String id) {
        Y9Word y9Word = y9WordRepository.findById(id).orElse(null);
        assert y9Word != null;
        Y9WordModel model = getY9Word(y9Word);
        return Y9Result.success(model);
    }

    /**
     * 文档类型信息封装类
     */
    @Setter
    @Getter
    private static class WordTypeInfo {
        private String openType;
        private String taoHongValue;

    }
}
