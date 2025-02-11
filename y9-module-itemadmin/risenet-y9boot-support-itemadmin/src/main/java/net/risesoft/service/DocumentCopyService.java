package net.risesoft.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import net.risesoft.entity.DocumentCopy;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
public interface DocumentCopyService {

    Page<DocumentCopy> findByUserIdAndStatusLessThan(String assignee, Integer status, int rows, int page, Sort sort);

    List<DocumentCopy> findByProcessSerialNumberAndSenderId(String processSerialNumber, String senderId);

    void save(List<DocumentCopy> documentCopyList);

    void save(DocumentCopy documentCopy);

    Optional<DocumentCopy> findById(String id);
}
