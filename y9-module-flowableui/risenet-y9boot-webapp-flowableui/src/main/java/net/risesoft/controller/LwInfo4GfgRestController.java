package net.risesoft.controller;

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
import net.risesoft.model.itemadmin.LwInfoModel;
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

        LwInfoModel lwInfoModel = new LwInfoModel();
        lwInfoModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        lwInfoModel.setProcessSerialNumber(processSerialNumber);
        lwInfoModel.setWnbh(bianhao);
        lwInfoModel.setLwDate("关联来文");
        lwInfoModel.setLwInfoUid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        lwInfoModel.setLwTitle("关联来文测试标题");
        lwInfoModel.setLwDept("关联来文单位");
        lwInfoModel.setLwfs("关联来文");
        lwInfoModel.setLwcode("关联来文");
        lwInfoModel.setLwwh("关联来文");
        lwInfoModel.setLwsx("2天");
        lwInfoModel.setWjtype("关联");
        lwInfoModel.setMiji("关联");
        lwInfoModel.setHuanji("关联");
        lwInfoModel.setBanfou("关联");
        lwInfoModel.setZbDept("关联");
        lwInfoModel.setFileProperty("关联");
        lwInfoModel.setShb("关联");
        lwInfoModel.setLimiTime("关联");
        lwInfoModel.setRecordTime("关联");
        lwInfoModel.setIsDebug("关联");
        lwInfoModel.setHandleStatus("关联");
        lwInfoModel.setBureauministerMind("关联");
        lwInfoModel.setOfficeministerMind("关联");
        lwInfoModel.setBureauSecertaryMind("关联");
        lwInfoModel.setUndertakePersonMind("关联");
        lwInfoModel.setTopproperty("关联");
        lwInfoModel.setSecondproperty("关联");
        lwInfoModel.setThirdproperty("关联");
        lwInfoModel.setAcceptorNot("关联");
        lwInfoModel.setHallIndex("关联");
        lwInfoModel.setHallReg("关联");
        lwInfoModel.setQqsxBtn("关联");
        lwInfoModel.setCreateTime("22222222");
        lwInfoModel.setNeeddo("关联");
        lwInfoModel.setDecdit("关联");
        lwInfoModel.setOveraccepttime("关联");
        lwInfoModel.setTouchUser("关联");
        lwInfoModel.setTouchTel("关联");
        lwInfoModel.setAcceptType("关联");
        lwInfoModel.setSendDept("关联");
        lwInfoModel.setAppDept("关联");
        lwInfoModel.setNopermitszyj("关联");
        lwInfoModel.setNopermitczyj("关联");
        lwInfoModel.setHandleType("关联");
        lwInfoModel.setFinishtype("关联");
        lwInfoModel.setXmmc("关联");
        lwInfoModel.setXmdm("关联");
        lwInfoModel.setSfxs("关联");
        lwInfoModel.setIsFlwdj("关联");
        return lwInfoApi.saveLwInfo(Y9LoginUserHolder.getTenantId(), lwInfoModel);
    }

    /**
     * 获取来文信息列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<LwInfoModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<LwInfoModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        return lwInfoApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

}
