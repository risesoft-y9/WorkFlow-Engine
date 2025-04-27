package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.Todo3rd;

/**
 * @author : qinman
 * @date : 2025-04-24
 * @since 9.6.8
 **/
public interface Todo3rdRepository extends JpaRepository<Todo3rd, String>, JpaSpecificationExecutor<Todo3rd> {
    List<Todo3rd> findByProcessSerialNumberAndReceiveUserIdAndTodoType(String processSerialNumber, String receiveUserId,
        Integer todoType);
}
