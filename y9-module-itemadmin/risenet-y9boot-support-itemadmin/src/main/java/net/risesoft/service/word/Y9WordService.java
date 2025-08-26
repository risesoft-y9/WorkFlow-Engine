package net.risesoft.service.word;

import java.util.List;

import net.risesoft.entity.documentword.Y9Word;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9WordService {

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     *
     * @param processSerialNumbers
     */
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 根据流程编号获取最新正文
     *
     * @param processSerialNumber
     * @return
     */
    Y9Word getByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程编号获取正文
     *
     * @param processSerialNumber
     * @return
     */
    List<Y9Word> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程编号和文档类别获取正文
     *
     * @param processSerialNumber
     * @param docCategory
     * @return
     */
    List<Y9Word> listByProcessSerialNumberAndDocCategory(String processSerialNumber, String docCategory);

    /**
     * 根据processSerialNumber和taohong获取套红正文
     *
     * @param processSerialNumber
     * @param taohong
     * @return
     */
    List<Y9Word> listByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong);

    /**
     * Description:
     *
     * @param tw
     */
    void save(Y9Word tw);

    /**
     * 保存word正文
     *
     * @param fileStoreId
     * @param fileSize
     * @param documenttitle
     * @param fileType
     * @param processSerialNumber
     * @param isTaoHong
     * @param docCategory 文档类别
     */
    void saveWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String isTaoHong, String docCategory);

    /**
     * 保存正文信息
     *
     * @param docjson
     * @param processSerialNumber
     * @return
     */
    Boolean saveWord(String docjson, String processSerialNumber);

    /**
     * Description:
     *
     * @param fileStoreId
     * @param fileType
     * @param fileName
     * @param fileSize
     * @param isTaoHong
     * @param userId
     * @param id
     */
    void updateById(String fileStoreId, String fileType, String fileName, String fileSize, String isTaoHong,
        String userId, String id);

}
