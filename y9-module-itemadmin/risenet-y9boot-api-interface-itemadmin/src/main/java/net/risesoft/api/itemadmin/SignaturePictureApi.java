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
     * 删除签名图片
     *
     * @param id id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("id") String id);

    /**
     * 根据id获取签名图片
     *
     * @param id id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<SignaturePictureModel> findById(@RequestParam("id") String id);

    /**
     * 根据人员id获取签名图片
     *
     * @param userId 人员id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     * @since 9.6.6
     */
    @GetMapping("/findByUserId")
    Y9Result<SignaturePictureModel> findByUserId(@RequestParam("userId") String userId);

    /**
     * 保存或更新签名图片
     *
     * @param spJson spJson
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片
     * @since 9.6.6
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<SignaturePictureModel> saveOrUpdate(@RequestParam("spJson") String spJson);
}
