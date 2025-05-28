package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DmyjSxInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface DmyjSxInfoRepository extends JpaRepository<DmyjSxInfo, String>, JpaSpecificationExecutor<DmyjSxInfo> {

    List<DmyjSxInfo> findByDmyjmijiAndDmyjmcAndDmyjsjLikeAndIsdelete(String miji, String dmyjmc, String dmyjsj,
        String isdelete);

    @Query("from DmyjSxInfo t where t.dmyjmiji=?1 and t.dmyjmc=?2 and t.dmyjsj like ?3 and t.isdelete !='1'")
    List<DmyjSxInfo> getDmyjSxInfo(String miji, String dmyjmc, String dmyjsj);
}
