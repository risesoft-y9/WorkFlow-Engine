package net.risesoft.service.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.DocumentCopy;
import net.risesoft.service.event.Y9TodoCreatedEvent;
import net.risesoft.service.event.Y9TodoDeletedEvent;
import net.risesoft.service.gfg.Todo3rdService;

/**
 * 待办事件监听器
 *
 * @author qinman
 * @date 2025/04/23
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActRuDetailListener {

    private final Todo3rdService todo3rdService;

    @TransactionalEventListener
    public void onTodoCreated(Y9TodoCreatedEvent<? extends ActRuDetail> event) {
        ActRuDetail actRuDetail = event.getEntity();
        todo3rdService.addTodo3rd(actRuDetail);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(actRuDetail.getAssigneeName() + "--新增待办");
        }
    }

    @TransactionalEventListener
    public void onTodoDeleted(Y9TodoDeletedEvent<? extends ActRuDetail> event) {
        ActRuDetail actRuDetail = event.getEntity();
        todo3rdService.deleteTodo3rd(actRuDetail);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(actRuDetail.getAssigneeName() + "--删除待办");
        }
    }

    @TransactionalEventListener
    public void onTodoCreated4DocumentCopy(Y9TodoCreatedEvent<? extends DocumentCopy> event) {
        DocumentCopy documentCopy = event.getEntity();
        todo3rdService.addTodo3rd(documentCopy);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(documentCopy.getUserName() + "--新增会签待办");
        }
    }

    @TransactionalEventListener
    public void onTodoDeleted4DocumentCopy(Y9TodoDeletedEvent<? extends DocumentCopy> event) {
        DocumentCopy documentCopy = event.getEntity();
        todo3rdService.deleteTodo3rd(documentCopy);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(documentCopy.getUserName() + "--删除会签待办");
        }
    }
}
