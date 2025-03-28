package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.DocumentService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/item", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemApiImpl implements ItemApi {

    private final DocumentService documentService;

    private final SpmApproveItemService spmApproveItemService;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final OrgUnitApi orgUnitApi;

    private final ItemMappingConfRepository itemMappingConfRepository;

    /**
     * 根据系统名称获取事项列表
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> findAll(@RequestParam String tenantId, @RequestParam String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SpmApproveItem> list = spmApproveItemRepository.findAll(systemName);
        List<ItemModel> itemModelList = new ArrayList<>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemModelList.add(itemModel);
        }
        return Y9Result.success(itemModelList);
    }

    /**
     * 根据流程的定义Key查找对应的事项信息
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ItemModel> findByProcessDefinitionKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ItemModel itemModel = spmApproveItemService.findByProcessDefinitionKey(tenantId, processDefinitionKey);
        return Y9Result.success(itemModel);
    }

    /**
     * 获取当前租户所有事项列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> getAllItem(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SpmApproveItem> list = spmApproveItemRepository.findAll();
        List<ItemModel> itemModelList = new ArrayList<>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemModelList.add(itemModel);
        }
        return Y9Result.success(itemModelList);
    }

    /**
     * 获取所有事项信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> getAllItemList(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SpmApproveItem> list = spmApproveItemService.list();
        List<ItemModel> itemList = new ArrayList<>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemList.add(itemModel);
        }
        return Y9Result.success(itemList);
    }

    /**
     * 根据事项id获取事项信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ItemModel> getByItemId(@RequestParam String tenantId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        ItemModel itemModel = new ItemModel();
        if (item != null) {
            Y9BeanUtil.copyProperties(item, itemModel);
        } else {
            LOGGER.info("根据事项id未找到该事项:" + itemId);
        }
        return Y9Result.success(itemModel);
    }

    /**
     * 获取有权限的第一个事项id
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是事项id
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getFirstItem(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        String itemId = documentService.getFirstItem();
        return Y9Result.success(itemId);
    }

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是表单id
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getFormIdByItemId(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String formId = documentService.getFormIdByItemId(itemId, processDefinitionKey);
        return Y9Result.success(formId);
    }

    /**
     * 获取有权限的事项列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemListModel>> getItemList(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        List<ItemListModel> list = documentService.listItems();
        return Y9Result.success(list);
    }

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return {@code Y9Result<List<ItemMappingConfModel>>} 通用请求返回对象 - data 是事项映射列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemMappingConfModel>> getItemMappingConf(@RequestParam String tenantId,
        @RequestParam String itemId, @RequestParam String mappingId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemMappingConf> list =
            itemMappingConfRepository.findByItemIdAndMappingIdOrderByCreateTimeDesc(itemId, mappingId);
        List<ItemMappingConfModel> itemList = new ArrayList<>();
        for (ItemMappingConf item : list) {
            ItemMappingConfModel itemModel = new ItemMappingConfModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemList.add(itemModel);
        }
        return Y9Result.success(itemList);
    }

    /**
     * 获取事项系统列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemSystemListModel>>} 通用请求返回对象 - data 是事项系统列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemSystemListModel>> getItemSystem(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> list = spmApproveItemRepository.getItemSystem();
        List<ItemSystemListModel> itemList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            ItemSystemListModel itemSystemListModel = new ItemSystemListModel();
            itemSystemListModel.setSystemName(map.get("systemName").toString());
            itemSystemListModel.setSysLevel(map.get("sysLevel").toString());
            itemList.add(itemSystemListModel);
        }
        return Y9Result.success(itemList);
    }

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId 租户Id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是新建事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemListModel>> getMyItemList(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        List<ItemListModel> list = documentService.listMyItems();
        return Y9Result.success(list);
    }

    /**
     * 判断流程定义key是否绑定事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> hasProcessDefinitionByKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean hasProcessDefinition = spmApproveItemService.hasProcessDefinitionByKey(processDefinitionKey);
        return Y9Result.success(hasProcessDefinition);
    }
}
