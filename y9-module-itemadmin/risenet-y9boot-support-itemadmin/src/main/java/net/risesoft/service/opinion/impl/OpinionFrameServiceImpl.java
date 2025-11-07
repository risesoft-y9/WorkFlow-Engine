package net.risesoft.service.opinion.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.OpinionFrame;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.opinion.OpinionFrameRepository;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.opinion.OpinionFrameService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OpinionFrameServiceImpl implements OpinionFrameService {

    private final OpinionFrameRepository opinionFrameRepository;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Override
    public OpinionFrame getById(String id) {
        return opinionFrameRepository.findById(id).orElse(null);
    }

    @Override
    public OpinionFrame getByMark(String mark) {
        return opinionFrameRepository.findByMark(mark);
    }

    @Override
    public List<OpinionFrame> listAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATEDATE_KEY);
        return opinionFrameRepository.findAll(sort);
    }

    @Override
    public Page<OpinionFrame> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATEDATE_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return opinionFrameRepository.findAll(pageable);
    }

    @Override
    public Page<OpinionFrame> pageAllNotUsed(String itemId, String processDefinitionId, String taskDefKey, int page,
        int rows) {
        List<ItemOpinionFrameBind> bindList = itemOpinionFrameBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<String> markList = new ArrayList<>();
        for (ItemOpinionFrameBind bind : bindList) {
            markList.add(bind.getOpinionFrameMark());
        }
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATEDATE_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        if (markList.isEmpty()) {
            return opinionFrameRepository.findAll(pageable);
        } else {
            return opinionFrameRepository.findByMarkNotIn(markList, pageable);
        }
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            OpinionFrame opinionFrame = this.getById(id);
            if (opinionFrame != null) {
                List<ItemOpinionFrameBind> list = itemOpinionFrameBindService.listByMark(opinionFrame.getMark());
                for (ItemOpinionFrameBind bind : list) {
                    itemOpinionFrameBindService.delete(bind.getId());
                }
                opinionFrameRepository.deleteById(id);
            }
        }
    }

    @Override
    @Transactional
    public OpinionFrame save(OpinionFrame opinionFrame) {
        return opinionFrameRepository.save(opinionFrame);
    }

    @Override
    @Transactional
    public OpinionFrame saveOrUpdate(OpinionFrame opinionFrame) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = opinionFrame.getId();
            if (StringUtils.isNotEmpty(id)) {
                OpinionFrame oldof = this.getById(id);
                if (null != oldof) {
                    oldof.setName(opinionFrame.getName());
                    oldof.setUserId(null == person ? "" : person.getPersonId());
                    oldof.setUserName(null == person ? "" : person.getName());
                    opinionFrameRepository.save(oldof);
                    return oldof;
                } else {
                    return opinionFrameRepository.save(opinionFrame);
                }
            }

            OpinionFrame newof = new OpinionFrame();
            newof.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newof.setMark(opinionFrame.getMark());
            newof.setName(opinionFrame.getName());
            newof.setTenantId(person.getTenantId());
            newof.setUserId(person.getPersonId());
            newof.setUserName(person.getName());
            newof.setDeleted(0);
            opinionFrameRepository.save(newof);
            return newof;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<OpinionFrame> search(int page, int rows, final String keyword) {
        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATEDATE_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return opinionFrameRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Page<OpinionFrame> search4NotUsed(String itemId, String processDefinitionId, String taskDefKey, int page,
        int rows, final String keyword) {
        List<ItemOpinionFrameBind> bindList = itemOpinionFrameBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<String> markList = new ArrayList<>();
        for (ItemOpinionFrameBind bind : bindList) {
            markList.add(bind.getOpinionFrameMark());
        }

        Sort sort = Sort.by(Sort.Direction.ASC, ItemConsts.CREATEDATE_KEY);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        if (markList.isEmpty()) {
            if (StringUtils.isEmpty(keyword)) {
                return opinionFrameRepository.findAll(pageable);
            } else {
                return opinionFrameRepository.findByNameContaining(keyword, pageable);
            }
        } else {
            if (StringUtils.isEmpty(keyword)) {
                return opinionFrameRepository.findByMarkNotIn(markList, pageable);
            } else {
                return opinionFrameRepository.findByMarkNotInAndNameLike(markList, "%" + keyword + "%", pageable);
            }
        }
    }
}
