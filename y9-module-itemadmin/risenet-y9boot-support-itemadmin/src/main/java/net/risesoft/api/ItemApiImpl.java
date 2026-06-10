package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.platform.org.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.service.core.DocumentService;
import net.risesoft.service.core.ItemService;
import net.risesoft.y9.Y9FlowableHolder;
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

    private final ItemService itemService;

    private final PositionApi positionApi;

    private final ItemMappingConfRepository itemMappingConfRepository;

    /**
     * 根据系统名称获取事项列表
     *
     * @param systemName 系统名称
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> findAll(@RequestParam String systemName) {
        List<Item> list = itemService.findBySystemName(systemName);
        List<ItemModel> itemModelList = new ArrayList<>();
        for (Item item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemModelList.add(itemModel);
        }
        return Y9Result.success(itemModelList);
    }

    /**
     * 根据流程的定义Key查找对应的事项信息
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ItemModel> findByProcessDefinitionKey(@RequestParam String processDefinitionKey) {
        ItemModel itemModel =
            itemService.findByProcessDefinitionKey(Y9LoginUserHolder.getTenantId(), processDefinitionKey);
        return Y9Result.success(itemModel);
    }

    /**
     * 根据流程的定义Key查找所有绑定的事项信息
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> findByProcessDefinitionKeyList(@RequestParam String processDefinitionKey) {
        List<Item> list = itemService.findByProcessDefinitionKeyList(processDefinitionKey);
        List<ItemModel> itemModelList = new ArrayList<>();
        for (Item item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemModelList.add(itemModel);
        }
        return Y9Result.success(itemModelList);
    }

    /**
     * 获取当前租户所有事项列表
     *
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemModel>> getAllItem() {
        List<Item> list = itemService.list();
        List<ItemModel> itemModelList = new ArrayList<>();
        for (Item item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemModelList.add(itemModel);
        }
        return Y9Result.success(itemModelList);
    }

    /**
     * 根据事项id获取事项信息
     *
     * @param itemId 事项id
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ItemModel> getByItemId(@RequestParam String itemId) {
        Item item = itemService.findById(itemId);
        ItemModel itemModel = new ItemModel();
        if (item != null) {
            Y9BeanUtil.copyProperties(item, itemModel);
        } else {
            LOGGER.info("根据事项id未找到该事项:{}", itemId);
        }
        return Y9Result.success(itemModel);
    }

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是表单id
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getFormIdByItemId(@RequestParam String itemId, @RequestParam String processDefinitionKey) {
        String formId = documentService.getFormIdByItemId(itemId, processDefinitionKey);
        return Y9Result.success(formId);
    }

    /**
     * 获取有权限的事项列表
     *
     * @param positionId 岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemListModel>> getItemList(@RequestParam String positionId) {
        Position position = positionApi.get(Y9LoginUserHolder.getTenantId(), positionId).getData();
        Y9FlowableHolder.setPosition(position);
        List<ItemListModel> list = documentService.listItems();
        return Y9Result.success(list);
    }

    /**
     * 获取事项系统字段映射配置
     *
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return {@code Y9Result<List<ItemMappingConfModel>>} 通用请求返回对象 - data 是事项映射列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemMappingConfModel>> getItemMappingConf(@RequestParam String itemId,
        @RequestParam String mappingId) {
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
     * @return {@code Y9Result<List<ItemSystemListModel>>} 通用请求返回对象 - data 是事项系统列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemSystemListModel>> getItemSystem() {
        return Y9Result.success(itemService.getItemSystem());
    }

    /**
     * 获取个人有权限事项列表
     *
     * @param positionId 岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是新建事项列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemListModel>> getMyItemList(@RequestParam String positionId) {
        Position position = positionApi.get(Y9LoginUserHolder.getTenantId(), positionId).getData();
        Y9FlowableHolder.setPosition(position);
        List<ItemListModel> list = documentService.listMyItems();
        return Y9Result.success(list);
    }
}
