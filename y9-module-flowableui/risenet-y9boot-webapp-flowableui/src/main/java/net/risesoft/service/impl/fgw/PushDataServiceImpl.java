package net.risesoft.service.impl.fgw;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.service.fgw.PushDataService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Date;

@Service(value = "pushDataService")
@Slf4j
@Transactional(readOnly = true)
public class PushDataServiceImpl implements PushDataService {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addPushData(String processSerialNumber, String processInstanceId, String eventtype) {
        String sql = "insert into PUSHDATA (ID,EVENTTYPE,CREATEDATE,PROCESSSERIALNUMBER,PROCESSINSTANCEID,TSZT) values (?,?,?,?,?,?)";
        Object[] args = {Y9IdGenerator.genId(),eventtype,new Date(),processSerialNumber,processInstanceId,"0"};
        jdbcTemplate.update(sql, args);
    }
}
