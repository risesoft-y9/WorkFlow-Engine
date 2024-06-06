package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.LinkInfo;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemLinkBindRepository;
import net.risesoft.repository.jpa.ItemLinkRoleRepository;
import net.risesoft.repository.jpa.LinkInfoRepository;
import net.risesoft.service.LinkInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class LinkInfoServiceImpl implements LinkInfoService {

    private final LinkInfoRepository linkInfoRepository;

    private final ItemLinkBindRepository itemLinkBindRepository;

    private final ItemLinkRoleRepository itemLinkRoleRepository;

    @SuppressWarnings("serial")
    @Override
    public List<LinkInfo> findAll(String linkName, String linkUrl) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return linkInfoRepository.findAll(new Specification<LinkInfo>() {
            @Override
            public Predicate toPredicate(Root<LinkInfo> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(linkName)) {
                    list.add(builder.like(root.get("linkName"), "%" + linkName + "%"));
                }
                if (StringUtils.isNotBlank(linkUrl)) {
                    list.add(builder.like(root.get("linkUrl"), "%" + linkUrl + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @Override
    public LinkInfo findById(String id) {
        return linkInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void remove(String id) {
        linkInfoRepository.deleteById(id);
        List<ItemLinkBind> list = itemLinkBindRepository.findByLinkIdOrderByCreateTimeDesc(id);
        for (ItemLinkBind bind : list) {
            itemLinkBindRepository.delete(bind);
            itemLinkRoleRepository.deleteByItemLinkId(bind.getId());
        }
    }

    @Override
    @Transactional
    public void saveOrUpdate(LinkInfo linkInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = linkInfo.getId();
        if (StringUtils.isNotBlank(id)) {
            LinkInfo item = this.findById(id);
            if (null != item) {
                item.setLinkName(linkInfo.getLinkName());
                item.setLinkUrl(linkInfo.getLinkUrl());
                return;
            }
        }
        LinkInfo item = new LinkInfo();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setLinkName(linkInfo.getLinkName());
        item.setLinkUrl(linkInfo.getLinkUrl());
        item.setCreateTime(sdf.format(new Date()));
        linkInfoRepository.saveAndFlush(item);
    }

}
