package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.tab.TabEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TabEntityService {

    /**
     * 根据唯一标示查找页签
     *
     * @param id
     * @return
     */
    TabEntity getById(String id);

    /**
     * 查找所有的页签实体
     *
     * @return
     */
    List<TabEntity> listAll();

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
