package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SignaturePicture;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.SignaturePictureRepository;
import net.risesoft.service.SignaturePictureService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "signaturePictureService")
public class SignaturePictureServiceImpl implements SignaturePictureService {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SignaturePictureRepository signaturePictureRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        signaturePictureRepository.deleteById(id);
    }

    @Override
    public SignaturePicture findById(String id) {
        return signaturePictureRepository.findById(id).orElse(null);
    }

    @Override
    public SignaturePicture findByUserId(String userId) {
        return signaturePictureRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = false)
    public SignaturePicture saveOrUpdate(SignaturePicture sp) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId(),
            userName = userInfo.getName();
        SignaturePicture oldsp = this.findByUserId(userId);
        if (null != oldsp) {
            oldsp.setModifyDate(sdf.format(new Date()));
            oldsp.setFileStoreId(sp.getFileStoreId());
            return signaturePictureRepository.save(oldsp);
        }

        SignaturePicture newsp = new SignaturePicture();
        newsp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newsp.setCreateDate(sdf.format(new Date()));
        newsp.setFileStoreId(sp.getFileStoreId());
        newsp.setModifyDate(sdf.format(new Date()));
        newsp.setTenantId(tenantId);
        newsp.setUserId(userId);
        newsp.setUserName(userName);

        return signaturePictureRepository.save(newsp);
    }
}
