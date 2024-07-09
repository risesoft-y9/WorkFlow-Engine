package net.risesoft.service;

import net.risesoft.entity.OpinionFrameOneClickSet;

import java.util.List;
import java.util.Map;

public interface OpinionFrameOneClickSetService {

	/**
	 * 保存一键设置信息
	 * @param opinionFrameOneClickSet
	 * @return
	 */
	Map<String,Object> save(OpinionFrameOneClickSet opinionFrameOneClickSet);

	/**
	 * 获取当前绑定的一键设置列表
	 * @param bindId
	 * @return
	 */
	List<OpinionFrameOneClickSet> findByBindId(String bindId);

	/**
	 * 删除一键设置
	 * @param id
	 */
	void delete(String id);
}
