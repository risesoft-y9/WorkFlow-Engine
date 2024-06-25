package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 意见
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/vue/opinion")
public class OpinionRestController {

    private final Opinion4PositionApi opinion4PositionApi;

    private final OrgUnitApi orgUnitApi;

    private final PersonApi personApi;

    /**
     * 验证是否签写意见
     *
     * @param taskId 任务ID
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/checkSignOpinion")
    public Y9Result<Map<String, Object>> checkSignOpinion(@RequestParam(required = false) String taskId,
        @RequestParam String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            Boolean checkSignOpinion =
                opinion4PositionApi.checkSignOpinion(tenantId, userId, processSerialNumber, taskId);
            map.put("checkSignOpinion", checkSignOpinion);
        } catch (Exception e) {
            LOGGER.error("查询" + taskId + "是否签写意见失败！", e);
        }
        return Y9Result.success(map, "查询成功");
    }

    /**
     * 获取意见框历史记录数量
     *
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return Y9Result<Integer>
     */
    @RequestMapping(value = "/countOpinionHistory", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Integer> countOpinionHistory(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String opinionFrameMark) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer num = opinion4PositionApi.countOpinionHistory(tenantId, processSerialNumber, opinionFrameMark);
        return Y9Result.success(num, "获取成功");
    }

    /**
     * 删除意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam @NotBlank String id) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            opinion4PositionApi.delete(tenantId, id);
            return Y9Result.successMsg("刪除成功");
        } catch (Exception e) {
            LOGGER.error("删除意见失败！", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 委办局树搜索
     *
     * @param name 人员名称
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/bureauTreeSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> deptTreeSearch(@RequestParam(required = false) String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = new ArrayList<>();
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
        if (bureau != null) {
            List<OrgUnit> orgUnitList =
                orgUnitApi.treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_PERSON, bureau.getDn()).getData();
            for (OrgUnit orgUnit : orgUnitList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", orgUnit.getId());
                map.put("name", orgUnit.getName());
                map.put("orgType", orgUnit.getOrgType());
                map.put("parentId", orgUnit.getParentId());
                map.put("isParent", true);
                if (OrgTypeEnum.PERSON.equals(orgUnit.getOrgType())) {
                    Person per = personApi.get(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                    if (per.getDisabled()) {// 除去禁用的人员
                        continue;
                    }
                    map.put("sex", per.getSex());
                    map.put("duty", per.getDuty());
                    map.put("isParent", false);
                    map.put("parentId", orgUnit.getParentId());
                }
                item.add(map);
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List < String>>
     */
    @RequestMapping(value = "/getBindOpinionFrame", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<String>> getBindOpinionFrame(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> list = opinion4PositionApi.getBindOpinionFrame(tenantId, itemId, processDefinitionId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取委办局树
     *
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getBureauTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getBureauTree(@RequestParam(required = false) String id) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(id)) {
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
            if (orgUnit != null) {
                Map<String, Object> m = new HashMap<>(16);
                m.put("id", orgUnit.getId());
                m.put("name", orgUnit.getName());
                m.put("parentId", orgUnit.getParentId());
                m.put("orgType", orgUnit.getOrgType());
                m.put("isParent", true);
                item.add(m);
            }
        } else {
            List<OrgUnit> list = orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_ORG).getData();
            for (OrgUnit orgUnit : list) {
                Map<String, Object> m = new HashMap<>(16);
                m.put("id", orgUnit.getId());
                m.put("name", orgUnit.getName());
                m.put("parentId", orgUnit.getParentId());
                m.put("orgType", orgUnit.getOrgType());
                m.put("isParent", true);
                if (orgUnit.getOrgType().equals(OrgTypeEnum.PERSON)) {
                    m.put("isParent", false);
                    Person person = personApi.get(tenantId, orgUnit.getId()).getData();
                    if (person.getDisabled()) {
                        continue;
                    }
                    m.put("sex", person.getSex());
                }
                if (orgUnit.getOrgType().equals(OrgTypeEnum.PERSON)
                    || orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                    item.add(m);
                }
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取意见框历史记录
     *
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return Y9Result<List < OpinionHistoryModel>>
     */
    @RequestMapping(value = "/opinionHistoryList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OpinionHistoryModel>> opinionHistoryList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String opinionFrameMark) {
        List<OpinionHistoryModel> list;
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = opinion4PositionApi.opinionHistoryList(tenantId, processSerialNumber, opinionFrameMark);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取新增或编辑意见前数据
     *
     * @param id 意见id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/newOrModify/personalComment", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> personalComment(@RequestParam(required = false) String id) {
        Map<String, Object> map = new HashMap<>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        map.put("date", sdf.format(new Date()));
        if (StringUtils.isNotBlank(id)) {
            OpinionModel opinion = opinion4PositionApi.getById(tenantId, id);
            map.put("opinion", opinion);
            map.put("date", opinion.getCreateDate());
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取意见列表
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态
     * @param opinionFrameMark 意见框标识
     * @param itemId 事项id
     * @param taskDefinitionKey 任务key
     * @param activitiUser 办理人
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/personCommentList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> personCommentList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String itembox,
        @RequestParam @NotBlank String opinionFrameMark, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskDefinitionKey, @RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String orderByUser) {
        List<Map<String, Object>> listMap;
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        listMap = opinion4PositionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox,
            opinionFrameMark, itemId, taskDefinitionKey, activitiUser, orderByUser);
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 保存意见
     *
     * @param jsonData 意见实体json
     * @return Y9Result<OpinionModel>
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<OpinionModel> save(@RequestParam @NotBlank String jsonData) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            OpinionModel opinion = Y9JsonUtil.readValue(jsonData, OpinionModel.class);
            String positionId = Y9LoginUserHolder.getPositionId();
            OpinionModel opinionModel = opinion4PositionApi.saveOrUpdate(tenantId, userId, positionId, opinion);
            return Y9Result.success(opinionModel, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}