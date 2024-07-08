package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemViewConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.ViewType;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.ViewTypeService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/viewType", produces = MediaType.APPLICATION_JSON_VALUE)
public class ViewTypeRestController {

    private final ViewTypeService viewTypeService;

    private final ItemViewConfService itemViewConfService;

    private final SpmApproveItemService spmApproveItemService;

    /**
     * 获取意见框
     *
     * @param id 意见框id
     * @return
     */
    @GetMapping(value = "/findById")
    public Y9Result<ViewType> getOpinionFrame(@RequestParam String id) {
        ViewType viewType = viewTypeService.findById(id);
        return Y9Result.success(viewType, "获取成功");
    }

    /**
     * 获取视图类型列表
     *
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @GetMapping(value = "/list")
    public Y9Page<ViewType> list(@RequestParam Integer page, @RequestParam Integer rows) {
        Page<ViewType> pageList = viewTypeService.findAll(page, rows);
        List<ViewType> list = pageList.getContent();
        for (ViewType viewType : list) {
            StringBuilder itemIds = new StringBuilder();
            StringBuilder itemNames = new StringBuilder();
            List<ItemViewConf> ivcList = itemViewConfService.findByViewType(viewType.getMark());
            for (ItemViewConf ivc : ivcList) {
                String itemId = ivc.getItemId();
                if (!itemIds.toString().contains(itemId)) {
                    SpmApproveItem item = spmApproveItemService.findById(itemId);
                    if (null != item) {
                        itemIds.append(itemId).append(";");
                        if (StringUtils.isEmpty(itemNames)) {
                            itemNames.append(item.getName());
                        } else {
                            itemNames.append("、" + item.getName());
                        }
                    }
                }
            }
            viewType.setItemNames(itemNames.toString());
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list, "获取列表成功");
    }

    @GetMapping(value = "/listAll")
    public Y9Result<List<ViewType>> listAll() {
        List<ViewType> list = viewTypeService.findAll();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除
     *
     * @param ids 视图类型id
     * @return
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        viewTypeService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存
     *
     * @param viewType 视图类型
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(ViewType viewType) {
        String id = viewType.getId();
        if (StringUtils.isEmpty(id)) {
            ViewType oldViewType = viewTypeService.findByMark(viewType.getMark());
            if (null != oldViewType) {
                return Y9Result.failure("保存失败：唯一标示【" + viewType.getMark() + "】已存在");
            }
        }
        viewTypeService.saveOrUpdate(viewType);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 根据关键字查找意见
     *
     * @param page 页码
     * @param rows 条数
     * @param keyword 视图类型名称
     * @return
     */
    @GetMapping(value = "/search")
    public Y9Page<ViewType> search(@RequestParam Integer page, @RequestParam Integer rows,
        @RequestParam(required = false) String keyword) {
        Page<ViewType> pageList = viewTypeService.search(page, rows, keyword);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }
}