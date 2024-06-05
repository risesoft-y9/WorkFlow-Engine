package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ErrorLog;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ErrorLogRepository;
import net.risesoft.service.ErrorLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    @Override
    @Transactional()
    public void saveErrorLog(ErrorLog errorLog) {
        try {
            if (StringUtils.isBlank(errorLog.getId())) {
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
