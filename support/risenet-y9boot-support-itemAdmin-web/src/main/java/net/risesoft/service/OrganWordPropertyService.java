package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.OrganWordProperty;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface OrganWordPropertyService {

    /**
     * Description:
     * 
     * @return
     */
    List<OrganWordProperty> findAll();

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
     * @return
     */
    List<OrganWordProperty> findByOrganWordId(String organWordId);

    /**
     * Description:
     * 
     * @param organWordId
     * @param name
     * @return
     */
    OrganWordProperty findByOrganWordIdAndName(String organWordId, String name);

    /**
     * Description: 根据传进来的机关代字Id的数组删除
     * 
     * @param organWordPropertyIds
     */
    public void removeOrganWordPropertys(String[] organWordPropertyIds);

    /**
     * Description: 保存机关代字
     * 
     * @param organWordProperty
     * @return
     */
    public Map<String, Object> save(OrganWordProperty organWordProperty);

    /**
     * 排序
     * 
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    public void update4Order(String[] idAndTabIndexs);
}
