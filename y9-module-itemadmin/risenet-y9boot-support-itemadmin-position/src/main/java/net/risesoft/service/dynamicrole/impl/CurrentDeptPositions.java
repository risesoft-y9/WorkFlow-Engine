package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Position;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前岗位所在的部门下面的岗位集合（排除当前岗位）
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
public class CurrentDeptPositions extends AbstractDynamicRoleMember {

    @Autowired
    private PositionApi positionApi;

    @Override
    public List<Position> getPositionList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Position position = positionApi.getPosition(tenantId, positionId).getData();
        List<Position> positionList = positionApi.listByParentId(tenantId, position.getParentId()).getData();
        positionList.remove(position);
        return positionList;
    }
}
