package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SignaturePictureServiceImpl implements SignaturePictureService {

    private final SignaturePictureRepository signaturePictureRepository;

    @Override
    @Transactional
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
    @Transactional
    public SignaturePicture saveOrUpdate(SignaturePicture sp) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        SignaturePicture existSignaturePicture = this.findByUserId(userId);
        if (null != existSignaturePicture) {
            existSignaturePicture.setFileStoreId(sp.getFileStoreId());
            return signaturePictureRepository.save(existSignaturePicture);
        }
        SignaturePicture newSignaturePicture = new SignaturePicture();
        newSignaturePicture.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newSignaturePicture.setFileStoreId(sp.getFileStoreId());
        newSignaturePicture.setTenantId(tenantId);
        newSignaturePicture.setUserId(userId);
        newSignaturePicture.setUserName(userName);
        return signaturePictureRepository.save(newSignaturePicture);
    }
}
