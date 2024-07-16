package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.LinkInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemLinkBindService;
import net.risesoft.service.LinkInfoService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/linkInfo", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(value = "/findAll")
    public Y9Result<List<LinkInfo>> findAll(@RequestParam(required = false) String linkName,
        @RequestParam(required = false) String linkUrl) {
        List<LinkInfo> list = linkInfoService.listAll(linkName, linkUrl);
        return Y9Result.success(list, "获取列表成功");
    }

    /**
     * 获取链接信息
     *
     * @param id 链接id
     * @return
     */
    @GetMapping(value = "/findById")
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
    @GetMapping(value = "/findByLinkId")
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
    @PostMapping(value = "/remove")
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
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(LinkInfo info) {
        linkInfoService.saveOrUpdate(info);
        return Y9Result.successMsg("保存成功");
    }

}
