package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ExtendedContent;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ExtendedContentRepository
    extends JpaRepository<ExtendedContent, String>, JpaSpecificationExecutor<ExtendedContent> {

    /**
     * 根据processSerialNumber获取统计
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("select count(*) from ExtendedContent t where t.processSerialNumber=?1 and t.category=?2")
    int findByProcSerialNumberAndCategory(String processSerialNumber, String category);

    /**
     * 根据processSerialNumber和类型获取所有内容
     * 
     * @param processSerialNumber
     * @param category
     * @return
     */
    @Query("from ExtendedContent t where t.processSerialNumber=?1 and t.category=?2 order by t.modifyDate ASC")
    List<ExtendedContent> findByPsnAndCategory(String processSerialNumber, String category);

    /**
     * 根据taskId获取统计
     * 
     * @param taskId
     * @return
     */
    @Query("select count(*) from ExtendedContent t where t.taskId=?1 and t.category=?2")
    int getCountByTaskIdAndCategory(String taskId, String category);

    /**
     * 根据人员id 统计
     * 
     * @param userid
     * @param category
     * @return
     */
    @Query("select count(*) from ExtendedContent t where t.processSerialNumber=?1 and t.userId=?2 and t.category=?3")
    int getCountByUserIdAndCategory(String processSerialNumber, String userid, String category);

    /**
     * 根据人员id，流程编号统计当前类型的内容数
     * 
     * @param processSerialNumber
     * @param category
     * @param personId
     * @return
     */
    @Query("select count(*) from ExtendedContent t where t.processSerialNumber = ?1 and t.category=?2 and t.userId=?3")
    Integer getCountPersonal(String processSerialNumber, String category, String personId);

    /**
     * 根据人员id统计当前任务，当前类型的内容数
     * 
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param personId
     * @return
     */
    @Query("select count(*) from ExtendedContent t where t.processSerialNumber = ?1 and t.taskId=?2 and t.category=?3 and t.userId=?4")
    Integer getCountPersonal(String processSerialNumber, String taskId, String category, String personId);

    /**
     * 根据人员Id和流程序列号获取办理信息
     * 
     * @param processSerialNumber
     * @param userid
     * @param category
     * @return
     */
    @Query(" from ExtendedContent t where t.processSerialNumber=?1 and t.userId=?2 and t.category=?3")
    public ExtendedContent getResultByUserIdAndCategory(String processSerialNumber, String userid, String category);

    /**
     * 更新内容
     * 
     * @param taskId
     * @param processSerialNumber
     */
    @Query("update ExtendedContent t set t.taskId=?1 where t.processSerialNumber=?2")
    void update(String taskId, String processSerialNumber);
}
