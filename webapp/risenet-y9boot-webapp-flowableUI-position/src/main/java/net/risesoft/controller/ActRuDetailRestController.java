package net.risesoft.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;

@Controller
@RequestMapping(value = "/actRuDetail")
public class ActRuDetailRestController {

	@Resource(name = "actRuDetailService")
	private ActRuDetailService actRuDetailService;

	/**
	 * 办结
	 *
	 * @param processSerialNumber 流程序列号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/complete")
	public Y9Result<String> complete(@RequestParam String processSerialNumber) {
		return actRuDetailService.complete(processSerialNumber);
	}

	/**
	 * 保存流程当前用户的参与人信息
	 *
	 * @param itemId              事项唯一标示
	 * @param processSerialNumber 流程序列号
	 * @param sponsorDeptId       主办处室id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveOrUpdate")
	public Y9Result<String> saveOrUpdate(@RequestParam String itemId, @RequestParam String processSerialNumber) {
		return actRuDetailService.saveOrUpdate(itemId, processSerialNumber);
	}
}
