package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.RelatedProcess;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.RelatedProcessRepository;
import net.risesoft.service.RelatedProcessService;
import net.risesoft.y9.Y9LoginUserHolder;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Service(value = "relatedProcessService")
public class RelatedProcessServiceImpl implements RelatedProcessService {

    private final RelatedProcessRepository relatedProcessRepository;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<RelatedProcess> list = relatedProcessRepository.findByParentItemId(itemId);
            if (null != list && !list.isEmpty()) {
                for (RelatedProcess associated : list) {
                    RelatedProcess item = new RelatedProcess();
                    item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    item.setParentItemId(newItemId);
                    item.setCreateDate(sdf.format(new Date()));
                    item.setItemId(associated.getItemId());
                    item.setItemName(associated.getItemName());
                    item.setTenantId(associated.getTenantId());
                    relatedProcessRepository.save(item);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制关联流程绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            relatedProcessRepository.deleteByParentItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除关联流程绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        relatedProcessRepository.deleteById(id);
    }

    @Override
    public Page<RelatedProcess> findAll(String parentItemId, int page, int rows) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows);
        return relatedProcessRepository.findByParentItemIdOrderByCreateDateAsc(parentItemId, pageable);
    }

    @Override
    @Transactional
    public void save(String parentItemId, String[] itemIdList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        for (String itemId : itemIdList) {
            String[] array = itemId.split(":");
            RelatedProcess oldItem = relatedProcessRepository.findByParentItemIdAndItemId(parentItemId, array[0]);
            if (null == oldItem) {
                RelatedProcess item = new RelatedProcess();
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setItemId(array[0]);
                item.setItemName(array[1]);
                item.setParentItemId(parentItemId);
                item.setTenantId(tenantId);
                item.setCreateDate(sdf.format(new Date()));
                relatedProcessRepository.save(item);
            }
        }
    }

}
