package net.risesoft.service.core.impl;

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
import net.risesoft.service.core.ProcessParamService;
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
public class ProcessParamServiceImpl implements ProcessParamService {

    private final ProcessParamRepository processParamRepository;

    private final VariableApi variableApi;

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
            oldProcessParam.setSystemCnName(processParam.getSystemCnName());
            if (StringUtils.isNotBlank(processParam.getBureauIds())) {
                oldProcessParam.setBureauIds(processParam.getBureauIds());
            }
            if (StringUtils.isNotBlank(processParam.getDeptIds())) {
                oldProcessParam.setDeptIds(processParam.getDeptIds());
            }
            oldProcessParam.setCompleter(processParam.getCompleter());
            if (StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                oldProcessParam.setProcessInstanceId(processParam.getProcessInstanceId());
            }
            oldProcessParam.setStartor(processParam.getStartor());
            oldProcessParam.setStartorName(processParam.getStartorName());
            oldProcessParam.setSponsorGuid(processParam.getSponsorGuid());
            oldProcessParam.setHostDeptId(processParam.getHostDeptId());
            oldProcessParam.setHostDeptName(processParam.getHostDeptName());
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
        ProcessParam newProcessParam = new ProcessParam();
        newProcessParam.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newProcessParam.setBureauIds(processParam.getBureauIds());
        newProcessParam.setCustomLevel(processParam.getCustomLevel());
        newProcessParam.setCustomNumber(processParam.getCustomNumber());
        newProcessParam.setDeptIds(processParam.getDeptIds());
        newProcessParam.setItemId(processParam.getItemId());
        newProcessParam.setItemName(processParam.getItemName());
        newProcessParam.setProcessInstanceId(processParam.getProcessInstanceId());
        newProcessParam.setProcessSerialNumber(processParam.getProcessSerialNumber());
        newProcessParam.setSystemName(processParam.getSystemName());
        newProcessParam.setSystemCnName(processParam.getSystemCnName());
        newProcessParam.setTitle(StringUtils.isBlank(processParam.getTitle()) ? "暂无标题" : processParam.getTitle());
        newProcessParam.setSearchTerm(processParam.getSearchTerm());
        newProcessParam.setCompleter(processParam.getCompleter());
        newProcessParam.setStartor(processParam.getStartor());
        newProcessParam.setStartorName(processParam.getStartorName());
        newProcessParam.setSponsorGuid(processParam.getSponsorGuid());
        newProcessParam.setSended(processParam.getSended());
        newProcessParam.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        newProcessParam.setCustomItem(processParam.getCustomItem());
        newProcessParam.setTarget(processParam.getTarget());
        newProcessParam.setDueDate(processParam.getDueDate());
        newProcessParam.setDescription(processParam.getDescription());
        newProcessParam.setHostDeptId(processParam.getHostDeptId());
        newProcessParam.setHostDeptName(processParam.getHostDeptName());
        processParamRepository.save(newProcessParam);
        return newProcessParam;
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
