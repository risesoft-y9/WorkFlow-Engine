package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.InterfaceResponseParams;
import net.risesoft.entity.ItemInterfaceParamsBind;
import net.risesoft.entity.ItemInterfaceTaskBind;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.InterfaceInfoRepository;
import net.risesoft.repository.jpa.InterfaceResponseParamsRepository;
import net.risesoft.repository.jpa.ItemInterfaceParamsBindRepository;
import net.risesoft.repository.jpa.ItemInterfaceTaskBindRepository;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 事项接口绑定信息
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemInterface", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemInterfaceApiImpl implements ItemInterfaceApi {

    private final ItemInterfaceTaskBindRepository itemInterfaceTaskBindRepository;

    private final InterfaceInfoRepository interfaceInfoRepository;

    private final InterfaceResponseParamsRepository interfaceResponseParamsRepository;

    private final ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    /**
     * 获取事项绑定的接口信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param processDefinitionId 流程定义id
     * @param condition 执行条件
     * @return {@code Y9Result<List<InterfaceModel>>} 通用请求返回对象 - data 是接口绑定参数列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<InterfaceModel>> getInterface(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String taskKey, @RequestParam String processDefinitionId, @RequestParam String condition) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemInterfaceTaskBind> list = itemInterfaceTaskBindRepository
            .findByItemIdAndTaskDefKeyAndProcessDefinitionIdAndExecuteConditionContaining(itemId, taskKey,
                processDefinitionId, condition);
        List<InterfaceModel> resList = new ArrayList<>();
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
                resList.add(model);
            }

        }
        return Y9Result.success(resList, "获取成功");
    }

    /**
     * 获取事项绑定的接口参数信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @return {@code Y9Result<List<InterfaceParamsModel>>} 通用请求返回对象 - data 是接口绑定参数列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<InterfaceParamsModel>> getInterfaceParams(@RequestParam String tenantId,
        @RequestParam String itemId, @RequestParam String interfaceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemInterfaceParamsBind> list =
            itemInterfaceParamsBindRepository.findByItemIdAndInterfaceIdOrderByCreateTimeDesc(itemId, interfaceId);
        List<InterfaceParamsModel> resList = new ArrayList<>();
        for (ItemInterfaceParamsBind bind : list) {
            InterfaceParamsModel model = new InterfaceParamsModel();
            model.setId(bind.getId());
            model.setBindType(bind.getBindType());
            model.setColumnName(bind.getColumnName());
            model.setParameterName(bind.getParameterName());
            model.setParameterType(bind.getParameterType());
            model.setTableName(bind.getTableName());
            model.setFileType("");
            if (ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue().equals(bind.getBindType())) {
                List<InterfaceResponseParams> plist =
                    interfaceResponseParamsRepository.findByParameterName(bind.getParameterName());
                if (plist != null && plist.size() > 0) {
                    model.setFileType(plist.get(0).getFileType());
                }
            }
            resList.add(model);

        }
        return Y9Result.success(resList, "获取成功");
    }

}
