package net.risesoft.controller.gfg;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.QueryListService;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 待办，在办，办结列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/workList/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkList4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    private final ItemViewConfApi itemViewConfApi;

    private final QueryListService queryListService;

    /**
     * 获取流程序列号的所有会签流程信息
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getSignDeptDetailList")
    public Y9Result<List<Map<String, Object>>> getSignDeptDetailList(@RequestParam String processSerialNumber) {
        return this.workList4GfgService.getSignDeptDetailList(processSerialNumber);
    }

    /**
     * 综合查询
     *
     * @param itemId 事项id
     * @param state 状态
     * @param createDate 日期
     * @param tableName 表名
     * @param searchMapStr 查询字段信息
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/queryList")
    public Y9Page<Map<String, Object>> queryList(@RequestParam String itemId,
        @RequestParam(required = false) String state, @RequestParam(required = false) String createDate,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.queryListService.pageQueryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
    }

    /**
     * 根据事项id和视图类型获取视图配置
     *
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/viewConf")
    public Y9Result<List<ItemViewConfModel>> viewConf(@RequestParam String itemId,
        @RequestParam(required = false) String viewType) {
        List<ItemViewConfModel> itemViewConfList =
            this.itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, viewType).getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}