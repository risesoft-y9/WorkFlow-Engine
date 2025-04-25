package net.risesoft.service.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import lombok.Getter;

/**
 * 待办新建事件
 *
 * @author qinman
 * @date 2025/04/23
 */
@Getter
public class Y9TodoCreatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = -178737462829509713L;

    private final T entity;

    public Y9TodoCreatedEvent(T entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}
