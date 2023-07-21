package net.risesoft.service.impl;

import org.springframework.stereotype.Service;

import net.risesoft.service.UtilService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "utilService")
public class UtilServiceImpl implements UtilService {

    @Override
    public void freeJump(String taskId, String targetTaskDefineKey) {}
}
