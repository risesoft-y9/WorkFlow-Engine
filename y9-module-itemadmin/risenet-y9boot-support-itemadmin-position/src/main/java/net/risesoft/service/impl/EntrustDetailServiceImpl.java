package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.EntrustDetail;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.EntrustDetailRepository;
import net.risesoft.service.EntrustDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EntrustDetailServiceImpl implements EntrustDetailService {

    private final EntrustDetailRepository entrustDetailRepository;

    @Override
    public EntrustDetail findByTaskId(String taskId) {
        return entrustDetailRepository.findByTaskId(taskId);
    }

    @Override
    public String getEntrustOwnerId(String taskId) {
        EntrustDetail ed = this.findByTaskId(taskId);
        return null == ed ? "" : ed.getOwnerId();
    }

    @Override
    public boolean haveEntrustDetailByTaskId(String taskId) {
        EntrustDetail ed = this.findByTaskId(taskId);
        return null == ed ? false : true;
    }

    @Override
    @Transactional()
    public void save(String processInstanceId, String taskId, String ownerId, String assigneeId) {
        EntrustDetail entrustDetail = entrustDetailRepository.findByTaskId(taskId);
        if (null != entrustDetail) {
            entrustDetail.setOwnerId(ownerId);
            entrustDetail.setProcessInstanceId(processInstanceId);
            entrustDetail.setTaskId(taskId);
            entrustDetailRepository.save(entrustDetail);
            return;
        }
        EntrustDetail newed = new EntrustDetail();
        newed.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newed.setOwnerId(ownerId);
        newed.setProcessInstanceId(processInstanceId);
        newed.setTaskId(taskId);
        entrustDetailRepository.save(newed);
    }

}
