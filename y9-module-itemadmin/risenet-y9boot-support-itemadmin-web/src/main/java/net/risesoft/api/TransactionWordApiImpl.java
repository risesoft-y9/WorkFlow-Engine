package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
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

import y9.client.rest.processadmin.RepositoryApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Slf4j
@RestController
@RequestMapping(value = "/services/rest/transactionWord", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransactionWordApiImpl implements TransactionWordApi {

    private static final Logger logger = LoggerFactory.getLogger(TransactionWordApiImpl.class);
    private final TransactionWordRepository transactionWordRepository;
    private final TransactionWordService transactionWordService;
    private final TransactionHistoryWordService transactionHistoryWordService;
    private final TransactionHistoryWordRepository transactionHistoryWordRepository;
    private final TaoHongTemplateService taoHongTemplateService;
    private final WordTemplateRepository wordTemplateRepository;
    private final ItemWordTemplateBindRepository wordTemplateBindRepository;
    private final PersonApi personManager;
    private final OrgUnitApi orgUnitApi;
    private final Y9FileStoreService y9FileStoreService;
    private final TaskApiClient taskManager;
    private final SpmApproveItemService spmApproveItemService;
    private final RepositoryApiClient repositoryManager;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Y9Result<Object> delBatchByProcessSerialNumbers(String tenantId,
        @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
        transactionHistoryWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    @Override
    public Y9Result<Object> deleteByIsTaoHong(String tenantId, String userId, String processSerialNumber,
        String isTaoHong) {
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        for (TransactionWord transactionWord : list) {
            transactionWordRepository.delete(transactionWord);
            try {
                y9FileStoreService.deleteFile(transactionWord.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ItemWordTypeEnum.PDF1.getValue().equals(isTaoHong)) {
            transactionHistoryWordService.deleteHistoryWordByIsTaoHong(processSerialNumber, "3");
        }
        return Y9Result.success();
    }

    /**
     * 公文传输获取正文
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param response
     * @return
     */
    @Override
    public Y9Result<TransactionWordModel> exchangeFindWordByProcessSerialNumber(String tenantId, String userId,
        String processSerialNumber) {
        TransactionWordModel word = new TransactionWordModel();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                TransactionWord transactionWord = list.get(0);
                Person user = personManager.get(tenantId, transactionWord.getUserId()).getData();
                word.setFileName(transactionWord.getTitle() + transactionWord.getFileType());
                word.setTitle(transactionWord.getTitle());
                word.setFileSize(transactionWord.getFileSize());
                word.setId(transactionWord.getId());
                word.setProcessSerialNumber(transactionWord.getProcessSerialNumber());
                word.setSaveDate(transactionWord.getSaveDate());
                word.setFileStoreId(transactionWord.getFileStoreId());
                word.setUserName(user.getName());
                word.setUserId(transactionWord.getUserId());
                word.setFileType(transactionWord.getFileType());
            }
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败", e);
            return Y9Result.failure("获取正文文件信息失败!");
        }
        return Y9Result.success(word);
    }

    @Override
    public Y9Result<TransactionHistoryWordModel> findHistoryVersionDoc(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        TransactionHistoryWordModel word = new TransactionHistoryWordModel();
        List<TransactionHistoryWord> historyWordList = transactionHistoryWordService.getListByTaskId(taskId);
        if (null != historyWordList && !historyWordList.isEmpty()) {
            TransactionHistoryWord historyWord = historyWordList.get(0);
            if (StringUtils.isNotEmpty(historyWord.getIstaohong())) {
                if (ItemWordTypeEnum.PDF.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF1.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.PDF2.getValue().equals(historyWord.getIstaohong())) {
                    word.setOpenWordOrPdf("openPDF");
                } else if (ItemWordTypeEnum.WORD.getValue().equals(historyWord.getIstaohong())
                    || ItemWordTypeEnum.REDHEADWORD.getValue().equals(historyWord.getIstaohong())) {
                    word.setOpenWordOrPdf("openWord");
                }
            } else {
                word.setOpenWordOrPdf("openWord");
            }

            word.setTitle(historyWord.getTitle());
            word.setFileSize(historyWord.getFileSize());
            word.setId(historyWord.getId());
            word.setProcessSerialNumber(historyWord.getProcessSerialNumber());
            word.setSaveDate(historyWord.getSaveDate());
            word.setFileStoreId(historyWord.getFileStoreId());
            word.setFileType(historyWord.getFileType());
            word.setIsTaoHong(StringUtils.isNotBlank(historyWord.getIstaohong()) ? historyWord.getIstaohong() : "");

            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, historyWord.getUserId()).getData();
            word.setUserName(orgUnit != null && StringUtils.isNotBlank(orgUnit.getId()) ? orgUnit.getName() : "");
            word.setUserId(historyWord.getUserId());
        }
        return Y9Result.success(word);
    }

    @Override
    public Y9Result<TransactionWordModel> findWordByProcessSerialNumber(String tenantId, String processSerialNumber) {
        TransactionWordModel word = new TransactionWordModel();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<TransactionWord> list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
            if (list != null && !list.isEmpty()) {
                TransactionWord transactionWord = list.get(0);
                Person user = personManager.get(tenantId, transactionWord.getUserId()).getData();

                word.setFileName(transactionWord.getTitle() + transactionWord.getFileType());
                word.setTitle(transactionWord.getTitle());
                word.setFileSize(transactionWord.getFileSize());
                word.setId(transactionWord.getId());
                word.setProcessSerialNumber(transactionWord.getProcessSerialNumber());
                word.setSaveDate(transactionWord.getSaveDate());
                word.setFileStoreId(transactionWord.getFileStoreId());
                word.setUserName(user.getName());
                word.setUserId(transactionWord.getUserId());
                word.setFileType(transactionWord.getFileType());
                word.setIsTaoHong(transactionWord.getIstaohong());
            }
        } catch (Exception e) {
            LOGGER.error("获取正文文件信息失败", e.getMessage());
            return Y9Result.failure("获取正文文件信息失败!");
        }
        return Y9Result.success(word);
    }

    private TransactionWordModel getTransactionWord(TransactionWord word) {
        TransactionWordModel wordModel = new TransactionWordModel();
        wordModel.setId(word.getId());
        wordModel.setTitle(word.getTitle());
        wordModel.setFileStoreId(word.getFileStoreId());
        wordModel.setFileSize(word.getFileSize());
        wordModel.setFileName(word.getFileName());
        wordModel.setProcessSerialNumber(word.getProcessSerialNumber());
        wordModel.setSaveDate(word.getSaveDate());
        wordModel.setUserId(word.getUserId());
        wordModel.setIsTaoHong(word.getIstaohong());
        wordModel.setFileType(word.getFileType());
        return wordModel;
    }

    @Override
    public Y9Result<List<TransactionWordModel>> getWordList(String tenantId, String userId,
        String processSerialNumber) {
        List<TransactionWordModel> resList = new ArrayList<>();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
            for (TransactionWord word : list) {
                TransactionWordModel model = getTransactionWord(word);
                resList.add(model);
            }
        } catch (Exception e) {
            LOGGER.error("获取正文列表失败", e);
            return Y9Result.failure("获取正文列表失败");
        }
        return Y9Result.success(resList);
    }

    @Override
    public Y9Result<String> openDocument(String tenantId, String userId, String processSerialNumber, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                return Y9Result.success(transactionWord.getFileStoreId());
            } else {
                logger.error("fileStoreId为空，保存正文的时候出错");
                return Y9Result.failure("fileStoreId为空，保存正文的时候出错");
            }
        } else {// 打开事项配置的正文模板
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
            String processDefinitionId = processDefinition.getId();
            ItemWordTemplateBind wordTemplateBind =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
            WordTemplate wordTemplate = wordTemplateRepository
                .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                return Y9Result.success(wordTemplate.getFilePath());
            } else {
                logger.error("数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员");
                return Y9Result.failure("数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员");
            }
        }
    }

    @Override
    @GetMapping(value = "/openDocumentByProcessSerialNumber")
    public Y9Result<String> openDocumentByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            // 获取套红文档
            list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, "1");
            // 套红文档不存在,则获取未套红文档
            if (list.isEmpty()) {
                list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, "0");
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
        }
        return Y9Result.failure("未找到文档信息");
    }

    @Override
    @GetMapping(value = "/openDocumentTemplate")
    public Y9Result<String> openDocumentTemplate(String tenantId, String userId, String templateGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/openTaohongTemplate");
        byte[] buf = null;
        TaoHongTemplate taohongTemplate = taoHongTemplateService.findOne(templateGuid);
        if (null != taohongTemplate) {
            buf = taohongTemplate.getTemplateContent();
            if (buf != null) {
                try {
                    return Y9Result.success(jodd.util.Base64.encodeToString(buf));
                } catch (Exception e) {
                    LOGGER.error("向jsp页面输出word二进制流错误");
                    e.printStackTrace();
                    return Y9Result.failure("向jsp页面输出word二进制流错误! ");
                }
            }
        } else {
            LOGGER.error("数据库没有templateGUID=" + templateGuid + "的模版，请联系管理员");
            return Y9Result.failure("数据库没有templateGUID=" + templateGuid + "的模版，请联系管理员");
        }
        return Y9Result.failure("未找到文档信息");
    }

    @Override
    public Y9Result<Object> openHistoryVersionDoc(String tenantId, String userId, String taskId) {
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

    @Override
    public Y9Result<String> openPdf(String tenantId, String userId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
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

    @Override
    @GetMapping(value = "/openRevokePDFAfterDocument")
    public Y9Result<String> openRevokePdfAfterDocument(String tenantId, String userId, String processSerialNumber,
        String isTaoHong) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
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

    @Override
    public Y9Result<String> openTaoHong(String tenantId, String userId, String activitiUser) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/openTaoHong");
        // 当前人员的委办局GUID
        OrgUnit currentBureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), activitiUser).getData();
        return Y9Result.success(currentBureau.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Result<Boolean> saveImportTransationWord(String tenantId, String userId, String docjson,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Boolean checkSave = false;
        try {
            Map<String, Object> documentMap = Y9JsonUtil.readValue(docjson, Map.class);
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
            LOGGER.error("保存公文传输转入工作流的正文信息失败", e.getMessage());
            return Y9Result.failure("保存公文传输转入工作流的正文信息失败");
        }
    }

    @Override
    public Y9Result<Y9WordInfo> showWord(String tenantId, String userId, String processSerialNumber, String itemId,
        String itembox, String taskId) {
        Y9WordInfo retMap = new Y9WordInfo();
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String fileDocumentId = "";
        String wordReadOnly = "";
        String openWordOrPdf = "";
        String isTaoHong = "0";
        if ("".equals(itembox) || ItemBoxTypeEnum.ADD.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.TODO.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.DRAFT.getValue().equalsIgnoreCase(itembox)) {
            wordReadOnly = "NO";
        } else if (ItemBoxTypeEnum.DONE.getValue().equalsIgnoreCase(itembox)
            || ItemBoxTypeEnum.DOING.getValue().equalsIgnoreCase(itembox)) {
            wordReadOnly = "YES";
        }
        String saveDate = "";
        List<TransactionWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
        }
        if (list != null && list.size() > 0) {
            TransactionWord d = list.get(0);
            fileDocumentId = d.getId();
            saveDate = d.getSaveDate();
            if (ItemWordTypeEnum.PDF2.getValue().equals(d.getIstaohong())) {
                // 打开word签章pdf的文件
                openWordOrPdf = "openWordToPDF";
                isTaoHong = "4";
            } else if (ItemWordTypeEnum.PDF1.getValue().equals(d.getIstaohong())) {
                // 打开word转pdf的文件
                openWordOrPdf = "openWordToPDF";
                isTaoHong = "3";
            } else if (ItemWordTypeEnum.PDF.getValue().equals(d.getIstaohong())) {
                // 打开pdf
                openWordOrPdf = "openPDF";
                isTaoHong = "2";
            } else if (ItemWordTypeEnum.REDHEADWORD.getValue().equals(d.getIstaohong())) {
                // 打开套红word
                openWordOrPdf = "openTaoHongWord";
                isTaoHong = "1";
            } else {// 打开word
                openWordOrPdf = "openWord";
                isTaoHong = "0";
            }
            retMap.setFileType(d.getFileType());
            retMap.setFileStoreId(d.getFileStoreId());
            retMap.setUid(d.getFileStoreId());
        } else {
            String processDefinitionId = "";
            if (StringUtils.isNoneBlank(taskId)) {
                TaskModel task = taskManager.findById(tenantId, taskId);
                processDefinitionId = task.getProcessDefinitionId();
            } else {
                SpmApproveItem item = spmApproveItemService.findById(itemId);
                String processDefinitionKey = item.getWorkflowGuid();
                ProcessDefinitionModel processDefinitionModel =
                    repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
                processDefinitionId = processDefinitionModel.getId();
            }
            ItemWordTemplateBind wordTemplateBind =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
            WordTemplate wordTemplate = wordTemplateRepository
                .findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                fileDocumentId = wordTemplate.getId();
                openWordOrPdf = "openWordTemplate";
            }
        }
        String activitiUser = person.getId();
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

    @Override
    public Y9Result<List<TaoHongTemplateModel>> taoHongTemplateList(String tenantId, String userId,
        String currentBureauGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/list");
        List<TaoHongTemplateModel> retList = new ArrayList<>();

        List<TaoHongTemplate> list = taoHongTemplateService.findByBureauGuid(currentBureauGuid);

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

    @Override
    public Y9Result<Boolean> uploadWord(String tenantId, String userId, String documentTitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, String fileSizeString, String fileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        try {
            if (StringUtils.isNotBlank(processSerialNumber) && !"".equals(processSerialNumber)) {
                List<TransactionWord> list =
                    transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
                if (list.isEmpty()) {
                    transactionWordService.saveTransactionWord(fileStoreId, fileSizeString, documentTitle, fileType,
                        processSerialNumber, isTaoHong);
                    transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle,
                        fileType, processSerialNumber, isTaoHong, taskId);
                } else {
                    if (StringUtils.isNotEmpty(list.get(0).getTitle())) {
                        documentTitle = list.get(0).getTitle();
                    } else {
                        documentTitle = "正文";
                    }
                    transactionWordService.updateTransactionWordById(fileStoreId, fileType, documentTitle + fileType,
                        fileSizeString, isTaoHong, userId, list.get(0).getId());

                    List<TransactionHistoryWord> hfdList = null;
                    if (StringUtils.isNotBlank(taskId)) {
                        hfdList = transactionHistoryWordRepository
                            .getByProcessSerialNumberAndIsTaoHongAndTaskId(processSerialNumber, isTaoHong, taskId);
                    } else {
                        /**
                         * 流程刚启动的时候taskId为空
                         */
                        hfdList = transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
                    }
                    if (hfdList.isEmpty()) {
                        /**
                         * 在当前任务还没有保存过正文
                         */
                        transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString,
                            documentTitle, fileType, processSerialNumber, isTaoHong, taskId);
                    } else {
                        transactionHistoryWordService.updateTransactionHistoryWordById(fileStoreId, fileType,
                            documentTitle + fileType, fileSizeString, isTaoHong, userId, hfdList.get(0).getId());
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

    @Override
    public Y9Result<TransactionWordModel> wordDownload(String tenantId, String id) {
        TransactionWord transactionWord = transactionWordRepository.findById(id).orElse(null);
        assert transactionWord != null;
        TransactionWordModel model = getTransactionWord(transactionWord);
        return Y9Result.success(model);
    }
}
