package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
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
@RequestMapping(value = "/services/rest/organWord")
public class OrganWordApiImpl implements OrganWordApi {

    @Autowired
    private OrganWordService organWordService;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private PersonApi personApi;

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
     * @return Integer
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/checkNumberStr", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer checkNumberStr(String tenantId, String userId, String characterValue, String custom, Integer year,
        Integer numberTemp, String itemId, Integer common, String processSerialNumber) throws Exception {
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Integer status = organWordService.checkNumberStr(characterValue, custom, year, numberTemp, itemId, common,
            processSerialNumber);
        return status;
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
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/exist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> exist(String tenantId, String userId, String custom, String processSerialNumber,
        String processInstanceId, String itembox) throws Exception {
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = organWordService.exist(custom, processSerialNumber, processInstanceId, itembox);
        return map;
    }

    /**
     * 查找有权限的机构代字
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param custom 机关代字标志
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/findByCustom", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findByCustom(String tenantId, String userId, String custom, String itemId,
        String processDefinitionId, String taskDefKey) throws Exception {
        Position position = positionApi.getPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setPosition(position);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap =
            organWordService.findByCustom(itemId, processDefinitionId, taskDefKey, custom);
        return listMap;
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
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getNumber(String tenantId, String userId, String custom, String characterValue,
        Integer year, Integer common, String itemId) throws Exception {
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = organWordService.getNumber(custom, characterValue, year, common, itemId);
        return map;
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
     * @return Integer
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/getNumberOnly", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer getNumberOnly(String tenantId, String userId, String custom, String characterValue, Integer year,
        Integer common, String itemId) throws Exception {
        Person person = personApi.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Integer number = organWordService.getNumberOnly(custom, characterValue, year, common, itemId);
        return number;
    }
}
