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

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.TaoHongTemplate;
import net.risesoft.entity.TransactionHistoryWord;
import net.risesoft.entity.TransactionWord;
import net.risesoft.entity.WordTemplate;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemWordTypeEnum;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.TransactionHistoryWordModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemWordTemplateBindRepository;
import net.risesoft.repository.jpa.TransactionHistoryWordRepository;
import net.risesoft.repository.jpa.TransactionWordRepository;
import net.risesoft.repository.jpa.WordTemplateRepository;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.TaoHongTemplateService;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.service.TransactionWordService;
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
@RequestMapping(value = "/services/rest/transactionWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionWordApiImpl implements TransactionWordApi {

    private final TransactionWordRepository transactionWordRepository;

    private final TransactionWordService transactionWordService;

    private final TransactionHistoryWordService transactionHistoryWordService;

    private final TransactionHistoryWordRepository transactionHistoryWordRepository;

    private final TaoHongTemplateService taoHongTemplateService;

    private final WordTemplateRepository wordTemplateRepository;

    private final ItemWordTemplateBindRepository wordTemplateBindRepository;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final Y9FileStoreService y9FileStoreService;

    private final TaskApi taskManager;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

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
        transactionWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
        transactionHistoryWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
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
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = transactionWordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        for (TransactionWord transactionWord : list) {
            transactionWordRepository.delete(transactionWord);
            try {
                y9FileStoreService.deleteFile(transactionWord.getFileStoreId());
            } catch (Exception e) {
                LOGGER.error("删除文件失败", e);
            }
        }
        if (ItemWordTypeEnum.PDF1.getValue().equals(isTaoHong)) {
            transactionHistoryWordService.deleteHistoryWordByIsTaoHong(processSerialNumber, "3");
        }
        return Y9Result.success();
    }

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<TransactionWordModel> exchangeFindWordByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processSerialNumber) {
        TransactionWordModel word = new TransactionWordModel();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordService.listByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                TransactionWord transactionWord = list.get(0);
                Person user = personApi.get(tenantId, transactionWord.getUserId()).getData();
                word = getTransactionWord(transactionWord);
                word.setFileName(transactionWord.getTitle() + transactionWord.getFileType());
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
    public Y9Result<TransactionHistoryWordModel> findHistoryVersionDoc(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<TransactionHistoryWord> historyWordList = transactionHistoryWordService.listByTaskId(taskId);
        TransactionHistoryWordModel history = new TransactionHistoryWordModel();
        if (null != historyWordList && !historyWordList.isEmpty()) {
            TransactionHistoryWord historyWord = historyWordList.get(0);
            if (StringUtils.isNotEmpty(historyWord.getIstaohong())) {
                if (ItemWordTypeEnum.PDF.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF1.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF2.getValue().equals(historyWord.getIstaohong())) {
                    history.setOpenWordOrPdf("openPDF");
                } else if (ItemWordTypeEnum.WORD.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.REDHEADWORD.getValue().equals(historyWord.getIstaohong())) {
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
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<TransactionWordModel> findWordByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        TransactionWordModel word = new TransactionWordModel();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<TransactionWord> list = transactionWordService.listByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                TransactionWord transactionWord = list.get(0);
                word = getTransactionWord(transactionWord);
                word.setFileName(transactionWord.getTitle() + transactionWord.getFileType());

                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, transactionWord.getUserId()).getData();
                word.setUserName(orgUnit.getName());

            }
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败", e);
            return Y9Result.failure("获取正文文件信息失败");
        }
        return Y9Result.success(word);
    }

    private TransactionWordModel getTransactionWord(TransactionWord transactionWord) {
        TransactionWordModel wordModel = new TransactionWordModel();
        wordModel.setId(transactionWord.getId());
        wordModel.setTitle(transactionWord.getTitle());
        wordModel.setFileStoreId(transactionWord.getFileStoreId());
        wordModel.setFileSize(transactionWord.getFileSize());
        wordModel.setFileName(transactionWord.getFileName());
        wordModel.setProcessSerialNumber(transactionWord.getProcessSerialNumber());
        wordModel.setSaveDate(transactionWord.getSaveDate());
        wordModel.setUserId(transactionWord.getUserId());
        wordModel.setIsTaoHong(transactionWord.getIstaohong());
        wordModel.setFileType(transactionWord.getFileType());
        return wordModel;
    }

    /**
     * 获取当前流程所有的正文文件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<TransactionWordModel>>} 通用请求返回对象 - data 是正文文件信息列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TransactionWordModel>> getWordList(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber) {
        List<TransactionWordModel> retList = new ArrayList<>();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
            for (TransactionWord word : list) {
                TransactionWordModel model = getTransactionWord(word);
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
        List<TransactionWord> list = new ArrayList<>();

        if (StringUtils.isNotBlank(processSerialNumber)) {
            if (StringUtils.isNotBlank(bindValue) && !"null".equals(bindValue)) {
                list = transactionWordService.listByProcessSerialNumberAndDocCategory(processSerialNumber, bindValue);
            } else {
                list = transactionWordService.listByProcessSerialNumber(processSerialNumber);
            }
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                return Y9Result.success(transactionWord.getFileStoreId());
            } else {
                LOGGER.error("fileStoreId为空，保存正文的时候出错");
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        } else {// 打开事项配置的正文模板
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = processDefinition.getId();

            if (StringUtils.isNotBlank(bindValue)) {
                ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository
                    .findByItemIdAndProcessDefinitionIdAndBindValue(itemId, processDefinitionId, bindValue);
                WordTemplate wordTemplate = wordTemplateRepository
                    .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    return Y9Result.success(wordTemplate.getFilePath());
                } else {
                    LOGGER.error("数据库没有processSerialNumber=" + processSerialNumber + "和bindVvalue=" + bindValue
                        + "绑定的正文，请联系管理员");
                    return Y9Result.failure("数据库没有processSerialNumber=" + processSerialNumber + "和bindVvalue="
                        + bindValue + "绑定的正文，请联系管理员");
                }
            } else {
                ItemWordTemplateBind wordTemplateBind =
                    wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
                WordTemplate wordTemplate = wordTemplateRepository
                    .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    return Y9Result.success(wordTemplate.getFilePath());
                } else {
                    LOGGER.error("数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员");
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
        List<TransactionWord> list =
            transactionWordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, "1");
        /*
         * 套红文档不存在,则获取未套红文档
         */
        if (list.isEmpty()) {
            list = transactionWordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, "0");
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                return Y9Result.success(transactionWord.getFileStoreId());
            } else {
                LOGGER.error("fileStoreId为空，保存正文的时候出错");
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
        LOGGER.debug("call /ntko/openTaohongTemplate");
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
            LOGGER.error("数据库没有templateGUID=" + templateGuid + "的模版，请联系管理员");
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
         * transactionHistoryWordService.getTransactionHistoryWordByTaskId( taskId);
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
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.listByProcessSerialNumber(processSerialNumber);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                return Y9Result.success(transactionWord.getFileStoreId());
            } else {// 从数据库读取正文
                LOGGER.error("fileStoreId为空，保存正文的时候出错");
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
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = transactionWordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                return Y9Result.success(transactionWord.getFileStoreId());
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
                TransactionWord tw = new TransactionWord();
                tw.setId(dMap.get("id").toString());
                tw.setFileName(dMap.get("fileName").toString());
                tw.setFileStoreId(dMap.get("filePath").toString());
                tw.setFileType(dMap.get("fileType").toString());
                tw.setProcessSerialNumber(processSerialNumber);
                tw.setSaveDate(sdf.format(new Date()));
                tw.setTenantId(dMap.get("tenantId").toString());
                tw.setUserId(dMap.get("userId").toString());
                tw.setTitle(dMap.get("title") == null ? "" : dMap.get("title").toString());
                transactionWordService.save(tw);
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
        Y9WordInfo retMap = new Y9WordInfo();
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
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            if (StringUtils.isNotBlank(bindValue)) {
                list = transactionWordService.listByProcessSerialNumberAndDocCategory(processSerialNumber, bindValue);
            } else {
                list = transactionWordService.listByProcessSerialNumber(processSerialNumber);
            }
        }
        if (list != null && !list.isEmpty()) {
            TransactionWord d = list.get(0);
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
            } else if (d.getIstaohong().equals(ItemWordTypeEnum.REDHEADWORD.getValue())) {
                openWordOrPdf = "openTaoHongWord";
                isTaoHong = "1";
            } else {
                openWordOrPdf = "openWord";
                isTaoHong = "0";
            }
            retMap.setFileType(d.getFileType());
            retMap.setFileStoreId(d.getFileStoreId());
            retMap.setUid(d.getFileStoreId());
        } else {
            String processDefinitionId;
            if (StringUtils.isNoneBlank(taskId)) {
                TaskModel task = taskManager.findById(tenantId, taskId).getData();
                processDefinitionId = task.getProcessDefinitionId();
            } else {
                SpmApproveItem item = spmApproveItemService.findById(itemId);
                String processDefinitionKey = item.getWorkflowGuid();
                ProcessDefinitionModel processDefinitionModel =
                    repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
                processDefinitionId = processDefinitionModel.getId();
            }
            if (StringUtils.isNotBlank(bindValue)) {
                ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository
                    .findByItemIdAndProcessDefinitionIdAndBindValue(itemId, processDefinitionId, bindValue);
                WordTemplate wordTemplate = wordTemplateRepository
                    .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
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
                WordTemplate wordTemplate = wordTemplateRepository
                    .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
                if (wordTemplate != null && wordTemplate.getId() != null) {
                    fileDocumentId = wordTemplate.getId();
                    openWordOrPdf = "openWordTemplate";
                    String fileName = wordTemplate.getFileName();
                    fileType = fileName.substring(fileName.lastIndexOf("."));
                }
            }
        }
        String activitiUser = person.getId();
        retMap.setFileType(fileType);
        retMap.setDocCategory(docCategory);
        retMap.setActivitiUser(activitiUser);
        retMap.setFileDocumentId(fileDocumentId);
        retMap.setProcessSerialNumber(processSerialNumber);
        retMap.setUserName(person.getName());
        retMap.setSaveDate(saveDate);
        retMap.setOpenWordOrPdf(openWordOrPdf);
        retMap.setWordReadOnly(wordReadOnly);
        retMap.setItemId(itemId);
        retMap.setItembox(itembox);
        retMap.setTaskId(taskId);
        retMap.setIsTaoHong(isTaoHong);
        return Y9Result.success(retMap);
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
            TaoHongTemplateModel taohong = new TaoHongTemplateModel();
            taohong.setHasDocumentTemplate("0");
            retList.add(taohong);
        } else {
            for (TaoHongTemplate taoHongTemplate : list) {
                TaoHongTemplateModel taohong = new TaoHongTemplateModel();
                taohong.setHasDocumentTemplate("1");
                taohong.setTemplateGuid(taoHongTemplate.getTemplateGuid());
                taohong.setTemplateFileName(taoHongTemplate.getTemplateFileName());
                taohong.setTemplateType(taoHongTemplate.getTemplateType());
                retList.add(taohong);
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
                List<TransactionWord> list =
                    transactionWordService.listByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
                if (list.isEmpty()) {
                    transactionWordService.saveTransactionWord(fileStoreId, fileSizeString, documentTitle, fileType,
                        processSerialNumber, isTaoHong, docCategory);
                    transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle,
                        fileType, processSerialNumber, isTaoHong, taskId, docCategory);
                } else {
                    if (StringUtils.isNotEmpty(list.get(0).getTitle())) {
                        documentTitle = list.get(0).getTitle();
                    } else {
                        documentTitle = "正文";
                    }
                    transactionWordService.updateTransactionWordById(fileStoreId, fileType, documentTitle + fileType,
                        fileSizeString, isTaoHong, userId, list.get(0).getId());

                    List<TransactionHistoryWord> thwlist;
                    if (StringUtils.isNotBlank(taskId)) {
                        thwlist = transactionHistoryWordRepository
                            .getByProcessSerialNumberAndIsTaoHongAndTaskId(processSerialNumber, isTaoHong, taskId);
                    } else {
                        /*
                         * 流程刚启动的时候taskId为空
                         */
                        thwlist = transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
                    }
                    if (thwlist.isEmpty()) {
                        /*
                         * 在当前任务还没有保存过正文
                         */
                        transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString,
                            documentTitle, fileType, processSerialNumber, isTaoHong, taskId, docCategory);
                    } else {
                        transactionHistoryWordService.updateTransactionHistoryWordById(fileStoreId, fileType,
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
    public Y9Result<TransactionWordModel> wordDownload(@RequestParam String tenantId, @RequestParam String id) {
        TransactionWord transactionWord = transactionWordRepository.findById(id).orElse(null);
        assert transactionWord != null;
        TransactionWordModel model = getTransactionWord(transactionWord);
        return Y9Result.success(model);
    }
}
