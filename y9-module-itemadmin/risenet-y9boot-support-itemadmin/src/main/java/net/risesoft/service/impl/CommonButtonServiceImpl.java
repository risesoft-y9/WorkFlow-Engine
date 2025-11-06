package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.button.CommonButton;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.button.CommonButtonRepository;
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
    public CommonButton getById(String id) {
        return commonButtonRepository.findById(id).orElse(null);
    }

    @Override
    public List<CommonButton> listAll() {
        return commonButtonRepository.findAll();
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
        String id = commonButton.getId();
        if (StringUtils.isNotEmpty(id)) {
            CommonButton existCommonButton = this.getById(id);
            if (null != existCommonButton) {
                existCommonButton.setName(commonButton.getName());
                existCommonButton.setUserId(userId);
                existCommonButton.setUserName(userName);
                return commonButtonRepository.save(existCommonButton);
            } else {
                return commonButtonRepository.save(commonButton);
            }
        }
        CommonButton newcbCommonButton = new CommonButton();
        newcbCommonButton.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newcbCommonButton.setName(commonButton.getName());
        newcbCommonButton.setCustomId("common_" + commonButton.getCustomId());
        newcbCommonButton.setUserId(userId);
        newcbCommonButton.setUserName(userName);
        newcbCommonButton.setTenantId(tenantId);
        return commonButtonRepository.save(newcbCommonButton);
    }
}
