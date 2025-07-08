package net.risesoft.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.template.BookMarkBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.BookMarkBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/bookMarkBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookMarkBindRestController {

    private final BookMarkBindService bookMarkBindService;

    /**
     * 删除书签绑定
     *
     * @param wordTemplateId 模板id
     * @param bookMarkName 书签名称
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteBind")
    public Y9Result<String> deleteBind(@RequestParam String wordTemplateId, @RequestParam String bookMarkName) {
        bookMarkBindService.deleteBind(wordTemplateId, bookMarkName);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存书签绑定
     *
     * @param bookMarkBind 绑定信息
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(BookMarkBind bookMarkBind) {
        bookMarkBindService.saveOrUpdate(bookMarkBind);
        return Y9Result.successMsg("保存成功");
    }
}