package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.button.CommonButton;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface CommonButtonService {

    /**
     * 新增的时候，判断传进来的customId是否已经存在
     *
     * @param customId 自定义id
     * @return boolean
     */
    boolean checkCustomId(String customId);

    /**
     * 根据唯一标示查找
     *
     * @param id 唯一标识
     * @return CommonButton
     */
    CommonButton getById(String id);

    /**
     * 查找所有按钮
     *
     * @return List<CommonButton>
     */
    List<CommonButton> listAll();

    /**
     * 根据传进来的Id集合删除
     *
     * @param commonButtonIds Id集合
     */
    void removeCommonButtons(String[] commonButtonIds);

    /**
     * 保存或者更新
     *
     * @param commonButton 按钮实体
     * @return CommonButton
     */
    CommonButton saveOrUpdate(CommonButton commonButton);
}
