package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.customgroup.CustomGroupApi;
import net.risesoft.api.itemadmin.QuickSendApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.model.CustomGroup;
import net.risesoft.model.Department;
import net.risesoft.model.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/quickSend")
public class QuickSendRestController {

    @Autowired
    private QuickSendApi quickSendApi;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private CustomGroupApi customGroupApi;

    /**
     * 获取快捷发送人
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAssignee")
    public Y9Result<List<Map<String, Object>>> getAssignee(@RequestParam String itemId, @RequestParam String taskKey) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assignee = quickSendApi.getAssignee(tenantId, Y9LoginUserHolder.getPositionId(), itemId, taskKey);
        if (StringUtils.isNotBlank(assignee)) {
            String[] ids = assignee.split(",");
            for (String id : ids) {
                Map<String, Object> map = new HashMap<String, Object>();
                String type = id.split(":")[0];
                String orgId = id.split(":")[1];
                Integer principalType = Integer.parseInt(type);
                if (principalType == ItemPermissionEnum.POSITION.getValue()) {
                    Position position = positionApi.getPosition(tenantId, orgId).getData();
                    map.put("id", position.getId());
                    map.put("name", position.getName());
                    map.put("orgType", OrgTypeEnum.POSITION.getEnName());
                    list.add(map);
                } else if (principalType == ItemPermissionEnum.DEPARTMENT.getValue()) {
                    Department dept = departmentApi.getDepartment(tenantId, orgId).getData();
                    map.put("id", dept.getId());
                    map.put("name", dept.getName());
                    map.put("orgType", OrgTypeEnum.DEPARTMENT.getEnName());
                    list.add(map);
                } else if (principalType == ItemPermissionEnum.CUSTOMGROUP.getValue()) {
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
     * 保存快捷发送人
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam String itemId, @RequestParam String taskKey,
        @RequestParam String assignee) {
        quickSendApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itemId, taskKey,
            assignee);
        return Y9Result.successMsg("保存成功");
    }
}
