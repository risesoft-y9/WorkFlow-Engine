package net.risesoft.controller.gfg;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.LwInfoApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.LwLinkBwModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 来文信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/lwInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class LwInfo4GfgRestController {

    private final LwInfoApi lwInfoApi;

    /**
     * 关联来文信息
     *
     * @param processSerialNumber 流程编号
     * @param bianhao 来文编号
     * @return Y9Result<Object>
     */
    @GetMapping(value = "/guanLianLaiWen")
    public Y9Result<Object> guanLianLaiWen(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String bianhao) {
        // try {
        // // 调用第三方接口
        // HttpClient client = new HttpClient();
        // client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        // client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        // PostMethod method = new PostMethod();
        // method.setPath("");
        // // 请求参数
        // method.addParameter("bianhao", bianhao);
        // // 设置请求超时时间10s
        // client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        // // 设置读取数据超时时间10s
        // client.getHttpConnectionManager().getParams().setSoTimeout(10000);
        // int httpCode = client.executeMethod(method);
        // if (httpCode == HttpStatus.SC_OK) {
        // String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
        // StandardCharsets.UTF_8);
        // LOGGER.debug("*********************关联来文信息结果:response={}", response);
        // LwInfoModel lwInfoModel = new LwInfoModel();
        // lwInfoApi.saveLwInfo(Y9LoginUserHolder.getTenantId(),lwInfoModel);
        // return Y9Result.success();
        // } else {
        // LOGGER.error("*********************关联来文信息失败:httpCode:{}", httpCode);
        // }
        // } catch (Exception e) {
        // LOGGER.error("*********************关联来文信息失败");
        // }
        // return Y9Result.failure("关联失败");

        LwLinkBwModel lwInfoModel = new LwLinkBwModel();
        lwInfoModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        lwInfoModel.setProcessSerialNumber(processSerialNumber);
        lwInfoModel.setWnbh(bianhao);
        lwInfoModel.setLwInfoUid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        lwInfoModel.setLwTitle("关联来文测试标题");
        lwInfoModel.setLwDept("关联来文单位");
        lwInfoModel.setLwsx("2天");
        lwInfoModel.setRecordTime("关联");
        lwInfoModel.setInputPerson("关联");
        lwInfoModel.setLwInfoUid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        lwInfoModel.setWnbhUid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        return lwInfoApi.saveLwInfo(Y9LoginUserHolder.getTenantId(), lwInfoModel);
    }

    /**
     * 获取来文信息列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<LwInfoModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<LwLinkBwModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        return lwInfoApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

}
