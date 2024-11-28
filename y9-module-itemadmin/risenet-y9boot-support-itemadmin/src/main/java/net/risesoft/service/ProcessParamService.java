package net.risesoft.service;

import net.risesoft.entity.ProcessParam;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ProcessParamService {

    /**
     * 根据流程实例id删除自定义变量
     *
     * @param processInstanceId
     */
    void deleteByPprocessInstanceId(String processInstanceId);

    /**
     * 根据流程编号删除自定义变量
     *
     * @param processSerialNumber
     */
    void deleteByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程实例查找流程数据对象
     *
     * @param processInstanceId
     * @return
     */
    ProcessParam findByProcessInstanceId(String processInstanceId);

    /**
     * Description: 根据流程实例查找流程数据对象
     *
     * @param processSerialNumber
     * @return
     */
    ProcessParam findByProcessSerialNumber(String processSerialNumber);

    /**
     * 保存或者更新流程数据对象
     *
     * @param processParam
     * @return
     */
    ProcessParam saveOrUpdate(ProcessParam processParam);

    /**
     * 设置流程的办结人员
     *
     * @param processInstanceId
     */
    void setUpCompleter(String processInstanceId);

    /**
     * 根据流程编号设置流程实例
     *
     * @param processSerialNumber
     * @param processInstanceId
     */
    void updateByProcessSerialNumber(String processSerialNumber, String processInstanceId);

    /**
     * 更新定制流程状态
     *
     * @param processSerialNumber
     * @param b
     */
    void updateCustomItem(String processSerialNumber, boolean b);


    void initCallActivity(String processSerialNumber, String subProcessSerialNumber,String subProcessInstanceId,String itemId,String itemName);
}
