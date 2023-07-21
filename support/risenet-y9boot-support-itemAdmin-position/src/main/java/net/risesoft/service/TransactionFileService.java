package net.risesoft.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.entity.TransactionFile;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TransactionFileService {

    /**
     * 根据流程编号删除附件，同时删除文件系统的文件
     *
     * @param processSerialNumbers
     */
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param ids
     * @return
     */
    public Map<String, Object> delFile(String ids);

    /**
     * Description: 附件下载
     * 
     * @param id
     * @return
     */
    public Map<String, Object> download(String id);

    /**
     * (软航附件控件) 根据流程序列号查询附件数量
     *
     * @param processSerialNumber
     * @return
     */
    public Integer fileCounts(String processSerialNumber);

    /**
     * Description: 获取附件列表
     * 
     * @param processSerialNumber
     * @param fileSource
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> getAttachmentList(String processSerialNumber, String fileSource, int page, int rows);

    /**
     * Description: 获取附件列表
     * 
     * @param processSerialNumber
     * @param fileSource
     * @return
     */
    public List<TransactionFile> getAttachmentModelList(String processSerialNumber, String fileSource);

    /**
     * (软航附件控件) 根据附件名称查询附件信息
     *
     * @param fileName
     * @param processSerialNumber
     * @return
     */
    public TransactionFile getFileInfoByFileName(String fileName, String processSerialNumber);

    /**
     * 根据processSerialNumber获取
     *
     * @param processSerialNumber
     * @return
     */
    public List<TransactionFile> getListByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程实例id和附件来源获取附件列表
     *
     * @param processSerialNumber
     * @param fileSource
     * @return
     */
    public List<TransactionFile> getListByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource);

    /**
     * 根据流程实例id、附件来源和文件类型获取附件数
     *
     * @param processSerialNumber
     * @param fileSource
     * @param fileType
     * @return
     */
    public Integer getTransactionFileCount(String processSerialNumber, String fileSource, String fileType);

    /**
     * (软航附件控件) 获取下一个附件控件
     *
     * @param nextTabIndex
     * @param processSerialNumber
     * @return
     */
    public TransactionFile getUpFileInfoByTabIndexOrProcessSerialNumber(Integer nextTabIndex, String processSerialNumber);

    /**
     * 
     * Description: 保存附件
     * 
     * @param file
     */
    public void save(TransactionFile file);

    /**
     * Description: 保存附件信息
     * 
     * @param attachjson
     * @param processSerialNumber
     * @return
     */
    public Boolean saveAttachment(String attachjson, String processSerialNumber);

    /**
     * 更新附件
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param taskId
     */
    public void update(String processSerialNumber, String processInstanceId, String taskId);

    /**
     * Description: 上传附件
     * 
     * @param filename
     * @param processInstanceId
     * @param taskId
     * @param processSerialNumber
     * @param describes
     * @param fileSource
     * @return
     */
    public Map<String, Object> upload(MultipartFile filename, String processInstanceId, String taskId, String processSerialNumber, String describes, String fileSource);

    /**
     * Description: 上传附件
     * 
     * @param fileName
     * @param fileSize
     * @param processInstanceId
     * @param taskId
     * @param processSerialNumber
     * @param describes
     * @param fileSource
     * @param y9FileStoreId
     * @return
     */
    public Map<String, Object> uploadRest(String fileName, String fileSize, String processInstanceId, String taskId, String processSerialNumber, String describes, String fileSource, String y9FileStoreId);

    /**
     * Description: 上传附件
     * 
     * @param transactionFile
     * @return
     * @throws ParseException
     */
    public TransactionFile uploadRestModel(TransactionFile transactionFile) throws ParseException;
}
