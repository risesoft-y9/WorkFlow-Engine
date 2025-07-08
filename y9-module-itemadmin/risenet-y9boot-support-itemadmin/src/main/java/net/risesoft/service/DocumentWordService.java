package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.documentword.DocumentWord;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentWordService {

    Y9Result<Object> copyByProcessSerialNumberAndWordType(String sourceProcessSerialNumber,
        String targetProcessSerialNumber, String wordType);

    /**
     * 根据流程编号和正文类型查询正文信息
     *
     * @param processSerialNumber 流程编号
     * @param wordType 正文类型
     * @return List<DocumentWordModel>
     */
    List<DocumentWordModel> findByProcessSerialNumberAndWordType(String processSerialNumber, String wordType);

    /**
     * 根据id查询正文信息
     *
     * @param id id
     * @return DocumentWord
     */
    DocumentWord findWordById(String id);

    /**
     * 替换正文
     * 
     * @param documentWord documentWord
     * @param oldId
     * @param taskId
     */
    void replaceWord(DocumentWord documentWord, String oldId, String taskId);

    /**
     * 保存正文信息
     *
     * @param documentWord documentWord
     */
    DocumentWord saveWord(DocumentWord documentWord);
}
