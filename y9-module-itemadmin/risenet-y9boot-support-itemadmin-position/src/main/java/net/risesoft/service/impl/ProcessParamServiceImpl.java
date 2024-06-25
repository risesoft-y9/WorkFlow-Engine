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
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final VariableApi variableManager;

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
    public ProcessParam saveOrUpdate(ProcessParam processParam) {
        String processSerialNumber = processParam.getProcessSerialNumber();
        ProcessParam oldpp = processParamRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != oldpp) {
            if (StringUtils.isNotBlank(processParam.getCustomLevel())) {
                oldpp.setCustomLevel(processParam.getCustomLevel());
            }
            if (StringUtils.isNotBlank(processParam.getCustomNumber())) {
                oldpp.setCustomNumber(processParam.getCustomNumber());
            }
            if (StringUtils.isNotBlank(processParam.getTitle())) {
                oldpp.setTitle(processParam.getTitle());
            }
            oldpp.setSearchTerm(processParam.getSearchTerm());
            oldpp.setTodoTaskUrlPrefix(processParam.getTodoTaskUrlPrefix());
            oldpp.setSystemCnName(processParam.getSystemCnName());
            if (StringUtils.isNotBlank(processParam.getBureauIds())) {
                oldpp.setBureauIds(processParam.getBureauIds());
            }
            if (StringUtils.isNotBlank(processParam.getDeptIds())) {
                oldpp.setDeptIds(processParam.getDeptIds());
            }
            oldpp.setIsSendSms(processParam.getIsSendSms());
            oldpp.setIsShuMing(processParam.getIsShuMing());
            oldpp.setSmsContent(processParam.getSmsContent());
            oldpp.setSmsPersonId(processParam.getSmsPersonId());
            oldpp.setCompleter(processParam.getCompleter());
            oldpp.setProcessInstanceId(processParam.getProcessInstanceId());
            oldpp.setStartor(processParam.getStartor());
            oldpp.setStartorName(processParam.getStartorName());
            oldpp.setSponsorGuid(processParam.getSponsorGuid());
            oldpp.setSended(processParam.getSended());
            processParamRepository.save(oldpp);
            String tenantId = Y9LoginUserHolder.getTenantId();
            String processInstanceId = processParam.getProcessInstanceId();
            try {
                if (StringUtils.isNotBlank(processInstanceId)) {
                    boolean update = false;
                    // 搜索字段不一样才修改
                    if (oldpp.getSearchTerm() != null && processParam.getSearchTerm() != null
                        && !oldpp.getSearchTerm().equals(processParam.getSearchTerm())) {
                        update = true;
                    }
                    if (update) {
                        Map<String, Object> val = new HashMap<String, Object>();
                        val.put("val", processParam.getSearchTerm());
                        variableManager.setVariableByProcessInstanceId(tenantId, processInstanceId, "searchTerm", val);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return oldpp;
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
        newpp.setTitle(processParam.getTitle());
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
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("val", pp.getSearchTerm());
            variableManager.setVariableByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId,
                "searchTerm", val);
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
