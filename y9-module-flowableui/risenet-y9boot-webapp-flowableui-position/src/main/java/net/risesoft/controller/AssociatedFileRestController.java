package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SearchService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 关联文件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/vue/associatedFile")
public class AssociatedFileRestController {

    private final SearchService searchService;

    private final AssociatedFile4PositionApi associatedFile4PositionApi;

    /**
     * 删除关联文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 要删除的流程实例ids，逗号隔开
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/delAssociatedFile", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delAssociatedFile(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String processInstanceIds) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Y9Result<Object> y9Result =
                associatedFile4PositionApi.deleteAssociatedFile(tenantId, processSerialNumber, processInstanceIds);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除关联文件失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取关联文件列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<AssociatedFileModel>>
     */
    @RequestMapping(value = "/getAssociatedFileList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<AssociatedFileModel>>
        getAssociatedFileList(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return associatedFile4PositionApi.getAssociatedFileAllList(tenantId, positionId, processSerialNumber);
    }

    /**
     * 获取历史文件
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/getDoneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.getSearchList(title, itemId, "", "", "", "", "", page, rows);
    }

    /**
     * 保存关联文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 流程实例ids，逗号隔开
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveAssociatedFile", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveAssociatedFile(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String processInstanceIds) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Y9Result<Object> y9Result = associatedFile4PositionApi.saveAssociatedFile(tenantId, positionId,
                processSerialNumber, processInstanceIds);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            LOGGER.error("保存关联文件失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}
