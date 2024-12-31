package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TypeSettingInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * 发文单排版信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TypeSettingInfoApi {

    /**
     * 获取排版信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<TypeSettingInfoModel>>
     */
    @GetMapping(value = "/getList")
    Y9Result<List<TypeSettingInfoModel>> getList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存排版信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param jsonData 排版信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveTypeSetting")
    Y9Result<Object> saveTypeSetting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("jsonData") String jsonData);
}
