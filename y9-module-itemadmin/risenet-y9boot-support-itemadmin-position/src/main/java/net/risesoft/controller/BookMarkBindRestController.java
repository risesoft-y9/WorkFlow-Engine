package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.BookMarkBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.BookMarkBindService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/bookMarkBind")
public class BookMarkBindRestController {

    
    private final BookMarkBindService bookMarkBindService;

    /**
     * 删除书签绑定
     *
     * @param wordTemplateId 模板id
     * @param bookMarkName 书签名称
     * @return
     */
    @RequestMapping(value = "/deleteBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteBind(@RequestParam(required = true) String wordTemplateId,
        @RequestParam(required = true) String bookMarkName) {
        bookMarkBindService.deleteBind(wordTemplateId, bookMarkName);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存书签绑定
     *
     * @param bookMarkBind 绑定信息
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(BookMarkBind bookMarkBind) {
        bookMarkBindService.saveOrUpdate(bookMarkBind);
        return Y9Result.successMsg("保存成功");
    }
}