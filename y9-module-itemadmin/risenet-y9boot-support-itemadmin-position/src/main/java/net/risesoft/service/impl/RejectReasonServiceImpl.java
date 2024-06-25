package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.RejectReason;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.RejectReasonRepository;
import net.risesoft.service.RejectReasonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class RejectReasonServiceImpl implements RejectReasonService {

    private final RejectReasonRepository rejectReasonRepository;

    @Override
    public RejectReason findByTaskIdAndAction(String taskId, Integer action) {
        return rejectReasonRepository.findByTaskIdAndAction(taskId, action);
    }

    @Override
    @Transactional
    public void save(String reason, String taskId, Integer action) {
        RejectReason r = this.findByTaskIdAndAction(taskId, action);
        if (null != r) {
            r.setReason(reason);
            rejectReasonRepository.save(r);
            return;
        }

        UserInfo person = Y9LoginUserHolder.getUserInfo();
        RejectReason rr = new RejectReason();
        rr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        rr.setTaskId(taskId);
        rr.setUserId(person.getPersonId());
        rr.setUserName(person.getName());
        rr.setUserMobile(person.getMobile());
        rr.setAction(action);
        rr.setReason(reason);

        rejectReasonRepository.save(rr);
    }
}
