package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SyncYearTableService {

    /**
     * 生成年度表结构
     * 
     * @param year 年
     * @return
     */
    Map<String, Object> syncYearTable(String year);

}
