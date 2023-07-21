package net.risesoft.service;

import net.risesoft.entity.SignaturePicture;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface SignaturePictureService {

    /**
     * 根据唯一标示
     * 
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据唯一标示查找签名图片
     * 
     * @param id
     * @return
     */
    SignaturePicture findById(String id);

    /**
     * Description: 根据人员Id查找签名图片
     * 
     * @param userId
     * @return
     */
    SignaturePicture findByUserId(String userId);

    /**
     * 保存或者修改签名照片信息
     * 
     * @param signaturePicture
     * @return
     */
    SignaturePicture saveOrUpdate(SignaturePicture signaturePicture);
}
