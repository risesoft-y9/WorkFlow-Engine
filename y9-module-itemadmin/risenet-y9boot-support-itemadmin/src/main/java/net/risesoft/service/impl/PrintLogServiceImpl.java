package net.risesoft.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.PrintLog;
import net.risesoft.repository.jpa.PrintLogRepository;
import net.risesoft.service.PrintLogService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrintLogServiceImpl implements PrintLogService {

    private final PrintLogRepository printLogRepository;

    @Override
    public List<PrintLog> getPrintLogList(String processSerialNumber) {
        return printLogRepository.findByProcessSerialNumberOrderByPrintTimeDesc(processSerialNumber);
    }

    @Override
    @Transactional
    public void savePrintLog(PrintLog log) {
        printLogRepository.save(log);
    }
}
