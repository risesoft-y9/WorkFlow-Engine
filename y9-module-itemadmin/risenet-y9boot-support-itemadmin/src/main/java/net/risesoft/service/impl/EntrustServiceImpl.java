package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.entrust.Entrust;
import net.risesoft.entity.entrust.EntrustHistory;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.EntrustItemModel;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.repository.jpa.EntrustHistoryRepository;
import net.risesoft.repository.jpa.EntrustRepository;
import net.risesoft.service.EntrustService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EntrustServiceImpl implements EntrustService {

    private final EntrustRepository entrustRepository;

    private final EntrustHistoryRepository entrustHistoryRepository;

    private final SpmApproveItemService spmApproveItemService;

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public void destroyEntrust(String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Entrust entrust = this.getById(id);
        if (0 != entrust.getUsed()) {
            EntrustHistory eh = new EntrustHistory();
            eh.setId(entrust.getId());
            eh.setItemId(entrust.getItemId());
            eh.setOwnerId(entrust.getOwnerId());
            eh.setAssigneeId(entrust.getAssigneeId());
            eh.setStartTime(entrust.getStartTime());
            eh.setEndTime(entrust.getEndTime());
            eh.setCreatTime(entrust.getCreatTime());
            eh.setUpdateTime(sdf.format(new Date()));
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        } else {
            entrustRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void destroyEntrust(String ownerId, String itemId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Entrust entrust = this.findOneByOwnerIdAndItemId(ownerId, itemId);
        if (0 != entrust.getUsed()) {
            EntrustHistory eh = new EntrustHistory();
            eh.setId(entrust.getId());
            eh.setItemId(entrust.getItemId());
            eh.setOwnerId(entrust.getOwnerId());
            eh.setAssigneeId(entrust.getAssigneeId());
            eh.setStartTime(entrust.getStartTime());
            eh.setEndTime(entrust.getEndTime());
            eh.setCreatTime(entrust.getCreatTime());
            eh.setUpdateTime(sdf.format(new Date()));
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        }
    }

    @Override
    @Transactional
    public void destroyEntrustById(String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Entrust entrust = this.getById(id);
        if (0 != entrust.getUsed()) {
            EntrustHistory eh = new EntrustHistory();
            eh.setId(entrust.getId());
            eh.setItemId(entrust.getItemId());
            eh.setOwnerId(entrust.getOwnerId());
            eh.setAssigneeId(entrust.getAssigneeId());
            eh.setStartTime(entrust.getStartTime());
            eh.setEndTime(entrust.getEndTime());
            eh.setCreatTime(entrust.getCreatTime());
            eh.setUpdateTime(sdf.format(new Date()));
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        }
    }

    @Override
    public Entrust findOneByOwnerIdAndItemId(String ownerId, String itemId) {
        Entrust entrust = entrustRepository.findOneByOwnerIdAndItemId(ownerId, itemId);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        if (entrust != null) {
            /**
             * 判断是否使用
             */
            entrust.setUsed(Entrust.NOTUSED);
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
            }
        }
        return entrust;
    }

    @Override
    public Entrust findOneByOwnerIdAndItemIdAndTime(String ownerId, String itemId, String dateTime) {
        Entrust entrust = entrustRepository.findOneByOwnerIdAndItemIdAndTime(ownerId, itemId, dateTime);
        if (null == entrust || null == entrust.getId()) {
            entrust = entrustRepository.findOneByOwnerIdAndItemIdAndTime(ownerId, Entrust.ITEMID4ALL, dateTime);
        }
        return entrust;
    }

    @Override
    public Entrust getById(String id) {
        Entrust entrust = entrustRepository.findById(id).orElse(null);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        if (null != entrust) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            OrgUnit pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());

            String itemId = entrust.getItemId();
            if (Entrust.ITEMID4ALL.equals(itemId)) {
                entrust.setItemName(Entrust.ITEMNAME4ALL);
            } else {
                Item itemTemp = spmApproveItemService.findById(itemId);
                if (null == itemTemp || StringUtils.isEmpty(itemTemp.getId())) {
                    entrust.setItemName("此事项已删除");
                } else {
                    entrust.setItemName(itemTemp.getName());
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(Entrust.NOTUSED);
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return entrust;
    }

    @Override
    public Integer getCountByOwnerIdAndItemId(String ownerId, String itemId) {
        return entrustRepository.getCountByOwnerIdAndItemId(ownerId, itemId);
    }

    @Override
    public List<Entrust> list(String ownerId) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(ownerId);
        OrgUnit pTemp = null;
        Item itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = spmApproveItemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("事项已删除");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(Entrust.NOTUSED);
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<Entrust> listAll() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll();
        OrgUnit pTemp;
        Item itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = spmApproveItemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("事项已删除");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(Entrust.NOTUSED);
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<Entrust> listByAssigneeId(String assigneeId) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(assigneeId);
        OrgUnit pTemp = null;
        Item itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = spmApproveItemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("事项已删除");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(Entrust.NOTUSED);
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<EntrustModel> listEntrustByUserId(String orgUnitId) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(orgUnitId);
        List<EntrustModel> list = new ArrayList<>();
        OrgUnit pTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            entrust.setUsed(Entrust.NOTUSED);// 判断是否使用
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            EntrustModel model = new EntrustModel();
            Y9BeanUtil.copyProperties(entrust, model);
            list.add(model);
        }
        return list;
    }

    @Override
    public List<EntrustItemModel> listItem(String userId, Integer page, Integer rows) {
        Page<Item> pageList = spmApproveItemService.page(page, rows);
        List<Item> itemList = pageList.getContent();
        List<EntrustItemModel> eimList = new ArrayList<>();
        EntrustItemModel eim = null;
        Boolean isEntrust = false;
        Integer count = 0;
        /**
         * 针对所有事项
         */
        eim = new EntrustItemModel();
        eim.setItemId(Entrust.ITEMID4ALL);
        eim.setItemName(Entrust.ITEMNAME4ALL);
        eim.setOwnerId(userId);
        count = this.getCountByOwnerIdAndItemId(userId, Entrust.ITEMID4ALL);
        isEntrust = 0 != count;
        eim.setIsEntrust(isEntrust);
        eimList.add(eim);
        for (Item item : itemList) {
            eim = new EntrustItemModel();
            eim.setItemId(item.getId());
            eim.setItemName(item.getName());
            eim.setOwnerId(userId);
            count = this.getCountByOwnerIdAndItemId(userId, item.getId());
            isEntrust = 0 != count;
            eim.setIsEntrust(isEntrust);

            eimList.add(eim);
        }
        return eimList;
    }

    @Override
    public List<EntrustModel> listMyEntrust(String orgUnitId) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(orgUnitId);
        List<EntrustModel> list = new ArrayList<>();
        for (Entrust entrust : entrustList) {
            entrust.setUsed(Entrust.NOTUSED);// 判断是否使用
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date = null;
            Date endTime4Date = null;
            Date currentDate = null;
            try {
                startTime4Date = sdf2.parse(startTime);
                endTime4Date = sdf2.parse(endTime);
                currentDate = sdf2.parse(sdf2.format(new Date()));
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(Entrust.USING);
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(Entrust.USED);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            EntrustModel model = new EntrustModel();
            Y9BeanUtil.copyProperties(entrust, model);
            list.add(model);
        }
        return list;
    }

    @Override
    @Transactional
    public void removeEntrust(String id) {
        entrustRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Entrust saveOrUpdate(Entrust entrust) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = entrust.getId();
        if (StringUtils.isNotEmpty(id)) {
            Entrust old = this.getById(id);
            old.setItemId(entrust.getItemId());
            old.setAssigneeId(entrust.getAssigneeId());
            old.setStartTime(entrust.getStartTime());
            old.setEndTime(entrust.getEndTime());
            old.setUpdateTime(sdf.format(new Date()));
            entrustRepository.save(old);
            return old;
        }
        Entrust newEntrust = new Entrust();
        newEntrust.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newEntrust.setItemId(entrust.getItemId());
        newEntrust.setOwnerId(Y9LoginUserHolder.getOrgUnitId());
        newEntrust.setAssigneeId(entrust.getAssigneeId());
        newEntrust.setStartTime(entrust.getStartTime());
        newEntrust.setEndTime(entrust.getEndTime());
        newEntrust.setCreatTime(sdf.format(new Date()));
        newEntrust.setUpdateTime(sdf.format(new Date()));
        entrustRepository.save(newEntrust);
        return newEntrust;
    }
}
