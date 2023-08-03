package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/organWord")
public class OrganWordRestController {

    @Autowired
    private OrganWordApi organWordManager;

    /**
     * 检查编号
     *
     * @param characterValue 机关代字
     * @param custom 编号标识
     * @param year 年份
     * @param number 编号
     * @param itemId 事项id
     * @param common
     * @param processSerialNumber 流程编号
     * @return
     */
    @RequestMapping(value = "/checkNumber", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> checkNumber(@RequestParam String characterValue, @RequestParam String custom,
        @RequestParam Integer year, @RequestParam Integer number, @RequestParam String itemId,
        @RequestParam Integer common, @RequestParam String processSerialNumber) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            Integer status = organWordManager.checkNumberStr(tenantId, userId, characterValue, custom, year, number,
                itemId, common, processSerialNumber);
            if (status == 0) {
                /**
                 * 当前编号已被使用，获取最新的可以用的编号
                 */
                Integer numberTemp =
                    organWordManager.getNumberOnly(tenantId, userId, custom, characterValue, year, 0, itemId);
                map.put("newNumber", numberTemp);
            }
            map.put("status", status);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 查找有权限的机构代字
     *
     * @param custom
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    @RequestMapping(value = "/findByCustom", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findByCustom(@RequestParam String custom, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        try {
            List<Map<String, Object>> listMap =
                organWordManager.findByCustom(tenantId, userId, custom, itemId, processDefinitionId, taskDefKey);
            return Y9Result.success(listMap, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
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
     * @param common
     * @return
     */
    @RequestMapping(value = "/getNumber", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getNumber(@RequestParam String custom, @RequestParam String itemId,
        @RequestParam String characterValue, @RequestParam Integer year, @RequestParam Integer common) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        try {
            Map<String, Object> map =
                organWordManager.getNumber(tenantId, userId, custom, characterValue, year, common, itemId);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

}
