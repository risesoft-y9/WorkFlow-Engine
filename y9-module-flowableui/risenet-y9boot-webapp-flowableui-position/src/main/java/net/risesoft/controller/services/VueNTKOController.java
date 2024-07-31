package net.risesoft.controller.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/services/vueNtko")
public class VueNTKOController {

    private final ProcessParamApi processParamApi;

    private final DraftApi draftApi;

    private final TransactionWordApi transactionWordApi;

    private final PersonApi personApi;

    private final PositionApi positionApi;

    private final AttachmentApi attachmentApi;

    private final Y9Properties y9Config;

    private final OrgUnitApi orgUnitApi;

    /**
     * 获取附件信息
     *
     * @param processSerialNumber 流程编号
     * @param itembox 状态
     * @param taskId 任务id
     * @param browser 浏览器类型
     * @param fileId 文件id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping("/showFile")
    public Y9Result<Map<String, Object>> showFile(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String browser, @RequestParam(required = false) String fileId,
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam(required = false) String positionId) {
        try {
            Map<String, Object> map = new HashMap<>();
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachmentApi.getFile(tenantId, fileId).getData();
            String downloadUrl =
                y9Config.getCommon().getItemAdminBaseUrl() + "/s/" + file.getFileStoreId() + "." + file.getFileType();
            map.put("fileName", file.getName());
            map.put("browser", browser);
            map.put("fileUrl", downloadUrl);
            map.put("tenantId", tenantId);
            map.put("userId", userId);
            map.put("taskId", taskId);
            map.put("positionId", positionId);
            map.put("itembox", itembox);
            map.put("fileId", fileId);
            map.put("userName", person.getName());
            map.put("processSerialNumber", processSerialNumber);
            return Y9Result.success(map, "获取信息成功");
        } catch (Exception e) {
            LOGGER.error("获取信息失败", e);
        }
        return Y9Result.failure("获取信息失败");
    }

    /**
     * 获取正文
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param itembox 状态
     * @param taskId 任务id
     * @param browser 浏览器类型
     * @param positionId 岗位id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping("/showWord")
    public Y9Result<Y9WordInfo> showWord(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam String itemId,
        @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String browser, @RequestParam(required = false) String bindValue,
        @RequestParam(required = false) String positionId, @RequestParam String tenantId, @RequestParam String userId,
        Model model) {
        try {
            Y9WordInfo wordInfo = transactionWordApi
                .showWord(tenantId, userId, processSerialNumber, itemId, itembox, taskId, bindValue).getData();
            String documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (draftModel != null) {
                    documentTitle = draftModel.getTitle();
                }
            } else {
                String[] pInstanceId = processInstanceId.split(",");
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, pInstanceId[0]).getData();
                documentTitle = processModel.getTitle();
                processInstanceId = pInstanceId[0];
            }
            wordInfo.setDocumentTitle(StringUtils.isNotBlank(documentTitle) ? documentTitle : "正文");
            wordInfo.setBrowser(browser);
            wordInfo.setProcessInstanceId(processInstanceId);
            wordInfo.setTenantId(tenantId);
            wordInfo.setUserId(userId);
            wordInfo.setPositionId(positionId);
            Position position = positionApi.get(tenantId, positionId).getData();
            OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, position.getParentId()).getData();
            wordInfo.setCurrentBureauGuid(currentBureau != null ? currentBureau.getId() : "");
            return Y9Result.success(wordInfo, "获取信息成功");
        } catch (Exception e) {
            LOGGER.error("获取信息失败", e);
        }
        return Y9Result.failure("获取信息失败");
    }

}
