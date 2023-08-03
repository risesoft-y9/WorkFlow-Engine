package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.TransactionWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface TransactionWordService {

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     * 
     * @param processSerialNumbers
     */
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 根据流程编号获取正文
     * 
     * @param processSerialNumber
     * @return
     */
    List<TransactionWord> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据processSerialNumber和taohong获取套红正文
     * 
     * @param processSerialNumber
     * @param taohong
     * @return
     */
    List<TransactionWord> findByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong);

    /**
     * 根据流程编号获取最新正文
     * 
     * @param processSerialNumber
     * @return
     */
    TransactionWord getByProcessSerialNumber(String processSerialNumber);

    /**
     * Description:
     * 
     * @param tw
     */
    void save(TransactionWord tw);

    /**
     * Description: 保存word正文
     * 
     * @param fileStoreId
     * @param fileSize
     * @param documenttitle
     * @param fileType
     * @param processSerialNumber
     * @param isTaoHong
     */
    void saveTransactionWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String isTaoHong);

    /**
     * Description: 保存正文信息
     * 
     * @param docjson
     * @param processSerialNumber
     * @return
     */
    Boolean saveWord(String docjson, String processSerialNumber);

    /**
     * Description: 更新套红正文
     * 
     * @param fileStoreId
     * @param fileType
     * @param fileName
     * @param fileSize
     * @param isTaoHong
     * @param userId
     * @param id
     */
    void updateTransactionWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String isTaoHong, String userId, String id);

}
