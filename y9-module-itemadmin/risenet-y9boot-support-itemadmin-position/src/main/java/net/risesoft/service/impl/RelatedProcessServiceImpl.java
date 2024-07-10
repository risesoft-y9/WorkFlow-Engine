package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.entity.RelatedProcess;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.RelatedProcessRepository;
import net.risesoft.service.RelatedProcessService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Service(value = "relatedProcessService")
public class RelatedProcessServiceImpl implements RelatedProcessService {

    private final RelatedProcessRepository relatedProcessRepository;

    @Override
    public Page<RelatedProcess> findAll(String parentItemId, int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "creatDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return relatedProcessRepository.findByParentItemId(parentItemId, pageable);
    }

    @Override
    public void delete(String id) {
        relatedProcessRepository.deleteById(id);
    }

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
    public void save(String parentItemId, List<String> itemIdList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        for (String itemId : itemIdList) {
            RelatedProcess oldItem = relatedProcessRepository.findByParentItemIdAndItemId(parentItemId, itemId);
            if (null == oldItem) {
                String[] array = itemId.split(":");
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
