package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.BookMarkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface BookMarkBindService {

    /**
     * Description:
     * 
     * @param wordTemplateId
     * @param bookMarkName
     */
    void deleteBind(String wordTemplateId, String bookMarkName);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    BookMarkBind findById(String id);

    /**
     * Description:
     * 
     * @param wordTemplateId
     * @return
     */
    List<BookMarkBind> findByWordTemplateId(String wordTemplateId);

    /**
     * Description:
     * 
     * @param wordTemplateId
     * @param bookMarkName
     * @return
     */
    BookMarkBind findByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName);

    /**
     * Description:
     * 
     * @param bookMarkBind
     */
    void saveOrUpdate(BookMarkBind bookMarkBind);
}
