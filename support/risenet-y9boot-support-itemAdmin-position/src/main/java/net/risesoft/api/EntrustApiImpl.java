package net.risesoft.api;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.EntrustApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.Entrust;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.service.EntrustDetailService;
import net.risesoft.service.EntrustService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 出差委托接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/entrust")
public class EntrustApiImpl implements EntrustApi {

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private EntrustDetailService entrustDetailService;

    /**
     * 销假:删除ownerId所有的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/destroyEntrust", produces = MediaType.APPLICATION_JSON_VALUE)
    public void destroyEntrust(String tenantId, String userId, String ownerId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            entrustService.destroyEntrust(ownerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销假:根据唯一标示删除正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/destroyEntrustById", produces = MediaType.APPLICATION_JSON_VALUE)
    public void destroyEntrustById(String tenantId, String userId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            entrustService.destroyEntrustById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销假:删除某个人的某个事项的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/destroyEntrustByOwnerIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public void destroyEntrustByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            entrustService.destroyEntrust(ownerId, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/findOneByOwnerIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntrustModel findOneByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        EntrustModel entrustModel = null;
        Entrust entrust = entrustService.findOneByOwnerIdAndItemId(ownerId, itemId);
        if (null != entrust) {
            Person assignee = personManager.getPerson(tenantId, entrust.getAssigneeId());
            Person owner = personManager.getPerson(tenantId, entrust.getOwnerId());

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
            }

            entrust.setAssigneeName(assignee.getName());
            entrust.setOwnerName(owner.getName());
            entrust.setItemName(itemModel.getName());
            entrustModel = ItemAdminModelConvertUtil.entrust2Model(entrust);
        } else {
            entrustModel = new EntrustModel();
        }
        return entrustModel;
    }

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @param currentTime 当前时间
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/findOneByOwnerIdAndItemIdAndTime", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntrustModel findOneByOwnerIdAndItemIdAndTime(String tenantId, String ownerId, String itemId,
        String currentTime) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Entrust entrust = entrustService.findOneByOwnerIdAndItemIdAndTime(ownerId, itemId, currentTime);
        EntrustModel entrustModel = null;
        if (null != entrust) {
            entrustModel = ItemAdminModelConvertUtil.entrust2Model(entrust);
        } else {
            entrustModel = new EntrustModel();
        }
        return entrustModel;
    }

    /**
     * 通过唯一标示获取委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntrustModel getById(String tenantId, String userId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Entrust entrust = entrustService.findOne(id);
        EntrustModel entrustModel = ItemAdminModelConvertUtil.entrust2Model(entrust);
        return entrustModel;
    }

    /**
     * 获取任务委托人id
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return String
     */
    @Override
    @GetMapping(value = "/getEntrustOwnerId", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntrustOwnerId(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustDetailService.getEntrustOwnerId(taskId);
    }

    /**
     * 获取事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param page 页码
     * @param rows 条数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getItemList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getItemList(String tenantId, String userId, String ownerId, Integer page, Integer rows)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = entrustService.itemList(ownerId, page, rows);
        return map;
    }

    /**
     * 查询任务是否有委托
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return boolean
     */
    @Override
    @GetMapping(value = "/haveEntrustDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean haveEntrustDetail(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustDetailService.haveEntrustDetailByTaskId(taskId);
    }

    /**
     * 删除id出差委托，不会放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/removeEntrust", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeEntrust(String tenantId, String userId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            entrustService.removeEntrust(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存委托详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param ownerId 委托人员id
     * @param assigneeId 受让人员id
     */
    @Override
    @PostMapping(value = "/saveEntrustDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveEntrustDetail(String tenantId, String processInstanceId, String taskId, String ownerId,
        String assigneeId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        entrustDetailService.save(processInstanceId, taskId, ownerId, assigneeId);
    }

    /**
     * 保存或者更新委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param entrustModel 实体类（EntrustModel）
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdate(String tenantId, String userId, @RequestBody EntrustModel entrustModel) throws Exception {
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        Entrust entrust = ItemAdminModelConvertUtil.entrustModel2Entrust(entrustModel);
        try {
            entrustService.saveOrUpdate(entrust);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
