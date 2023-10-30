package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.DocumentService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/item")
public class ItemApiImpl implements ItemApi {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private SpmApproveItemRepository spmApproveItemRepository;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private ItemMappingConfRepository itemMappingConfRepository;

    @Override
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemModel> findAll(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<SpmApproveItem> list = spmApproveItemRepository.findAll(systemName);
        List<ItemModel> itemModelList = new ArrayList<ItemModel>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            itemModel.setId(item.getId());
            itemModel.setAccountability(item.getAccountability());
            itemModel.setExpired(item.getExpired());
            itemModel.setIsDocking(item.getIsDocking());
            itemModel.setIsOnline(item.getIsOnline());
            itemModel.setLegalLimit(item.getLegalLimit());
            itemModel.setName(item.getName());
            itemModel.setNature(item.getNature());
            itemModel.setStarter(item.getStarter());
            itemModel.setStarterId(item.getStarterId());
            itemModel.setSysLevel(item.getSysLevel());
            itemModel.setSystemName(item.getSystemName());
            itemModel.setType(item.getType());
            itemModel.setWorkflowGuid(item.getWorkflowGuid());
            itemModel.setFormType(item.getFormType());
            itemModel.setCustomItem(item.getCustomItem());
            itemModel.setDockingItemId(item.getDockingItemId());
            itemModel.setDockingSystem(item.getDockingSystem());
            itemModelList.add(itemModel);
        }
        return itemModelList;
    }

    @Override
    @GetMapping(value = "/findByProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return spmApproveItemService.findByProcessDefinitionKey(tenantId, processDefinitionKey);
    }

    @Override
    @GetMapping(value = "/getAllItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemModel> getAllItem(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<SpmApproveItem> list = spmApproveItemRepository.findAll();
        List<ItemModel> itemModelList = new ArrayList<ItemModel>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            itemModel.setId(item.getId());
            itemModel.setAccountability(item.getAccountability());
            itemModel.setExpired(item.getExpired());
            itemModel.setIsDocking(item.getIsDocking());
            itemModel.setIsOnline(item.getIsOnline());
            itemModel.setLegalLimit(item.getLegalLimit());
            itemModel.setName(item.getName());
            itemModel.setNature(item.getNature());
            itemModel.setStarter(item.getStarter());
            itemModel.setStarterId(item.getStarterId());
            itemModel.setSysLevel(item.getSysLevel());
            itemModel.setSystemName(item.getSystemName());
            itemModel.setType(item.getType());
            itemModel.setWorkflowGuid(item.getWorkflowGuid());
            itemModel.setFormType(item.getFormType());
            itemModel.setCustomItem(item.getCustomItem());
            itemModel.setDockingItemId(item.getDockingItemId());
            itemModel.setDockingSystem(item.getDockingSystem());
            itemModelList.add(itemModel);
        }
        return itemModelList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/getAllItemList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemModel> getAllItemList(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = spmApproveItemService.list();
        List<SpmApproveItem> list = (List<SpmApproveItem>)map.get("rows");
        List<ItemModel> itemList = new ArrayList<ItemModel>();
        for (SpmApproveItem item : list) {
            ItemModel itemModel = new ItemModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemList.add(itemModel);
        }
        return itemList;
    }

    @Override
    @GetMapping(value = "/getByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemModel getByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        ItemModel itemModel = new ItemModel();
        if (item != null) {
            itemModel.setId(item.getId());
            itemModel.setAccountability(item.getAccountability());
            itemModel.setExpired(item.getExpired());
            itemModel.setIsDocking(item.getIsDocking());
            itemModel.setIsOnline(item.getIsOnline());
            itemModel.setLegalLimit(item.getLegalLimit());
            itemModel.setName(item.getName());
            itemModel.setNature(item.getNature());
            itemModel.setStarter(item.getStarter());
            itemModel.setStarterId(item.getStarterId());
            itemModel.setSysLevel(item.getSysLevel());
            itemModel.setSystemName(item.getSystemName());
            itemModel.setType(item.getType());
            itemModel.setWorkflowGuid(item.getWorkflowGuid());
            itemModel.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            itemModel.setFormType(item.getFormType());
            itemModel.setIconData(item.getIconData());
            itemModel.setCustomItem(item.getCustomItem());
            itemModel.setDockingItemId(item.getDockingItemId());
            itemModel.setDockingSystem(item.getDockingSystem());
        }
        return itemModel;
    }

    @Override
    @GetMapping(value = "/getFirstItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFirstItem(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        return documentService.getFirstItem();
    }

    @Override
    @GetMapping(value = "/getFormIdByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFormIdByItemId(String tenantId, String itemId, String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return documentService.getFormIdByItemId(itemId, processDefinitionKey);
    }

    @Override
    @GetMapping(value = "/getItemList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getItemList(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = documentService.getItemList();
        return listMap;
    }

    @Override
    @GetMapping(value = "/getItemPageList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getItemList(String tenantId, String personId, Integer page, Integer rows, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, personId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = spmApproveItemService.list();
        return map;
    }

    @Override
    @GetMapping(value = "/getItemMappingConf", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemMappingConfModel> getItemMappingConf(String tenantId, String itemId, String mappingId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemMappingConf> list =
            itemMappingConfRepository.findByItemIdAndMappingIdOrderByCreateTimeDesc(itemId, mappingId);
        List<ItemMappingConfModel> itemList = new ArrayList<ItemMappingConfModel>();
        for (ItemMappingConf item : list) {
            ItemMappingConfModel itemModel = new ItemMappingConfModel();
            Y9BeanUtil.copyProperties(item, itemModel);
            itemList.add(itemModel);
        }
        return itemList;
    }

    @Override
    @GetMapping(value = "/getItemSystem", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getItemSystem(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            listMap = spmApproveItemRepository.getItemSystem();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    @Override
    @GetMapping(value = "/getMyItemList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getMyItemList(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = documentService.getMyItemList();
        return listMap;
    }

    @Override
    @GetMapping(value = "/hasProcessDefinitionByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean hasProcessDefinitionByKey(String tenantId, String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean hasKey = spmApproveItemService.hasProcessDefinitionByKey(processDefinitionKey);
        return hasKey;
    }
}
