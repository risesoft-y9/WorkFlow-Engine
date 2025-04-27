package net.risesoft.service.gfg;

import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.DocumentCopy;

/**
 * @author : qinman
 * @date : 2025-04-24
 * @since 9.6.8
 **/
public interface Todo3rdService {

    void addTodo3rd(ActRuDetail actRuDetail);

    void addTodo3rd(DocumentCopy documentCopy);

    void updateTodo3rd(ActRuDetail actRuDetail);

    void deleteTodo3rd(ActRuDetail actRuDetail);

    void deleteTodo3rd(DocumentCopy documentCopy);
}
