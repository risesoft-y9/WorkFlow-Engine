package net.risesoft.service.view.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.view.ViewType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.view.ViewTypeRepository;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.service.view.ViewTypeService;
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
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATETIME_KEY);
        return viewTypeRepository.findAll(sort);
    }

    @Override
    public Page<ViewType> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return viewTypeRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        Arrays.stream(ids).forEach(id -> {
            ViewType viewType = viewTypeRepository.findById(id).orElse(null);
            if (viewType != null) {
                viewTypeRepository.delete(viewType);
                itemViewConfService.removeByViewType(viewType.getMark());
            }
        });
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
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = viewType.getId();
            if (StringUtils.isNotEmpty(id)) {
                ViewType oldViewType = this.findById(id);
                if (null != oldViewType) {
                    oldViewType.setName(viewType.getName());
                    oldViewType.setUserName(null == person ? "" : person.getName());
                    viewTypeRepository.save(oldViewType);
                    return oldViewType;
                } else {
                    return viewTypeRepository.save(viewType);
                }
            }

            ViewType newViewType = new ViewType();
            newViewType.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newViewType.setMark(viewType.getMark());
            newViewType.setName(viewType.getName());
            newViewType.setUserName(person.getName());
            viewTypeRepository.save(newViewType);
            return newViewType;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<ViewType> search(int page, int rows, final String name) {
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return viewTypeRepository.findByNameContaining(name, pageable);
    }
}
