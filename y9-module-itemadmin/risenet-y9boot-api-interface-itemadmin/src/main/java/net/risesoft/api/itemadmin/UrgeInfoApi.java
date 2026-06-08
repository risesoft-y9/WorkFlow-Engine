package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
public interface UrgeInfoApi {

    /**
     * 删除催办信息
     *
     * @param id 催办信息唯一标示
     * @return
     */
    @PostMapping(value = "/deleteById", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> deleteById(@RequestParam("id") String id);

    /**
     * 查找催办信息
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    @GetMapping(value = "/findByProcessSerialNumber", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<List<UrgeInfoModel>>
        findByProcessSerialNumber(@RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存催办信息
     *
     * @param processSerialNumber 流程序列号
     * @param msgContent 催办消息
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> save(@RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("msgContent") String msgContent);
}
