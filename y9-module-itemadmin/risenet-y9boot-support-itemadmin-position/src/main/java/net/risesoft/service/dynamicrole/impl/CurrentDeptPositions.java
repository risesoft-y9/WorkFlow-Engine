package net.risesoft.service.dynamicrole.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Position;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 当前岗位所在的部门下面的岗位集合（排除当前岗位）
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentDeptPositions extends AbstractDynamicRoleMember {

    private final PositionApi positionApi;

    @Override
    public List<Position> getPositionList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Position position = positionApi.get(tenantId, positionId).getData();
        List<Position> positionList = positionApi.listByParentId(tenantId, position.getParentId()).getData();
        positionList.remove(position);
        return positionList;
    }
}
