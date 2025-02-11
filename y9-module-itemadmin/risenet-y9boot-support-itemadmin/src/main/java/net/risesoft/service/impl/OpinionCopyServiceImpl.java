package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.OpinionCopy;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.OpinionCopyRepository;
import net.risesoft.service.OpinionCopyService;

/**
 * @author : qinman
 * @date : 2025-02-11
 * @since 9.6.8
 **/
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OpinionCopyServiceImpl implements OpinionCopyService {

    private final OpinionCopyRepository opinionCopyRepository;

    @Override
    public Optional<OpinionCopy> findById(String id) {
        return opinionCopyRepository.findById(id);
    }

    @Override
    public List<OpinionCopy> findByProcessSerialNumber(String processSerialNumber) {
        return opinionCopyRepository.findByProcessSerialNumberOrderByCreateTimeAsc(processSerialNumber);
    }

    @Override
    @Transactional
    public Optional<OpinionCopy> saveOrUpdate(OpinionCopy opinionCopy) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = opinionCopy.getId();
        if (StringUtils.isNotBlank(id)) {
            Optional<OpinionCopy> optional = opinionCopyRepository.findById(id);
            if (optional.isPresent()) {
                OpinionCopy oldOc = optional.get();
                oldOc.setContent(opinionCopy.getContent());
                oldOc.setUpdateTime(sdf.format(new Date()));
                return Optional.of(opinionCopyRepository.save(oldOc));
            }
        }
        OpinionCopy newOc = new OpinionCopy();
        newOc.setId(Y9IdGenerator.genId());
        newOc.setProcessSerialNumber(opinionCopy.getProcessSerialNumber());
        newOc.setContent(opinionCopy.getContent());
        newOc.setUserId(opinionCopy.getUserId());
        newOc.setUserName(opinionCopy.getUserName());
        newOc.setCreateTime(sdf.format(new Date()));
        newOc.setUpdateTime(sdf.format(new Date()));
        newOc.setSend(opinionCopy.isSend());
        return Optional.of(opinionCopyRepository.save(newOc));
    }
}
