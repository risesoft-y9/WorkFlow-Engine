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
     * 删除排版信息
     *
     * @param tenantId 租户id
     * @param id 排版信息id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/delTypeSetting")
    Y9Result<Object> delTypeSetting(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

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
     * 获取排版信息
     *
     * @param tenantId 租户id
     * @param id 排版信息id
     * @return Y9Result<TypeSettingInfoModel>
     */
    @GetMapping(value = "/getTypeSetting")
    Y9Result<TypeSettingInfoModel> getTypeSetting(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id);

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

    /**
     * 修改排版信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param jsonData 排版信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/updateTypeSetting")
    Y9Result<Object> updateTypeSetting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("jsonData") String jsonData);

    /**
     * 更新排版信息
     *
     * @param tenantId 租户id
     * @param id 排版信息id
     * @param fileId 文件id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/updateFile")
    Y9Result<Object> updateFile(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id,
        @RequestParam("fileId") String fileId, @RequestParam("fileType") String fileType);
}
