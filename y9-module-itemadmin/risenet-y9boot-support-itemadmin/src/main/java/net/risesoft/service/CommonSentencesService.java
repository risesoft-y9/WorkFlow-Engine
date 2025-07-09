package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.commonsentences.CommonSentences;
import net.risesoft.entity.commonsentences.CommonSentencesInit;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.CommonSentencesInitRepository;
import net.risesoft.repository.jpa.CommonSentencesRepository;
import net.risesoft.util.CommentUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class CommonSentencesService {

    private final CommonSentencesRepository commonSentencesRepository;

    private final CommonSentencesInitRepository commonSentencesInitRepository;

    /**
     * 根据id删除常用语
     *
     * @param id
     */
    @Transactional
    public void deleteById(String id) {
        commonSentencesRepository.deleteById(id);
    }

    public List<CommonSentences> getByUserId(String userId) {
        List<CommonSentences> resList = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            resList = commonSentencesRepository.findByUserId(userId);
        }
        return resList;
    }

    /**
     * 获取常用语
     *
     * @return
     */
    @Transactional
    public List<CommonSentences> listSentencesService() {
        List<CommonSentences> resList = new ArrayList<>();
        String userId = Y9LoginUserHolder.getPersonId();
        List<CommonSentences> list = commonSentencesRepository.findAllByUserId(userId);
        List<CommonSentencesInit> listInit = commonSentencesInitRepository.findByUserId(userId);
        if (list.isEmpty() && listInit.isEmpty()) {
            String[] comment = CommentUtil.getComment();
            // 保存初始化记录，用户已经初始化过常用语
            CommonSentencesInit commonSentencesInit = new CommonSentencesInit();
            commonSentencesInit.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            commonSentencesInit.setUserId(userId);
            commonSentencesInitRepository.save(commonSentencesInit);
            int i = 0;
            for (String option : comment) {
                CommonSentences commonSentences = saveCommonSentences(userId, option, i);
                i = i + 1;
                resList.add(commonSentences);
            }
        } else {
            resList.addAll(list);
        }
        return resList;
    }

    /**
     * 删除常用语
     *
     * @param tabIndex
     */
    @Transactional
    public void removeCommonSentences(int tabIndex) {
        String userId = Y9LoginUserHolder.getPersonId();
        CommonSentences commonSentences = commonSentencesRepository.findByUserIdAndTabIndex(userId, tabIndex);
        commonSentencesRepository.delete(commonSentences);
    }

    @Transactional
    public void removeUseNumber() {
        List<CommonSentences> list = commonSentencesRepository.findByUserId(Y9LoginUserHolder.getPersonId());
        for (CommonSentences info : list) {
            info.setUseNumber(0);
            commonSentencesRepository.save(info);
        }
    }

    /**
     * 保存常用语
     *
     * @param content
     * @throws Exception
     */
    @Transactional
    public void save(String content) {
        int tabIndex = 0;
        String userId = Y9LoginUserHolder.getPersonId();
        List<CommonSentences> commonSentencesList = getByUserId(userId);
        if (commonSentencesList.isEmpty()) {
            tabIndex = 0;
        } else {
            tabIndex = commonSentencesList.get(commonSentencesList.size() - 1).getTabIndex() + 1;
        }
        saveCommonSentences(userId, content, tabIndex);
    }

    /**
     * 保存更新常用语
     *
     * @param id
     * @param content
     */
    @Transactional
    public void save(String id, String content) {
        if (StringUtils.isNotBlank(id)) {
            Optional<CommonSentences> commonSentences = commonSentencesRepository.findById(id);
            if (commonSentences.isPresent() && commonSentences.get().getId() != null) {
                commonSentences.get().setContent(content);
                commonSentencesRepository.save(commonSentences.get());
            }
        } else {
            this.save(content);
        }

    }

    @Transactional
    public CommonSentences saveCommonSentences(String userId, String content, int tabIndex) {
        CommonSentences commonSentences = commonSentencesRepository.findByUserIdAndTabIndex(userId, tabIndex);
        if (commonSentences == null || commonSentences.getId() == null) {
            commonSentences = new CommonSentences();
            commonSentences.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        commonSentences.setUserId(userId);
        commonSentences.setTenantId(Y9LoginUserHolder.getTenantId());
        commonSentences.setContent(content);
        commonSentences.setTabIndex(tabIndex);
        return commonSentencesRepository.save(commonSentences);
    }

    @Transactional
    public void updateUseNumber(String id) {
        CommonSentences info = commonSentencesRepository.findById(id).orElse(null);
        if (info != null) {
            info.setUseNumber(info.getUseNumber() == null ? 1 : info.getUseNumber() + 1);
            commonSentencesRepository.save(info);
        }
    }

}
