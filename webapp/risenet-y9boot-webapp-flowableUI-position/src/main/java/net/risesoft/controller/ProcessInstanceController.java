package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/processInstance")
public class ProcessInstanceController {

    @Autowired
    private ProcessInstanceApi processInstanceApi;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/processInstanceList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> processInstanceList(@RequestParam(required = true) int page, @RequestParam(required = true) int rows, @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = processInstanceApi.processInstanceList(tenantId, positionId, title, page, rows);
        List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), list, "获取列表成功");
    }
}
