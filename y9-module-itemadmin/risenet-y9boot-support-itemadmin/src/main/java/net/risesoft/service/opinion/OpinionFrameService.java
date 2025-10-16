package net.risesoft.service.opinion;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.opinion.OpinionFrame;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OpinionFrameService {

    /**
     * 
     * @param id
     * @return
     */
    OpinionFrame getById(String id);

    /**
     * 
     * @param mark
     * @return
     */
    OpinionFrame getByMark(String mark);

    /**
     * 
     * @return
     */
    List<OpinionFrame> listAll();

    /**
     * 
     * @param page
     * @param rows
     * @return
     */
    Page<OpinionFrame> pageAll(int page, int rows);

    /**
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param page
     * @param rows
     * @return
     */
    Page<OpinionFrame> pageAllNotUsed(String itemId, String processDefinitionId, String taskDefKey, int page, int rows);

    /**
     * 
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 
     * @param opinionFrame
     * @return
     */
    OpinionFrame save(OpinionFrame opinionFrame);

    /**
     * 
     * @param opinionFrame
     * @return
     */
    OpinionFrame saveOrUpdate(OpinionFrame opinionFrame);

    /**
     * 
     * @param page
     * @param rows
     * @param keyword
     * @return
     */
    Page<OpinionFrame> search(int page, int rows, String keyword);

    /**
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
