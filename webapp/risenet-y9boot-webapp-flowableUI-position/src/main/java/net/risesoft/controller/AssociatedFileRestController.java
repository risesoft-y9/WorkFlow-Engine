package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SearchService;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping("/vue/associatedFile")
public class AssociatedFileRestController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private AssociatedFile4PositionApi associatedFileManager;

    /**
     * 删除关联文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 要删除的流程实例ids，逗号隔开
     * @return
     */
    @RequestMapping(value = "/delAssociatedFile", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delAssociatedFile(@RequestParam(required = true) String processSerialNumber, @RequestParam(required = true) String processInstanceIds) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            boolean b = associatedFileManager.deleteAssociatedFile(tenantId, processSerialNumber, processInstanceIds);
            if (b) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取关联文件列表
     *
     * @param processSerialNumber 流程编号
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAssociatedFileList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getAssociatedFileList(@RequestParam(required = true) String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = associatedFileManager.getAssociatedFileAllList(tenantId, positionId, processSerialNumber);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success((List<Map<String, Object>>)map.get("rows"), "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取历史文件
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/getDoneList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String title, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return searchService.getSearchList(title, itemId, "", "", "", page, rows);
    }

    /**
     * 保存关联文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 流程实例ids，逗号隔开
     * @return
     */
    @RequestMapping(value = "/saveAssociatedFile", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveAssociatedFile(@RequestParam(required = true) String processSerialNumber, @RequestParam(required = true) String processInstanceIds) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            boolean b = associatedFileManager.saveAssociatedFile(tenantId, positionId, processSerialNumber, processInstanceIds);
            if (b) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
