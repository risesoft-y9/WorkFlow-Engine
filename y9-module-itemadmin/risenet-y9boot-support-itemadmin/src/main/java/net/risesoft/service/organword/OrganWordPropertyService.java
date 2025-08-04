package net.risesoft.service.organword;

import java.util.List;

import net.risesoft.entity.organword.OrganWordProperty;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OrganWordPropertyService {

    /**
     * Description:
     *
     * @param id
     * @return
     */
    OrganWordProperty findById(String id);

    /**
     * Description:
     *
     * @param organWordId
     * @param name
     * @return
     */
    OrganWordProperty findByOrganWordIdAndName(String organWordId, String name);

    /**
     * Description:
     *
     * @return
     */
    List<OrganWordProperty> listAll();

    /**
     * Description:
     *
     * @param organWordId
     * @return
     */
    List<OrganWordProperty> listByOrganWordId(String organWordId);

    /**
     * Description: 根据传进来的机关代字Id的数组删除
     *
     * @param organWordPropertyIds
     */
    void removeOrganWordPropertys(String[] organWordPropertyIds);

    /**
     * Description: 保存机关代字
     *
     * @param organWordProperty
     * @return
     */
    OrganWordProperty save(OrganWordProperty organWordProperty);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void update4Order(String[] idAndTabIndexs);
}
