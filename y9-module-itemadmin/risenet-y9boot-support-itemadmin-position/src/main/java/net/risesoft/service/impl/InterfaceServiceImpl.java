package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.InterfaceRequestParams;
import net.risesoft.entity.InterfaceResponseParams;
import net.risesoft.entity.ItemInterfaceBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.InterfaceInfoRepository;
import net.risesoft.repository.jpa.InterfaceRequestParamsRepository;
import net.risesoft.repository.jpa.InterfaceResponseParamsRepository;
import net.risesoft.repository.jpa.ItemInterfaceBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.InterfaceService;
import net.risesoft.y9.json.Y9JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Service
@RequiredArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {

    private final InterfaceInfoRepository interfaceInfoRepository;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final ItemInterfaceBindRepository itemInterfaceBindRepository;

    private final InterfaceRequestParamsRepository interfaceRequestParamsRepository;

    private final InterfaceResponseParamsRepository interfaceResponseParamsRepository;

    @SuppressWarnings("serial")
    @Override
    public List<ItemInterfaceBind> findByInterfaceId(String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        List<ItemInterfaceBind> list = itemInterfaceBindRepository.findAll(new Specification<ItemInterfaceBind>() {
            @Override
            public Predicate toPredicate(Root<ItemInterfaceBind> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(builder.equal(root.get("interfaceId"), id));
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
        for (ItemInterfaceBind bind : list) {
            SpmApproveItem item = spmApproveItemRepository.findById(bind.getItemId()).orElse(null);
            bind.setItemName(item != null ? item.getName() : "事项不存在");
        }
        return list;
    }

    @SuppressWarnings("serial")
    @Override
    public List<InterfaceInfo> findInterfaceList(String name, String type, String address) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return interfaceInfoRepository.findAll(new Specification<InterfaceInfo>() {
            @Override
            public Predicate toPredicate(Root<InterfaceInfo> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(name)) {
                    list.add(builder.like(root.get("interfaceName"), "%" + name + "%"));
                }
                if (StringUtils.isNotBlank(address)) {
                    list.add(builder.like(root.get("interfaceAddress"), "%" + address + "%"));
                }
                if (StringUtils.isNotBlank(type)) {
                    list.add(builder.equal(root.get("requestType"), type));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @SuppressWarnings("serial")
    @Override
    public List<InterfaceRequestParams> findRequestParamsList(String name, String type, String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return interfaceRequestParamsRepository.findAll(new Specification<InterfaceRequestParams>() {
            @Override
            public Predicate toPredicate(Root<InterfaceRequestParams> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(name)) {
                    list.add(builder.like(root.get("parameterName"), "%" + name + "%"));
                }
                if (StringUtils.isNotBlank(id)) {
                    list.add(builder.equal(root.get("interfaceId"), id));
                }
                if (StringUtils.isNotBlank(type)) {
                    list.add(builder.equal(root.get("parameterType"), type));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @SuppressWarnings("serial")
    @Override
    public List<InterfaceResponseParams> findResponseParamsList(String name, String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return interfaceResponseParamsRepository.findAll(new Specification<InterfaceResponseParams>() {
            @Override
            public Predicate toPredicate(Root<InterfaceResponseParams> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(name)) {
                    list.add(builder.like(root.get("parameterName"), "%" + name + "%"));
                }
                if (StringUtils.isNotBlank(id)) {
                    list.add(builder.equal(root.get("interfaceId"), id));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @Override
    @Transactional()
    public void removeInterface(String id) {
        interfaceInfoRepository.deleteById(id);
        interfaceRequestParamsRepository.deleteByInterfaceId(id);
        interfaceResponseParamsRepository.deleteByInterfaceId(id);
    }

    @Override
    @Transactional()
    public void removeReqParams(String[] ids) {
        for (String id : ids) {
            interfaceRequestParamsRepository.deleteById(id);
        }
    }

    @Override
    @Transactional()
    public void removeResParams(String[] ids) {
        for (String id : ids) {
            interfaceResponseParamsRepository.deleteById(id);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional()
    public void saveAllResponseParams(String interfaceId, String jsonData) {
        Map<String, Object> map = Y9JsonUtil.readValue(jsonData, Map.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String columnName : map.keySet()) {
            List<InterfaceResponseParams> list = interfaceResponseParamsRepository.findByParameterName(columnName);
            if (list.isEmpty()) {
                InterfaceResponseParams item = new InterfaceResponseParams();
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setInterfaceId(interfaceId);
                item.setParameterName(columnName);
                item.setCreateTime(sdf.format(new Date()));
                item.setRemark("");
                interfaceResponseParamsRepository.saveAndFlush(item);
            }
        }
    }

    @Override
    @Transactional()
    public void saveInterfaceInfo(InterfaceInfo info) {
        String id = info.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            InterfaceInfo item = interfaceInfoRepository.findById(id).orElse(null);
            if (null != item) {
                item.setInterfaceName(info.getInterfaceName());
                item.setRequestType(info.getRequestType());
                item.setInterfaceAddress(info.getInterfaceAddress());
                item.setAbnormalStop(info.getAbnormalStop());
                item.setAsyn(info.getAsyn());
                return;
            }
        }
        InterfaceInfo item = new InterfaceInfo();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setInterfaceName(info.getInterfaceName());
        item.setRequestType(info.getRequestType());
        item.setInterfaceAddress(info.getInterfaceAddress());
        item.setAbnormalStop(info.getAbnormalStop());
        item.setAsyn(info.getAsyn());
        item.setCreateTime(sdf.format(new Date()));
        interfaceInfoRepository.saveAndFlush(item);
    }

    @Override
    @Transactional()
    public void saveRequestParams(InterfaceRequestParams info) {
        String id = info.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            InterfaceRequestParams item = interfaceRequestParamsRepository.findById(id).orElse(null);
            if (null != item) {
                item.setInterfaceId(info.getInterfaceId());
                item.setParameterName(info.getParameterName());
                item.setParameterType(info.getParameterType());
                item.setRemark(info.getRemark());
                return;
            }
        }
        InterfaceRequestParams item = new InterfaceRequestParams();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setInterfaceId(info.getInterfaceId());
        item.setParameterName(info.getParameterName());
        item.setParameterType(info.getParameterType());
        item.setCreateTime(sdf.format(new Date()));
        item.setRemark(info.getRemark());
        interfaceRequestParamsRepository.saveAndFlush(item);
    }

    @Override
    @Transactional()
    public void saveResponseParams(InterfaceResponseParams info) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = info.getId();
        if (StringUtils.isNotBlank(id)) {
            InterfaceResponseParams item = interfaceResponseParamsRepository.findById(id).orElse(null);
            if (null != item) {
                item.setInterfaceId(info.getInterfaceId());
                item.setParameterName(info.getParameterName());
                item.setRemark(info.getRemark());
                return;
            }
        }
        InterfaceResponseParams item = new InterfaceResponseParams();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setInterfaceId(info.getInterfaceId());
        item.setParameterName(info.getParameterName());
        item.setCreateTime(sdf.format(new Date()));
        item.setRemark(info.getRemark());
        interfaceResponseParamsRepository.saveAndFlush(item);
    }

}
