package net.risesoft.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.ItemSmsHttpService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSmsHttpServiceImpl implements ItemSmsHttpService {

    @Override
    public void sendSmsHttpList(String tenantId, String userId, List<String> mobile, String smsContent,
        String systemName) throws Exception {

    }
}
