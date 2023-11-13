package net.risesoft.controller.mobile;

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

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Person;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
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
@RequestMapping("/mobile/sysDocking")
@Slf4j
public class MobileSystemDockingController {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private ProcessParamService processParamService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/forwarding")
    public void forwarding(@RequestParam String tenantId, @RequestParam String itemId, @RequestParam String mappingId,
        @RequestParam String userId, @RequestParam String userChoice, @RequestParam String formJsonData,
        HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "提交成功");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            Map<String, Object> mapForm = Y9JsonUtil.readValue(formJsonData, Map.class);
            List<ItemMappingConfModel> list = itemManager.getItemMappingConf(tenantId, itemId, mappingId);
            Map<String, Object> formMap = new HashMap<String, Object>(16);
            for (ItemMappingConfModel mapping : list) {
                String text = mapForm.get(mapping.getMappingName()).toString();
                formMap.put(mapping.getColumnName(), text);
            }
            String title = formMap.get("title").toString();
            String number = formMap.get("number").toString();
            String level = formMap.get("level").toString();
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, formMap.get("guid").toString(), "", title,
                number, level, false);
            if (!map1.isSuccess()) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "发生异常");
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            }
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            formJsonData = Y9JsonUtil.writeValueAsString(formMap);
            String tempIds = itemManager.getFormIdByItemId(tenantId, itemId, item.getWorkflowGuid());
            if (StringUtils.isNotBlank(tempIds)) {
                List<String> tempIdList = Y9Util.stringToList(tempIds, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : tempIdList) {
                    formDataManager.saveFormData(tenantId, formId, formJsonData);
                }
            }
            map = documentManager.startProcess(tenantId, userId, itemId, formMap.get("guid").toString(),
                item.getWorkflowGuid(), userChoice);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "提交失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

}
