package net.risesoft.api;

import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.ItemInterfaceParamsBind;
import net.risesoft.entity.ItemInterfaceTaskBind;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.InterfaceInfoRepository;
import net.risesoft.repository.jpa.ItemInterfaceParamsBindRepository;
import net.risesoft.repository.jpa.ItemInterfaceTaskBindRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 事项接口绑定信息
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@RestController
@RequestMapping(value = "/services/rest/itemInterface")
public class ItemInterfaceApiImpl implements ItemInterfaceApi {

    @Autowired
    private ItemInterfaceTaskBindRepository itemInterfaceTaskBindRepository;

    @Autowired
    private InterfaceInfoRepository interfaceInfoRepository;

    @Autowired
    private ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    /**
     * 获取事项接口信息
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param processDefinitionId 流程定义id
     * @param condition 执行条件
     * @return
     */
    @Override
    public Y9Result<List<InterfaceModel>> getInterface(@NotBlank String tenantId, @NotBlank String itemId, String taskKey, String processDefinitionId, String condition) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemInterfaceTaskBind> list = itemInterfaceTaskBindRepository.findByItemIdAndTaskDefKeyAndProcessDefinitionIdAndExecuteConditionContaining(itemId, taskKey, processDefinitionId, condition);
        List<InterfaceModel> res_list = new ArrayList<InterfaceModel>();
        for (ItemInterfaceTaskBind bind : list) {
            InterfaceModel model = new InterfaceModel();
            InterfaceInfo info = interfaceInfoRepository.findById(bind.getInterfaceId()).orElse(null);
            if (info != null) {
                model.setId(info.getId());
                model.setInterfaceAddress(info.getInterfaceAddress());
                model.setInterfaceName(info.getInterfaceName());
                model.setRequestType(info.getRequestType());
                model.setAsyn(info.getAsyn());
                model.setAbnormalStop(info.getAbnormalStop());
                res_list.add(model);
            }

        }
        return Y9Result.success(res_list, "获取成功");
    }

    /**
     * 获取事项接口参数信息
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @return
     */
    @Override
    public Y9Result<List<InterfaceParamsModel>> getInterfaceParams(@NotBlank String tenantId, @NotBlank String itemId, @NotBlank String interfaceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemInterfaceParamsBind> list = itemInterfaceParamsBindRepository.findByItemIdAndInterfaceIdOrderByCreateTimeDesc(itemId, interfaceId);
        List<InterfaceParamsModel> res_list = new ArrayList<InterfaceParamsModel>();
        for (ItemInterfaceParamsBind bind : list) {
            InterfaceParamsModel model = new InterfaceParamsModel();
            model.setId(bind.getId());
            model.setBindType(bind.getBindType());
            model.setColumnName(bind.getColumnName());
            model.setParameterName(bind.getParameterName());
            model.setParameterType(bind.getParameterType());
            model.setTableName(bind.getTableName());
            res_list.add(model);

        }
        return Y9Result.success(res_list, "获取成功");
    }

}
