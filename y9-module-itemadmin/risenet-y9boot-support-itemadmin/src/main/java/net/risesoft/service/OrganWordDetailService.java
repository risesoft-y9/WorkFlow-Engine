package net.risesoft.service;

import net.risesoft.entity.OrganWordDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OrganWordDetailService {

    /**
     * Description:
     *
     * @param custom
     * @param characterValue
     * @param year
     * @param itemId
     * @return
     */
    OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue, Integer year,
        String itemId);

    /**
     * Description:
     *
     * @param organWordDetail
     * @return
     */
    OrganWordDetail save(OrganWordDetail organWordDetail);

    /**
     * Description:
     *
     * @param organWordDetail
     * @return
     */
    OrganWordDetail saveOrUpdate(OrganWordDetail organWordDetail);

    /**
     * Description: 获取最大的编号
     *
     * @param custom
     * @param itemId
     * @return
     */
    Integer getMaxNumber(String custom, String itemId);

    /**
     * Description: 根据custom、characterValue、itemId查询
     *
     * @param custom
     * @param characterValue
     * @param itemId
     * @return
     */
    OrganWordDetail findByCustomAndCharacterValueAndItemId(String custom, String characterValue, String itemId);

    /**
     * Description: 根据custom、itemId查询
     *
     * @param custom
     * @param itemId
     * @return
     */
    OrganWordDetail findByCustomAndItemId(String custom, String itemId);
}
