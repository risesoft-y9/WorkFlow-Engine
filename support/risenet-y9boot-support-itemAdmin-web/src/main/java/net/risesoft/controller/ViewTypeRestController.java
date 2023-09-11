package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/viewType")
public class ViewTypeRestController {

    @Autowired
    private ViewTypeService viewTypeService;

    @Autowired
    private ItemViewConfService itemViewConfService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    /**
     * 获取意见框
     *
     * @param id 意见框id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findById", method = RequestMethod.GET, produces = "application/json")
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
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<ViewType> list(@RequestParam Integer page, @RequestParam Integer rows) {
        Page<ViewType> pageList = viewTypeService.findAll(page, rows);
        List<ViewType> list = pageList.getContent();
        for (ViewType viewType : list) {
            String itemIds = "", itemNames = "";
            List<ItemViewConf> ivcList = itemViewConfService.findByViewType(viewType.getMark());
            for (ItemViewConf ivc : ivcList) {
                String itemId = ivc.getItemId();
                if (!itemIds.contains(itemId)) {
                    SpmApproveItem item = spmApproveItemService.findById(itemId);
                    if (null != item) {
                        itemIds += itemId + ";";
                        if (StringUtils.isEmpty(itemNames)) {
                            itemNames = item.getName();
                        } else {
                            itemNames += "、" + item.getName();
                        }
                    }
                }
            }
            viewType.setItemNames(itemNames);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list, "获取列表成功");
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<ViewType>> listAll() {
        List<ViewType> list = viewTypeService.findAll();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> remove(@RequestParam String[] ids) {
        viewTypeService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存
     *
     * @param viewType
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
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
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<ViewType> search(@RequestParam Integer page, @RequestParam Integer rows,
        @RequestParam(required = false) String keyword) {
        Page<ViewType> pageList = viewTypeService.search(page, rows, keyword);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }
}