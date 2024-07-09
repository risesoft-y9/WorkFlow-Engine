package net.risesoft.service;

import net.risesoft.entity.RelatedProcess;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RelatedProcessService {

    /**
     * 获取所有绑定的事项
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

    void copyBindInfo(String itemId,String newItemId);

    /**
     *保存绑定的子事项
     * @param parentItemId
     * @param itemIdList
     * @return
     */
    void save(String parentItemId, List<String> itemIdList);

}
