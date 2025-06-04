package net.risesoft.util.gfg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessModel;

@Service(value = "OldUtil")
public class OldUtil {
    public static final String ljProcessId = "_BTjEIOYIEeO5oaEWear_Cw";// 大厅来件
    public static final String fwProcessId = "_N7pw8DY8EeO5EqWURXzpRA"; // 发文
    public static final String zqyjProcessId = "_HtSCIfOuEeSrt-rFRbhdxw"; // 征求意见
    public static final String qianbaoProcessId = "_6j7bwTlcEeSDIc7kqzcU1A"; // 签报

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Value("${y9.common.itemId}")
    public static String newFwProcessId;

    @Resource(name = "jdbcTemplate4Dedicated")
    private JdbcTemplate jdbcTemplate4Dedicated;

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

    // 查询老系统流程实例
    public ProcessInstance getOldProcessModel(String instanceId) {
        ProcessInstance processInstance = null;
        List<ProcessInstance> list = new ArrayList<>();
        String sqlRuntime = "select * from BPM_PROCESSINSTANCERUNTIME where instanceid = '" + instanceId + "'";
        list = jdbcTemplate4Dedicated.query(sqlRuntime, new BeanPropertyRowMapper<>(ProcessInstance.class));
        if (list.size() <= 0) {
            String sqlDone = "select * from BPM_PROCESSINSTANCEDONE where instanceid = '" + instanceId + "'";
            list = jdbcTemplate4Dedicated.query(sqlDone, new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() <= 0) {
            String sqlHistory = "select * from BPM_PROCESSINSTANCEHISTORY where instanceid = '" + instanceId + "'";
            list = jdbcTemplate4Dedicated.query(sqlHistory, new BeanPropertyRowMapper<>(ProcessInstance.class));
        }
        if (list.size() > 0) {
            if (list.get(0) != null) {
                processInstance = list.get(0);
            }
        }
        return processInstance;
    }
}
