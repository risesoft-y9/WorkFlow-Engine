package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.org.PersonApi;
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
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
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
@RestController
@RequestMapping(value = "/services/rest/transactionWord")
public class TransactionWordApiImpl implements TransactionWordApi {

    private static final Logger logger = LoggerFactory.getLogger(TransactionWordApiImpl.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TransactionWordRepository transactionWordRepository;

    @Autowired
    private TransactionWordService transactionWordService;

    @Autowired
    private TransactionHistoryWordService transactionHistoryWordService;

    @Autowired
    private TransactionHistoryWordRepository transactionHistoryWordRepository;

    @Autowired
    private TaoHongTemplateService taoHongTemplateService;

    @Autowired
    private WordTemplateRepository wordTemplateRepository;

    @Autowired
    private ItemWordTemplateBindRepository wordTemplateBindRepository;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApi repositoryManager;

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     */
    @Override
    @PostMapping(value = "/delBatchByProcessSerialNumbers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delBatchByProcessSerialNumbers(String tenantId, @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
        transactionHistoryWordService.delBatchByProcessSerialNumbers(processSerialNumbers);
    }

    /**
     * 删除撤销PDF文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return
     */
    @Override
    @PostMapping(value = "/deleteByIsTaoHong", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByIsTaoHong(String tenantId, String userId, String processSerialNumber, String isTaoHong) {
        List<TransactionWord> list = new ArrayList<TransactionWord>();
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
    }

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/exchangeFindWordByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> exchangeFindWordByProcessSerialNumber(String tenantId, String userId, String processSerialNumber) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
            if (list != null && list.size() > 0) {
                Map<String, Object> m = new HashMap<String, Object>(16);
                TransactionWord transactionWord = list.get(0);
                Person user = personManager.getPerson(tenantId, transactionWord.getUserId());
                m.put("fileName", transactionWord.getTitle() + transactionWord.getFileType());
                m.put("title", transactionWord.getTitle());
                m.put("fileSize", transactionWord.getFileSize());
                m.put("id", transactionWord.getId());
                m.put("processSerialNumber", transactionWord.getProcessSerialNumber());
                m.put("saveDate", transactionWord.getSaveDate());
                m.put("fileStoreId", transactionWord.getFileStoreId());
                m.put("userName", user.getName());
                m.put("fileType", transactionWord.getFileType());
                listMap.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/findHistoryVersionDoc", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findHistoryVersionDoc(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(1);
        List<TransactionHistoryWord> historyWord = transactionHistoryWordService.getListByTaskId(taskId);
        if (null != historyWord && StringUtils.isNotBlank(historyWord.get(0).getId())) {
            if (StringUtils.isNotEmpty(historyWord.get(0).getIstaohong())) {
                if (ItemWordTypeEnum.PDF.getValue().equals(historyWord.get(0).getIstaohong()) || ItemWordTypeEnum.PDF1.getValue().equals(historyWord.get(0).getIstaohong()) || ItemWordTypeEnum.PDF2.getValue().equals(historyWord.get(0).getIstaohong())) {
                    map.put("openWordOrPdf", "openPDF");
                } else if (ItemWordTypeEnum.WORD.getValue().equals(historyWord.get(0).getIstaohong()) || ItemWordTypeEnum.REDHEADWORD.getValue().equals(historyWord.get(0).getIstaohong())) {
                    map.put("openWordOrPdf", "openWord");
                }
            } else {
                map.put("openWordOrPdf", "openWord");
            }
            map.put("fileStoreId", historyWord.get(0).getFileStoreId());
            map.put("title", historyWord.get(0).getTitle());
            map.put("fileType", historyWord.get(0).getFileType());
            map.put("saveDate", historyWord.get(0).getSaveDate());
            Person p = personManager.getPerson(tenantId, historyWord.get(0).getUserId());
            map.put("userName", p != null && StringUtils.isNotBlank(p.getId()) ? p.getName() : "");
            map.put("isTaoHong", StringUtils.isNotBlank(historyWord.get(0).getIstaohong()) ? historyWord.get(0).getIstaohong() : "");
        }
        return map;
    }

    /**
     * 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/findWordByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findWordByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<TransactionWord> list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
            if (list != null && list.size() > 0) {
                TransactionWord transactionWord = list.get(0);
                Person user = personManager.getPerson(tenantId, transactionWord.getUserId());
                map.put("fileName", transactionWord.getTitle() + transactionWord.getFileType());
                map.put("fileSize", transactionWord.getFileSize());
                map.put("id", transactionWord.getId());
                map.put("processSerialNumber", transactionWord.getProcessSerialNumber());
                map.put("saveDate", transactionWord.getSaveDate());
                map.put("userName", user.getName());
                map.put("fileStoreId", transactionWord.getFileStoreId());
                map.put("isTaoHong", transactionWord.getIstaohong());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取正文列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return List&lt;Map&lt;String, Object&gt; &gt;
     */
    @Override
    @GetMapping(value = "/getWordList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getWordList(String tenantId, String userId, String processSerialNumber) {
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
            for (TransactionWord word : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("id", word.getId());
                map.put("title", word.getTitle());
                map.put("fileStoreId", word.getFileStoreId());
                map.put("fileSize", word.getFileSize());
                map.put("fileName", word.getFileName());
                map.put("processSerialNumber", word.getProcessSerialNumber());
                map.put("saveDate", word.getSaveDate());
                map.put("userId", word.getUserId());
                map.put("istaohong", word.getIstaohong());
                retList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 打开正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @return String
     */
    @Override
    @GetMapping(value = "/openDocument", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openDocument(String tenantId, String userId, String processSerialNumber, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                String docBase = transactionWord.getFileStoreId();
                return docBase;
            } else {
                logger.error("fileStoreId为空，保存正文的时候出错");
            }
        } else {// 打开事项配置的正文模板
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
            String processDefinitionId = processDefinition.getId();
            ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
            WordTemplate wordTemplate = wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                String docBase = wordTemplate.getFilePath();
                return docBase;
            } else {
                logger.error("数据库没有processSerialNumber=" + processSerialNumber + "的正文，请联系管理员");
            }
        }
        return null;
    }

    /**
     * 根据流程编号打开正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return String
     */
    @Override
    @GetMapping(value = "/openDocumentByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openDocumentByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            // 获取套红文档
            list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, "1");
            /**
             * 套红文档不存在,则获取未套红文档
             */
            if (list.isEmpty()) {
                list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, "0");
            }
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                String docBase = transactionWord.getFileStoreId();
                return docBase;
            } else {
                logger.error("fileStoreId为空，保存正文的时候出错");
            }
        }
        return null;
    }

    /**
     * 打开套红模板
     *
     * @param tenantId 租户id
     * @param userId 人id
     * @param templateGUID 模板id
     * @return String
     */
    @Override
    @GetMapping(value = "/openDocumentTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openDocumentTemplate(String tenantId, String userId, String templateGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/openTaohongTemplate");
        byte[] buf = null;
        TaoHongTemplate taohongTemplate = taoHongTemplateService.findOne(templateGuid);
        if (null != taohongTemplate) {
            buf = taohongTemplate.getTemplateContent();
            if (buf != null) {
                try {
                    String docBase = jodd.util.Base64.encodeToString(buf);
                    return docBase;
                } catch (Exception e) {
                    logger.error("向jsp页面输出word二进制流错误");
                    e.printStackTrace();
                } finally {
                }
            }
        } else {
            logger.error("数据库没有templateGUID=" + templateGuid + "的模版，请联系管理员");
        }
        return null;
    }

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     */
    @Override
    @GetMapping(value = "/openHistoryVersionDoc", produces = MediaType.APPLICATION_JSON_VALUE)
    public void openHistoryVersionDoc(String tenantId, String userId, String taskId) {
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
    }

    /**
     * 打开PDF
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return String
     */
    @Override
    @GetMapping(value = "/openPdf", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openPdf(String tenantId, String userId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                String docBase = transactionWord.getFileStoreId();
                return docBase;
            } else {// 从数据库读取正文
                logger.error("fileStoreId为空，保存正文的时候出错");
            }
        }
        return null;
    }

    /**
     * 打开撤销PDF后的正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return String
     */
    @Override
    @GetMapping(value = "/openRevokePDFAfterDocument", produces = MediaType.APPLICATION_JSON_VALUE)
    public String openRevokePdfAfterDocument(String tenantId, String userId, String processSerialNumber, String isTaoHong) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
        }
        TransactionWord transactionWord;
        if (!list.isEmpty()) {
            transactionWord = list.get(0);
            if (StringUtils.isNotBlank(transactionWord.getFileStoreId())) {
                String docBase = transactionWord.getFileStoreId();
                return docBase;
            } else {// 从数据库读取正文
                logger.error("fileStoreId为空，保存正文的时候出错");
            }
        }
        return null;
    }

    /**
     * 选择套红
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/openTaoHong", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> openTaoHong(String tenantId, String userId, String activitiUser) {
        Map<String, Object> model = new HashMap<String, Object>(16);

        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/openTaoHong");
        // 当前人员的委办局GUID
        OrgUnit currentBureau = personManager.getBureau(Y9LoginUserHolder.getTenantId(), activitiUser);
        model.put("currentBureauGuid", currentBureau.getId());
        return model;
    }

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param docjson 正文json信息
     * @param processSerialNumber 流程序列号
     * @return Boolean
     */
    @SuppressWarnings("unchecked")
    @Override
    @PostMapping(value = "/saveImportTransationWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean saveImportTransationWord(String tenantId, String userId, String docjson, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
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
        } catch (Exception e) {
            e.printStackTrace();
            checkSave = false;
        }
        return checkSave;
    }

    /**
     * 获取正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/showWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> showWord(String tenantId, String userId, String processSerialNumber, String itemId, String itembox, String taskId) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        String fileDocumentId = "";
        String wordReadOnly = "";
        String openWordOrPdf = "";
        String isTaoHong = "0";
        if ("".equals(itembox) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.ADD.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DRAFT.getValue())) {
            wordReadOnly = "NO";
        } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())) {
            wordReadOnly = "YES";
        }
        String saveDate = "";
        List<TransactionWord> list = new ArrayList<TransactionWord>();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            list = transactionWordService.findByProcessSerialNumber(processSerialNumber);
        }
        if (list != null && list.size() > 0) {
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
            retMap.put("fileType", d.getFileType());
            retMap.put("uid", d.getFileStoreId());
        } else {
            String processDefinitionId = "";
            if (StringUtils.isNoneBlank(taskId)) {
                TaskModel task = taskManager.findById(tenantId, taskId);
                processDefinitionId = task.getProcessDefinitionId();
            } else {
                SpmApproveItem item = spmApproveItemService.findById(itemId);
                String processDefinitionKey = item.getWorkflowGuid();
                ProcessDefinitionModel processDefinitionModel = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
                processDefinitionId = processDefinitionModel.getId();
            }
            ItemWordTemplateBind wordTemplateBind = wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
            WordTemplate wordTemplate = wordTemplateRepository.findById(wordTemplateBind != null ? wordTemplateBind.getTemplateId() : "").orElse(null);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                fileDocumentId = wordTemplate.getId();
                openWordOrPdf = "openWordTemplate";
            }
        }
        String activitiUser = person.getId();
        retMap.put("activitiUser", activitiUser);
        retMap.put("fileDocumentId", fileDocumentId);
        retMap.put("processSerialNumber", processSerialNumber);
        retMap.put("userName", person.getName());
        retMap.put("saveDate", saveDate);
        retMap.put("openWordOrPdf", openWordOrPdf);
        retMap.put("wordReadOnly", wordReadOnly);
        retMap.put("itemId", itemId);
        retMap.put("itembox", itembox);
        retMap.put("taskId", taskId);
        retMap.put("isTaoHong", isTaoHong);
        return retMap;
    }

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/taoHongTemplateList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> taoHongTemplateList(String tenantId, String userId, String currentBureauGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        logger.debug("call /ntko/list");
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

        List<TaoHongTemplate> list = taoHongTemplateService.findByBureauGuid(currentBureauGuid);

        if (list.isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("hasTaoHongTemplate", "0");
            retList.add(map);
        } else {
            for (TaoHongTemplate taoHongTemplate : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("hasDocumentTemplate", "1");
                map.put("templateGuid", taoHongTemplate.getTemplateGuid());
                map.put("template_fileName", taoHongTemplate.getTemplateFileName());
                map.put("templateType", taoHongTemplate.getTemplateType());
                retList.add(map);
            }
        }
        return retList;
    }

    /**
     * 草稿箱保存正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param documentTitle 文档标题
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param fileSizeString 文件大小
     * @param fileStoreId 文件id
     * @return String
     */
    @Override
    @PostMapping(value = "/uploadWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadWord(String tenantId, String userId, String documentTitle, String fileType, String processSerialNumber, String isTaoHong, String taskId, String fileSizeString, String fileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        String info = "";
        try {
            if (StringUtils.isNotBlank(processSerialNumber) && !"".equals(processSerialNumber)) {
                List<TransactionWord> list = transactionWordService.findByProcessSerialNumberAndIstaohong(processSerialNumber, isTaoHong);
                if (list.size() == 0) {
                    transactionWordService.saveTransactionWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber, isTaoHong);
                    transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber, isTaoHong, taskId);
                } else {
                    if (StringUtils.isNotEmpty(list.get(0).getTitle())) {
                        documentTitle = list.get(0).getTitle();
                    } else {
                        documentTitle = "正文";
                    }
                    transactionWordService.updateTransactionWordById(fileStoreId, fileType, documentTitle + fileType, fileSizeString, isTaoHong, userId, list.get(0).getId());

                    List<TransactionHistoryWord> thwlist = null;
                    if (StringUtils.isNotBlank(taskId)) {
                        thwlist = transactionHistoryWordRepository.getByProcessSerialNumberAndIsTaoHongAndTaskId(processSerialNumber, isTaoHong, taskId);
                    } else {
                        /**
                         * 流程刚启动的时候taskId为空
                         */
                        thwlist = transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
                    }
                    if (thwlist.size() == 0) {
                        /**
                         * 在当前任务还没有保存过正文
                         */
                        transactionHistoryWordService.saveTransactionHistoryWord(fileStoreId, fileSizeString, documentTitle, fileType, processSerialNumber, isTaoHong, taskId);
                    } else {
                        transactionHistoryWordService.updateTransactionHistoryWordById(fileStoreId, fileType, documentTitle + fileType, fileSizeString, isTaoHong, userId, thwlist.get(0).getId());
                    }
                }
                info = "success:true";
            } else {
                info = "success:false";
            }
        } catch (Exception e) {
            info = "success:false";
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id 正文id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/wordDownload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> wordDownload(String tenantId, String id) {
        Optional<TransactionWord> transactionWord = transactionWordRepository.findById(id);
        Map<String, Object> map = new HashMap<>(16);
        map.put("fileSize", transactionWord.get().getFileSize());
        map.put("filename", transactionWord.get().getTitle());
        map.put("fileStoreId", transactionWord.get().getFileStoreId());
        return map;
    }
}
