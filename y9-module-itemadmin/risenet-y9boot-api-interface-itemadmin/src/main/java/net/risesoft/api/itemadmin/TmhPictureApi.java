package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TmhPictureModel;
import net.risesoft.pojo.Y9Result;

/**
 * 条码号图片接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TmhPictureApi {

    /**
     * 根据流程编号查询条码号图片
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param tmhType 条码号类型
     * @return Y9Result<TmhPictureModel> 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping(value = "/findByProcessSerialNumber")
    Y9Result<TmhPictureModel> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("tmhType") String tmhType);

    /**
     * 保存或更新条码号图片
     *
     * @param tenantId 租户id
     * @param tmhPictureModel 条码号图片模型
     * @return Y9Result<Object> 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody TmhPictureModel tmhPictureModel);
}
