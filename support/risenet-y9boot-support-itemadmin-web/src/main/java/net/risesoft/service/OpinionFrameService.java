package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.OpinionFrame;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface OpinionFrameService {

    /**
     * Description:
     * 
     * @return
     */
    List<OpinionFrame> findAll();

    /**
     * Description:
     * 
     * @param page
     * @param rows
     * @return
     */
    Page<OpinionFrame> findAll(int page, int rows);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param page
     * @param rows
     * @return
     */
    Page<OpinionFrame> findAllNotUsed(String itemId, String processDefinitionId, String taskDefKey, int page, int rows);

    /**
     * Description:
     * 
     * @param mark
     * @return
     */
    OpinionFrame findByMark(String mark);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    OpinionFrame findOne(String id);

    /**
     * Description:
     * 
     * @param id
     */
    void remove(String id);

    /**
     * Description:
     * 
     * @param ids
     */
    void remove(String[] ids);

    /**
     * Description:
     * 
     * @param opinionFrame
     * @return
     */
    OpinionFrame save(OpinionFrame opinionFrame);

    /**
     * Description:
     * 
     * @param opinionFrame
     * @return
     */
    OpinionFrame saveOrUpdate(OpinionFrame opinionFrame);

    /**
     * Description:
     * 
     * @param page
     * @param rows
     * @param keyword
     * @return
     */
    Page<OpinionFrame> search(int page, int rows, String keyword);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param page
     * @param rows
     * @param keyword
     * @return
     */
    Page<OpinionFrame> search4NotUsed(String itemId, String processDefinitionId, String taskDefKey, int page, int rows,
        String keyword);
}
