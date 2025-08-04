package net.risesoft.service.opinion;

import java.util.List;

import net.risesoft.entity.commonsentences.CommonSentences;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface CommonSentencesService {

    /**
     * 根据id删除常用语
     *
     * @param id
     */
    void deleteById(String id);

    List<CommonSentences> getByUserId(String userId);

    /**
     * 获取常用语
     *
     * @return
     */
    List<CommonSentences> listSentencesService();

    /**
     * 删除常用语
     *
     * @param tabIndex
     */
    void removeCommonSentences(int tabIndex);

    void removeUseNumber();

    /**
     * 保存常用语
     *
     * @param content
     * @throws Exception
     */
    void save(String content);

    /**
     * 保存更新常用语
     *
     * @param id
     * @param content
     */
    void save(String id, String content);

    CommonSentences saveCommonSentences(String userId, String content, int tabIndex);

    void updateUseNumber(String id);
}
