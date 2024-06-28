package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.entity.OrganWord;
import net.risesoft.model.itemadmin.OrganWordModel;
import net.risesoft.model.itemadmin.OrganWordPropertyModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OrganWordService {

    /**
     * 判断标识是否可用
     *
     * @param id
     * @param custom
     * @return
     */
    boolean checkCustom(String id, String custom);

    /**
     * 0:当前编号已被使用.1.当前编号没有被使用。2:当前编号不存在 3:发生异常
     *
     * @param characterValue
     * @param custom
     * @param year
     * @param numberTemp
     * @param itemId
     * @param common
     * @param processSerialNumber
     * @return
     */
    Integer checkNumberStr(String characterValue, String custom, Integer year, Integer numberTemp, String itemId,
        Integer common, String processSerialNumber);

    /**
     * 检查部门编号是否存在 0:当前编号已被使用.1.当前编号没有被使用。2:当前编号不存在 3:发生异常
     *
     * @param custom
     * @param year
     * @param numberTemp
     * @param processDefineKey
     * @param common
     * @param processSerialNumber
     * @return
     */
    Integer checkNumberStr4DeptName(String custom, Integer year, Integer numberTemp, String processDefineKey,
        Integer common, String processSerialNumber);

    /**
     * 判断机构代字custom在某个流程实例中是否已经编号,没有编号的话就查找有权限的编号的机关代字
     *
     * @param custom
     * @param processSerialNumber
     * @param processInstanceId
     * @param itembox
     * @return
     */
    OrganWordModel exist(String custom, String processSerialNumber, String processInstanceId, String itembox);

    /**
     * 根据租户Id获取机关文字列表并按照tabIndex 升序
     * 
     * @return
     */
    List<OrganWord> findAll();

    /**
     * 根据标识查找未删除的机关代字列表
     *
     * @param custom
     * @return
     */
    OrganWord findByCustom(String custom);

    /**
     * Description: 查找有权限的机构代字
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param custom
     * @return
     */
    List<OrganWordPropertyModel> findByCustom(String itemId, String processDefinitionId, String taskDefKey,
        String custom);

    /**
     * 根据Id查找机关代字
     *
     * @param id
     * @return
     */
    OrganWord findOne(String id);

    /**
     * 一般的自动编号
     *
     * @param custom
     * @param characterValue
     * @param year
     * @param common
     * @param itemId
     * @return
     */
    Integer getNumber(String custom, String characterValue, Integer year, Integer common, String itemId);

    /**
     * 当前部门的自动编号
     *
     * @param custom
     * @param year
     * @param common
     * @param processDefineKey
     * @return
     */
    Map<String, Object> getNumber4DeptName(String custom, Integer year, Integer common, String processDefineKey);

    /**
     * 获取编号的数字
     *
     * @param custom
     * @param characterValue
     * @param year
     * @param common
     * @param itemId
     * @return
     */
    Integer getNumberOnly(String custom, String characterValue, Integer year, Integer common, String itemId);

    /**
     * 获取所有的编号列表
     * 
     * @param rows
     * @param page
     * @return
     */
    Page<OrganWord> list(int rows, int page);

    /**
     * Description: 根据传进来的机关代字Id的数组逻辑删除
     *
     * @param organWordIds
     */
    void removeOrganWords(String[] organWordIds);

    /**
     * 保存机关代字
     *
     * @param organWord
     * @return
     */
    Map<String, Object> save(OrganWord organWord);
}
