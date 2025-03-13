package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.LeaderOpinion;

/**
 * @author : qinman
 * @date : 2025-03-12
 * @since 9.6.8
 **/
public interface LeaderOpinionService {

    void deleteById(String id);

    List<LeaderOpinion> findByProcessSerialNumber(String processSerialNumber);

    void save(LeaderOpinion leaderOpinion);
}
