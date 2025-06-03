package net.risesoft.util.gfg;

import net.risesoft.entity.LinkModel;
import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessModel;
import net.risesoft.model.platform.TenantSystem;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.text.SimpleDateFormat;
import java.util.*;

public class OldUtil {
    public static final String ljProcessId 	= "_BTjEIOYIEeO5oaEWear_Cw";//大厅来件
    public static final String fwProcessId 	= "_N7pw8DY8EeO5EqWURXzpRA"; //发文
    public static final String zqyjProcessId = "_HtSCIfOuEeSrt-rFRbhdxw"; //征求意见
    public static final String qianbaoProcessId = "_6j7bwTlcEeSDIc7kqzcU1A"; //签报
    public static final String newFwProcessId = Y9Context.getProperty("y9.common.itemId");

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static JdbcTemplate getOldjdbcTemplate() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(Y9Context.getProperty("y9.app.oldDataSource.driver-class-name"));
        ds.setUrl(Y9Context.getProperty("y9.app.oldDataSource.url"));
        ds.setUsername(Y9Context.getProperty("y9.app.oldDataSource.username"));
        ds.setPassword(Y9Context.getProperty("y9.app.oldDataSource.password"));
        JdbcTemplate oldjdbcTemplate = new JdbcTemplate(ds);
        return oldjdbcTemplate;
    }

    //查询老系统流程实例
    public static ProcessInstance getOldProcessModel(String instanceId) {
        ProcessInstance processInstance = null;
        List<ProcessInstance> list = new ArrayList<>();
        JdbcTemplate oldjdbcTemplate = OldUtil.getOldjdbcTemplate();
        String sqlRuntime = "select * from BPM_PROCESSINSTANCERUNTIME where instanceid = '"+instanceId+"'";
        list = oldjdbcTemplate.query(sqlRuntime, new BeanPropertyRowMapper<>(ProcessInstance.class));
        if (list.size() <= 0) {
            String sqlDone = "select * from BPM_PROCESSINSTANCEDONE where instanceid = '"+instanceId+"'";
            list = oldjdbcTemplate.query(sqlDone, new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() <= 0) {
            String sqlHistory = "select * from BPM_PROCESSINSTANCEHISTORY where instanceid = '"+instanceId+"'";
            list = oldjdbcTemplate.query(sqlHistory , new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() > 0){
            if (list.get(0) != null){
                processInstance = list.get(0);
            }
        }
        return processInstance;
    }

    //查询所有流程
    public static List<ProcessModel> allProccesId(){
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
}
