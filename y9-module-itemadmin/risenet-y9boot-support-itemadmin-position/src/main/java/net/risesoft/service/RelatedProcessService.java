package net.risesoft.service;

import org.springframework.data.domain.Page;

import net.risesoft.entity.RelatedProcess;

public interface RelatedProcessService {

    /**
     * 获取所有绑定的事项
     * 
     * @param parentItemId
     * @param page
     * @param rows
     * @return
     */
    Page<RelatedProcess> findAll(String parentItemId, int page, int rows);

    /**
     * 根据唯一标示查找
     * 
     * @param id
     */
    void delete(String id);

    /**
     * 复制绑定信息
     * 
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId, String newItemId);

    /**
     * 删除绑定信息
     * 
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 保存绑定的子事项
     * 
     * @param parentItemId
     * @param itemIdList
     * @return
     */
    void save(String parentItemId, String[] itemIdList);

}
