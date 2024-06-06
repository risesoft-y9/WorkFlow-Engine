package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.LinkInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemLinkBindService;
import net.risesoft.service.LinkInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/linkInfo")
public class LinkInfoRestController {

    private final LinkInfoService linkInfoService;

    private final ItemLinkBindService itemLinkBindService;

    /**
     * 获取链接列表
     *
     * @param linkName 链接名称
     * @param linkUrl 链接地址
     * @return
     */
    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<LinkInfo>> findAll(@RequestParam(required = false) String linkName, @RequestParam(required = false) String linkUrl) {
        List<LinkInfo> list = linkInfoService.findAll(linkName, linkUrl);
        return Y9Result.success(list, "获取列表成功");
    }

    /**
     * 获取链接信息
     *
     * @param id 链接id
     * @return
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<LinkInfo> findById(@RequestParam String id) {
        LinkInfo info = linkInfoService.findById(id);
        return Y9Result.success(info, "获取成功");
    }

    /**
     * 获取链接绑定事项列表
     *
     * @param id 链接id
     * @return
     */
    @RequestMapping(value = "/findByLinkId", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemLinkBind>> findByLinkId(@RequestParam String id) {
        List<ItemLinkBind> list = itemLinkBindService.findByLinkId(id);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除链接信息
     *
     * @param id 链接id
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam String id) {
        linkInfoService.remove(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存链接信息
     *
     * @param info 链接信息
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(LinkInfo info) {
        linkInfoService.saveOrUpdate(info);
        return Y9Result.successMsg("保存成功");
    }

}
