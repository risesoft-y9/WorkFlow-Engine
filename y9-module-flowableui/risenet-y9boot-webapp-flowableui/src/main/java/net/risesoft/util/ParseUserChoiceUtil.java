package net.risesoft.util;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Component
public class ParseUserChoiceUtil {

    public static void removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        list.clear();
        list.addAll(newList);
    }

    public static List<String> parseUserChoice(String userChoice) {
        String users = "";
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                String[] s2 = s.split(SysVariables.COLON);
                int principalType = ItemPermissionEnum.POSITION.getValue();
                String userIdTemp = s;
                if (s2.length == 2) {
                    principalType = Integer.parseInt(s2[0]);
                    userIdTemp = s2[1];
                }
                if (principalType == ItemPermissionEnum.POSITION.getValue()) {
                    OrgUnit orgUnit =
                        Y9Context.getBean(OrgUnitApi.class).getOrgUnitPersonOrPosition(tenantId, userIdTemp).getData();
                    if (null == orgUnit) {
                        continue;
                    }
                    users = addUserId(users, userIdTemp);
                } else if (principalType == ItemPermissionEnum.DEPARTMENT.getValue()) {
                    List<Position> employeeList = new ArrayList<>();
                    getAllPosition(employeeList, userIdTemp);
                    for (Position pTemp : employeeList) {
                        users = addUserId(users, pTemp.getId());
                    }
                } else if (principalType == ItemPermissionEnum.CUSTOMGROUP.getValue()) {
                    List<CustomGroupMember> list =
                        Y9Context.getBean(CustomGroupApi.class).listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getPersonId(), userIdTemp, OrgTypeEnum.POSITION).getData();
                    for (CustomGroupMember pTemp : list) {
                        OrgUnit orgUnit = Y9Context.getBean(OrgUnitApi.class)
                            .getOrgUnitPersonOrPosition(tenantId, pTemp.getMemberId()).getData();
                        if (orgUnit != null && StringUtils.isNotBlank(orgUnit.getId())) {
                            users = addUserId(users, orgUnit.getId());
                        }
                    }
                }
            }
        }
        List<String> result = Y9Util.stringToList(users, SysVariables.SEMICOLON);
        removeDuplicateWithOrder(result);
        return result;
    }

    /*
     * 向userIds中添加内容
     *
     * @param userIds
     * @param userId 人员Guid
     * @return
     */
    private static String addUserId(String userIds, String userId) {
        /*
         * 由于串行、并行的时候人员存在顺序的，所以写在这里，保证人员顺序
         */
        if (StringUtils.isNotBlank(userIds)) {
            if (!userIds.contains(userId)) {
                userIds = userIds + SysVariables.SEMICOLON + userId;
            }
        } else {
            userIds = userId;
        }
        return userIds;
    }

    private static void getAllPosition(List<Position> list, String deptId) {
        List<Department> deptList =
            Y9Context.getBean(DepartmentApi.class).listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 =
            Y9Context.getBean(PositionApi.class).listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        if (!list0.isEmpty()) {
            list.addAll(list0);
        }
        for (Department dept : deptList) {
            getAllPosition(list, dept.getId());
        }
    }
}
