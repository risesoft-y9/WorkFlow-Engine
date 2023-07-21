package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.service.ItemDataCopyService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Controller
@RequestMapping(value = "/itemTest")
public class ItemTestController {

    @Autowired
    private ItemDataCopyService itemDataCopyService;

    /**
     * 从源租户复制事项的所有数据到目标租户
     * 
     * @param sId
     * @param tId
     * @param itemId
     */
    @ResponseBody
    @RequestMapping(value = "/t1")
    public void t1(String sId, String tId, String itemId) {
        try {
            itemDataCopyService.dataCopy(sId, tId, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从源租户复制事项的视图配置数据到目标租户
     * 
     * @param sId
     * @param tId
     * @param itemId
     */
    @ResponseBody
    @RequestMapping(value = "/t2")
    public void t2(String sId, String tId, String itemId) {
        itemDataCopyService.copyItemViewConf(sId, tId, itemId);
    }
}
