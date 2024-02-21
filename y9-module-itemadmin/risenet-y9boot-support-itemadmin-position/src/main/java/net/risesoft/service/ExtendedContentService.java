package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import net.risesoft.entity.ExtendedContent;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ExtendedContentService {

    /**
     * 获取内容列表
     *
     * @param processSerialNumber
     * @param itembox
     * @param taskId
     * @param category
     * @return
     */
    List<Map<String, Object>> contentList(String processSerialNumber, String taskId, String itembox, String category);

    /**
     * 删除内容
     *
     * @param id
     * @return
     */
    Map<String, Object> delete(String id);

    /**
     * 根据id获取内容
     *
     * @param id
     * @return
     */
    ExtendedContent findById(String id);

    /**
     * Description: 根据processSerialNumber获取统计
     * 
     * @param processSerialNumber
     * @param category
     * @return
     */
    int findByProcSerialNumberAndCategory(String processSerialNumber, String category);

    /**
     * Description: 根据taskId获取统计
     * 
     * @param taskId
     * @param category
     * @return
     */
    int getCountByTaskIdAndCategory(String taskId, String category);

    /**
     * Description: 根据人员统计
     * 
     * @param processSerialNumber
     * @param userid
     * @param category
     * @return
     */
    int getCountByUserIdAndCategory(String processSerialNumber, String userid, String category);

    /**
     * 根据人员id，流程编号统计当前类型的内容数
     *
     * @param processSerialNumber
     * @param category
     * @param personId
     * @return
     */
    Integer getCountPersonal(String processSerialNumber, String category, String personId);

    /**
     * 根据人员id统计当前任务，当前类型的内容数
     *
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param personId
     * @return
     */
    Integer getCountPersonal(String processSerialNumber, String taskId, String category, String personId);

    /**
     * 获取最新填写的内容
     *
     * @param processSerialNumber
     * @param category
     * @return
     */
    ExtendedContent getNewConentByProcessSerialNumber(String processSerialNumber, String category);

    /**
     * 根据人员Id和流程序列号获取办理信息
     *
     * @param userid
     * @param processSerialNumber
     * @param category
     * @return
     */
    ExtendedContent getResultByUserIdAndCategory(String processSerialNumber, String userid, String category);

    /**
     * 新增或编辑内容
     *
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param id
     * @param model
     * @return
     */
    Model newOrModifyContent(String processSerialNumber, String taskId, String category, String id, Model model);

    /**
     * 保存内容
     *
     * @param content
     * @return
     */
    Map<String, Object> saveOrUpdate(ExtendedContent content);

    /**
     * 更新内容
     *
     * @param processSerialNumber
     * @param taskId
     */
    void update(String processSerialNumber, String taskId);

}
