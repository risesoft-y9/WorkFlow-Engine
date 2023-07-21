package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.SignaturePicture;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SignaturePictureRepository extends JpaRepository<SignaturePicture, String>, JpaSpecificationExecutor<SignaturePicture> {

    SignaturePicture findByUserId(String userId);
}
