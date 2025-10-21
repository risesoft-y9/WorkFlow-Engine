package net.risesoft.service.entrust.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.Item;
import net.risesoft.entity.entrust.Entrust;
import net.risesoft.entity.entrust.EntrustHistory;
import net.risesoft.enums.EntrustUseEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.EntrustItemModel;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.repository.entrust.EntrustHistoryRepository;
import net.risesoft.repository.entrust.EntrustRepository;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.entrust.EntrustService;
import net.risesoft.util.Y9DateTimeUtils;
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

    private final ItemService itemService;

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public void destroyEntrust(String id) {
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
            eh.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        } else {
            entrustRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void destroyEntrust(String ownerId, String itemId) {
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
            eh.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        }
    }

    @Override
    @Transactional
    public void destroyEntrustById(String id) {
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
            eh.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
            entrustHistoryRepository.save(eh);
            entrustRepository.delete(entrust);
        }
    }

    @Override
    public Entrust findOneByOwnerIdAndItemId(String ownerId, String itemId) {
        Entrust entrust = entrustRepository.findOneByOwnerIdAndItemId(ownerId, itemId);
        if (entrust != null) {
            /**
             * 判断是否使用
             */
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
            }
        }
        return entrust;
    }

    @Override
    public Entrust findOneByOwnerIdAndItemIdAndTime(String ownerId, String itemId, String dateTime) {
        Entrust entrust = entrustRepository.findOneByOwnerIdAndItemIdAndTime(ownerId, itemId, dateTime);
        if (null == entrust || null == entrust.getId()) {
            entrust = entrustRepository.findOneByOwnerIdAndItemIdAndTime(ownerId, ItemConsts.ITEMID4ALL, dateTime);
        }
        return entrust;
    }

    @Override
    public Entrust getById(String id) {
        Entrust entrust = entrustRepository.findById(id).orElse(null);
        if (null != entrust) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            OrgUnit pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());

            String itemId = entrust.getItemId();
            if (ItemConsts.ITEMID4ALL.equals(itemId)) {
                entrust.setItemName(ItemConsts.ITEMNAME4ALL);
            } else {
                Item itemTemp = itemService.findById(itemId);
                if (null == itemTemp || StringUtils.isEmpty(itemTemp.getId())) {
                    entrust.setItemName("此事项已删除");
                } else {
                    entrust.setItemName(itemTemp.getName());
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(ownerId);
        OrgUnit pTemp;
        Item itemTemp;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = itemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("事项不存在");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<Entrust> listAll() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll();
        OrgUnit pTemp;
        Item itemTemp;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = itemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("当前对应的事项不存在");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<Entrust> listByAssigneeId(String assigneeId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(assigneeId);
        OrgUnit pTemp;
        Item itemTemp;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            String itemId = entrust.getItemId();
            if ("ALL".equals(itemId)) {
                entrust.setItemName("所有事项");
            } else {
                itemTemp = itemService.findById(entrust.getItemId());
                if (null != itemTemp) {
                    entrust.setItemName(itemTemp.getName());
                } else {
                    entrust.setItemName("不存在此事项");
                }
            }
            /**
             * 判断是否使用
             */
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entrustList;
    }

    @Override
    public List<EntrustModel> listEntrustByUserId(String orgUnitId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(orgUnitId);
        List<EntrustModel> list = new ArrayList<>();
        OrgUnit pTemp;
        for (Entrust entrust : entrustList) {
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
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
        Page<Item> pageList = itemService.page(page, rows);
        List<Item> itemList = pageList.getContent();
        List<EntrustItemModel> eimList = new ArrayList<>();
        EntrustItemModel eim;
        boolean isEntrust;
        Integer count;
        /**
         * 针对所有事项
         */
        eim = new EntrustItemModel();
        eim.setItemId(ItemConsts.ITEMID4ALL);
        eim.setItemName(ItemConsts.ITEMNAME4ALL);
        eim.setOwnerId(userId);
        count = this.getCountByOwnerIdAndItemId(userId, ItemConsts.ITEMID4ALL);
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
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(orgUnitId);
        List<EntrustModel> list = new ArrayList<>();
        for (Entrust entrust : entrustList) {
            entrust.setUsed(EntrustUseEnum.TODO.getValue());
            String startTime = entrust.getStartTime();
            String endTime = entrust.getEndTime();
            Date startTime4Date;
            Date endTime4Date;
            Date currentDate;
            try {
                startTime4Date = Y9DateTimeUtils.parseDate(startTime);
                endTime4Date = Y9DateTimeUtils.parseDate(endTime);
                currentDate = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatCurrentDate());
                boolean b =
                    startTime4Date.getTime() == currentDate.getTime() || endTime4Date.getTime() == currentDate.getTime()
                        || (currentDate.after(startTime4Date) && currentDate.before(endTime4Date));
                if (b) {
                    entrust.setUsed(EntrustUseEnum.DOING.getValue());
                } else if (currentDate.after(endTime4Date)) {
                    entrust.setUsed(EntrustUseEnum.DONE.getValue());
                }
            } catch (Exception e) {
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
        String id = entrust.getId();
        if (StringUtils.isNotEmpty(id)) {
            Entrust old = this.getById(id);
            old.setItemId(entrust.getItemId());
            old.setAssigneeId(entrust.getAssigneeId());
            old.setStartTime(entrust.getStartTime());
            old.setEndTime(entrust.getEndTime());
            old.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
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
        newEntrust.setCreatTime(Y9DateTimeUtils.formatCurrentDateTime());
        newEntrust.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
        entrustRepository.save(newEntrust);
        return newEntrust;
    }
}
