package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ActRuDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ActRuDetailRepository
    extends JpaRepository<ActRuDetail, String>, JpaSpecificationExecutor<ActRuDetail> {

    int countBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee);

    int countBySystemNameAndAssigneeAndEndedTrueAndDeletedFalse(String systemName, String assignee);

    int countBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndCreateTimeAndPlaceOnFileFalse(String systemName,
        String assignee, String createTime);

    int countBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndPlaceOnFileFalse(String systemName, String assignee);

    int countBySystemNameAndAssigneeAndStatusAndCreateTimeAndDeletedFalse(String systemName, String assignee,
        int status, String createTime);

    int countByAssigneeAndStatusAndDeletedFalse(String assignee, int status);

    int countBySystemNameAndAssigneeAndStatusAndDeletedFalse(String systemName, String assignee, int status);

    int countBySystemNameAndAssigneeAndStatusAndEndedFalseAndCreateTimeAndDeletedFalse(String systemName,
        String assignee, int status, String createTime);

    int countByAssigneeAndStatusAndEndedFalseAndDeletedFalse(String assignee, int status);

    int countBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(String systemName, String assignee,
        int status);

    int countBySystemNameAndDeletedTrue(String systemName);

    int countBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId);

    int countBySystemNameAndDeptIdAndEndedTrueAndDeletedTrue(String systemName, String deptId);

    int countBySystemNameAndEndedFalse(String systemName);

    int countBySystemNameAndEndedTrueAndDeletedFalse(String systemName);

    int countBySystemNameAndEndedTrueAndDeletedTrue(String systemName);

    List<ActRuDetail> findByProcessInstanceId(String processInstanceId);

    ActRuDetail findByProcessInstanceIdAndAssigneeAndStatus(String processInstanceId, String assignee, int status);

    ActRuDetail findByTaskIdAndAssignee(String taskId, String assignee);

    List<ActRuDetail> findByTaskId(String taskId);

    List<ActRuDetail> findByProcessInstanceIdAndStatusOrderByCreateTimeAsc(String processInstanceId, int status);

    List<ActRuDetail> findByProcessSerialNumber(String processSerialNumber);

    ActRuDetail findByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee);

    List<ActRuDetail> findByProcessSerialNumberAndAssigneeAndStatus(String processSerialNumber, String assignee,
        int status);

    List<ActRuDetail> findByProcessSerialNumberAndEnded(String processSerialNumber, boolean ended);

    List<ActRuDetail> findByProcessSerialNumberAndStatusOrderByCreateTimeAsc(String processSerialNumber, int status);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndDeletedFalse(String systemName, String assignee, Pageable pageable);

    @Query(nativeQuery = true,
        value = "SELECT A.* FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.DELETED =FALSE AND T.SYSTEMNAME = ?1) A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.DELETED =FALSE AND SYSTEMNAME = ?1) A WHERE A.RS_NUM = 1) ALIAS")
    Page<ActRuDetail> findBySystemNameNativeQuery(String systemName, Pageable pageable);

    /**
     * 某个系统标识的在办、办结件
     * 
     * @param systemName 系统名称
     * @param ended 是否办结
     * @param pageable 分页信息
     * @return
     */
    @Query(nativeQuery = true,
        value = "SELECT A.* FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 and T.ENDED =?2 AND  T.DELETED = FALSE) A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1  AND T.ENDED =?2 AND T.DELETED = FALSE) A WHERE A.RS_NUM = 1) ALIAS")
    Page<ActRuDetail> findBySystemNameAndEndedNativeQuery(String systemName, boolean ended, Pageable pageable);

    /**
     * 某个系统标识的删除件
     *
     * @param systemName 系统名称
     * @param pageable 分页信息
     * @return
     */
    @Query(nativeQuery = true,
        value = "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 and T.DELETED = TRUE) A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.DELETED = TRUE) A WHERE A.RS_NUM = 1) ALIAS")
    Page<ActRuDetail> findBySystemNameAndDeletedTrueNativeQuery(String systemName, Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndDeletedFalseAndPlaceOnFileFalse(String systemName, String assignee,
        Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT A.* FROM ("
        + "SELECT *,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.DEPTID = ?2 AND T.ENDED = ?3 AND T.DELETED = false"
        + ") A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM ("
            + "SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.DEPTID = ?2 AND T.ENDED = ?3 AND T.DELETED = FALSE) A WHERE A.RS_NUM = 1"
            + ") ALIAS")
    Page<ActRuDetail> findBySystemNameAndDeptIdAndEndedAndDeletedFalseNativeQuery(String systemName, String deptId,
        boolean ended, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT A.* FROM ("
        + "SELECT *,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.BUREAUID = ?2 AND T.ENDED = ?3 AND  T.DELETED = FALSE"
        + ") A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM ("
            + "SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.BUREAUID = ?2 AND T.ENDED = ?3 AND T.DELETED = FALSE) A WHERE A.RS_NUM = 1"
            + ") ALIAS")
    Page<ActRuDetail> findBySystemNameAndBureauIdAndEndedAndDeletedFalseNativeQuery(String systemName, String bureauId,
        boolean ended, Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndEndedTrueAndDeletedFalse(String systemName, String assignee,
        Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndPlaceOnFileFalse(String systemName,
        String assignee, Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndDeletedFalse(String systemName, String assignee, boolean ended,
        Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee, Pageable pageable);

    @Query(nativeQuery = true,
        value = "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.DEPTID = ?2 AND T.DELETED = TRUE) A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.DEPTID = ?2 AND T.DELETED = TRUE) A WHERE A.RS_NUM = 1) ALIAS")
    Page<ActRuDetail> findBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId, Pageable pageable);

    @Query(nativeQuery = true,
        value = "SELECT A.* FROM (SELECT T.*,ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.BUREAUID = ?2 AND T.DELETED = TRUE) A WHERE A.RS_NUM = 1",
        countQuery = "SELECT COUNT(*) FROM (SELECT A.* FROM (SELECT ROW_NUMBER() OVER (PARTITION BY T.PROCESSSERIALNUMBER ORDER BY T.LASTTIME DESC) AS RS_NUM FROM FF_ACT_RU_DETAIL T WHERE T.SYSTEMNAME = ?1 AND T.BUREAUID = ?2 AND T.DELETED = TRUE) A WHERE A.RS_NUM = 1) ALIAS")
    Page<ActRuDetail> findBySystemNameAndBureauIdAndDeletedTrue(String systemName, String bureauId, Pageable pageable);

    List<ActRuDetail> findBySystemNameAndAssigneeAndStatusAndDeletedFalse(String systemName, String assignee,
        int status);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndStatusAndDeletedFalse(String systemName, String assignee,
        int status, Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndTaskDefKeyAndAssigneeAndStatusAndDeletedFalse(String systemName,
        String taskDefKey, String assignee, int status, Pageable pageable);

    Page<ActRuDetail> findByAssigneeAndStatusAndDeletedFalse(String assignee, int status, Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(String systemName,
        String assignee, int status, Pageable pageable);

    Page<ActRuDetail> findByAssigneeAndStatusAndEndedFalseAndDeletedFalse(String assignee, int status,
        Pageable pageable);

    Page<ActRuDetail> findBySystemNameAndStatusAndEndedFalse(String systemName, int status, Pageable pageable);

    Page<ActRuDetail> findBySystemNameInAndAssigneeAndStatusAndDeletedFalse(List<String> systemNameList,
        String assignee, int status, Pageable pageable);

    Page<ActRuDetail> findBySystemNameNotAndAssigneeAndStatusAndDeletedFalse(String systemName, String assignee,
        int status, Pageable pageable);

    List<ActRuDetail> findBySystemNameNotAndAssigneeAndStatusAndDeletedFalseOrderByCreateTimeDesc(String systemName,
        String assignee, int status);
}
