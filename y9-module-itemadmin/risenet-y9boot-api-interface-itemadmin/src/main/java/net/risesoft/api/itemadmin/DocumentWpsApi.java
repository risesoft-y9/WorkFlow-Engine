package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.pojo.Y9Result;

/**
 * WPS正文接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DocumentWpsApi {

    /**
     * 根据id获取正文信息
     *
     * @param tenantId 租户id
     * @param id id
     * @return Y9Result<DocumentWpsModel>
     */
    @GetMapping("/findById")
    Y9Result<DocumentWpsModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程编号获取正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<DocumentWpsModel>
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<DocumentWpsModel> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存正文信息
     *
     * @param tenantId 租户id
     * @param documentWps wps文档对象
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveDocumentWps", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveDocumentWps(@RequestParam("tenantId") String tenantId,
        @RequestBody DocumentWpsModel documentWps);

    /**
     * 保存正文内容状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param hasContent 是否有内容
     * @return Y9Result<Object>
     */
    @PostMapping("/saveWpsContent")
    Y9Result<Object> saveWpsContent(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("hasContent") String hasContent);

}
