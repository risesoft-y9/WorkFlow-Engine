package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.view.ViewType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.view.ViewTypeRepository;
import net.risesoft.service.ViewTypeService;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewTypeServiceImpl implements ViewTypeService {

    private final ViewTypeRepository viewTypeRepository;

    private final ItemViewConfService itemViewConfService;

    @Override
    public ViewType findById(String id) {
        return viewTypeRepository.findById(id).orElse(null);
    }

    @Override
    public ViewType findByMark(String mark) {
        return viewTypeRepository.findByMark(mark);
    }

    @Override
    public List<ViewType> listAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        return viewTypeRepository.findAll(sort);
    }

    @SuppressWarnings("serial")
    @Override
    public Page<ViewType> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return viewTypeRepository.findAll(new Specification<ViewType>() {
            @Override
            public Predicate toPredicate(Root<ViewType> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
    }

    @Override
    @Transactional
    public void remove(String id) {
        ViewType viewType = this.findById(id);
        if (viewType != null) {
            itemViewConfService.removeByViewType(viewType.getMark());
        }
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            this.remove(id);
            viewTypeRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public ViewType save(ViewType viewType) {
        return viewTypeRepository.save(viewType);
    }

    @Override
    @Transactional
    public ViewType saveOrUpdate(ViewType viewType) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = viewType.getId();
            if (StringUtils.isNotEmpty(id)) {
                ViewType oldof = this.findById(id);
                if (null != oldof) {
                    oldof.setModifyDate(sdf.format(new Date()));
                    oldof.setName(viewType.getName());
                    oldof.setUserName(null == person ? "" : person.getName());
                    this.save(oldof);
                    return oldof;
                } else {
                    return this.save(viewType);
                }
            }

            ViewType newof = new ViewType();
            newof.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newof.setMark(viewType.getMark());
            newof.setCreateDate(sdf.format(new Date()));
            newof.setModifyDate(sdf.format(new Date()));
            newof.setName(viewType.getName());
            newof.setUserName(person.getName());
            this.save(newof);

            return newof;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("serial")
    @Override
    public Page<ViewType> search(int page, int rows, final String keyword) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return viewTypeRepository.findAll(new Specification<ViewType>() {
            @Override
            public Predicate toPredicate(Root<ViewType> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                Path<String> nameExp = root.get("name");
                if (StringUtils.isNotEmpty(keyword)) {
                    list.add(builder.like(nameExp, "%" + keyword + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
    }
}
