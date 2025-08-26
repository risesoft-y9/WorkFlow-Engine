package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
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
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.documentword.Y9WordHistoryRepository;
import net.risesoft.repository.documentword.Y9WordRepository;
import net.risesoft.repository.template.ItemWordTemplateBindRepository;
import net.risesoft.repository.template.WordTemplateRepository;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.template.TaoHongTemplateService;
import net.risesoft.service.word.Y9WordHistoryService;
import net.risesoft.service.word.Y9WordService;
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
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            List<Y9Word> list = y9WordService.listByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                Y9Word y9Word = list.get(0);
                Person user = personApi.get(tenantId, y9Word.getUserId()).getData();
                word = getY9Word(y9Word);
                word.setFileName(y9Word.getTitle() + y9Word.getFileType());
                word.setUserName(user.getName());
            }
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败", e);
            return Y9Result.failure("获取正文文件信息失败");
        }
        return Y9Result.success(word);
    }

    /**
     * 根据任务id获取正文历史文件信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<TransactionHistoryWordModel>} 通用请求返回对象 - data 是历史正文文件信息对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordHistoryModel> findHistoryVersionDoc(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Y9WordHistory> historyWordList = y9WordHistoryService.listByTaskId(taskId);
        Y9WordHistoryModel history = new Y9WordHistoryModel();
        if (null != historyWordList && !historyWordList.isEmpty()) {
            Y9WordHistory historyWord = historyWordList.get(0);
            if (StringUtils.isNotEmpty(historyWord.getIstaohong())) {
                if (ItemWordTypeEnum.PDF.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF1.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF2.getValue().equals(historyWord.getIstaohong())) {
                    history.setOpenWordOrPdf("openPDF");
                } else if (ItemWordTypeEnum.WORD.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.WORD_RED_HEAD.getValue().equals(historyWord.getIstaohong())) {
                    history.setOpenWordOrPdf("openWord");
                }
            } else {
                history.setOpenWordOrPdf("openWord");
            }

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
        return Y9Result.success(history);
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
            LOGGER.error("获取正文文件信息失败", e);
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
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Y9Word> list = new ArrayList<>();

        if (StringUtils.isNotBlank(processSerialNumber)) {
            if (StringUtils.isNotBlank(bindValue) && !"null".equals(bindValue)) {
                list = y9WordService.listByProcessSerialNumberAndDocCategory(processSerialNumber, bindValue);
            } else {
                list = y9WordService.listByProcessSerialNumber(processSerialNumber);
            }
        }
        Y9Word y9Word;
        if (!list.isEmpty()) {
            y9Word = list.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        } else {// 打开事项配置的正文模板
            Item item = itemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = processDefinition.getId();

            if (StringUtils.isNotBlank(bindValue)) {
                ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository
                    .findByItemIdAndProcessDefinitionIdAndBindValue(itemId, processDefinitionId, bindValue);
                WordTemplate wordTemplate =
                    wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "")
                        .orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    return Y9Result.success(wordTemplate.getFilePath());
                } else {
                    LOGGER.error("数据库没有processSerialNumber={}和bindValue={}绑定的正文，请联系管理员", processSerialNumber,
                        bindValue);
                    return Y9Result.failure(
                        "数据库没有processSerialNumber=" + processSerialNumber + "和bindValue=" + bindValue + "绑定的正文，请联系管理员");
                }
            } else {
                ItemWordTemplateBind wordTemplateBind =
                    wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
                WordTemplate wordTemplate =
                    wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "")
                        .orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    return Y9Result.success(wordTemplate.getFilePath());
                } else {
                    LOGGER.error("数据库没有processSerialNumber={}的正文，请联系管理员", processSerialNumber);
                    return Y9Result.failure("数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员");
                }
            }
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
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        }
        return Y9Result.failure("未找到文档信息");
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
        return Y9Result.failure("未找到文档信息");
    }

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    @Deprecated
    public Y9Result<Object> openHistoryVersionDoc(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String taskId) {
        /*
         * Runtime runtime = Runtime.getRuntime(); TransactionHistoryWord hword =
         * y9WordHistoryService.getTransactionHistoryWordByTaskId( taskId);
         * //保存至系统临时存储的文件夹 String filePath = System.getProperty("java.io.tmpdir");
         * BufferedOutputStream bos = null; FileOutputStream fos = null; File file =
         * null; try { File dir = new File(filePath); if (!dir.exists() &&
         * dir.isDirectory()) {//判断文件目录是否存在 dir.mkdirs(); } file = new File(filePath +
         * "正文" + hword.getVersion() + ".doc"); fos = new FileOutputStream(file); bos =
         * new BufferedOutputStream(fos); bos.write(hword.getContent());
         *
         * runtime.exec("rundll32 url.dll FileProtocolHandler " + filePath + "正文" +
         * hword.getVersion() + ".doc"); } catch (Exception e) { e.printStackTrace(); }
         * finally { if (bos != null) { try { bos.close(); } catch (IOException e1) {
         * e1.printStackTrace(); } } if (fos != null) { try { fos.close(); } catch
         * (IOException e1) { e1.printStackTrace(); } } }
         */
        return Y9Result.success();
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Y9Word> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = y9WordService.listByProcessSerialNumber(processSerialNumber);
        }
        if (!list.isEmpty()) {
            Y9Word y9Word = list.get(0);
            if (StringUtils.isNotBlank(y9Word.getFileStoreId())) {
                return Y9Result.success(y9Word.getFileStoreId());
            } else {
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        }
        return Y9Result.failure("未找到文档信息");
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
                LOGGER.error("fileStoreId为空，保存正文的时候出错");
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        }
        return Y9Result.failure("未找到文档信息");
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
     * @param docjson 正文json信息
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     * @since 9.6.6
     */
    @SuppressWarnings("unchecked")
    @Override
    public Y9Result<Boolean> saveImportTransationWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String docjson, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        boolean checkSave = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, Object> documentMap = Y9JsonUtil.readValue(docjson, Map.class);
            assert documentMap != null;
            List<Map<String, Object>> documentList = (List<Map<String, Object>>)documentMap.get("document");
            for (Map<String, Object> dMap : documentList) {
                Y9Word tw = new Y9Word();
                tw.setId(dMap.get("id").toString());
                tw.setFileName(dMap.get("fileName").toString());
                tw.setFileStoreId(dMap.get("filePath").toString());
                tw.setFileType(dMap.get("fileType").toString());
                tw.setProcessSerialNumber(processSerialNumber);
                tw.setSaveDate(sdf.format(new Date()));
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
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @param bindValue 绑定值
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9WordInfo> showWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String itemId, String itembox, String taskId,
        String bindValue) {
        Y9WordInfo wordInfo = new Y9WordInfo();
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String fileDocumentId = "";
        String wordReadOnly = "";
        String openWordOrPdf = "";
        String isTaoHong = "0";
        String docCategory = "";
        String fileType = ".doc";
        if ("".equals(itembox) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.ADD.getValue())
            || itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())
            || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DRAFT.getValue())) {
            wordReadOnly = "NO";
        } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())
            || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())) {
            wordReadOnly = "YES";
        }
        String saveDate = "";
        List<Y9Word> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            if (StringUtils.isNotBlank(bindValue)) {
                list = y9WordService.listByProcessSerialNumberAndDocCategory(processSerialNumber, bindValue);
            } else {
                list = y9WordService.listByProcessSerialNumber(processSerialNumber);
            }
        }
        if (list != null && !list.isEmpty()) {
            Y9Word d = list.get(0);
            fileDocumentId = d.getId();
            saveDate = d.getSaveDate();
            if (d.getIstaohong().equals(ItemWordTypeEnum.PDF2.getValue())) {
                openWordOrPdf = "openWordToPDF";
                isTaoHong = "4";
            } else if (d.getIstaohong().equals(ItemWordTypeEnum.PDF1.getValue())) {
                openWordOrPdf = "openWordToPDF";
                isTaoHong = "3";
            } else if (d.getIstaohong().equals(ItemWordTypeEnum.PDF.getValue())) {
                openWordOrPdf = "openPDF";
                isTaoHong = "2";
            } else if (d.getIstaohong().equals(ItemWordTypeEnum.WORD_RED_HEAD.getValue())) {
                openWordOrPdf = "openTaoHongWord";
                isTaoHong = "1";
            } else {
                openWordOrPdf = "openWord";
                isTaoHong = "0";
            }
            wordInfo.setFileType(d.getFileType());
            wordInfo.setFileStoreId(d.getFileStoreId());
            wordInfo.setUid(d.getFileStoreId());
        } else {
            String processDefinitionId;
            if (StringUtils.isNoneBlank(taskId)) {
                TaskModel task = taskApi.findById(tenantId, taskId).getData();
                processDefinitionId = task.getProcessDefinitionId();
            } else {
                Item item = itemService.findById(itemId);
                String processDefinitionKey = item.getWorkflowGuid();
                ProcessDefinitionModel processDefinitionModel =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
                processDefinitionId = processDefinitionModel.getId();
            }
            if (StringUtils.isNotBlank(bindValue)) {
                ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository
                    .findByItemIdAndProcessDefinitionIdAndBindValue(itemId, processDefinitionId, bindValue);
                WordTemplate wordTemplate =
                    wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "")
                        .orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    fileDocumentId = wordTemplate.getId();
                    openWordOrPdf = "openWordTemplate";
                    String fileName = wordTemplate.getFileName();
                    fileType = fileName.substring(fileName.lastIndexOf("."));
                } else {
                    openWordOrPdf = "openWord";
                    docCategory = bindValue;
                }
            } else {
                ItemWordTemplateBind wordTemplateBind =
                    wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
                WordTemplate wordTemplate =
                    wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "")
                        .orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    fileDocumentId = wordTemplate.getId();
                    openWordOrPdf = "openWordTemplate";
                    String fileName = wordTemplate.getFileName();
                    fileType = fileName.substring(fileName.lastIndexOf("."));
                }
            }
        }
        String activitiUser = person.getId();
        wordInfo.setFileType(fileType);
        wordInfo.setDocCategory(docCategory);
        wordInfo.setActivitiUser(activitiUser);
        wordInfo.setFileDocumentId(fileDocumentId);
        wordInfo.setProcessSerialNumber(processSerialNumber);
        wordInfo.setUserName(person.getName());
        wordInfo.setSaveDate(saveDate);
        wordInfo.setOpenWordOrPdf(openWordOrPdf);
        wordInfo.setWordReadOnly(wordReadOnly);
        wordInfo.setItemId(itemId);
        wordInfo.setItembox(itembox);
        wordInfo.setTaskId(taskId);
        wordInfo.setIsTaoHong(isTaoHong);
        return Y9Result.success(wordInfo);
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        try {
            if (StringUtils.isNotBlank(processSerialNumber)) {
                List<Y9Word> list = y9WordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
                if (list.isEmpty()) {
                    y9WordService.saveWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber,
                        isTaoHong, docCategory);
                    y9WordHistoryService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle,
                        fileType, processSerialNumber, isTaoHong, taskId, docCategory);
                } else {
                    if (StringUtils.isNotEmpty(list.get(0).getTitle())) {
                        documentTitle = list.get(0).getTitle();
                    } else {
                        documentTitle = "正文";
                    }
                    y9WordService.updateById(fileStoreId, fileType, documentTitle + fileType, fileSizeString, isTaoHong,
                        userId, list.get(0).getId());

                    List<Y9WordHistory> thwlist;
                    if (StringUtils.isNotBlank(taskId)) {
                        thwlist = y9WordHistoryRepository
                            .getByProcessSerialNumberAndIsTaoHongAndTaskId(processSerialNumber, isTaoHong, taskId);
                    } else {
                        /*
                         * 流程刚启动的时候taskId为空
                         */
                        thwlist = y9WordHistoryRepository.findByProcessSerialNumber(processSerialNumber);
                    }
                    if (thwlist.isEmpty()) {
                        /*
                         * 在当前任务还没有保存过正文
                         */
                        y9WordHistoryService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle,
                            fileType, processSerialNumber, isTaoHong, taskId, docCategory);
                    } else {
                        y9WordHistoryService.updateTransactionHistoryWordById(fileStoreId, fileType,
                            documentTitle + fileType, fileSizeString, isTaoHong, docCategory, userId,
                            thwlist.get(0).getId());
                    }
                }
                return Y9Result.success(true);
            } else {
                return Y9Result.success(false);
            }
        } catch (Exception e) {
            LOGGER.error("草稿箱保存正文失败", e);
            return Y9Result.failure("草稿箱保存正文失败");
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
}
