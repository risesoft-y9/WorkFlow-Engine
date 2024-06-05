package net.risesoft.service.form.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9FormOptionClassRepository;
import net.risesoft.repository.form.Y9FormOptionValueRepository;
import net.risesoft.service.form.Y9FormOptionClassService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormOptionClassServiceImpl implements Y9FormOptionClassService {

    private final Y9FormOptionClassRepository y9FormOptionClassRepository;

    private final Y9FormOptionValueRepository y9FormOptionValueRepository;

    @Override
    @Transactional()
    public Map<String, Object> delOptionClass(String type) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "删除成功");
        try {
            if (StringUtils.isNotBlank(type)) {
                y9FormOptionClassRepository.deleteById(type);
                y9FormOptionValueRepository.deleteByType(type);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "删除失败");
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    @Transactional()
    public Map<String, Object> delOptionValue(String id) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "删除成功");
        try {
            if (StringUtils.isNotBlank(id)) {
                y9FormOptionValueRepository.deleteById(id);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "删除失败");
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public List<Y9FormOptionClass> findAllOptionClass() {
        return y9FormOptionClassRepository.findAll();
    }

    @Override
    public List<Y9FormOptionValue> findAllOptionValue() {
        return y9FormOptionValueRepository.findAll();
    }

    @Override
    public Y9FormOptionValue findById(String id) {
        return y9FormOptionValueRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9FormOptionClass> findByName(String name) {
        return y9FormOptionClassRepository.findByNameContaining(StringUtils.isBlank(name) ? "" : name);
    }

    @Override
    public Y9FormOptionClass findByType(String type) {
        return y9FormOptionClassRepository.findByType(type);
    }

    @Override
    public List<Y9FormOptionValue> findByTypeOrderByTabIndexAsc(String type) {
        return y9FormOptionValueRepository.findByTypeOrderByTabIndexAsc(type);
    }

    @Transactional()
    @Override
    public Map<String, Object> saveOptionClass(Y9FormOptionClass optionClass) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "保存成功");
        try {
            Y9FormOptionClass y9FormOptionClass = y9FormOptionClassRepository.findByType(optionClass.getType());
            if (y9FormOptionClass == null) {
                y9FormOptionClass = new Y9FormOptionClass();
                y9FormOptionClass.setType(optionClass.getType());
            }
            y9FormOptionClass.setName(optionClass.getName());
            y9FormOptionClassRepository.save(y9FormOptionClass);
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "保存失败");
            e.printStackTrace();
        }
        return retMap;
    }

    @Transactional()
    @Override
    public Map<String, Object> saveOptionValue(Y9FormOptionValue optionValue) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "保存成功");
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
            y9FormOptionValueRepository.save(y9FormOptionValue);
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "保存失败");
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    @Transactional()
    public Map<String, Object> saveOrder(String ids) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "保存成功");
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
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "保存失败");
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    @Transactional()
    public Map<String, Object> updateOptionValue(String id) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, true);
        retMap.put("msg", "设置成功");
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
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            retMap.put("msg", "设置失败");
            e.printStackTrace();
        }
        return retMap;
    }

}
