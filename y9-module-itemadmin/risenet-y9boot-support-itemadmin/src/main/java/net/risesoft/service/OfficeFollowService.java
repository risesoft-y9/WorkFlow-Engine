package net.risesoft.service;

import net.risesoft.entity.OfficeFollow;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OfficeFollowService {

    /**
     * 根据流程实例获取是否有关注
     *
     * @param processInstanceId
     * @return
     */
    int countByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例id删除关注
     *
     * @param processInstanceId
     */
    void deleteByProcessInstanceId(String processInstanceId);

    /**
     * 取消关注
     *
     * @param processInstanceIds
     */
    void delOfficeFollow(String processInstanceIds);

    /**
     * 获取我的关注数量
     *
     * @return
     */
    int getFollowCount();

    /**
     * 获取关注列表
     *
     * @param searchName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeFollowModel> pageBySearchName(String searchName, int page, int rows);

    /**
     * 根据系统名称获取关注列表
     *
     * @param systemName
     * @param searchName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeFollowModel> pageBySystemNameAndSearchName(String systemName, String searchName, int page, int rows);

    /**
     * 保存办件关注信息
     *
     * @param officeFollow
     */
    void saveOfficeFollow(OfficeFollow officeFollow);

    /**
     * 更新标题
     *
     * @param processInstanceId
     * @param documentTitle
     */
    void updateTitle(String processInstanceId, String documentTitle);

}
