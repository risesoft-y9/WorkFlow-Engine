package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OpinionHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionHistoryRepository
    extends JpaRepository<OpinionHistory, String>, JpaSpecificationExecutor<OpinionHistory> {

    int countByProcessSerialNumberAndOpinionFrameMark(String processSerialNumber, String opinionFrameMark);

    List<OpinionHistory> findByProcessSerialNumberAndOpinionFrameMark(String processSerialNumber,
        String opinionFrameMark);

}
