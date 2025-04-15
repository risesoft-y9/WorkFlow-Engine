package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.CustomView;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.jpa.CustomViewRepository;
import net.risesoft.service.CustomViewService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomViewServiceImpl implements CustomViewService {

    private final CustomViewRepository customViewRepository;

    private final Y9FormFieldRepository y9FormFieldRepository;

    @Override
    @Transactional
    public void delCustomView(String viewType) {
        customViewRepository.deleteByUserIdAndViewType(Y9LoginUserHolder.getOrgUnitId(), viewType);
    }

    @Override
    public Y9Result<List<CustomViewModel>> listCustomView(String viewType) {
        List<CustomView> list =
            customViewRepository.findByUserIdAndViewTypeOrderByTabIndex(Y9LoginUserHolder.getOrgUnitId(), viewType);
        List<CustomViewModel> listCustomViewModel = new ArrayList<>();
        for (CustomView customView : list) {
            CustomViewModel customViewModelTemp = new CustomViewModel();
            Y9BeanUtil.copyProperties(customView, customViewModelTemp);
            List<Y9FormField> y9FormFieldList =
                y9FormFieldRepository.findByFormIdAndFieldName(customView.getFormId(), customView.getFieldName());
            customViewModelTemp
                .setColumnName(y9FormFieldList.size() > 0 ? y9FormFieldList.get(0).getFieldName() : "bucunzai");
            customViewModelTemp
                .setDisPlayName(y9FormFieldList.size() > 0 ? y9FormFieldList.get(0).getFieldCnName() : "该列不存在");
            customViewModelTemp.setFormId(y9FormFieldList.size() > 0 ? y9FormFieldList.get(0).getFormId() : "");
            customViewModelTemp.setFieldId(y9FormFieldList.size() > 0 ? y9FormFieldList.get(0).getId() : "bucunzai");
            listCustomViewModel.add(customViewModelTemp);
        }
        return Y9Result.success(listCustomViewModel);
    }

    @Override
    @Transactional
    public void saveCustomView(String jsonData) {
        List<CustomView> list = Y9JsonUtil.readList(jsonData, CustomView.class);
        int maxTabIndex = 1;
        List<String> ids = new ArrayList<>();
        String viewType = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (CustomView info : list) {
            viewType = info.getViewType();
            if (StringUtils.isBlank(info.getId())) {
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setCreateTime(dateFormat.format(new Date()));
            }
            info.setTabIndex(maxTabIndex);
            info.setUserId(Y9LoginUserHolder.getOrgUnitId());
            info.setUserName(Y9LoginUserHolder.getOrgUnit().getName());
            Y9FormField y9FormField = y9FormFieldRepository.findById(info.getFieldId()).orElse(null);
            if (y9FormField != null) {
                info.setFormId(y9FormField.getFormId());
                info.setFieldName(y9FormField.getFieldName());
                customViewRepository.save(info);
                ids.add(info.getId());
                maxTabIndex++;
            }
        }
        customViewRepository.deleteByUserIdAndViewTypeAndIdNotIn(Y9LoginUserHolder.getOrgUnitId(), viewType, ids);
    }
}
