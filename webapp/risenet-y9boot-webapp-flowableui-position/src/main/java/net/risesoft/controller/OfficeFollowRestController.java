package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping("/vue/officeFollow")
public class OfficeFollowRestController {

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private OfficeFollow4PositionApi officeFollow4PositionApi;

    @Autowired
    private ProcessParamApi processParamApi;

    @Autowired
    private HistoricProcessApi historicProcessApi;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    /**
     * 取消关注
     *
     * @param processInstanceIds 流程实例ids，逗号隔开
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delOfficeFollow", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delOfficeFollow(@RequestParam(required = false) String processInstanceIds) {
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            String tenantId = Y9LoginUserHolder.getTenantId();
            map = officeFollow4PositionApi.delOfficeFollow(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceIds);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("取消关注成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("取消关注失败");
    }

    /**
     * 获取我的关注列表
     *
     * @param page 页码
     * @param rows 条数
     * @param searchName 搜索词
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/followList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> followList(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows, @RequestParam(required = false) String searchName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        map = officeFollow4PositionApi.getOfficeFollowList(tenantId, Y9LoginUserHolder.getPositionId(), searchName, page, rows);
        return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
    }

    /**
     * 获取左侧关注菜单数字
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getFollowCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Integer> getFollowCount() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int followCount = officeFollow4PositionApi.getFollowCount(tenantId, Y9LoginUserHolder.getPositionId());
        return Y9Result.success(followCount, "获取成功");
    }

    /**
     * 保存关注
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOfficeFollow", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOfficeFollow(@RequestParam(required = true) String processInstanceId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Position position = Y9LoginUserHolder.getPosition();
            String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
            OfficeFollowModel officeFollow = new OfficeFollowModel();
            if (StringUtils.isNotBlank(processInstanceId)) {
                ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                officeFollow.setGuid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                OrgUnit orgUnit = departmentApi.getBureau(tenantId, position.getParentId()).getData();
                officeFollow.setBureauId(orgUnit != null ? orgUnit.getId() : "");
                officeFollow.setBureauName(orgUnit != null ? orgUnit.getName() : "");
                officeFollow.setCreateTime(sdf.format(new Date()));
                officeFollow.setDocumentTitle(processParamModel.getTitle());
                officeFollow.setFileType(processParamModel.getItemName());
                officeFollow.setHandleTerm("");
                officeFollow.setItemId(processParamModel.getItemId());
                officeFollow.setJinjichengdu(processParamModel.getCustomLevel());
                officeFollow.setNumbers(processParamModel.getCustomNumber());
                officeFollow.setProcessInstanceId(processInstanceId);
                officeFollow.setProcessSerialNumber(processParamModel.getProcessSerialNumber());
                officeFollow.setSendDept("");
                officeFollow.setSystemName(processParamModel.getSystemName());
                HistoricProcessInstanceModel historicProcessInstanceModel = historicProcessApi.getById(tenantId, processInstanceId);
                if (historicProcessInstanceModel == null) {
                    OfficeDoneInfoModel officeDoneInfoModel = officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                    officeFollow.setStartTime(officeDoneInfoModel != null ? officeDoneInfoModel.getStartTime() : "");
                } else {
                    officeFollow.setStartTime(sdf.format(historicProcessInstanceModel.getStartTime()));
                }
                officeFollow.setUserId(positionId);
                officeFollow.setUserName(position.getName());
                Map<String, Object> map = officeFollow4PositionApi.saveOfficeFollow(tenantId, officeFollow);
                if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                    return Y9Result.successMsg("关注成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("关注失败");
    }

}
