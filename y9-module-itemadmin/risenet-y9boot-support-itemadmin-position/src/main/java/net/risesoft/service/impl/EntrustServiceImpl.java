package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Entrust;
import net.risesoft.entity.EntrustHistory;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.EntrustItemModel;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.Position;
import net.risesoft.repository.jpa.EntrustHistoryRepository;
import net.risesoft.repository.jpa.EntrustRepository;
import net.risesoft.service.EntrustService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EntrustServiceImpl implements EntrustService {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

    private final EntrustRepository entrustRepository;

    private final EntrustHistoryRepository entrustHistoryRepository;

    private final SpmApproveItemService spmApproveItemService;

    private final PositionApi positionApi;

    @Override
    @Transactional()
    public void destroyEntrust(String id) {
        Entrust entrust = this.findOne(id);
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
    @Transactional()
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
            eh.setUpdateTime(sdf.format(new Date()));

            entrustHistoryRepository.save(eh);

            entrustRepository.delete(entrust);
        }
    }

    @Override
    @Transactional()
    public void destroyEntrustById(String id) {
        Entrust entrust = this.findOne(id);
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
    public List<Entrust> findAll() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll();
        Position pTemp;
        SpmApproveItem itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = positionApi.get(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = positionApi.get(tenantId, entrust.getOwnerId()).getData();
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
    public List<Entrust> findByAssigneeId(String assigneeId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(assigneeId);
        Position pTemp = null;
        SpmApproveItem itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = positionApi.get(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = positionApi.get(tenantId, entrust.getOwnerId()).getData();
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
    public Entrust findOne(String id) {
        Entrust entrust = entrustRepository.findById(id).orElse(null);
        if (null != entrust) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Position pTemp = positionApi.get(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = positionApi.get(tenantId, entrust.getOwnerId()).getData();
            entrust.setOwnerName(pTemp.getName());

            String itemId = entrust.getItemId();
            if (Entrust.ITEMID4ALL.equals(itemId)) {
                entrust.setItemName(Entrust.ITEMNAME4ALL);
            } else {
                SpmApproveItem itemTemp = spmApproveItemService.findById(itemId);
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
    public Entrust findOneByOwnerIdAndItemId(String ownerId, String itemId) {
        Entrust entrust = entrustRepository.findOneByOwnerIdAndItemId(ownerId, itemId);
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
    public Integer getCountByOwnerIdAndItemId(String ownerId, String itemId) {
        return entrustRepository.getCountByOwnerIdAndItemId(ownerId, itemId);
    }

    @Override
    public List<EntrustModel> getEntrustList(String positionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(positionId);
        List<EntrustModel> list = new ArrayList<EntrustModel>();
        Position pTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = positionApi.get(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = positionApi.get(tenantId, entrust.getOwnerId()).getData();
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
    public List<EntrustModel> getMyEntrustList(String positionId) {
        List<Entrust> entrustList = entrustRepository.findByAssigneeIdOrderByStartTimeDesc(positionId);
        List<EntrustModel> list = new ArrayList<EntrustModel>();
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

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> itemList(String userId, Integer page, Integer rows) {
        Map<String, Object> map = spmApproveItemService.list(page, rows);
        List<SpmApproveItem> itemList = (List<SpmApproveItem>)map.get("rows");
        List<EntrustItemModel> eimList = new ArrayList<EntrustItemModel>();
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
        isEntrust = 0 == count ? false : true;
        eim.setIsEntrust(isEntrust);
        eimList.add(eim);

        for (SpmApproveItem item : itemList) {
            eim = new EntrustItemModel();
            eim.setItemId(item.getId());
            eim.setItemName(item.getName());
            eim.setOwnerId(userId);
            count = this.getCountByOwnerIdAndItemId(userId, item.getId());
            isEntrust = 0 == count ? false : true;
            eim.setIsEntrust(isEntrust);

            eimList.add(eim);
        }

        map.put("rows", eimList);
        return map;
    }

    @Override
    public List<Entrust> list(String ownerId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Entrust> entrustList = entrustRepository.findAll(ownerId);
        Position pTemp = null;
        SpmApproveItem itemTemp = null;
        for (Entrust entrust : entrustList) {
            pTemp = positionApi.get(tenantId, entrust.getAssigneeId()).getData();
            entrust.setAssigneeName(pTemp.getName());
            pTemp = positionApi.get(tenantId, entrust.getOwnerId()).getData();
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
    @Transactional()
    public void removeEntrust(String id) {
        entrustRepository.deleteById(id);
    }

    @Override
    @Transactional()
    public Entrust saveOrUpdate(Entrust entrust) throws ParseException {
        String id = entrust.getId();
        if (StringUtils.isNotEmpty(id)) {
            Entrust old = this.findOne(id);
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
        newEntrust.setOwnerId(Y9LoginUserHolder.getPositionId());
        newEntrust.setAssigneeId(entrust.getAssigneeId());
        newEntrust.setStartTime(entrust.getStartTime());
        newEntrust.setEndTime(entrust.getEndTime());
        newEntrust.setCreatTime(sdf.format(new Date()));
        newEntrust.setUpdateTime(sdf.format(new Date()));
        entrustRepository.save(newEntrust);
        return newEntrust;
    }
}
