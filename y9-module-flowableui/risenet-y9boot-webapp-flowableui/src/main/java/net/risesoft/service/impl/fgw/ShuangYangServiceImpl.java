package net.risesoft.service.impl.fgw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.RemoteCallUtil;
import net.risesoft.entity.PushData;
import net.risesoft.service.fgw.ShuangYangService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Service(value = "shuangYangService")
@Transactional(readOnly = true)
public class ShuangYangServiceImpl implements ShuangYangService {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    //发文关联来件查来件大厅编码
    private static String getFwLinkLjSql(final PushData pushData){
        String sql = "SELECT lw.hallreg from bpm_linkruntime l, d_gw_lwinfo lw WHERE lw.lwinfouid = l.toinstanceid AND l.frominstanceid = '"+pushData.getProcessinstanceid()+"' AND lw.hallreg IS NOT NULL AND l.linktype != 'BING_AN'";
        return sql;
    }
    //来件关联发文查来件大厅编码
    private static String getLjLinkFwSql(PushData pushData) {
        String sql ="";
        if (pushData != null && "办结".equals(pushData.getEventtype())) {
            sql ="SELECT lw.hallreg, lwpi.instanceid lwpiinstanceid, lwpi.bingan FROM bpm_processinstancedone lwpi, bpm_linkruntime l, d_gw_lwinfo lw WHERE lwpi.businesskey = lw.lwinfouid AND lwpi.instanceid = l.frominstanceid AND l.toinstanceid ='"+pushData.getProcessinstanceid()+"' AND lw.hallreg IS NOT NULL AND l.linktype != 'BING_AN'";
        } else {
            sql ="SELECT lw.hallreg, lwpi.instanceid lwpiinstanceid, lwpi.bingan FROM bpm_processinstanceruntime lwpi, bpm_linkruntime l, d_gw_lwinfo lw WHERE lwpi.businesskey = lw.lwinfouid AND lwpi.instanceid = l.frominstanceid AND l.toinstanceid ='"+pushData.getProcessinstanceid()+"' AND lw.hallreg IS NOT NULL AND l.linktype != 'BING_AN'";
        }
        return sql;
    }
    //查询并案
    private static String getBingAnSql(String instanceId,String eventType){
        StringBuffer sb = new StringBuffer();
        sb.append(" select lw.hallreg,lwpi.instanceid from ");
        if("办结".equals(eventType)){
            sb.append(" bpm_processinstancedone lwpi, ");
        }else{
            sb.append(" bpm_processinstanceruntime lwpi, ");
        }
        sb.append("d_gw_lwinfo lw ");
        sb.append(" WHERE lwpi.businesskey = lw.lwinfouid AND ");
        sb.append(" lwpi.masterid = '" + instanceId + "'");
        return sb.toString();
    }

    //查询是否是委托评估
    private static String getIswtpgSql(final PushData pushData){
        String sql ="select * from y9_form_fw where iswtpg = '1' AND GUID='"+pushData.getProcessserialnumber()+"'";
        return sql;
    }

