package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.TabEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface TabEntityService {

    /**
     * 查找所有的页签实体
     * 
     * @return
     */
    List<TabEntity> findAll();

    /**
     * 根据唯一标示查找页签
     * 
     * @param id
     * @return
     */
    TabEntity findOne(String id);

    /**
     * 根据传进来的Id集合删除页签实体
     * 
     * @param tabEntityIds
     */
    void removeTabEntitys(String[] tabEntityIds);

    /**
     * 保存或者更新页签实体
     * 
     * @param tabEntity
     * @return
     */
    TabEntity saveOrUpdate(TabEntity tabEntity);
}
