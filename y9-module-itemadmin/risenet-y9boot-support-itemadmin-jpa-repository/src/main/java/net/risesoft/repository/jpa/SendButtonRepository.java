package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.button.SendButton;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SendButtonRepository extends JpaRepository<SendButton, String>, JpaSpecificationExecutor<SendButton> {

    @Override
    @Query("from SendButton t order by t.createTime asc")
    List<SendButton> findAll();

    SendButton findByCustomId(String customId);
}
