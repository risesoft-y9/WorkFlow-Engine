package net.risesoft.controller.gfg;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import net.risesoft.service.fgw.HTKYService;
import net.risesoft.util.gfg.OldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
import net.risesoft.service.fgw.HTKYService;
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

    @Autowired
    private HTKYService htkyService;

    @Resource(name = "jdbcTemplate4Dedicated")
    private JdbcTemplate jdbcTemplate4Dedicated;

    /**
     * 关联来文信息
     *
     * @param processSerialNumber 流程编号
     * @param bianhao 来文编号
     * @return Y9Result<Object>
     */
    @GetMapping(value = "/guanLianLaiWen")
    public Y9Result<Object> guanLianLaiWen(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String bianhao) {
        if (!bianhao.endsWith("号")) {
            bianhao = bianhao + "号";
        }
        // 1.判断是否已在老系统生成过待办，已生成的件不能关联
        Boolean isExist = htkyService.findIsExist(bianhao);
        if (isExist) {
            return Y9Result.failure("此委内编号已生成“行政许可来文待办件”，请先处理该来文待办件。");
        } else {
            // 2.判断当前人是否有权限在来文信息中关联来文
            Boolean flag = htkyService.isAssociated(bianhao);
            if (flag) {
                //3.查询老系统数据
                JdbcTemplate oldjdbcTemplate = OldUtil.getOldjdbcTemplate();
                String sql = "select lwinfouid,wnbh,lwtitle,lwdept,to_char(limittime, 'yyyy-MM-dd hh24:mi:ss') limittime,lwcode,zbdept,fileproperty from D_GW_LWINFO where wnbh = '"+bianhao+"'";
                List<Map<String, Object>> lwMap = oldjdbcTemplate.query(sql,(rs, rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lwinfouid", rs.getObject("lwinfouid"));
                    map.put("wnbh", rs.getObject("wnbh"));
                    map.put("lwtitle", rs.getObject("lwtitle"));
                    map.put("limittime", rs.getObject("limittime"));
                    map.put("lwcode", rs.getObject("lwcode"));
                    map.put("zbdept", rs.getObject("zbdept"));
                    map.put("fileproperty", rs.getObject("fileproperty"));
                    map.put("lwdept", rs.getObject("lwdept"));
                    return map;
                });
                if (lwMap.size()>0){
                    Map<String, Object> map = lwMap.get(0);
                    LwLinkBwModel lwInfoModel = new LwLinkBwModel();
                    lwInfoModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    lwInfoModel.setProcessSerialNumber(processSerialNumber);
                    lwInfoModel.setWnbh(bianhao);
                    lwInfoModel.setLwInfoUid(String.valueOf(map.get("lwinfouid")));
                    lwInfoModel.setLwTitle(String.valueOf(map.get("lwtitle")));
                    lwInfoModel.setLwDept(String.valueOf(map.get("lwdept")));
                    lwInfoModel.setLwsx(String.valueOf(map.get("limittime")));
                    lwInfoModel.setRecordTime(String.valueOf(new Timestamp(System.currentTimeMillis())));
                    lwInfoModel.setInputPerson(Y9LoginUserHolder.getUserInfo().getName());
                    //插入新系统 办文信息的来文信息表(已存在则提示：来文信息已经存在~)
                    Y9Result o =lwInfoApi.saveLwInfo(Y9LoginUserHolder.getTenantId(), lwInfoModel);
                    if (!o.isSuccess()){
                        return Y9Result.failure(o.getMsg());
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = sdf.format(new Date());
                    //插入老系统 关联文件表
                    String insertsql = "insert into BPM_LINKRUNTIME (LINKID,DESCRIPTION,FROMINSTANCEID,NAME,TOINSTANCEID,BPMSERVER,CREATED," +
                            "CREATERDN,CREATERNAME,CREATERUID,UPDATED,UPDATERDN,UPDATERNAME,UPDATERUID,LINKTYPE) " +
                            "values ('"+Y9IdGenerator.genId()+"','"+String.valueOf(map.get("lwtitle"))+"','"+processInstanceId+"'," +
                            "'"+bianhao+"','"+String.valueOf(map.get("lwinfouid"))+"','new','"+date+"'," +
                            "'"+Y9LoginUserHolder.getUserInfo().getDn()+"','"+Y9LoginUserHolder.getUserInfo().getName()+"','"+Y9LoginUserHolder.getPersonId()+"'," +
                            "'"+date+"','"+Y9LoginUserHolder.getUserInfo().getDn()+"','"+Y9LoginUserHolder.getUserInfo().getName()+"','"+Y9LoginUserHolder.getPersonId()+"','NORMAL_OFFICELINE')";
                    oldjdbcTemplate.update(insertsql);
                    return Y9Result.success();
                }else{
                    return Y9Result.failure("未查询到对应委内编号来文信息~");
                }
            }else {
                return Y9Result.success(null,"对不起!您没有权限关联此来文！");
            }
        }
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

    @GetMapping(value = "/deletefwgllw")
    public Y9Result deletefwgllw(@RequestParam @NotBlank String id, @RequestParam @NotBlank String lwinfouid, @RequestParam @NotBlank String processInstanceId) {
        Boolean flag = lwInfoApi.delLwInfo(Y9LoginUserHolder.getTenantId(), id).isSuccess();
        if (flag){
            String sql = "delete from BPM_LINKRUNTIME where FROMINSTANCEID='"+processInstanceId+"' and TOINSTANCEID ='"+lwinfouid+"'";
            int i =OldUtil.getOldjdbcTemplate().update(sql);
            if (i>0) {
                return Y9Result.success();
            }
        }
            return Y9Result.failure("删除失败");
    }
}
