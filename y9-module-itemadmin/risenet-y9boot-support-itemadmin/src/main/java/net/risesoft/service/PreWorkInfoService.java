package net.risesoft.service;

import net.risesoft.entity.PreWorkInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface PreWorkInfoService {

    void deleteById(String id);

    void save(PreWorkInfo preWorkInfo);
}
