package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface CommonSentencesApi {

    /**
     * 删除常用语
     *
     * @param tenantId 租户id
     * @param id 常用语id
     */
    void delete(String tenantId, String id);

    /**
     * 获取常用语字符串
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    String getCommonSentencesStr(String tenantId, String userId);

    /**
     * 获取常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> listSentencesService(String tenantId, String userId);

    /**
     * 根据排序号tabIndex删除常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param tabIndex 排序号
     */
    void removeCommonSentences(String tenantId, String userId, int tabIndex);

    /**
     * 根据id保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 常用语的唯一标识
     * @param content 内容
     */
    void save(String tenantId, String userId, String id, String content);

    /**
     * 根据排序号tabIndex保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param content 常用语内容
     * @param tabIndex 排序号
     */
    void saveCommonSentences(String tenantId, String userId, String content, int tabIndex);
}
