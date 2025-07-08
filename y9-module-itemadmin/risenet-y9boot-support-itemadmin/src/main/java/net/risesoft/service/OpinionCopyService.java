package net.risesoft.service;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.opinion.OpinionCopy;

/**
 * @author : qinman
 * @date : 2025-02-11
 * @since 9.6.8
 **/
public interface OpinionCopyService {

    Optional<OpinionCopy> findById(String id);

    List<OpinionCopy> findByProcessSerialNumber(String processSerialNumber);

    Optional<OpinionCopy> saveOrUpdate(OpinionCopy opinionCopy);

    void deleteById(String id);
}
