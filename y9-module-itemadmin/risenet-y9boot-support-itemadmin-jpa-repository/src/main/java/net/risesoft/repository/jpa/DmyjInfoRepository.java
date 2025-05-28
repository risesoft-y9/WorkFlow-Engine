package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DmyjInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface DmyjInfoRepository extends JpaRepository<DmyjInfo, String>, JpaSpecificationExecutor<DmyjInfo> {

    List<DmyjInfo> findByDmyjmijiAndDmyjsjLikeAndIsdelete(String miji, String dmyjsj, String isdelete);

    @Query("from DmyjInfo t where t.dmyjmiji=?1 and t.dmyjsj like ?2 and t.isdelete !='1'")
    List<DmyjInfo> getDmyjInfo(String miji, String dmyjsj);

}
