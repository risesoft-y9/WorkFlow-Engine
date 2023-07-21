package net.risesoft.controller.sync;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.service.ChaoSongInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 同步抄送
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping("/services/rest/chaosong")
@Slf4j
public class SyncChaoSongInfoRestController {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChaoSongInfoService chaoSongInfoService;

    /**
     * 同步所有
     * 
     * @param tenantId
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/syncAll")
    public void syncYearTable4Tenant(String tenantId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String sql = "SELECT * FROM ff_chaosong";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        LOGGER.info("********************未阅：{}", list.size());
        int i = 0;
        for (Map<String, Object> m : list) {
            try {
                ChaoSongInfo info = new ChaoSongInfo();
                info.setId((String)m.get("ID"));
                info.setCreateTime((String)m.get("CREATETIME"));
                info.setItemId(m.get("ITEMID") != null ? (String)m.get("ITEMID") : "");
                info.setItemName(m.get("ITEMNAME") != null ? (String)m.get("ITEMNAME") : "");
                info.setOpinionState(m.get("opinionState") != null ? (String)m.get("opinionState") : "");
                info.setProcessInstanceId(m.get("PROCESSINSTANCEID") != null ? (String)m.get("PROCESSINSTANCEID") : "");
                info.setProcessSerialNumber(m.get("PROCESSSERIALNUMBER") != null ? (String)m.get("PROCESSSERIALNUMBER") : "");
                info.setReadTime(m.get("READTIME") != null ? (String)m.get("READTIME") : "");
                info.setSendDeptId(m.get("SENDDEPTID") != null ? (String)m.get("SENDDEPTID") : "");
                info.setSendDeptName(m.get("SENDDEPTNAME") != null ? (String)m.get("SENDDEPTNAME") : "");
                info.setSenderId(m.get("SENDERID") != null ? (String)m.get("SENDERID") : "");
                info.setSenderName(m.get("SENDERNAME") != null ? (String)m.get("SENDERNAME") : "");
                info.setStatus(Integer.parseInt(m.get("STATUS").toString()));
                info.setTaskId(m.get("TASKID") != null ? (String)m.get("TASKID") : "");
                info.setTenantId(tenantId);
                info.setTitle(m.get("TITLE") != null ? (String)m.get("TITLE") : "");
                info.setUserDeptId(m.get("USERDEPTID") != null ? (String)m.get("USERDEPTID") : "");
                info.setUserDeptName(m.get("USERDEPTNAME") != null ? (String)m.get("USERDEPTNAME") : "");
                info.setUserId(m.get("USERID") != null ? (String)m.get("USERID") : "");
                info.setUserName(m.get("USERNAME") != null ? (String)m.get("USERNAME") : "");
                chaoSongInfoService.save(info);
            } catch (Exception e) {
                i++;
                e.printStackTrace();
            }
        }
        LOGGER.info("********************同步失败：{}", i);
        try {
            sql = "SELECT * FROM ff_chaosong_2020";
            list = jdbcTemplate.queryForList(sql);
            i = 0;
            LOGGER.info("********************2020年：" + list.size());
            for (Map<String, Object> m : list) {
                try {
                    ChaoSongInfo info = new ChaoSongInfo();
                    info.setId((String)m.get("ID"));
                    info.setCreateTime((String)m.get("CREATETIME"));
                    info.setItemId(m.get("ITEMID") != null ? (String)m.get("ITEMID") : "");
                    info.setItemName(m.get("ITEMNAME") != null ? (String)m.get("ITEMNAME") : "");
                    info.setOpinionState(m.get("opinionState") != null ? (String)m.get("opinionState") : "");
                    info.setProcessInstanceId(m.get("PROCESSINSTANCEID") != null ? (String)m.get("PROCESSINSTANCEID") : "");
                    info.setProcessSerialNumber(m.get("PROCESSSERIALNUMBER") != null ? (String)m.get("PROCESSSERIALNUMBER") : "");
                    info.setReadTime(m.get("READTIME") != null ? (String)m.get("READTIME") : "");
                    info.setSendDeptId(m.get("SENDDEPTID") != null ? (String)m.get("SENDDEPTID") : "");
                    info.setSendDeptName(m.get("SENDDEPTNAME") != null ? (String)m.get("SENDDEPTNAME") : "");
                    info.setSenderId(m.get("SENDERID") != null ? (String)m.get("SENDERID") : "");
                    info.setSenderName(m.get("SENDERNAME") != null ? (String)m.get("SENDERNAME") : "");
                    info.setStatus(Integer.parseInt(m.get("STATUS").toString()));
                    info.setTaskId(m.get("TASKID") != null ? (String)m.get("TASKID") : "");
                    info.setTenantId(tenantId);
                    info.setTitle(m.get("TITLE") != null ? (String)m.get("TITLE") : "");
                    info.setUserDeptId(m.get("USERDEPTID") != null ? (String)m.get("USERDEPTID") : "");
                    info.setUserDeptName(m.get("USERDEPTNAME") != null ? (String)m.get("USERDEPTNAME") : "");
                    info.setUserId(m.get("USERID") != null ? (String)m.get("USERID") : "");
                    info.setUserName(m.get("USERNAME") != null ? (String)m.get("USERNAME") : "");
                    chaoSongInfoService.save(info);
                } catch (Exception e) {
                    i++;
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败：{}", i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sql = "SELECT * FROM ff_chaosong_2021";
            list = jdbcTemplate.queryForList(sql);
            i = 0;
            LOGGER.info("********************2021年：{}", list.size());
            for (Map<String, Object> m : list) {
                try {
                    ChaoSongInfo info = new ChaoSongInfo();
                    info.setId((String)m.get("ID"));
                    info.setCreateTime((String)m.get("CREATETIME"));
                    info.setItemId(m.get("ITEMID") != null ? (String)m.get("ITEMID") : "");
                    info.setItemName(m.get("ITEMNAME") != null ? (String)m.get("ITEMNAME") : "");
                    info.setOpinionState(m.get("opinionState") != null ? (String)m.get("opinionState") : "");
                    info.setProcessInstanceId(m.get("PROCESSINSTANCEID") != null ? (String)m.get("PROCESSINSTANCEID") : "");
                    info.setProcessSerialNumber(m.get("PROCESSSERIALNUMBER") != null ? (String)m.get("PROCESSSERIALNUMBER") : "");
                    info.setReadTime(m.get("READTIME") != null ? (String)m.get("READTIME") : "");
                    info.setSendDeptId(m.get("SENDDEPTID") != null ? (String)m.get("SENDDEPTID") : "");
                    info.setSendDeptName(m.get("SENDDEPTNAME") != null ? (String)m.get("SENDDEPTNAME") : "");
                    info.setSenderId(m.get("SENDERID") != null ? (String)m.get("SENDERID") : "");
                    info.setSenderName(m.get("SENDERNAME") != null ? (String)m.get("SENDERNAME") : "");
                    info.setStatus(Integer.parseInt(m.get("STATUS").toString()));
                    info.setTaskId(m.get("TASKID") != null ? (String)m.get("TASKID") : "");
                    info.setTenantId(tenantId);
                    info.setTitle(m.get("TITLE") != null ? (String)m.get("TITLE") : "");
                    info.setUserDeptId(m.get("USERDEPTID") != null ? (String)m.get("USERDEPTID") : "");
                    info.setUserDeptName(m.get("USERDEPTNAME") != null ? (String)m.get("USERDEPTNAME") : "");
                    info.setUserId(m.get("USERID") != null ? (String)m.get("USERID") : "");
                    info.setUserName(m.get("USERNAME") != null ? (String)m.get("USERNAME") : "");
                    chaoSongInfoService.save(info);
                } catch (Exception e) {
                    i++;
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败：{}", i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
