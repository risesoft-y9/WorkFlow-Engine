package net.risesoft.util.gfg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessModel;
import net.risesoft.y9.Y9Context;

public class OldUtil {
    public static final String ljProcessId = "_BTjEIOYIEeO5oaEWear_Cw";// 大厅来件
    public static final String fwProcessId = "_N7pw8DY8EeO5EqWURXzpRA"; // 发文
    public static final String zqyjProcessId = "_HtSCIfOuEeSrt-rFRbhdxw"; // 征求意见
    public static final String qianbaoProcessId = "_6j7bwTlcEeSDIc7kqzcU1A"; // 签报
    public static final String newFwProcessId = Y9Context.getProperty("y9.common.itemId");

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 查询所有流程
    public static List<ProcessModel> allProccesId() {
        List<ProcessModel> list = new ArrayList<>();
        ProcessModel lj = new ProcessModel();
        lj.setProcessId(ljProcessId);
        lj.setProcessName("大厅来件");
        ProcessModel fw = new ProcessModel();
        fw.setProcessId(fwProcessId);
        fw.setProcessName("发文");
        ProcessModel zqyj = new ProcessModel();
        ProcessModel nfw = new ProcessModel();
        nfw.setProcessId(OldUtil.newFwProcessId);
        nfw.setProcessName("新发文");
        zqyj.setProcessId(zqyjProcessId);
        zqyj.setProcessName("征求意见");
        ProcessModel qb = new ProcessModel();
        qb.setProcessId(qianbaoProcessId);
        qb.setProcessName("签报");
        list.add(lj);
        list.add(fw);
        list.add(nfw);
        list.add(zqyj);
        list.add(qb);
        return list;
    }

