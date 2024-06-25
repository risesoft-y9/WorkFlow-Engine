package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.SignaturePictureModel;
import net.risesoft.pojo.Y9Result;

/**
 * 签名图片
 *
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据唯一标示查找签名图片
     *
     * @param tenantId 租户id
     * @param id id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     */
    @GetMapping("/findById")
    Y9Result<SignaturePictureModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据人员Id查找签名图片
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     */
    @GetMapping("/findByUserId")
    Y9Result<SignaturePictureModel> findByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 保存或者修改签名照片信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param spJson spJson
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<SignaturePictureModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("spJson") String spJson);
}
