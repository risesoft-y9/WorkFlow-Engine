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
     * 根据id查询WPS正文
     *
     * @param tenantId 租户id
     * @param id id
     * @return {@code Y9Result<DocumentWpsModel>} 通用请求返回对象 - data 是wps文档
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<DocumentWpsModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程编号查询WPS正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<DocumentWpsModel>} 通用请求返回对象- data 是wps文档
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<DocumentWpsModel> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存WPS正文
     *
     * @param tenantId 租户id
     * @param documentWpsModel wps文档对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveDocumentWps", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveDocumentWps(@RequestParam("tenantId") String tenantId,
        @RequestBody DocumentWpsModel documentWpsModel);

    /**
     * 保存WPS正文内容
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param hasContent 是否有内容
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveWpsContent")
    Y9Result<Object> saveWpsContent(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("hasContent") String hasContent);

}
