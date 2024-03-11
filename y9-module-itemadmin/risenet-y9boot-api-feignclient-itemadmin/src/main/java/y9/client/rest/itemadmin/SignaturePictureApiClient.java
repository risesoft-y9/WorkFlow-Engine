package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.SignaturePictureApi;
import net.risesoft.model.itemadmin.SignaturePictureModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "SignaturePictureApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/signaturePicture")
public interface SignaturePictureApiClient extends SignaturePictureApi {

    /**
     * 根据唯一标示
     *
     * @param tenantId 租户id
     * @param id id
     */
    @Override
    @PostMapping("/deleteById")
    void deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据唯一标示查找签名图片
     *
     * @param tenantId 租户id
     * @param id id
     * @return SignaturePictureModel
     */
    @Override
    @GetMapping("/findById")
    SignaturePictureModel findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据人员Id查找签名图片
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return SignaturePictureModel
     */
    @Override
    @GetMapping("/findByUserId")
    SignaturePictureModel findByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 保存或者修改签名照片信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param spJson spJson
     * @return SignaturePictureModel
     */
    @Override
    @PostMapping("/saveOrUpdate")
    SignaturePictureModel saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("spJson") String spJson);
}
