package net.risesoft.service;

import net.risesoft.entity.AfterWorkInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AfterWorkInfoService {

    void deleteById(String id);

    void save(AfterWorkInfo afterWorkInfo);
}
