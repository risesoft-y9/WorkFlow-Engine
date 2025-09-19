package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.template.BookMarkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface BookMarkBindService {

    /**
     * 
     * @param wordTemplateId 正文模版id
     * @param bookMarkName 书签名称
     */
    void deleteBind(String wordTemplateId, String bookMarkName);

    /**
     *
     * @param id 唯一标识
     * @return BookMarkBind
     */
    BookMarkBind findById(String id);

    /**
     * 
     * @param wordTemplateId 正文模版id
     * @param bookMarkName 书签名称
     * @return BookMarkBind
     */
    BookMarkBind findByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName);

    /**
     * 
     * @param wordTemplateId 正文模版id
     * @return List<BookMarkBind>
     */
    List<BookMarkBind> listByWordTemplateId(String wordTemplateId);

    /**
     * 
     * @param bookMarkBind 书签绑定信息
     */
    void saveOrUpdate(BookMarkBind bookMarkBind);
}
