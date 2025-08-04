package net.risesoft.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.service.AsyncUtilService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncUtilServiceImpl implements AsyncUtilService {

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    @Async
    @Override
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSongApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }
}
