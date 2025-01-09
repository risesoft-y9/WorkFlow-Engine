package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ProcessParamRepository;
import net.risesoft.service.ProcessParamService;
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
public class ProcessParamServiceImpl implements ProcessParamService {

    private final ProcessParamRepository processParamRepository;

    private final VariableApi variableApi;

    private final SpmApproveItemService spmApproveItemService;

    @Override
    @Transactional
    public void deleteByPprocessInstanceId(String processInstanceId) {
        processParamRepository.deleteByPprocessInstanceId(processInstanceId);
    }

    @Override
    @Transactional
    public void deleteByProcessSerialNumber(String processSerialNumber) {
        processParamRepository.deleteByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public ProcessParam findByProcessInstanceId(String processInstanceId) {
        return processParamRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public ProcessParam findByProcessSerialNumber(String processSerialNumber) {
        return processParamRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    @Transactional
    public void initCallActivity(String processSerialNumber, String subProcessSerialNumber, String subProcessInstanceId,
        String itemId, String itemName) {
        ProcessParam parent = processParamRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != parent) {
            ProcessParam newProcessParam = new ProcessParam();
            Y9BeanUtil.copyProperties(parent, newProcessParam);
            newProcessParam.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newProcessParam.setProcessSerialNumber(subProcessSerialNumber);
            newProcessParam.setProcessInstanceId(subProcessInstanceId);
            newProcessParam.setItemId(itemId);
            newProcessParam.setItemName(itemName);
            processParamRepository.save(newProcessParam);
        }
    }

    @Override
    @Transactional
    public ProcessParam saveOrUpdate(ProcessParam processParam) {
        String processSerialNumber = processParam.getProcessSerialNumber();
        ProcessParam oldProcessParam = processParamRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != oldProcessParam) {
            if (StringUtils.isNotBlank(processParam.getCustomLevel())) {
                oldProcessParam.setCustomLevel(processParam.getCustomLevel());
            }
            if (StringUtils.isNotBlank(processParam.getCustomNumber())) {
                oldProcessParam.setCustomNumber(processParam.getCustomNumber());
            }
            if (StringUtils.isNotBlank(processParam.getTitle())) {
                oldProcessParam.setTitle(processParam.getTitle());
            }
            oldProcessParam.setSearchTerm(processParam.getSearchTerm());
            oldProcessParam.setTodoTaskUrlPrefix(processParam.getTodoTaskUrlPrefix());
            oldProcessParam.setSystemCnName(processParam.getSystemCnName());
            if (StringUtils.isNotBlank(processParam.getBureauIds())) {
                oldProcessParam.setBureauIds(processParam.getBureauIds());
            }
            if (StringUtils.isNotBlank(processParam.getDeptIds())) {
                oldProcessParam.setDeptIds(processParam.getDeptIds());
            }
            oldProcessParam.setIsSendSms(processParam.getIsSendSms());
            oldProcessParam.setIsShuMing(processParam.getIsShuMing());
            oldProcessParam.setSmsContent(processParam.getSmsContent());
            oldProcessParam.setSmsPersonId(processParam.getSmsPersonId());
            oldProcessParam.setCompleter(processParam.getCompleter());
            if (StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                oldProcessParam.setProcessInstanceId(processParam.getProcessInstanceId());
            }
            oldProcessParam.setStartor(processParam.getStartor());
            oldProcessParam.setStartorName(processParam.getStartorName());
            oldProcessParam.setSponsorGuid(processParam.getSponsorGuid());
            oldProcessParam.setSended(processParam.getSended());
            oldProcessParam.setTarget(processParam.getTarget());
            oldProcessParam.setDueDate(processParam.getDueDate());
            oldProcessParam.setDescription(processParam.getDescription());
            processParamRepository.save(oldProcessParam);
            String tenantId = Y9LoginUserHolder.getTenantId();
            String processInstanceId = processParam.getProcessInstanceId();
            try {
                if (StringUtils.isNotBlank(processInstanceId)) {
                    boolean update = oldProcessParam.getSearchTerm() != null && processParam.getSearchTerm() != null
                        && !oldProcessParam.getSearchTerm().equals(processParam.getSearchTerm());
                    // 搜索字段不一样才修改
                    if (update) {
                        Map<String, Object> val = new HashMap<>();
                        val.put("val", processParam.getSearchTerm());
                        variableApi.setVariableByProcessInstanceId(tenantId, processInstanceId, "searchTerm", val);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return oldProcessParam;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ProcessParam newpp = new ProcessParam();
        newpp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newpp.setBureauIds(processParam.getBureauIds());
        newpp.setCustomLevel(processParam.getCustomLevel());
        newpp.setCustomNumber(processParam.getCustomNumber());
        newpp.setDeptIds(processParam.getDeptIds());
        newpp.setItemId(processParam.getItemId());
        newpp.setItemName(processParam.getItemName());
        newpp.setProcessInstanceId(processParam.getProcessInstanceId());
        newpp.setProcessSerialNumber(processParam.getProcessSerialNumber());
        newpp.setSystemName(processParam.getSystemName());
        newpp.setSystemCnName(processParam.getSystemCnName());
        newpp.setTitle(StringUtils.isBlank(processParam.getTitle()) ? "暂无标题" : processParam.getTitle());
        newpp.setSearchTerm(processParam.getSearchTerm());
        newpp.setTodoTaskUrlPrefix(processParam.getTodoTaskUrlPrefix());
        newpp.setIsSendSms(processParam.getIsSendSms());
        newpp.setIsShuMing(processParam.getIsShuMing());
        newpp.setSmsContent(processParam.getSmsContent());
        newpp.setSmsPersonId(processParam.getSmsPersonId());
        newpp.setCompleter(processParam.getCompleter());
        newpp.setStartor(processParam.getStartor());
        newpp.setStartorName(processParam.getStartorName());
        newpp.setSponsorGuid(processParam.getSponsorGuid());
        newpp.setSended(processParam.getSended());
        newpp.setCreateTime(sdf.format(new Date()));
        newpp.setCustomItem(processParam.getCustomItem());
        newpp.setTarget(processParam.getTarget());
        newpp.setDueDate(processParam.getDueDate());
        newpp.setDescription(processParam.getDescription());
        processParamRepository.save(newpp);
        return newpp;
    }

    @Override
    @Transactional
    public void setUpCompleter(String processInstanceId) {
        ProcessParam pp = processParamRepository.findByProcessInstanceId(processInstanceId);
        if (null != pp) {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            pp.setCompleter(person.getName());
            processParamRepository.save(pp);
        }
    }

    @Override
    @Transactional
    public void updateByProcessSerialNumber(String processSerialNumber, String processInstanceId) {
        ProcessParam pp = processParamRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != pp) {
            pp.setProcessInstanceId(processInstanceId);
            processParamRepository.save(pp);
            Map<String, Object> val = new HashMap<>();
            val.put("val", pp.getSearchTerm());
            variableApi.setVariableByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId, "searchTerm",
                val);
        }
    }

    @Override
    @Transactional
    public void updateCustomItem(String processSerialNumber, boolean b) {
        try {
            processParamRepository.updateCustomItem(processSerialNumber, b);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
