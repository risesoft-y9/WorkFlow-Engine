package net.risesoft.service.word;

import net.risesoft.entity.documentword.DocumentHistoryWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentHisWordService {

    /**
     * 根据id查询正文信息
     *
     * @param id id
     * @return DocumentHistoryWord
     */
    DocumentHistoryWord findWordById(String id);

}