    @Override
    public void toShuangYang() {
        try {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName(Y9Context.getProperty("y9.app.oldDataSource.driver-class-name"));
            ds.setUrl(Y9Context.getProperty("y9.app.oldDataSource.url"));
            ds.setUsername(Y9Context.getProperty("y9.app.oldDataSource.username"));
            ds.setPassword(Y9Context.getProperty("y9.app.oldDataSource.password"));
            JdbcTemplate oldjdbcTemplate = new JdbcTemplate(ds);
            //查询未推送的数据
            List<PushData> pushDataList = jdbcTemplate.query("select * from PUSHDATA where tszt ='0' order by CREATEDATE", new RowMapper<PushData>() {
                @Override
                public PushData mapRow(ResultSet rs, int rowNum) throws SQLException {
                    PushData pushData = new PushData();
                    pushData.setId(rs.getString("ID"));
                    pushData.setCreatedate(rs.getDate("CREATEDATE"));
                    pushData.setEventtype(rs.getString("EVENTTYPE"));
                    pushData.setProcessinstanceid(rs.getString("PROCESSINSTANCEID"));
                    pushData.setProcessserialnumber(rs.getString("PROCESSSERIALNUMBER"));
                    pushData.setTsdate(rs.getDate("TSDATE"));
                    pushData.setTsjg(rs.getString("TSJG"));
                    pushData.setTszt(rs.getString("TSZT"));
                    pushData.setXmdm(rs.getString("XMDM"));
                    return pushData;
                }
            });
            for (PushData pushData : pushDataList) {
                Boolean result = false;
                LOGGER.info("处理的pushData的id："+pushData.getId());
                //发文关联来件
                List<Map<String, Object>> fwLinkLj = oldjdbcTemplate.query(getFwLinkLjSql(pushData),(rs,rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hallreg", rs.getObject("hallreg"));
                    return map;
                });
                for(int i = 0; i < fwLinkLj.size(); ++i) {
                    Map<String,Object> map = fwLinkLj.get(i);
                    PushData pd = new PushData();
                    pd.setProcessinstanceid(pushData.getProcessinstanceid());
                    pd.setProcessserialnumber(pushData.getProcessserialnumber());
                    pd.setCreatedate(pushData.getCreatedate());
                    pd.setXmdm(map.get("hallreg").toString());
                    pd.setEventtype(pushData.getEventtype());
                    result = pushDataInterface(pd);
                }

                //来件关联发文
                List<Map<String, Object>> ljLinkFw = oldjdbcTemplate.query(getLjLinkFwSql(pushData),(rs,rowNum) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hallreg", rs.getObject("hallreg"));
                    map.put("instanceid", rs.getObject("instanceid"));
                    map.put("bingan", rs.getObject("bingan"));
                    return map;
                });
                for(int i =0; i < ljLinkFw.size(); i++){
                    Map<String,Object> map = ljLinkFw.get(i);
                    PushData pd = new PushData();
                    pd.setProcessinstanceid(pushData.getProcessinstanceid());
                    pd.setProcessserialnumber(pushData.getProcessserialnumber());
                    pd.setCreatedate(pushData.getCreatedate());
                    pd.setXmdm(map.get("hallreg").toString());
                    pd.setEventtype(pushData.getEventtype());
                    result = pushDataInterface(pd);
                    if(map.get("bingan") != null && map.get("bingan").toString().equals("1")){ //并案文件
                        List<Map<String, Object>> ljLinkfwBingAn = oldjdbcTemplate.query(getBingAnSql(map.get("instanceid")+"",pushData.getEventtype()),(rs,rowNum) -> {
                            Map<String, Object> rsmap = new HashMap<>();
                            rsmap.put("hallreg", rs.getObject("hallreg"));
                            rsmap.put("instanceid", rs.getObject("instanceid"));
                            return rsmap;
                        });
                        for(int j =0; j < ljLinkfwBingAn.size(); j++){
                            Map<String,Object> mapBingAn = ljLinkfwBingAn.get(j);
                            PushData pdBingAn = new PushData();
                            pdBingAn.setProcessinstanceid(mapBingAn.get("instanceid")+"");
                            pdBingAn.setCreatedate(pushData.getCreatedate());
                            pdBingAn.setXmdm(mapBingAn.get("hallreg").toString());
                            pd.setEventtype(pushData.getEventtype());
                            result = pushDataInterface(pdBingAn);
                        }
                    }
                }
                if(fwLinkLj.size() == 0 && ljLinkFw.size() == 0){
                    List<Map<String, Object>> listwtpg = oldjdbcTemplate.query(getIswtpgSql(pushData),(rs,rowNum) -> {
                        Map<String, Object> rsmap = new HashMap<>();
                        rsmap.put("guid", rs.getObject("guid"));
                        return rsmap;
                    });
                    if (listwtpg.size() == 0) {
                        if ("上会".equals(pushData.getEventtype())) {
                            PushData pd = new PushData();
                            pd.setProcessinstanceid((pushData.getProcessinstanceid()));
                            pd.setCreatedate(pushData.getCreatedate());
                            pd.setXmdm("");
                            pd.setEventtype("非法上会");
                            result = pushDataInterface(pd);
                        }
                        if ("彻底删除".equals(pushData.getEventtype())) {
                            PushData pd = new PushData();
                            pd.setProcessinstanceid(pushData.getProcessinstanceid());
                            pd.setCreatedate(pushData.getCreatedate());
                            pd.setXmdm("");
                            pd.setEventtype(pushData.getEventtype());
                            result = pushDataInterface(pd);
                        }
                    } else {
                        PushData pd = new PushData();
                        pd.setProcessinstanceid(pushData.getProcessinstanceid());
                        pd.setCreatedate(pushData.getCreatedate());
                        pd.setXmdm("");
                        pd.setEventtype(pushData.getEventtype());
                        result = pushDataInterface(pd);
                    }
                }
                if(result){
                    pushData.setTszt("1");
                    pushData.setTsdate(new Date());
                    pushData.setTsjg("推送成功");
                    Object[] args = {pushData.getTszt(), pushData.getTsdate(), pushData.getTsjg(),pushData.getId()};
                    jdbcTemplate.update("update PUSHDATA set TSZT = ?,TSDATE = ? ,TSJG = ? where ID = ?",args);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //推送至双杨
    public Boolean pushDataInterface(PushData data){
        Boolean result = false;
        try{
            String url = Y9Context.getProperty("y9.common.shangHuiUrl");
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            xml += "<pushdata>";
            if("非法上会".equals(data.getEventtype())){
                xml += "<instanceId></instanceId>";
                xml += "<fwinstanceId>" + data.getProcessinstanceid() + "</fwinstanceId>";
            }else{
                xml += "<instanceId>" + data.getProcessinstanceid() + "</instanceId>";
                xml += "<fwinstanceId></fwinstanceId>";
            }
            xml += "<xmdm>" + data.getXmdm() + "</xmdm>";
            xml += "<eventType>" + data.getEventtype() + "</eventType>";
            xml += "<operateDate>" + sdf.format(data.getCreatedate() != null ? data.getCreatedate() : new Date()) + "</operateDate>";
            xml += "</pushdata>";
            LOGGER.info("推送双杨xml:"+xml);
            Object o = RemoteCallUtil.invoke(url,"invoke", new Object[] { xml });
            result = Boolean.parseBoolean(o.toString());
            LOGGER.info("推送双杨返回结果:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
