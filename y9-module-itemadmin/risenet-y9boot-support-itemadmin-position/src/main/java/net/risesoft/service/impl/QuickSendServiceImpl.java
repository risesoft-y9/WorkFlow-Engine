package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.QuickSend;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.QuickSendRepository;
import net.risesoft.service.QuickSendService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Service
@RequiredArgsConstructor
public class QuickSendServiceImpl implements QuickSendService {

    private final QuickSendRepository quickSendRepository;

    @Override
    public String getAssignee(String itemId, String taskKey) {
        QuickSend quickSend =
            quickSendRepository.findByItemIdAndPositionIdAndTaskKey(itemId, Y9LoginUserHolder.getPositionId(), taskKey);
        return quickSend != null ? quickSend.getAssignee() : "";
    }

    @Override
    public void saveOrUpdate(String itemId, String taskKey, String assignee) {
        QuickSend quickSend =
            quickSendRepository.findByItemIdAndPositionIdAndTaskKey(itemId, Y9LoginUserHolder.getPositionId(), taskKey);
        if (quickSend != null) {
            quickSend.setAssignee(assignee);
            quickSend.setUpdateTime(new Date());
            quickSendRepository.save(quickSend);
            return;
        }
        quickSend = new QuickSend();
        quickSend.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        quickSend.setItemId(itemId);
        quickSend.setAssignee(assignee);
        quickSend.setTaskKey(taskKey);
        quickSend.setPositionId(Y9LoginUserHolder.getPositionId());
        quickSend.setUpdateTime(new Date());
        quickSendRepository.save(quickSend);
    }

}
