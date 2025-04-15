package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.FileAttribute;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface FileAttributeRepository
    extends JpaRepository<FileAttribute, String>, JpaSpecificationExecutor<FileAttribute> {

    @Query("from FileAttribute where LENGTH(pcode) = 4 order by pcode asc")
    List<FileAttribute> findByPcode1();

    @Query("from FileAttribute where LENGTH(pcode) = 8 and pcode like ?1 order by pcode asc")
    List<FileAttribute> findByPcode2(String pcode);

    @Query("from FileAttribute where LENGTH(pcode) = 12 and pcode like ?1 order by pcode asc")
    List<FileAttribute> findByPcode3(String s);
}
