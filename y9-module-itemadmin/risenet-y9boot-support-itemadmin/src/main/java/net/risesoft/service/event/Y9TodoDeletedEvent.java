package net.risesoft.service.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import lombok.Getter;

/**
 * 待办删除事件
 *
 * @author shidaobang
 * @date 2022/12/01
 */
@Getter
public class Y9TodoDeletedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = 2186781181314466191L;

    private final T entity;

    public Y9TodoDeletedEvent(T entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}