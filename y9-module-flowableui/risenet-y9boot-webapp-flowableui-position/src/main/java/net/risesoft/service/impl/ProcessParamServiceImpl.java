package net.risesoft.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "processParamService")
@Transactional(readOnly = true)
public class ProcessParamServiceImpl implements ProcessParamService {

    @Autowired
    private Item4PositionApi item4PositionApi;

    @Autowired
    private ProcessParamApi processParamApi;

    @Resource(name = "asyncUtilService")
    private AsyncUtilService asyncUtilService;

    @Override
    public Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber, String processInstanceId, String documentTitle, String number, String level, Boolean customItem) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            ProcessParamModel processParamModel = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
            if (StringUtils.isNotBlank(processInstanceId)) {
                if (StringUtils.isNotBlank(documentTitle) && (StringUtils.isBlank(processParamModel.getTitle()) || !processParamModel.getTitle().equals(documentTitle))) {
                    asyncUtilService.updateTitle(tenantId, processInstanceId, documentTitle);
                }
            }
            ProcessParamModel pp = new ProcessParamModel();
            pp.setDeptIds("");
            pp.setBureauIds("");
            pp.setCustomLevel(level);
            pp.setCustomNumber(number);
            pp.setItemId(itemId);
            pp.setItemName(item.getName());
            pp.setProcessInstanceId(processInstanceId);
            pp.setProcessSerialNumber(processSerialNumber);
            pp.setSystemName(item.getSystemName());
            pp.setSystemCnName(item.getSysLevel());
            pp.setTitle(documentTitle);
            pp.setSponsorGuid(processParamModel != null ? processParamModel.getSponsorGuid() : "");
            pp.setSended(processParamModel != null ? processParamModel.getSended() : "");
            pp.setStartor(processParamModel != null ? processParamModel.getStartor() : "");
            pp.setStartorName(processParamModel != null ? processParamModel.getStartorName() : "");
            pp.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            pp.setCustomItem(processParamModel != null ? processParamModel.getCustomItem() : customItem);
            StringBuffer searchTerm = new StringBuffer();
            searchTerm.append(documentTitle).append("|").append(number).append("|").append(level).append("|").append(item.getName());
            pp.setSearchTerm(searchTerm.toString());
            processParamApi.saveOrUpdate(tenantId, pp);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
