package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.LeaderOpinion;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.LeaderOpinionRepository;
import net.risesoft.service.LeaderOpinionService;

/**
 * @author : qinman
 * @date : 2025-03-12
 * @since 9.6.8
 **/
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderOpinionServiceImpl implements LeaderOpinionService {

    private final LeaderOpinionRepository leaderOpinionRepository;

    @Override
    public LeaderOpinion findById(String id) {
        return leaderOpinionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        leaderOpinionRepository.deleteById(id);
    }

    @Override
    public List<LeaderOpinion> findByProcessSerialNumber(String processSerialNumber) {
        return leaderOpinionRepository.findByProcessSerialNumberOrderByCreateDateAsc(processSerialNumber);
    }

    @Override
    @Transactional
    public void save(LeaderOpinion leaderOpinion) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = leaderOpinion.getId();
        if (StringUtils.isNotBlank(id)) {
            Optional<LeaderOpinion> oldOptional = leaderOpinionRepository.findById(id);
            if (oldOptional.isPresent()) {
                LeaderOpinion oldLeaderOpinion = oldOptional.get();
                oldLeaderOpinion.setPositionName(leaderOpinion.getPositionName());
                oldLeaderOpinion.setOpinionContent(leaderOpinion.getOpinionContent());
                oldLeaderOpinion.setOpinionDate(leaderOpinion.getOpinionDate());
                oldLeaderOpinion.setFileStoreId(leaderOpinion.getFileStoreId());
                oldLeaderOpinion.setFileName(leaderOpinion.getFileName());
                oldLeaderOpinion.setPersonName(leaderOpinion.getPersonName());
                oldLeaderOpinion.setPersonId(leaderOpinion.getPersonId());
                oldLeaderOpinion.setUpdateDate(sdf.format(new Date()));
                leaderOpinionRepository.save(oldLeaderOpinion);
                return;
            }
        } else {
            id = Y9IdGenerator.genId();
        }
        LeaderOpinion newLeaderOpinion = new LeaderOpinion();
        newLeaderOpinion.setId(id);
        newLeaderOpinion.setProcessSerialNumber(leaderOpinion.getProcessSerialNumber());
        newLeaderOpinion.setPositionName(leaderOpinion.getPositionName());
        newLeaderOpinion.setOpinionContent(leaderOpinion.getOpinionContent());
        newLeaderOpinion.setOpinionDate(leaderOpinion.getOpinionDate());
        newLeaderOpinion.setFileStoreId(leaderOpinion.getFileStoreId());
        newLeaderOpinion.setFileName(leaderOpinion.getFileName());
        newLeaderOpinion.setPersonName(leaderOpinion.getPersonName());
        newLeaderOpinion.setPersonId(leaderOpinion.getPersonId());
        newLeaderOpinion.setCreateDate(sdf.format(new Date()));
        newLeaderOpinion.setUpdateDate(sdf.format(new Date()));
        leaderOpinionRepository.save(newLeaderOpinion);
    }
}
