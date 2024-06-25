package net.risesoft.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.CustomProcessInfoRepository;
import net.risesoft.service.CustomProcessInfoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class CustomProcessInfoServiceImpl implements CustomProcessInfoService {

    private final CustomProcessInfoRepository customProcessInfoRepository;

    @Override
    public CustomProcessInfo getCurrentTaskNextNode(String processSerialNumber) {
        try {
            List<CustomProcessInfo> taskList =
                customProcessInfoRepository.findByProcessSerialNumberOrderByTabIndexAsc(processSerialNumber);
            boolean isnext = false;
            for (CustomProcessInfo info : taskList) {
                if (isnext) {
                    return info;
                }
                if (info.getCurrentTask()) {
                    isnext = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("getCurrentTaskNextNode error", e);
            return null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public boolean saveOrUpdate(String itemId, String processSerialNumber, List<Map<String, Object>> taskList) {
        try {
            int i = 1;
            customProcessInfoRepository.deleteByProcessSerialNumber(processSerialNumber);
            for (Map<String, Object> map : taskList) {
                CustomProcessInfo info = new CustomProcessInfo();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemId(itemId);
                info.setProcessSerialNumber(processSerialNumber);
                info.setTaskKey((String)map.get("taskKey"));
                info.setTaskName((String)map.get("taskName"));
                info.setTaskType(
                    StringUtils.isBlank((String)map.get("type")) ? SysVariables.USERTASK : (String)map.get("type"));
                info.setTabIndex(i);
                List<Map<String, Object>> orgList = (List<Map<String, Object>>)map.get("orgList");
                String orgId = "";
                if (orgList != null) {
                    for (Map<String, Object> org : orgList) {
                        orgId = Y9Util.genCustomStr(orgId, (String)org.get("id"), ";");
                    }
                }
                info.setOrgId(orgId);
                if (i == 1) {
                    /*
                     * 第一个节点，设置成当前运行节点
                     */
                    info.setCurrentTask(true);
                    info.setOrgId((String)map.get("orgName"));
                }
                customProcessInfoRepository.save(info);
                i++;
            }
        } catch (Exception e) {
            LOGGER.error("saveOrUpdate error", e);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateCurrentTask(String processSerialNumber) {
        try {
            List<CustomProcessInfo> taskList =
                customProcessInfoRepository.findByProcessSerialNumberOrderByTabIndexAsc(processSerialNumber);
            boolean isSet = false;
            for (CustomProcessInfo info : taskList) {
                if (isSet) {
                    info.setCurrentTask(true);
                    customProcessInfoRepository.save(info);
                    break;
                }
                /*
                 * 找到当前运行节点,设为false,下次循环更新下一个节点结束
                 */
                if (info.getCurrentTask()) {
                    isSet = true;
                    info.setCurrentTask(false);
                    customProcessInfoRepository.save(info);
                }
            }
        } catch (Exception e) {
            LOGGER.error("updateCurrentTask error", e);
            return false;
        }
        return true;
    }

}