    // //查询老系统流程实例
    // public static ProcessInstance getOldProcessModel(String instanceId) {
    // ProcessInstance processInstance = null;
    // List<ProcessInstance> list = new ArrayList<>();
    // JdbcTemplate oldjdbcTemplate = OldUtil.getOldjdbcTemplate();
    // String sqlRuntime = "select * from BPM_PROCESSINSTANCERUNTIME where instanceid = '"+instanceId+"'";
    // list = oldjdbcTemplate.query(sqlRuntime, (rs, rowNum) ->{
    // ProcessInstance pi = new ProcessInstance();
    // pi.setInstanceid(rs.getString("instanceid") != null ?rs.getString("instanceid") : "");
    // pi.setTenantid(rs.getString("tenantid") != null ?rs.getString("tenantid") : "");
    // pi.setActionstatus(rs.getString("actionstatus") != null ?rs.getString("actionstatus") : "");
    // pi.setArchived(rs.getInt("archived"));
    // pi.setBpmserver(rs.getString("bpmserver") != null ?rs.getString("bpmserver") : "");
    // pi.setBusinesskey(rs.getString("businesskey") != null ?rs.getString("businesskey") : "");
    // pi.setCreated(rs.getString("created") != null ?rs.getDate("created") : new Date());
    // pi.setCreaterDn(rs.getString("createrdn") != null ?rs.getString("createrdn") : "");
    // pi.setCreaterName(rs.getString("creatername") != null ?rs.getString("creatername") : "");
    // pi.setCreaterUid(rs.getString("createruid") != null ?rs.getString("createruid") : "");
    // pi.setDebug(rs.getString("debug") != null ?rs.getString("debug") : "");
    // pi.setDeleted(rs.getString("deleted") != null ?rs.getString("deleted") : "");
    // pi.setDescription(rs.getString("description") != null ?rs.getString("description") : "");
    // pi.setInstanceexpiredate(rs.getString("instanceexpiredate") != null ?rs.getDate("instanceexpiredate") : new
    // Date());
    // pi.setInstancereminddate(rs.getString("instancereminddate") != null ?rs.getDate("instancereminddate") :new
    // Date());
    // pi.setName(rs.getString("name") != null ?rs.getString("name") : "");
    // pi.setParentid(rs.getString("parentid") != null ?rs.getString("parentid") : "");
    // pi.setPriority(rs.getString("priority") !=null ?rs.getInt("priority") : 0);
    // pi.setProcessId(rs.getString("processid") != null ?rs.getString("processid") : "");
    // pi.setProcessName(rs.getString("processname") != null ?rs.getString("processname") : "");
    // pi.setProcessversion(rs.getString("processversion") != null ?rs.getInt("processversion") : 0);
    // pi.setRouteType(rs.getString("routetype") != null ?rs.getString("routetype") : "");
    // pi.setRunning(rs.getString("running") != null ?rs.getString("running") : "");
    // pi.setState(rs.getString("state") != null ?rs.getString("state") : "");
    // pi.setStep(rs.getString("step") != null ?rs.getInt("step") : 0);
    // pi.setTitle(rs.getString("title") != null ?rs.getString("title") : "");
    // pi.setUpdated(rs.getString("updated") != null ?rs.getDate("updated") : new Date());
    // pi.setUpdaterDn(rs.getString("updaterdn") != null ?rs.getString("updaterdn") : "");
    // pi.setUpdaterName(rs.getString("updatername") != null ?rs.getString("updatername") : "");
    // pi.setUpdaterUid(rs.getString("updateruid") != null ?rs.getString("updateruid") : "");
    // pi.setYear(rs.getString("year") != null ?rs.getInt("year") : 0);
    // pi.setSerialnumber(rs.getString("serialnumber") != null ?rs.getString("serialnumber") : "");
    // pi.setFromid(rs.getString("fromid") != null ?rs.getString("fromid") : "");
    // pi.setBingan(rs.getInt("bingan"));
    // pi.setSlaveindex(rs.getString("slaveindex") != null ?rs.getInt("slaveindex") : 0);
    // pi.setMasterId(rs.getString("masterid") != null ?rs.getString("masterid") : "");
    // return pi;
    // });
    // if (list.size() <= 0) {
    // String sqlDone = "select * from BPM_PROCESSINSTANCEDONE where instanceid = '"+instanceId+"'";
    // list = oldjdbcTemplate.query(sqlDone, (rs, rowNum) ->{
    // ProcessInstance pi = new ProcessInstance();
    // pi.setInstanceid(rs.getString("instanceid") != null ?rs.getString("instanceid") : "");
    // pi.setTenantid(rs.getString("tenantid") != null ?rs.getString("tenantid") : "");
    // pi.setActionstatus(rs.getString("actionstatus") != null ?rs.getString("actionstatus") : "");
    // pi.setArchived(rs.getInt("archived") );
    // pi.setBpmserver(rs.getString("bpmserver") != null ?rs.getString("bpmserver") : "");
    // pi.setBusinesskey(rs.getString("businesskey") != null ?rs.getString("businesskey") : "");
    // pi.setCreated(rs.getString("created") != null ?rs.getDate("created") : new Date());
    // pi.setCreaterDn(rs.getString("createrdn") != null ?rs.getString("createrdn") : "");
    // pi.setCreaterName(rs.getString("creatername") != null ?rs.getString("creatername") : "");
    // pi.setCreaterUid(rs.getString("createruid") != null ?rs.getString("createruid") : "");
    // pi.setDebug(rs.getString("debug") != null ?rs.getString("debug") : "");
    // pi.setDeleted(rs.getString("deleted") != null ?rs.getString("deleted") : "");
    // pi.setDescription(rs.getString("description") != null ?rs.getString("description") : "");
    // pi.setInstanceexpiredate(rs.getString("instanceexpiredate") != null ?rs.getDate("instanceexpiredate") : new
    // Date());
    // pi.setInstancereminddate(rs.getString("instancereminddate") != null ?rs.getDate("instancereminddate") :new
    // Date());
    // pi.setName(rs.getString("name") != null ?rs.getString("name") : "");
    // pi.setParentid(rs.getString("parentid") != null ?rs.getString("parentid") : "");
    // pi.setPriority(rs.getString("priority") !=null ?rs.getInt("priority") : 0);
    // pi.setProcessId(rs.getString("processid") != null ?rs.getString("processid") : "");
    // pi.setProcessName(rs.getString("processname") != null ?rs.getString("processname") : "");
    // pi.setProcessversion(rs.getString("processversion") != null ?rs.getInt("processversion") : 0);
    // pi.setRouteType(rs.getString("routetype") != null ?rs.getString("routetype") : "");
    // pi.setRunning(rs.getString("running") != null ?rs.getString("running") : "");
    // pi.setState(rs.getString("state") != null ?rs.getString("state") : "");
    // pi.setStep(rs.getString("step") != null ?rs.getInt("step") : 0);
    // pi.setTitle(rs.getString("title") != null ?rs.getString("title") : "");
    // pi.setUpdated(rs.getString("updated") != null ?rs.getDate("updated") : new Date());
    // pi.setUpdaterDn(rs.getString("updaterdn") != null ?rs.getString("updaterdn") : "");
    // pi.setUpdaterName(rs.getString("updatername") != null ?rs.getString("updatername") : "");
    // pi.setUpdaterUid(rs.getString("updateruid") != null ?rs.getString("updateruid") : "");
    // pi.setYear(rs.getString("year") != null ?rs.getInt("year") : 0);
    // pi.setSerialnumber(rs.getString("serialnumber") != null ?rs.getString("serialnumber") : "");
    // pi.setFromid(rs.getString("fromid") != null ?rs.getString("fromid") : "");
    // pi.setBingan(rs.getString("bingan") != null ?rs.getInt("bingan") : 0);
    // pi.setSlaveindex(rs.getString("slaveindex") != null ?rs.getInt("slaveindex") : 0);
    // pi.setMasterId(rs.getString("masterid") != null ?rs.getString("masterid") : "");
    // return pi;
    // });
    // }
    // if (list.size() <= 0) {
    // String sqlHistory = "select * from BPM_PROCESSINSTANCEHISTORY where instanceid = '"+instanceId+"'";
    // list = oldjdbcTemplate.query(sqlHistory, (rs, rowNum) ->{
    // ProcessInstance pi = new ProcessInstance();
    // pi.setInstanceid(rs.getString("instanceid") != null ?rs.getString("instanceid") : "");
    // pi.setTenantid(rs.getString("tenantid") != null ?rs.getString("tenantid") : "");
    // pi.setActionstatus(rs.getString("actionstatus") != null ?rs.getString("actionstatus") : "");
    // pi.setArchived(rs.getInt("archived"));
    // pi.setBpmserver(rs.getString("bpmserver") != null ?rs.getString("bpmserver") : "");
    // pi.setBusinesskey(rs.getString("businesskey") != null ?rs.getString("businesskey") : "");
    // pi.setCreated(rs.getString("created") != null ?rs.getDate("created") : new Date());
    // pi.setCreaterDn(rs.getString("createrdn") != null ?rs.getString("createrdn") : "");
    // pi.setCreaterName(rs.getString("creatername") != null ?rs.getString("creatername") : "");
    // pi.setCreaterUid(rs.getString("createruid") != null ?rs.getString("createruid") : "");
    // pi.setDebug(rs.getString("debug") != null ?rs.getString("debug") : "");
    // pi.setDeleted(rs.getString("deleted") != null ?rs.getString("deleted") : "");
    // pi.setDescription(rs.getString("description") != null ?rs.getString("description") : "");
    // pi.setInstanceexpiredate(rs.getString("instanceexpiredate") != null ?rs.getDate("instanceexpiredate") : new
    // Date());
    // pi.setInstancereminddate(rs.getString("instancereminddate") != null ?rs.getDate("instancereminddate") :new
    // Date());
    // pi.setName(rs.getString("name") != null ?rs.getString("name") : "");
    // pi.setParentid(rs.getString("parentid") != null ?rs.getString("parentid") : "");
    // pi.setPriority(rs.getString("priority") !=null ?rs.getInt("priority") : 0);
    // pi.setProcessId(rs.getString("processid") != null ?rs.getString("processid") : "");
    // pi.setProcessName(rs.getString("processname") != null ?rs.getString("processname") : "");
    // pi.setProcessversion(rs.getString("processversion") != null ?rs.getInt("processversion") : 0);
    // pi.setRouteType(rs.getString("routetype") != null ?rs.getString("routetype") : "");
    // pi.setRunning(rs.getString("running") != null ?rs.getString("running") : "");
    // pi.setState(rs.getString("state") != null ?rs.getString("state") : "");
    // pi.setStep(rs.getString("step") != null ?rs.getInt("step") : 0);
    // pi.setTitle(rs.getString("title") != null ?rs.getString("title") : "");
    // pi.setUpdated(rs.getString("updated") != null ?rs.getDate("updated") : new Date());
    // pi.setUpdaterDn(rs.getString("updaterdn") != null ?rs.getString("updaterdn") : "");
    // pi.setUpdaterName(rs.getString("updatername") != null ?rs.getString("updatername") : "");
    // pi.setUpdaterUid(rs.getString("updateruid") != null ?rs.getString("updateruid") : "");
    // pi.setYear(rs.getString("year") != null ?rs.getInt("year") : 0);
    // pi.setSerialnumber(rs.getString("serialnumber") != null ?rs.getString("serialnumber") : "");
    // pi.setFromid(rs.getString("fromid") != null ?rs.getString("fromid") : "");
    // pi.setBingan(rs.getString("bingan") != null ?rs.getInt("bingan") : 0);
    // pi.setSlaveindex(rs.getString("slaveindex") != null ?rs.getInt("slaveindex") : 0);
    // pi.setMasterId(rs.getString("masterid") != null ?rs.getString("masterid") : "");
    // return pi;
    // });
    // }
    // if (list.size() > 0){
    // String inid = list.get(0).getInstanceid();
    // if (StringUtils.isNotBlank(inid)){
    // processInstance = list.get(0);
    // }
    // }
    // return processInstance;
    // }

