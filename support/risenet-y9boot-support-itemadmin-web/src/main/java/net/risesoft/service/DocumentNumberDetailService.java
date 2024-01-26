package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.DocumentNumberDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface DocumentNumberDetailService {

    /**
     * 查找所有，但是这里始终只会有一条数据
     * 
     * @return
     */
    public List<DocumentNumberDetail> findAll();

    /**
     * 根据id查找年份
     * 
     * @param id
     * @return
     */
    public DocumentNumberDetail findOne(String id);

    /**
     * Description: 保存年份
     * 
     * @param documentNumberDetail
     * @return
     */
    public DocumentNumberDetail saveDocumentNumberDetail(DocumentNumberDetail documentNumberDetail);
}
