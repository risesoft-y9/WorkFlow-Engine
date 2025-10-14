package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.button.SendButton;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.button.SendButtonRepository;
import net.risesoft.service.SendButtonService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SendButtonServiceImpl implements SendButtonService {

    private final SendButtonRepository sendButtonRepository;

    @Override
    public boolean checkCustomId(String customId) {
        boolean b = false;
        SendButton sb = sendButtonRepository.findByCustomId(customId);
        if (null != sb) {
            b = true;
        }
        return b;
    }

    @Override
    public SendButton getById(String id) {
        return sendButtonRepository.findById(id).orElse(null);
    }

    @Override
    public List<SendButton> listAll() {
        return sendButtonRepository.findAll();
    }

    @Override
    @Transactional
    public void removeSendButtons(String[] sendButtonIds) {
        for (String sendButtonId : sendButtonIds) {
            sendButtonRepository.deleteById(sendButtonId);
        }
    }

    @Override
    @Transactional
    public SendButton saveOrUpdate(SendButton sendButton) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getParentId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();
        String id = sendButton.getId();
        if (StringUtils.isNotEmpty(id)) {
            SendButton oldsb = this.getById(id);
            if (null != oldsb) {
                oldsb.setName(sendButton.getName());
                oldsb.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
                oldsb.setUserId(userId);
                oldsb.setUserName(userName);

                return sendButtonRepository.save(oldsb);
            } else {
                return sendButtonRepository.save(sendButton);
            }
        }

        SendButton newsb = new SendButton();
        newsb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newsb.setName(sendButton.getName());
        newsb.setCustomId("send_" + sendButton.getCustomId());
        newsb.setUserId(userId);
        newsb.setUserName(userName);
        newsb.setTenantId(tenantId);
        newsb.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        newsb.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
        return sendButtonRepository.save(newsb);
    }
}
