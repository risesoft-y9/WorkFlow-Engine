package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.QuickSendApi;
import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 快捷发送
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/quickSend", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuickSendRestController {

    private final QuickSendApi quickSendApi;

    private final OrgUnitApi orgUnitApi;

    private final DepartmentApi departmentApi;

    private final CustomGroupApi customGroupApi;

    /**
     * 获取快捷发送人数据
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getAssignee")
    public Y9Result<List<Map<String, Object>>> getAssignee(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String taskKey) {
        List<Map<String, Object>> list = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assignee =
            quickSendApi.getAssignee(tenantId, Y9LoginUserHolder.getPositionId(), itemId, taskKey).getData();
        if (StringUtils.isNotBlank(assignee)) {
            String[] ids = assignee.split(",");
            for (String id : ids) {
                Map<String, Object> map = new HashMap<>();
                String type = id.split(":")[0];
                String orgId = id.split(":")[1];
                int principalType = Integer.parseInt(type);
                if (principalType == ItemPermissionEnum.POSITION.getValue()) {
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgId).getData();
                    map.put("id", orgUnit.getId());
                    map.put("name", orgUnit.getName());
                    map.put("orgType", OrgTypeEnum.POSITION.getEnName());
                    list.add(map);
                } else if (principalType == ItemPermissionEnum.DEPARTMENT.getValue()) {
                    Department dept = departmentApi.get(tenantId, orgId).getData();
                    map.put("id", dept.getId());
                    map.put("name", dept.getName());
                    map.put("orgType", OrgTypeEnum.DEPARTMENT.getEnName());
                    list.add(map);
                } else if (principalType == ItemPermissionEnum.GROUP_CUSTOM.getValue()) {
                    CustomGroup customGroup =
                        customGroupApi.findCustomGroupById(tenantId, Y9LoginUserHolder.getPersonId(), orgId).getData();
                    map.put("id", customGroup.getId());
                    map.put("name", customGroup.getGroupName());
                    map.put("orgType", "customGroup");
                    list.add(map);
                }
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存快捷发送人信息
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String taskKey,
        @RequestParam(required = false) String assignee) {
        quickSendApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itemId, taskKey,
            assignee);
        return Y9Result.successMsg("保存成功");
    }
}
