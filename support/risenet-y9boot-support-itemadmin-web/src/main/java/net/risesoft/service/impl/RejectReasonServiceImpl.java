package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * @date 2022/12/22
 */
@Service(value = "rejectReasonService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class RejectReasonServiceImpl implements RejectReasonService {

    @Autowired
    private RejectReasonRepository rejectReasonRepository;

    @Override
    public RejectReason findByTaskIdAndAction(String taskId, Integer action) {
        return rejectReasonRepository.findByTaskIdAndAction(taskId, action);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(String reason, String taskId, Integer action) {
        RejectReason r = this.findByTaskIdAndAction(taskId, action);
        if (null != r) {
            r.setReason(reason);
            rejectReasonRepository.save(r);
            return;
        }

        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        RejectReason rr = new RejectReason();
        rr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        rr.setTaskId(taskId);
        rr.setUserId(userInfo.getPersonId());
        rr.setUserName(userInfo.getName());
        rr.setUserMobile(userInfo.getMobile());
        rr.setAction(action);
        rr.setReason(reason);

        rejectReasonRepository.save(rr);
    }
}
