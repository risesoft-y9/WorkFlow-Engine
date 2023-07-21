package net.risesoft.repository.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.RejectReason;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface RejectReasonRepository extends JpaRepository<RejectReason, Serializable>, JpaSpecificationExecutor<RejectReason> {
    RejectReason findByTaskIdAndAction(String taskId, Integer action);
}
