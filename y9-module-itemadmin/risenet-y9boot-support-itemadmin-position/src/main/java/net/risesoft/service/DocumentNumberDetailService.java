package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.DocumentNumberDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentNumberDetailService {

    /**
     * 根据id查找年份
     *
     * @param id
     * @return
     */
    DocumentNumberDetail getById(String id);

    /**
     * 查找所有，但是这里始终只会有一条数据
     *
     * @return
     */
    List<DocumentNumberDetail> listAll();

    /**
     *
     * Description: 保存年份
     *
     * @param documentNumberDetail
     * @return
     */
    DocumentNumberDetail saveDocumentNumberDetail(DocumentNumberDetail documentNumberDetail);
}
