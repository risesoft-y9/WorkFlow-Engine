package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 办理意见
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/opinion", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionRestController {

    private final OpinionApi opinionApi;

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
                opinionApi.checkSignOpinion(tenantId, userId, processSerialNumber, taskId).getData();
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
    @GetMapping(value = "/countOpinionHistory")
    public Y9Result<Integer> countOpinionHistory(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String opinionFrameMark) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer num = opinionApi.countOpinionHistory(tenantId, processSerialNumber, opinionFrameMark).getData();
        return Y9Result.success(num, "获取成功");
    }

    /**
     * 删除意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam @NotBlank String id) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            opinionApi.delete(tenantId, id);
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
    @GetMapping(value = "/bureauTreeSearch")
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
    @GetMapping(value = "/getBindOpinionFrame")
    public Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return opinionApi.getBindOpinionFrame(tenantId, itemId, processDefinitionId);
    }

    /**
     * 获取委办局树
     *
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/getBureauTree")
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
    @GetMapping(value = "/opinionHistoryList")
    public Y9Result<List<OpinionHistoryModel>> opinionHistoryList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String opinionFrameMark) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return opinionApi.opinionHistoryList(tenantId, processSerialNumber, opinionFrameMark);
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
     * @return Y9Result<List < OpinionListModel>>
     */
    @GetMapping(value = "/personCommentList")
    public Y9Result<List<OpinionListModel>> personCommentList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String itembox,
        @RequestParam @NotBlank String opinionFrameMark, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskDefinitionKey, @RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String orderByUser) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        return opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, opinionFrameMark,
            itemId, taskDefinitionKey, activitiUser, orderByUser);
    }

    /**
     * 获取新增或编辑意见前数据
     *
     * @param id 意见id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/newOrModify/personalComment")
    public Y9Result<Map<String, Object>> personalComment(@RequestParam(required = false) String id) {
        Map<String, Object> map = new HashMap<>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        map.put("date", sdf.format(new Date()));
        if (StringUtils.isNotBlank(id)) {
            OpinionModel opinion = opinionApi.getById(tenantId, id).getData();
            map.put("opinion", opinion);
            map.put("date", opinion.getCreateDate());
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存意见
     *
     * @param jsonData 意见实体json
     * @return Y9Result<OpinionModel>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<OpinionModel> save(@RequestParam @NotBlank String jsonData) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            OpinionModel opinion = Y9JsonUtil.readValue(jsonData, OpinionModel.class);
            String positionId = Y9LoginUserHolder.getPositionId();
            OpinionModel opinionModel = opinionApi.saveOrUpdate(tenantId, userId, positionId, opinion).getData();
            return Y9Result.success(opinionModel, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 更新意见
     *
     * @param content 意见内容
     * @param id 意见id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/updateOpinion")
    public Y9Result<Object> updateOpinion(@RequestParam @NotBlank String content, @RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return opinionApi.updateOpinion(tenantId, id, content);
    }
}