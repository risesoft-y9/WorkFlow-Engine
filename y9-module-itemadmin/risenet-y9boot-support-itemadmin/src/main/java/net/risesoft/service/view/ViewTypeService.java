package net.risesoft.service.view;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.view.ViewType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ViewTypeService {

    /**
     * 
     *
     * @param id
     * @return
     */
    ViewType findById(String id);

    /**
     * 
     *
     * @param mark
     * @return
     */
    ViewType findByMark(String mark);

    /**
     * 
     *
     * @return
     */
    List<ViewType> listAll();

    /**
     * 
     *
     * @param page
     * @param rows
     * @return
     */
    Page<ViewType> pageAll(int page, int rows);

    /**
     * 
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 
     *
     * @param viewType
     * @return
     */
    ViewType save(ViewType viewType);

    /**
     * 
     *
     * @param viewType
     * @return
     */
    ViewType saveOrUpdate(ViewType viewType);

    /**
     * 
     *
     * @param page
     * @param rows
     * @param name
     * @return
     */
    Page<ViewType> search(int page, int rows, String name);
}
