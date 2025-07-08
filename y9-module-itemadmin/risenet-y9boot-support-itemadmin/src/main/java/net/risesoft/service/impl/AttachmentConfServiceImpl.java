package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.attachment.AttachmentConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.attachment.AttachmentConfRepository;
import net.risesoft.service.AttachmentConfService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AttachmentConfServiceImpl implements AttachmentConfService {

    private final AttachmentConfRepository attachmentConfRepository;

    @Override
    public AttachmentConf findById(String id) {
        return attachmentConfRepository.findById(id).orElse(null);
    }

    @Override
    public List<AttachmentConf> listByAttachmentType(String attachmentType, Integer configType) {
        return attachmentConfRepository.findByAttachmentTypeAndConfigTypeOrderByTabIndexAsc(attachmentType, configType);
    }

    @Override
    @Transactional
    public void removeByAttachmentType(String attachmentType) {
        List<AttachmentConf> list = attachmentConfRepository.findByAttachmentTypeOrderByTabIndexAsc(attachmentType);
        attachmentConfRepository.deleteAll(list);
    }

    @Override
    @Transactional
    public void removeAttachmentConfs(String[] attachmentConfIds) {
        for (String id : attachmentConfIds) {
            attachmentConfRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveOrUpdate(AttachmentConf attachmentConf) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = attachmentConf.getId();
        if (StringUtils.isNotBlank(id)) {
            AttachmentConf oldConf = this.findById(id);
            if (null != oldConf) {
                oldConf.setColumnName(attachmentConf.getColumnName());
                oldConf.setDisPlayWidth(attachmentConf.getDisPlayWidth());
                oldConf.setDisPlayName(attachmentConf.getDisPlayName());
                oldConf.setDisPlayAlign(attachmentConf.getDisPlayAlign());
                oldConf.setUpdateTime(sdf.format(new Date()));
                oldConf.setInputBoxType(attachmentConf.getInputBoxType());
                oldConf.setLabelName(attachmentConf.getLabelName());
                oldConf.setConfigType(attachmentConf.getConfigType());
                oldConf.setOptionClass(attachmentConf.getOptionClass());
                oldConf.setSpanWidth(attachmentConf.getSpanWidth());
                oldConf.setIsRequired(attachmentConf.getIsRequired());
                attachmentConfRepository.save(oldConf);
                return;
            } else {
                attachmentConfRepository.save(attachmentConf);
                return;
            }
        }

        AttachmentConf newConf = new AttachmentConf();
        newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newConf.setColumnName(attachmentConf.getColumnName());
        newConf.setDisPlayWidth(attachmentConf.getDisPlayWidth());
        newConf.setDisPlayName(attachmentConf.getDisPlayName());
        newConf.setDisPlayAlign(attachmentConf.getDisPlayAlign());
        newConf.setAttachmentType(attachmentConf.getAttachmentType());
        newConf.setUpdateTime(sdf.format(new Date()));
        newConf.setUpdateTime(sdf.format(new Date()));
        newConf.setInputBoxType(attachmentConf.getInputBoxType());
        newConf.setLabelName(attachmentConf.getLabelName());
        newConf.setConfigType(attachmentConf.getConfigType());
        newConf.setOptionClass(attachmentConf.getOptionClass());
        newConf.setSpanWidth(attachmentConf.getSpanWidth());
        newConf.setIsRequired(attachmentConf.getIsRequired());
        Integer index =
            attachmentConfRepository.getMaxTabIndex(attachmentConf.getAttachmentType(), attachmentConf.getConfigType());
        if (index == null) {
            newConf.setTabIndex(1);
        } else {
            newConf.setTabIndex(index + 1);
        }
        attachmentConfRepository.save(newConf);
    }

    @Override
    @Transactional
    public void updateOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                attachmentConfRepository.updateOrder(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
