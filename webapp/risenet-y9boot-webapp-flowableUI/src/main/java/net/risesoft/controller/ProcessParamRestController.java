package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/processParam")
public class ProcessParamRestController {

    @Autowired
    private ProcessParamService processParamService;

    /**
     * 保存流程变量
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @param number 编号
     * @param level 紧急程度
     * @param customItem 是否定制流程
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String documentTitle,
        @RequestParam(required = false) String number, @RequestParam(required = false) String level, @RequestParam(required = false) Boolean customItem) {
        return processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, documentTitle, number, level, customItem);
    }
}
