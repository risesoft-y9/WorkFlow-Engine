package net.risesoft.service;

import org.springframework.data.domain.Page;

import net.risesoft.entity.DraftEntity;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DraftEntityService {

    /**
     * 根据流程实例删除草稿
     *
     * @param processSerialNumber
     */
    void deleteByProcessSerialNumber(String processSerialNumber);

    /**
     * 彻底删除草稿
     *
     * @param ids
     * @return
     */
    void deleteDraft(String ids);

    /**
     *
     * Description: 打开草稿
     *
     * @param processSerialNumber
     * @param itemId
     * @param mobile
     * @return
     */
    OpenDataModel openDraft(String processSerialNumber, String itemId, boolean mobile);

    /**
     * 获取草稿列表
     *
     * @param itemId
     * @param userId
     * @param page
     * @param rows
     * @param title
     * @param delFlag
     * @return
     */
    Page<DraftEntity> pageDraftList(String itemId, String userId, int page, int rows, String title, boolean delFlag);

    /**
     *
     * Description: 获取草稿列表
     *
     * @param systemName
     * @param userId
     * @param page
     * @param rows
     * @param title
     * @param delFlag
     * @return
     */
    Page<DraftEntity> pageDraftListBySystemName(String systemName, String userId, int page, int rows, String title,
        boolean delFlag);

    /**
     * 还原草稿
     *
     * @param ids
     * @return
     */
    void reduction(String ids);

    /**
     * 删除草稿
     *
     * @param ids
     * @return
     */
    void removeDraft(String ids);

    /**
     *
     * Description: 保存草稿
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param number
     * @param level
     * @param title
     * @param type
     * @return
     */
    void saveDraft(String itemId, String processSerialNumber, String processDefinitionKey, String number, String level,
        String title, String type);

    /**
     *
     * Description: 保存草稿
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param number
     * @param level
     * @param jijian
     * @param title
     * @param type
     * @return
     */
    Y9Result<Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey, String number,
        String level, String jijian, String title, String type);
}
