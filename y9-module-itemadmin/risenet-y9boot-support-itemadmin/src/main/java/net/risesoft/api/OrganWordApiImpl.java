package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.itemadmin.OrganWordModel;
import net.risesoft.model.itemadmin.OrganWordPropertyModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 编号接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/organWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganWordApiImpl implements OrganWordApi {

    private final OrganWordService organWordService;

    private final PersonApi personApi;

    /**
     * 检查编号是否已经被使用了
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param characterValue 机关代字
     * @param custom 机关代字标志
     * @param year 文号年份
     * @param numberTemp 编号
     * @param itemId 事项id
     * @param common common
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> checkNumberStr(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String characterValue, @RequestParam String custom, @RequestParam Integer year,
        @RequestParam Integer numberTemp, @RequestParam String itemId, @RequestParam Integer common,
        @RequestParam String processSerialNumber) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.checkNumberStr(characterValue, custom, year, numberTemp, itemId,
            common, processSerialNumber));
    }

    /**
     * 判断机构代字custom在某个流程实例中是否已经编号,没有编号的话就查找有权限的编号的机关代字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @return {@code Y9Result<OrganWordModel>} 通用请求返回对象 -data 是编号的机关代字
     * @since 9.6.6
     */
    @Override
    public Y9Result<OrganWordModel> exist(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String custom, @RequestParam String processSerialNumber, String processInstanceId,
        @RequestParam String itembox) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.exist(custom, processSerialNumber, processInstanceId, itembox));
    }

    /**
     *
     * 查找有权限的机构代字
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param custom 机关代字标志
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return {@code Y9Result<List<OrganWordPropertyModel>>} 通用请求返回对象 -data是数据字典列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OrganWordPropertyModel>> findByCustom(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam String custom, @RequestParam String itemId,
        @RequestParam String processDefinitionId, String taskDefKey) {
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.listByCustom(itemId, processDefinitionId, taskDefKey, custom));
    }

    /**
     * 查找有权限的机构代字
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return {@code Y9Result<List<OrganWordPropertyModel>>} 通用请求返回对象 -data是数据字典列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OrganWordPropertyModel>> findByCustomNumber(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam String itemId, @RequestParam String processDefinitionId,
        String taskDefKey) {
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.listByCustomNumber(itemId, processDefinitionId, taskDefKey));
    }

    /**
     * 获取编号
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param year 文号年份
     * @param common common
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data是编号
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getNumber(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String custom, @RequestParam String characterValue, @RequestParam Integer year,
        @RequestParam Integer common, @RequestParam String itemId) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.getNumber(custom, characterValue, year, common, itemId));
    }

    /**
     * 获取编号的数字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param year 文号年份
     * @param common common
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data是编号的数字
     * @since 9.6.6
     *
     */
    @Override
    public Y9Result<Integer> getNumberOnly(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String custom, @RequestParam String characterValue, @RequestParam Integer year,
        @RequestParam Integer common, @RequestParam String itemId) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.getNumberOnly(custom, characterValue, year, common, itemId));
    }

    /**
     * 获取临时编号
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param characterValue 机关代字
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 -data是编号的数字
     * @since 9.6.6
     *
     */
    @Override
    public Y9Result<String> getTempNumber(String tenantId, String userId, String custom, String characterValue,
        String itemId) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.getTempNumber(custom, characterValue, itemId));
    }

    /**
     * 保存编号
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param numberString 编号字符串
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 -data是保存结果
     * @since 9.6.6
     *
     */
    @Override
    public Y9Result<Map<String, Object>> saveNumberString(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String custom, @RequestParam String numberString, @RequestParam String itemId,
        @RequestParam String processSerialNumber) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(organWordService.saveNumberString(custom, numberString, itemId, processSerialNumber));
    }
}
