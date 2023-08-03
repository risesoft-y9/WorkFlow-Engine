package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpeakInfo;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.SpeakInfoRepository;
import net.risesoft.service.SpeakInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "speakInfoService")
public class SpeakInfoServiceImpl implements SpeakInfoService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SpeakInfoRepository speakInfoRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "删除失败");
        SpeakInfo speakInfo = this.findById(id);
        Date createDate = new Date();
        Date dateAfterCreateDate5Minute = new Date();
        Date currentDate = new Date();
        try {
            createDate = sdf.parse(speakInfo.getCreateTime());
            dateAfterCreateDate5Minute = new Date(createDate.getTime() + 300000);
            currentDate = sdf.parse(sdf.format(new Date()));
            if (currentDate.after(dateAfterCreateDate5Minute)) {
                map.put("msg", "该信息已提交超过5分钟,不可删除!");
                map.put(UtilConsts.SUCCESS, false);
            } else {
                speakInfoRepository.deleteById(id);
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", "删除成功");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public SpeakInfo findById(String id) {
        return speakInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public List<SpeakInfo> findByProcessInstanceId(String processInstanceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String currentUserId = userInfo.getPersonId();
        List<SpeakInfo> siList =
            speakInfoRepository.findByProcessInstanceIdAndDeletedFalseOrderByCreateTimeAsc(processInstanceId);
        Date createDate = new Date();
        Date dateAfterCreateDate5Minute = new Date();
        Date currentDate = new Date();
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
            try {
                createDate = sdf.parse(speakInfo.getCreateTime());
                dateAfterCreateDate5Minute = new Date(createDate.getTime() + 300000);
                currentDate = sdf.parse(sdf.format(new Date()));
                if (currentDate.after(dateAfterCreateDate5Minute)) {
                    speakInfo.setEdited(false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
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
    @Transactional(readOnly = false)
    public String saveOrUpdate(SpeakInfo speakInfo) {
        String id = speakInfo.getId();
        if (StringUtils.isNotEmpty(id)) {
            SpeakInfo oldsi = this.findById(id);
            oldsi.setContent(speakInfo.getContent());
            oldsi.setUpdateTime(sdf.format(new Date()));

            speakInfoRepository.save(oldsi);
            return id;
        }
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), userName = userInfo.getName();
        SpeakInfo newsi = new SpeakInfo();
        String newId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        newsi.setId(newId);
        newsi.setProcessInstanceId(speakInfo.getProcessInstanceId());
        newsi.setContent(speakInfo.getContent());
        newsi.setDeleted(false);
        newsi.setCreateTime(sdf.format(new Date()));
        newsi.setUpdateTime(sdf.format(new Date()));
        newsi.setUserId(userId);
        newsi.setUserName(userName);
        newsi.setReadUserId(userId);
        speakInfoRepository.save(newsi);
        return newId;
    }
}
