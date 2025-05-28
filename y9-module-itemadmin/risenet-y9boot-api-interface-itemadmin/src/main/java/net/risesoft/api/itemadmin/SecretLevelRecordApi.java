package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DmyjInfoModel;
import net.risesoft.model.itemadmin.SecretLevelModel;
import net.risesoft.pojo.Y9Result;

/**
 * 密级记录接口
 *
 * @author yihong
 * @date 2025/02/12
 */
public interface SecretLevelRecordApi {

    /**
     * 获取定密依据信息
     *
     * @param tenantId 租户id
     * @param miji 密级
     * @param dmyjmc 定密依据名称
     * @param dmyjsj 定密依据司局
     * @return {@code Y9Result<List<DmyjInfoModel>>} 通用请求返回对象
     * @since 9.6.8
     */
    @GetMapping("/getDmyjInfo")
    Y9Result<List<DmyjInfoModel>> getDmyjInfo(@RequestParam("tenantId") String tenantId,
        @RequestParam("miji") String miji, @RequestParam(value = "dmyjmc", required = false) String dmyjmc,
        @RequestParam("dmyjsj") String dmyjsj);

    /**
     * 获取密级记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<SecretLevelModel>>} 通用请求返回对象
     * @since 9.6.8
     */
    @GetMapping("/getRecord")
    Y9Result<List<SecretLevelModel>> getRecord(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存密级修改记录
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param secretLevel 密级
     * @param secretBasis 定密依据
     * @param secretItem 具体事项
     * @param description 说明
     * @param tableName 表名
     * @param fieldName 字段名
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping("/saveRecord")
    Y9Result<Object> saveRecord(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam(value = "secretLevel") String secretLevel,
        @RequestParam(value = "secretBasis") String secretBasis, @RequestParam(value = "secretItem") String secretItem,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "tableName") String tableName, @RequestParam(value = "fieldName") String fieldName);

}
