package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.LinkInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface LinkInfoService {

    /**
     * 根据id获取链接信息
     *
     * @param id
     * @return
     */
    LinkInfo findById(String id);

    /**
     * 获取链接列表
     *
     * @param linkName
     * @param linkUrl
     * @return
     */
    List<LinkInfo> listAll(String linkName, String linkUrl);

    /**
     * 删除链接信息
     *
     * @param id
     */
    void remove(String id);

    /**
     * 保存链接信息
     *
     * @param linkInfo
     * @return
     */
    void saveOrUpdate(LinkInfo linkInfo);

}
