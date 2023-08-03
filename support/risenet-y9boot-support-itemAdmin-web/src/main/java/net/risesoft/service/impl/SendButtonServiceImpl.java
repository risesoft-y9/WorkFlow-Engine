package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SendButton;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.SendButtonRepository;
import net.risesoft.service.SendButtonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "sendButtonService")
public class SendButtonServiceImpl implements SendButtonService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SendButtonRepository sendButtonRepository;

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
    public List<SendButton> findAll() {
        return sendButtonRepository.findAll();
    }

    @Override
    public SendButton findOne(String id) {
        return sendButtonRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeSendButtons(String[] sendButtonIds) {
        for (String sendButtonId : sendButtonIds) {
            sendButtonRepository.deleteById(sendButtonId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public SendButton saveOrUpdate(SendButton sendButton) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getParentId(), userName = userInfo.getName(),
            tenantId = Y9LoginUserHolder.getTenantId();

        String id = sendButton.getId();
        if (StringUtils.isNotEmpty(id)) {
            SendButton oldsb = this.findOne(id);
            if (null != oldsb) {
                oldsb.setName(sendButton.getName());
                oldsb.setUpdateTime(sdf.format(new Date()));
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
        newsb.setCreateTime(sdf.format(new Date()));
        newsb.setUpdateTime(sdf.format(new Date()));
        return sendButtonRepository.save(newsb);
    }
}
