package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.entity.TransactionFile;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.pojo.Y9Page;

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
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param ids
     * @return
     */
    void delFile(String ids);

    /**
     * (软航附件控件) 根据流程序列号查询附件数量
     *
     * @param processSerialNumber
     * @return
     */
    Integer fileCounts(String processSerialNumber);

    /**
     * 获取附件
     *
     * @param id
     * @return
     */
    TransactionFile findById(String id);

    /**
     * (软航附件控件) 根据附件名称查询附件信息
     *
     * @param fileName
     * @param processSerialNumber
     * @return
     */
    TransactionFile getFileInfoByFileName(String fileName, String processSerialNumber);

    /**
     * 根据流程实例id、附件来源和文件类型获取附件数
     *
     * @param processSerialNumber
     * @param fileSource
     * @param fileType
     * @return
     */
    Integer getTransactionFileCount(String processSerialNumber, String fileSource, String fileType);

    /**
     * (软航附件控件) 获取下一个附件控件
     *
     * @param nextTabIndex
     * @param processSerialNumber
     * @return
     */
    TransactionFile getUpFileInfoByTabIndexOrProcessSerialNumber(Integer nextTabIndex, String processSerialNumber);

    /**
     * 根据processSerialNumber获取
     *
     * @param processSerialNumber
     * @return
     */
    List<TransactionFile> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程实例id和附件来源获取附件列表
     *
     * @param processSerialNumber
     * @param fileSource
     * @return
     */
    List<TransactionFile> listByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource);

    /**
     * Description: 获取附件列表
     *
     * @param processSerialNumber
     * @param fileSource
     * @return
     */
    List<TransactionFile> listSearchByProcessSerialNumber(String processSerialNumber, String fileSource);

    /**
     * Description: 获取附件列表
     *
     * @param processSerialNumber
     * @param fileSource
     * @param page
     * @param rows
     * @return
     */
    Y9Page<AttachmentModel> pageByProcessSerialNumber(String processSerialNumber, String fileSource, int page,
        int rows);

    /**
     * Description: 保存附件
     *
     * @param file
     */
    void save(TransactionFile file);

    /**
     * Description: 保存附件信息
     *
     * @param attachjson
     * @param processSerialNumber
     * @return
     */
    Boolean saveAttachment(String attachjson, String processSerialNumber);

    /**
     * 更新附件
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param taskId
     */
    void update(String processSerialNumber, String processInstanceId, String taskId);

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
    Map<String, Object> upload(MultipartFile filename, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource);

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
    void uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId);

    /**
     * Description: 上传附件
     *
     * @param transactionFile
     * @return
     */
    TransactionFile uploadRestModel(TransactionFile transactionFile);
}