    // 查询老系统流程实例
    public static ProcessInstance getOldProcessModel(String instanceId) {
        ProcessInstance processInstance = null;
        List<ProcessInstance> list = new ArrayList<>();
        JdbcTemplate oldjdbcTemplate = OldUtil.getOldjdbcTemplate();
        String sqlRuntime = "select * from BPM_PROCESSINSTANCERUNTIME where instanceid = '" + instanceId + "'";
        list = oldjdbcTemplate.query(sqlRuntime, new BeanPropertyRowMapper<>(ProcessInstance.class));
        if (list.size() <= 0) {
            String sqlDone = "select * from BPM_PROCESSINSTANCEDONE where instanceid = '" + instanceId + "'";
            list = oldjdbcTemplate.query(sqlDone, new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() <= 0) {
            String sqlHistory = "select * from BPM_PROCESSINSTANCEHISTORY where instanceid = '" + instanceId + "'";
            list = oldjdbcTemplate.query(sqlHistory, new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() > 0) {
            if (list.get(0) != null) {
                processInstance = list.get(0);
            }
        }
        return processInstance;
    }

    public static JdbcTemplate getOldjdbcTemplate() {
        if (Y9Context.getProperty("y9.app.oldDataSource.driver-class-name") != null) {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName(Y9Context.getProperty("y9.app.oldDataSource.driver-class-name"));
            ds.setUrl(Y9Context.getProperty("y9.app.oldDataSource.url"));
            ds.setUsername(Y9Context.getProperty("y9.app.oldDataSource.username"));
            ds.setPassword(Y9Context.getProperty("y9.app.oldDataSource.password"));
            JdbcTemplate oldjdbcTemplate = new JdbcTemplate(ds);
            return oldjdbcTemplate;
        }
        return null;
    }
}
