package net.risesoft.y9;

import java.util.Map;

import com.alibaba.ttl.TransmittableThreadLocal;

import net.risesoft.model.platform.org.Position;

/**
 * 工作流 holder
 *
 * @author shidaobang
 * @date 2025/10/29
 */
public class Y9FlowableHolder {
    private static final TransmittableThreadLocal<Position> POSITION_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> POSITION_ID_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, Object>> MAP_HOLDER = new TransmittableThreadLocal<>();

    public static void clear() {
        POSITION_HOLDER.remove();
        POSITION_ID_HOLDER.remove();

        MAP_HOLDER.remove();
    }

    public static Map<String, Object> getMap() {
        return MAP_HOLDER.get();
    }

    public static void setMap(final Map<String, Object> map) {
        MAP_HOLDER.set(map);
    }

    public static Position getPosition() {
        return POSITION_HOLDER.get();
    }

    public static void setPosition(final Position position) {
        POSITION_HOLDER.set(position);
        POSITION_ID_HOLDER.set(position.getId());
    }

    public static String getPositionId() {
        return POSITION_ID_HOLDER.get();
    }

    public static void setPositionId(final String positionId) {
        POSITION_ID_HOLDER.set(positionId);
    }

}
