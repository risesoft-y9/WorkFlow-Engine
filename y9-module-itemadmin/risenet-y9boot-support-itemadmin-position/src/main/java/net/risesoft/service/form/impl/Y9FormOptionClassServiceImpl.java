package net.risesoft.service.form.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormOptionClassRepository;
import net.risesoft.repository.form.Y9FormOptionValueRepository;
import net.risesoft.service.form.Y9FormOptionClassService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormOptionClassServiceImpl implements Y9FormOptionClassService {

    private final Y9FormOptionClassRepository y9FormOptionClassRepository;

    private final Y9FormOptionValueRepository y9FormOptionValueRepository;

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> delOptionClass(String type) {
        try {
            if (StringUtils.isNotBlank(type)) {
                y9FormOptionClassRepository.deleteById(type);
                y9FormOptionValueRepository.deleteByType(type);
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> delOptionValue(String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                y9FormOptionValueRepository.deleteById(id);
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除字典数据失败", e);
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    public Y9FormOptionValue findById(String id) {
        return y9FormOptionValueRepository.findById(id).orElse(null);
    }

    @Override
    public Y9FormOptionClass findByType(String type) {
        return y9FormOptionClassRepository.findByType(type);
    }

    @Override
    public List<Y9FormOptionClass> listAllOptionClass() {
        return y9FormOptionClassRepository.findAll();
    }

    @Override
    public List<Y9FormOptionValue> listAllOptionValue() {
        return y9FormOptionValueRepository.findAll();
    }

    @Override
    public List<Y9FormOptionClass> listByName(String name) {
        return y9FormOptionClassRepository.findByNameContaining(StringUtils.isBlank(name) ? "" : name);
    }

    @Override
    public List<Y9FormOptionValue> listByTypeOrderByTabIndexAsc(String type) {
        return y9FormOptionValueRepository.findByTypeOrderByTabIndexAsc(type);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Result<Y9FormOptionClass> saveOptionClass(Y9FormOptionClass optionClass) {
        try {
            Y9FormOptionClass y9FormOptionClass = y9FormOptionClassRepository.findByType(optionClass.getType());
            if (y9FormOptionClass == null) {
                y9FormOptionClass = new Y9FormOptionClass();
                y9FormOptionClass.setType(optionClass.getType());
            }
            y9FormOptionClass.setName(optionClass.getName());
            y9FormOptionClass = y9FormOptionClassRepository.save(y9FormOptionClass);

            return Y9Result.success(y9FormOptionClass, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存字典类型失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Result<Y9FormOptionValue> saveOptionValue(Y9FormOptionValue optionValue) {
        try {
            Y9FormOptionValue y9FormOptionValue = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(optionValue.getId())) {
                y9FormOptionValue = y9FormOptionValueRepository.findById(optionValue.getId()).orElse(null);
            }
            if (y9FormOptionValue == null || y9FormOptionValue.getId() == null) {
                y9FormOptionValue = new Y9FormOptionValue();
                y9FormOptionValue.setId(StringUtils.isBlank(optionValue.getId()) ? Y9IdGenerator.genId(IdType.SNOWFLAKE)
                    : optionValue.getId());
                Integer tabIndex = y9FormOptionValueRepository.getMaxTabIndex(optionValue.getType());
                y9FormOptionValue.setTabIndex((tabIndex == null || tabIndex == 0) ? 1 : tabIndex + 1);
                if ((tabIndex == null || tabIndex == 0)) {
                    y9FormOptionValue.setDefaultSelected(1);
                }
            } else {
                y9FormOptionValue.setTabIndex(optionValue.getTabIndex());
            }
            y9FormOptionValue.setCode(optionValue.getCode());
            y9FormOptionValue.setName(optionValue.getName());
            y9FormOptionValue.setType(optionValue.getType());
            y9FormOptionValue.setUpdateTime(sdf.format(new Date()));
            y9FormOptionValue = y9FormOptionValueRepository.save(y9FormOptionValue);

            return Y9Result.success(y9FormOptionValue, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存字典数据失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> saveOrder(String ids) {
        try {
            String[] id = ids.split(",");
            for (String idTemp : id) {
                String guid = idTemp.split(":")[0];
                String tabIndex = idTemp.split(":")[1];
                Y9FormOptionValue y9FormOptionValue = y9FormOptionValueRepository.findById(guid).orElse(null);
                if (y9FormOptionValue != null) {
                    y9FormOptionValue.setTabIndex(Integer.valueOf(tabIndex));
                    y9FormOptionValueRepository.save(y9FormOptionValue);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> updateOptionValue(String id) {
        try {
            Y9FormOptionValue y9FormOptionValue = y9FormOptionValueRepository.findById(id).orElse(null);
            if (y9FormOptionValue != null) {
                List<Y9FormOptionValue> list =
                    y9FormOptionValueRepository.findByTypeOrderByTabIndexAsc(y9FormOptionValue.getType());
                for (Y9FormOptionValue optionValue : list) {
                    if (optionValue.getId().equals(id)) {
                        optionValue.setDefaultSelected(1);
                        y9FormOptionValueRepository.save(optionValue);
                    } else {
                        optionValue.setDefaultSelected(0);
                        y9FormOptionValueRepository.save(optionValue);
                    }
                }
            }
            return Y9Result.successMsg("设置成功");
        } catch (Exception e) {
            LOGGER.error("设置失败", e);
            return Y9Result.failure("设置失败");
        }
    }

}
