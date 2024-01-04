package net.risesoft.controller.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

@Controller
@RequestMapping(value = "/services/vueNtko")
public class VueNTKOController {

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private Draft4PositionApi draftManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private Attachment4PositionApi attachment4PositionApi;

    @Autowired
    private Y9Properties y9Config;

    @Autowired
    private OrgUnitApi orgUnitApi;

    /**
     * 获取附件信息
     *
     * @param processSerialNumber
     * @param fileName
     * @param itembox
     * @param taskId
     * @param browser
     * @param fileId
     * @param tenantId
     * @param userId
     * @param positionId
     * @param fileUrl
     * @return
     */
    @RequestMapping("/showFile")
    @ResponseBody
    public Y9Result<Map<String, Object>> showFile(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String fileName, @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String browser, @RequestParam(required = false) String fileId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, @RequestParam(required = false) String positionId, @RequestParam(required = false) String fileUrl) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Person person = personManager.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId);
            String downloadUrl = y9Config.getCommon().getItemAdminBaseUrl() + "/s/" + file.getFileStoreId() + "." + file.getFileType();
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
            e.printStackTrace();
        }
        return Y9Result.failure("获取信息失败");
    }

    /**
     * 获取正文
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param itemId
     * @param itembox
     * @param taskId
     * @param browser
     * @param tenantId
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping("/showWord")
    @ResponseBody
    public Y9Result<Map<String, Object>> showWord(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String itembox,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String browser, @RequestParam(required = false) String positionId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, Model model) {
        try {
            Map<String, Object> map = transactionWordManager.showWord(tenantId, userId, processSerialNumber, itemId, itembox, taskId);
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                String[] pInstanceId = processInstanceId.split(",");
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, pInstanceId[0]);
                documentTitle = processModel.getTitle();
                processInstanceId = pInstanceId[0];
            }
            map.put("documentTitle", documentTitle != null ? documentTitle : "正文");
            map.put("browser", browser);
            map.put("processInstanceId", processInstanceId);
            map.put("tenantId", tenantId);
            map.put("userId", userId);
            map.put("positionId", positionId);
            Position position = positionApi.getPosition(tenantId, positionId).getData();
            OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, position.getParentId()).getData();
            model.addAttribute("currentBureauGuid", currentBureau != null ? currentBureau.getId() : "");
            return Y9Result.success(map, "获取信息成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取信息失败");
    }

}
