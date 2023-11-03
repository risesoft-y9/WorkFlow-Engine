package net.risesoft.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.TransactionFile;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.repository.jpa.TransactionFileRepository;
import net.risesoft.service.TransactionFileService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/attachment")
public class AttachmentApiImpl implements AttachmentApi {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TransactionFileService transactionFileService;

    @Autowired
    private TransactionFileRepository transactionFileRepository;

    @Autowired
    private PersonApi personApi;

    @Override
    @GetMapping(value = "/attachmentDownload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> attachmentDownload(String tenantId, String userId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionFileService.download(id);
        return map;
    }

    @Override
    @PostMapping(value = "/delBatchByProcessSerialNumbers", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delBatchByProcessSerialNumbers(String tenantId, @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionFileService.delBatchByProcessSerialNumbers(processSerialNumbers);

    }

    @Override
    @PostMapping(value = "/delFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delFile(String tenantId, String userId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = transactionFileService.delFile(ids);
        return map;
    }

    @Override
    @GetMapping(value = "/fileCounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer fileCounts(String tenantId, String userId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return transactionFileService.fileCounts(processSerialNumber);
    }

    @Override
    @GetMapping(value = "/getAttachmentCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getAttachmentCount(String tenantId, String userId, String processSerialNumber, String fileSource,
        String fileType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        fileType = fileType.toLowerCase();
        int count = transactionFileService.getTransactionFileCount(processSerialNumber, fileSource, fileType);
        return count;
    }

    @Override
    @GetMapping(value = "/getAttachmentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAttachmentList(String tenantId, String userId, String processSerialNumber,
        String fileSource, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = transactionFileService.getAttachmentList(processSerialNumber, fileSource, page, rows);
        return map;
    }

    @Override
    @GetMapping(value = "/getAttachmentModelList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttachmentModel> getAttachmentModelList(String tenantId, String userId, String processSerialNumber,
        String fileSource) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<TransactionFile> transactionFileList =
            transactionFileService.getAttachmentModelList(processSerialNumber, fileSource);
        List<AttachmentModel> attachmentModelList =
            ItemAdminModelConvertUtil.attachmentList2ModelList(transactionFileList);
        return attachmentModelList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @PostMapping(value = "/saveAttachment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean saveAttachment(String tenantId, String userId, String attachjson, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Boolean checkSave = false;
        try {
            Map<String, Object> attachmentjson = Y9JsonUtil.readValue(attachjson, Map.class);
            List<Map<String, Object>> attachmentList = (List<Map<String, Object>>)attachmentjson.get("attachment");
            for (Map<String, Object> map : attachmentList) {
                TransactionFile file = new TransactionFile();
                file.setDescribes(map.get("describes") == null ? "" : map.get("describes").toString());
                file.setFileStoreId(map.get("filePath").toString());
                file.setFileSize(map.get("fileSize") == null ? "" : map.get("fileSize").toString());
                file.setFileSource(map.get("fileSource") == null ? "" : map.get("fileSource").toString());
                file.setFileType(map.get("fileType") == null ? "" : map.get("fileType").toString());
                file.setId(map.get("id").toString());
                file.setName(map.get("fileName").toString());
                file.setPersonId(map.get("personId") == null ? "" : map.get("personId").toString());
                file.setPersonName(map.get("personName") == null ? "" : map.get("personName").toString());
                file.setProcessSerialNumber(processSerialNumber);
                file.setUploadTime(sdf.format(new Date()));
                transactionFileService.save(file);
                checkSave = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            checkSave = false;
        }
        return checkSave;
    }

    @Override
    @PostMapping(value = "/saveOrUpdateUploadInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveOrUpdateUploadInfo(String tenantId, String userId, String fileName, String fileType,
        String fileSizeString, String fileSource, String processInstanceId, String processSerialNumber, String taskId,
        String y9FileStoreId) {
        String msg = null;
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        try {
            TransactionFile attachment = transactionFileService.getFileInfoByFileName(fileName, processSerialNumber);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setName(fileName);
                attachment.setFileSize(fileSizeString);
                attachment.setTaskId(taskId);
                attachment.setPersonId(userId);
                attachment.setPersonName(person.getName());
                attachment.setUploadTime(sdf.format(new Date()));
                transactionFileRepository.save(attachment);
            } else {
                TransactionFile fileAttachment = new TransactionFile();
                fileAttachment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                fileAttachment.setFileStoreId(y9FileStoreId);
                fileAttachment.setName(fileName);
                fileAttachment.setFileSize(fileSizeString);
                fileAttachment.setFileType(fileType);
                fileAttachment.setUploadTime(sdf.format(new Date()));
                fileAttachment.setPersonId(userId);
                fileAttachment.setPersonName(person.getName());
                fileAttachment.setProcessInstanceId(processInstanceId);
                fileAttachment.setProcessSerialNumber(processSerialNumber);
                fileAttachment.setTaskId(taskId);
                fileAttachment.setFileSource(fileSource);
                fileAttachment.setTabIndex(transactionFileService.fileCounts(processSerialNumber) + 1);
                transactionFileRepository.save(fileAttachment);
            }
            msg = "success:true";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "success:false";
        }
        return msg;
    }

    @Override
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> upload(String tenantId, String userId, String fileName, String fileSize,
        String processInstanceId, String taskId, String describes, String processSerialNumber, String fileSource,
        String y9FileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = transactionFileService.uploadRest(fileName, fileSize, processInstanceId, taskId, processSerialNumber,
            describes, fileSource, y9FileStoreId);
        return map;
    }

    @Override
    @PostMapping(value = "/uploadModel", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean uploadModel(String tenantId, String userId, @RequestBody AttachmentModel attachmentModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        boolean success = false;

        TransactionFile transactionFile = ItemAdminModelConvertUtil.attachmentModel2TransactionFile(attachmentModel);
        try {
            transactionFileService.uploadRestModel(transactionFile);
            success = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return success;
    }
}
