package net.risesoft.controller.mobile.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 对接系统接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/v1/sysDocking")
@Slf4j
public class MobileV1SystemDockingController {

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private FormDataApi formDataApi;

    @Autowired
    private Item4PositionApi item4PositionApi;

    @Autowired
    private Document4PositionApi document4PositionApi;

    @Autowired
    private ProcessParamService processParamService;

    /**
     * 对接系统提交接口
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param mappingId 对接系统标识
     * @param positionId 岗位id
     * @param userChoice 接收人
     * @param formJsonData 表单数据
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/forwarding")
    public Y9Result<Map<String, Object>> forwarding(@RequestParam String tenantId, @RequestParam String itemId, @RequestParam String mappingId, @RequestParam String positionId, @RequestParam String userChoice, @RequestParam String formJsonData, HttpServletResponse response) throws Exception {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Position position = positionApi.getPosition(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            Map<String, Object> mapForm = Y9JsonUtil.readValue(formJsonData, Map.class);
            List<ItemMappingConfModel> list = item4PositionApi.getItemMappingConf(tenantId, itemId, mappingId);
            Map<String, Object> formMap = new HashMap<String, Object>(16);
            for (ItemMappingConfModel mapping : list) {
                String text = mapForm.get(mapping.getMappingName()).toString();
                formMap.put(mapping.getColumnName(), text);
            }
            String title = formMap.get("title").toString();
            String number = formMap.get("number").toString();
            String level = formMap.get("level").toString();
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, formMap.get("guid").toString(), "", title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            formJsonData = Y9JsonUtil.writeValueAsString(formMap);
            String tempIds = item4PositionApi.getFormIdByItemId(tenantId, itemId, item.getWorkflowGuid());
            if (StringUtils.isNotBlank(tempIds)) {
                List<String> tempIdList = Y9Util.stringToList(tempIds, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : tempIdList) {
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            Map<String, Object> map = document4PositionApi.startProcess(tenantId, positionId, itemId, formMap.get("guid").toString(), item.getWorkflowGuid(), userChoice);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, "提交成功");
            }
            return Y9Result.failure(map.get("msg").toString());
        } catch (Exception e) {
            return Y9Result.failure("提交失败");
        }
    }

}
