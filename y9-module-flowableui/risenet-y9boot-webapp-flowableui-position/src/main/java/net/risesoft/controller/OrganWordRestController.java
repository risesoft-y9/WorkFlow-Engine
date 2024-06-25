package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 编号
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/organWord")
public class OrganWordRestController {

    private final OrganWordApi organWordApi;

    /**
     * 检查编号
     *
     * @param characterValue 机关代字
     * @param custom 编号标识
     * @param year 年份
     * @param number 编号
     * @param itemId 事项id
     * @param common 是否公共
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/checkNumber", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> checkNumber(@RequestParam @NotBlank String characterValue,
        @RequestParam @NotBlank String custom, @RequestParam Integer year,
        @RequestParam(required = false) Integer number, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) Integer common, @RequestParam @NotBlank String processSerialNumber) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            Integer status = organWordApi.checkNumberStr(tenantId, userId, characterValue, custom, year, number, itemId,
                common, processSerialNumber);
            if (status == 0) {
                /*
                  当前编号已被使用，获取最新的可以用的编号
                 */
                Integer numberTemp =
                    organWordApi.getNumberOnly(tenantId, userId, custom, characterValue, year, 0, itemId);
                map.put("newNumber", numberTemp);
            }
            map.put("status", status);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 查找有权限的机构代字
     *
     * @param custom 编号标识
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/findByCustom", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findByCustom(@RequestParam @NotBlank String custom,
        @RequestParam @NotBlank String itemId, @RequestParam @NotBlank String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, Object>> listMap = organWordApi.findByCustom(tenantId, Y9LoginUserHolder.getPositionId(),
                custom, itemId, processDefinitionId, taskDefKey);
            return Y9Result.success(listMap, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取最新编号
     *
     * @param custom 编号标识
     * @param itemId 事项id
     * @param characterValue 机关代字
     * @param year 年份
     * @param common 是否公共
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getNumber", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getNumber(@RequestParam @NotBlank String custom,
        @RequestParam @NotBlank String itemId, @RequestParam @NotBlank String characterValue,
        @RequestParam Integer year, @RequestParam(required = false) Integer common) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        try {
            Map<String, Object> map =
                organWordApi.getNumber(tenantId, userId, custom, characterValue, year, common, itemId);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

}
