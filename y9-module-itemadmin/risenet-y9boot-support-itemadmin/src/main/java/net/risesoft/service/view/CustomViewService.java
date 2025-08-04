package net.risesoft.service.view;

import java.util.List;

import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface CustomViewService {

    /**
     * 删除自定义视图
     *
     * @param viewType 视图类型
     */
    void delCustomView(String viewType);

    /**
     * 获取自定义视图
     *
     * @param viewType 视图类型
     * @return
     */
    Y9Result<List<CustomViewModel>> listCustomView(String viewType);

    /**
     * 保存自定义视图
     *
     * @param jsonData 自定义视图
     */
    void saveCustomView(String jsonData);
}
