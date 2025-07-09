package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.documentword.TransactionHistoryWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TransactionHistoryWordService {

    /**
     * 根据流程编号删除历史正文，同时删除文件系统的文件
     *
     * @param processSerialNumbers
     */
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * Description: 删除历史正文
     *
     * @param processSerialNumber
     * @param isTaoHong
     */
    void deleteHistoryWordByIsTaoHong(String processSerialNumber, String isTaoHong);

    /**
     * 根据流程编号获取最新正文
     *
     * @param processSerialNumber
     * @return
     */
    TransactionHistoryWord getByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据taskId找历史正文
     *
     * @param taskId
     * @return
     */
    TransactionHistoryWord getTransactionHistoryWordByTaskId(String taskId);

    /**
     * 根据流程编号获取正文
     *
     * @param processSerialNumber
     * @return
     */
    List<TransactionHistoryWord> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据taskId找历史正文
     *
     * @param taskId
     * @return List
     */
    List<TransactionHistoryWord> listByTaskId(String taskId);

    /**
     * Description: 保存word正文
     *
     * @param fileStoreId
     * @param fileSize
     * @param documenttitle
     * @param fileType
     * @param processSerialNumber
     * @param isTaoHong
     * @param taskId
     * @param docCategory
     */
    void saveTransactionHistoryWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, String docCategory);

    /**
     * 如果用户在启动流程之前先保存了正文，这时意见数据表中之前保存的数据的taskId仍为空， 此时需要根据processSerialNumber给历史正文表的taskId赋值
     *
     * @param processSerialNumber
     * @param taskId
     * @return
     */
    int update(String taskId, String processSerialNumber);

    /**
     * Description: 更新正文
     *
     * @param fileStoreId
     * @param fileType
     * @param fileName
     * @param fileSize
     * @param isTaoHong
     * @param docCategory
     * @param userId
     * @param id
     */
    void updateTransactionHistoryWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String isTaoHong, String docCategory, String userId, String id);
}
