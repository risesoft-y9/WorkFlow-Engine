package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.Item;
import net.risesoft.entity.interfaceinfo.InterfaceInfo;
import net.risesoft.entity.interfaceinfo.InterfaceRequestParams;
import net.risesoft.entity.interfaceinfo.InterfaceResponseParams;
import net.risesoft.entity.interfaceinfo.ItemInterfaceBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.interfaceinfo.InterfaceInfoRepository;
import net.risesoft.repository.interfaceinfo.InterfaceRequestParamsRepository;
import net.risesoft.repository.interfaceinfo.InterfaceResponseParamsRepository;
import net.risesoft.repository.interfaceinfo.ItemInterfaceBindRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.InterfaceService;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Service
@RequiredArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {

    private final InterfaceInfoRepository interfaceInfoRepository;

    private final ItemRepository itemRepository;

    private final ItemInterfaceBindRepository itemInterfaceBindRepository;

    private final InterfaceRequestParamsRepository interfaceRequestParamsRepository;

    private final InterfaceResponseParamsRepository interfaceResponseParamsRepository;

    @Override
    public InterfaceInfo findById(String id) {
        return interfaceInfoRepository.findById(id).orElse(null);
    }

    @Override
    public List<InterfaceInfo> findByInterfaceName(String interfaceName) {
        return interfaceInfoRepository.findByInterfaceNameLike("%" + interfaceName + "%");
    }

    @Override
    public List<InterfaceInfo> findAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        return interfaceInfoRepository.findAll(sort);
    }

    @Override
    public List<ItemInterfaceBind> listInterfaceById(String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        List<ItemInterfaceBind> list =
            itemInterfaceBindRepository.findAll((Specification<ItemInterfaceBind>)(root, query, builder) -> {
                List<Predicate> list1 = new ArrayList<>();
                list1.add(builder.equal(root.get(ItemConsts.INTERFACEID_KEY), id));
                Predicate[] predicates = new Predicate[list1.size()];
                list1.toArray(predicates);
                return builder.and(predicates);
            }, sort);
        for (ItemInterfaceBind bind : list) {
            Item item = itemRepository.findById(bind.getItemId()).orElse(null);
            bind.setItemName(item != null ? item.getName() : "事项不存在");
        }
        return list;
    }

    @Override
    public List<InterfaceInfo> listInterfaces(String name, String type, String address) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        return interfaceInfoRepository.findAll((Specification<InterfaceInfo>)(root, query, builder) -> {
            List<Predicate> list = new ArrayList<>();
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
        }, sort);
    }

    @Override
    public List<InterfaceRequestParams> listRequestParams(String name, String type, String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        return interfaceRequestParamsRepository
            .findAll((Specification<InterfaceRequestParams>)(root, query, builder) -> {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(name)) {
                    list.add(builder.like(root.get("parameterName"), "%" + name + "%"));
                }
                if (StringUtils.isNotBlank(id)) {
                    list.add(builder.equal(root.get(ItemConsts.INTERFACEID_KEY), id));
                }
                if (StringUtils.isNotBlank(type)) {
                    list.add(builder.equal(root.get("parameterType"), type));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }, sort);
    }

    @Override
    public List<InterfaceResponseParams> listResponseParamsByNameAndId(String name, String id) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        return interfaceResponseParamsRepository
            .findAll((Specification<InterfaceResponseParams>)(root, query, builder) -> {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(name)) {
                    list.add(builder.like(root.get("parameterName"), "%" + name + "%"));
                }
                if (StringUtils.isNotBlank(id)) {
                    list.add(builder.equal(root.get(ItemConsts.INTERFACEID_KEY), id));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }, sort);
    }

    @Override
    @Transactional
    public void removeInterface(String id) {
        interfaceInfoRepository.deleteById(id);
        interfaceRequestParamsRepository.deleteByInterfaceId(id);
        interfaceResponseParamsRepository.deleteByInterfaceId(id);
    }

    @Override
    @Transactional
    public void removeReqParams(String[] ids) {
        for (String id : ids) {
            interfaceRequestParamsRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void removeResParams(String[] ids) {
        for (String id : ids) {
            interfaceResponseParamsRepository.deleteById(id);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void saveAllResponseParams(String interfaceId, String jsonData) {
        Map<String, Object> map = Y9JsonUtil.readValue(jsonData, Map.class);
        assert map != null;
        for (String columnName : map.keySet()) {
            List<InterfaceResponseParams> list = interfaceResponseParamsRepository.findByParameterName(columnName);
            if (list.isEmpty()) {
                InterfaceResponseParams item = new InterfaceResponseParams();
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setInterfaceId(interfaceId);
                item.setParameterName(columnName);
                item.setRemark("");
                interfaceResponseParamsRepository.saveAndFlush(item);
            }
        }
    }

    @Override
    @Transactional
    public InterfaceInfo saveInterfaceInfo(InterfaceInfo info) {
        String id = info.getId();
        if (StringUtils.isNotBlank(id)) {
            InterfaceInfo item = interfaceInfoRepository.findById(id).orElse(null);
            if (null != item) {
                item.setInterfaceName(info.getInterfaceName());
                item.setRequestType(info.getRequestType());
                item.setInterfaceAddress(info.getInterfaceAddress());
                item.setAbnormalStop(info.getAbnormalStop());
                item.setAsyn(info.getAsyn());
                return item;
            }
        }
        InterfaceInfo item = new InterfaceInfo();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setInterfaceName(info.getInterfaceName());
        item.setRequestType(info.getRequestType());
        item.setInterfaceAddress(info.getInterfaceAddress());
        item.setAbnormalStop(info.getAbnormalStop());
        item.setAsyn(info.getAsyn());
        return interfaceInfoRepository.saveAndFlush(item);
    }

    @Override
    @Transactional
    public void saveRequestParams(InterfaceRequestParams info) {
        String id = info.getId();
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
        item.setRemark(info.getRemark());
        interfaceRequestParamsRepository.saveAndFlush(item);
    }

    @Override
    @Transactional
    public void saveResponseParams(InterfaceResponseParams info) {
        String id = info.getId();
        if (StringUtils.isNotBlank(id)) {
            InterfaceResponseParams item = interfaceResponseParamsRepository.findById(id).orElse(null);
            if (null != item) {
                item.setInterfaceId(info.getInterfaceId());
                item.setParameterName(info.getParameterName());
                item.setRemark(info.getRemark());
                item.setFileType(info.getFileType());
                return;
            }
        }
        InterfaceResponseParams item = new InterfaceResponseParams();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        item.setInterfaceId(info.getInterfaceId());
        item.setParameterName(info.getParameterName());
        item.setRemark(info.getRemark());
        item.setFileType(info.getFileType());
        interfaceResponseParamsRepository.saveAndFlush(item);
    }
}
