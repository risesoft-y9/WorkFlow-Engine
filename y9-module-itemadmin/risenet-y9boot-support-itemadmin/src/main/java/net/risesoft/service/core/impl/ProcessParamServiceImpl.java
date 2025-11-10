package net.risesoft.service.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ProcessParamRepository;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
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
        ProcessParam existingProcessParam = processParamRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != existingProcessParam) {
            return updateExistingProcessParam(existingProcessParam, processParam);
        } else {
            return createNewProcessParam(processParam);
        }
    }

    /**
     * 更新已存在的流程参数
     */
    private ProcessParam updateExistingProcessParam(ProcessParam oldProcessParam, ProcessParam processParam) {
        // 更新基础字段
        updateProcessParamFields(oldProcessParam, processParam);
        // 保存更新后的实体
        processParamRepository.save(oldProcessParam);
        // 处理搜索字段更新
        handleSearchTermUpdate(oldProcessParam, processParam);
        return oldProcessParam;
    }

    /**
     * 创建新的流程参数
     */
    private ProcessParam createNewProcessParam(ProcessParam processParam) {
        ProcessParam newProcessParam = new ProcessParam();
        newProcessParam.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        // 设置所有字段
        updateProcessParamFields(newProcessParam, processParam);
        // 设置默认标题
        newProcessParam.setTitle(StringUtils.isBlank(processParam.getTitle()) ? "暂无标题" : processParam.getTitle());
        processParamRepository.save(newProcessParam);
        return newProcessParam;
    }

    /**
     * 更新ProcessParam字段的通用方法
     */
    private void updateProcessParamFields(ProcessParam target, ProcessParam source) {
        if (StringUtils.isNotBlank(source.getCustomLevel())) {
            target.setCustomLevel(source.getCustomLevel());
        }
        if (StringUtils.isNotBlank(source.getCustomNumber())) {
            target.setCustomNumber(source.getCustomNumber());
        }
        if (StringUtils.isNotBlank(source.getTitle())) {
            target.setTitle(source.getTitle());
        }
        target.setSearchTerm(source.getSearchTerm());
        target.setSystemCnName(source.getSystemCnName());
        if (StringUtils.isNotBlank(source.getBureauIds())) {
            target.setBureauIds(source.getBureauIds());
        }
        if (StringUtils.isNotBlank(source.getDeptIds())) {
            target.setDeptIds(source.getDeptIds());
        }
        target.setCompleter(source.getCompleter());
        if (StringUtils.isNotBlank(source.getProcessInstanceId())) {
            target.setProcessInstanceId(source.getProcessInstanceId());
        }
        target.setStartor(source.getStartor());
        target.setStartorName(source.getStartorName());
        target.setSponsorGuid(source.getSponsorGuid());
        target.setHostDeptId(source.getHostDeptId());
        target.setHostDeptName(source.getHostDeptName());
        target.setSended(source.getSended());
        target.setTarget(source.getTarget());
        target.setDueDate(source.getDueDate());
        target.setDescription(source.getDescription());
        target.setCustomItem(source.getCustomItem());
        target.setSystemName(source.getSystemName());
        target.setItemId(source.getItemId());
        target.setItemName(source.getItemName());
        target.setProcessSerialNumber(source.getProcessSerialNumber());
    }

    /**
     * 处理搜索字段更新
     */
    private void handleSearchTermUpdate(ProcessParam oldProcessParam, ProcessParam processParam) {
        String processInstanceId = processParam.getProcessInstanceId();
        if (StringUtils.isBlank(processInstanceId)) {
            return;
        }
        try {
            boolean shouldUpdate = oldProcessParam.getSearchTerm() != null && processParam.getSearchTerm() != null
                && !oldProcessParam.getSearchTerm().equals(processParam.getSearchTerm());
            // 搜索字段不一样才修改
            if (shouldUpdate) {
                Map<String, Object> val = new HashMap<>();
                val.put("val", processParam.getSearchTerm());
                variableApi.setVariableByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId,
                    "searchTerm", val);
            }
        } catch (Exception e) {
            LOGGER.error("更新搜索字段失败: processInstanceId={}", processInstanceId, e);
        }
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
            LOGGER.error("更新自定义项失败: processSerialNumber={}", processSerialNumber, e);
        }
    }
}
