package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.SpeakInfo;
import net.risesoft.enums.ItemAdminAuditLogEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.SpeakInfoRepository;
import net.risesoft.service.SpeakInfoService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9StringUtil;
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
public class SpeakInfoServiceImpl implements SpeakInfoService {

    private final SpeakInfoRepository speakInfoRepository;

    @Override
    @Transactional
    public Y9Result<Object> deleteById(String id) {
        SpeakInfo speakInfo = this.findById(id);
        Date createDate;
        Date dateAfterCreateDate5Minute;
        Date currentDate;
        try {
            createDate = speakInfo.getCreateTime();
            dateAfterCreateDate5Minute = new Date(createDate.getTime() + 300000);
            currentDate = Y9DateTimeUtils.parseDateTime(Y9DateTimeUtils.formatCurrentDateTime());
            if (currentDate.after(dateAfterCreateDate5Minute)) {
                return Y9Result.failure("该信息已提交超过5分钟,不可删除!");
            } else {
                speakInfoRepository.deleteById(id);
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.SPEAKINFO_DELETE.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.SPEAKINFO_DELETE.getDescription(),
                        speakInfo.getUserName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        speakInfo.getContent()))
                    .objectId(id)
                    .oldObject(speakInfo)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除信息失败", e);
        }
        return Y9Result.failure("删除失败!");
    }

    @Override
    public SpeakInfo findById(String id) {
        return speakInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<SpeakInfo> findByProcessInstanceId(String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String currentUserId = person.getPersonId();
        List<SpeakInfo> siList =
            speakInfoRepository.findByProcessInstanceIdAndDeletedFalseOrderByCreateTimeAsc(processInstanceId);
        Date createDate;
        Date dateAfterCreateDate5Minute;
        Date currentDate;
        for (SpeakInfo speakInfo : siList) {
            String readUserId = StringUtils.isBlank(speakInfo.getReadUserId()) ? "" : speakInfo.getReadUserId();
            if (!readUserId.contains(currentUserId)) {
                readUserId = Y9Util.genCustomStr(readUserId, currentUserId);
            }
            speakInfo.setReadUserId(readUserId);
            speakInfoRepository.save(speakInfo);
            speakInfo.setEdited(true);
            if (!currentUserId.equals(speakInfo.getUserId())) {
                speakInfo.setEdited(false);
                continue;
            }
            createDate = speakInfo.getCreateTime();
            dateAfterCreateDate5Minute = new Date(createDate.getTime() + 300000);
            currentDate = new Date();
            if (currentDate.after(dateAfterCreateDate5Minute)) {
                speakInfo.setEdited(false);
            }
        }
        return siList;
    }

    @Override
    public int getNotReadCount(String processInstanceId, String userId) {
        return speakInfoRepository.countByProcessInstanceIdAndDeletedFalseAndUserIdNotAndReadUserIdNotLike(
            processInstanceId, userId, "%" + userId + "%");
    }

    @Override
    @Transactional
    public String saveOrUpdate(SpeakInfo speakInfo) {
        String id = speakInfo.getId();
        if (StringUtils.isNotEmpty(id)) {
            SpeakInfo oldSpeakInfo = this.findById(id);
            oldSpeakInfo.setContent(speakInfo.getContent());
            speakInfoRepository.save(oldSpeakInfo);
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(ItemAdminAuditLogEnum.SPEAKINFO_ADD.getAction())
                .description(Y9StringUtil.format(ItemAdminAuditLogEnum.SPEAKINFO_ADD.getDescription(),
                    speakInfo.getUserName(), speakInfo.getUpdateTime(), speakInfo.getContent()))
                .objectId(id)
                .oldObject(speakInfo)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
            return id;
        }
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        SpeakInfo newSpeakInfo = new SpeakInfo();
        String newId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        newSpeakInfo.setId(newId);
        newSpeakInfo.setProcessInstanceId(speakInfo.getProcessInstanceId());
        newSpeakInfo.setContent(speakInfo.getContent());
        newSpeakInfo.setDeleted(false);
        newSpeakInfo.setUserId(userId);
        newSpeakInfo.setUserName(userName);
        newSpeakInfo.setReadUserId(userId);
        speakInfoRepository.save(newSpeakInfo);
        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(ItemAdminAuditLogEnum.SPEAKINFO_ADD.getAction())
            .description(Y9StringUtil.format(ItemAdminAuditLogEnum.SPEAKINFO_ADD.getDescription(),
                speakInfo.getUserName(), speakInfo.getCreateTime(), speakInfo.getContent()))
            .objectId(id)
            .oldObject(speakInfo)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
        return newId;
    }
}