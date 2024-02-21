package net.risesoft.service.form.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9ValidType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.form.Y9ValidTypeRepository;
import net.risesoft.service.form.Y9ValidTypeService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "y9ValidTypeService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9ValidTypeServiceImpl implements Y9ValidTypeService {

    @Autowired
    private Y9ValidTypeRepository y9ValidTypeRepository;

    @Autowired
    private PersonApi personManager;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delValidType(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String[] id = ids.split(",");
            for (String idTemp : id) {
                y9ValidTypeRepository.deleteById(idTemp);
            }
            map.put("msg", "删除成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<Y9ValidType> findAll() {
        return y9ValidTypeRepository.findAll();
    }

    @Override
    public Y9ValidType findById(String id) {
        return y9ValidTypeRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> getValidTypeList(String validType, String validCnName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Y9ValidType> list = new ArrayList<Y9ValidType>();
            if (StringUtils.isBlank(validType)) {
                list = y9ValidTypeRepository
                    .findByValidCnNameLike(StringUtils.isNotBlank(validCnName) ? "%" + validCnName + "%" : "%%");
            } else {
                list = y9ValidTypeRepository.findByValidTypeAndValidCnNameLike(validType,
                    StringUtils.isNotBlank(validCnName) ? "%" + validCnName + "%" : "%%");
            }
            for (Y9ValidType y9VlidType : list) {
                Map<String, Object> m = new HashMap<String, Object>(16);
                m.put("id", y9VlidType.getId());
                m.put("validCnName", y9VlidType.getValidCnName());
                m.put("validName", y9VlidType.getValidName());
                m.put("validType", y9VlidType.getValidType());
                String personId = y9VlidType.getPersonId();
                Person person = personManager.getPerson(Y9LoginUserHolder.getTenantId(), personId).getData();
                m.put("personName", person.getName());
                m.put("updateTime", sdf.format(y9VlidType.getUpdateTime()));
                resList.add(m);
            }
            map.put("rows", resList);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrUpdate(Y9ValidType y9ValidType) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String id = y9ValidType.getId();
        try {
            if (StringUtils.isNotEmpty(id)) {
                Y9ValidType oldType = this.findById(id);
                if (null == oldType) {
                    y9ValidTypeRepository.save(y9ValidType);
                } else {
                    oldType.setUpdateTime(new Date());
                    oldType.setPersonId(null == person ? "" : person.getPersonId());
                    oldType.setTenantId(Y9LoginUserHolder.getTenantId());
                    oldType.setValidCnName(y9ValidType.getValidCnName());
                    oldType.setValidContent(y9ValidType.getValidContent());
                    oldType.setValidName(y9ValidType.getValidName());
                    oldType.setValidType(y9ValidType.getValidType());
                    y9ValidTypeRepository.save(oldType);
                }
            } else {
                Y9ValidType newType = new Y9ValidType();
                newType.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newType.setUpdateTime(new Date());
                newType.setPersonId(Y9LoginUserHolder.getPersonId());
                newType.setTenantId(Y9LoginUserHolder.getTenantId());
                newType.setValidCnName(y9ValidType.getValidCnName());
                newType.setValidContent(y9ValidType.getValidContent());
                newType.setValidName(y9ValidType.getValidName());
                newType.setValidType(y9ValidType.getValidType());
                y9ValidTypeRepository.save(newType);
            }
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        return map;
    }

}
