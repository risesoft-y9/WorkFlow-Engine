package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.SignaturePictureModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SignaturePictureApi {

    /**
     * 根据唯一标示
     * 
     * @param tenantId 租户id
     * @param id id
     */
    void deleteById(String tenantId, String id);

    /**
     * 根据唯一标示查找签名图片
     * 
     * @param tenantId 租户id
     * @param id id
     * @return SignaturePictureModel
     */
    SignaturePictureModel findById(String tenantId, String id);

    /**
     * 根据人员Id查找签名图片
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @return SignaturePictureModel
     */
    SignaturePictureModel findByUserId(String tenantId, String userId);

    /**
     * 保存或者修改签名照片信息
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param spJson spJson
     * @return SignaturePictureModel
     */
    SignaturePictureModel saveOrUpdate(String tenantId, String userId, String spJson);
}
