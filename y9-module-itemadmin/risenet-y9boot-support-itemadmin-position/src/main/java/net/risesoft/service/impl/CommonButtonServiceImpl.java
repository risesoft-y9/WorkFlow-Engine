package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.CommonButton;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.CommonButtonRepository;
import net.risesoft.service.CommonButtonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class CommonButtonServiceImpl implements CommonButtonService {

    private final CommonButtonRepository commonButtonRepository;

    @Override
    public boolean checkCustomId(String customId) {
        boolean b = false;
        CommonButton cb = commonButtonRepository.findByCustomId(customId);
        if (null != cb) {
            b = true;
        }
        return b;
    }

    @Override
    public List<CommonButton> findAll() {
        return commonButtonRepository.findAll();
    }

    @Override
    public CommonButton findOne(String id) {
        return commonButtonRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void removeCommonButtons(String[] commonButtonIds) {
        for (String commonButtonId : commonButtonIds) {
            commonButtonRepository.deleteById(commonButtonId);
        }
    }

    @Override
    @Transactional
    public CommonButton saveOrUpdate(CommonButton commonButton) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = commonButton.getId();
        if (StringUtils.isNotEmpty(id)) {
            CommonButton oldcb = this.findOne(id);
            if (null != oldcb) {
                oldcb.setName(commonButton.getName());
                oldcb.setUpdateTime(sdf.format(new Date()));
                oldcb.setUserId(userId);
                oldcb.setUserName(userName);

                return commonButtonRepository.save(oldcb);
            } else {
                return commonButtonRepository.save(commonButton);
            }
        }

        CommonButton newcb = new CommonButton();
        newcb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newcb.setName(commonButton.getName());
        newcb.setCustomId("common_" + commonButton.getCustomId());
        newcb.setUserId(userId);
        newcb.setUserName(userName);
        newcb.setTenantId(tenantId);
        newcb.setCreateTime(sdf.format(new Date()));
        newcb.setUpdateTime(sdf.format(new Date()));
        return commonButtonRepository.save(newcb);
    }
}
