package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.organword.OrganWordUseHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OrganWordUseHistoryRepository
    extends JpaRepository<OrganWordUseHistory, String>, JpaSpecificationExecutor<OrganWordUseHistory> {

    OrganWordUseHistory findByItemIdAndNumberString(String itemId, String numberString);

    OrganWordUseHistory findByProcessSerialNumberAndCustom(String processInstanceId, String custom);

    OrganWordUseHistory findByItemIdAndNumberStringAndCustom(String itemId, String numberString, String custom);

    OrganWordUseHistory findByItemIdAndNumberStringAndCustomAndProcessSerialNumber(String itemId, String numberString,
        String custom, String processSerialNumber);
}
