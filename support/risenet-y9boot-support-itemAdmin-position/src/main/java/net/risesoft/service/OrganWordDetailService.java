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
    OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue, Integer year, String itemId);

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
}
