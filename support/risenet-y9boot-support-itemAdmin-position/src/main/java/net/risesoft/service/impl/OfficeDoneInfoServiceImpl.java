package net.risesoft.service.impl;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ErrorLog;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.nosql.elastic.repository.OfficeDoneInfoRepository;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.util.Y9EsIndexConst;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "officeDoneInfoService")
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class OfficeDoneInfoServiceImpl implements OfficeDoneInfoService {

    private static IndexCoordinates INDEX = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private OfficeDoneInfoRepository officeDoneInfoRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public int countByItemId(String itemId) {
        Criteria criteria = new Criteria();
        criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
        criteria.and("endTime").exists();
        if (StringUtils.isNotBlank(itemId)) {
            criteria.and("itemId").is(itemId);
        }
        Query query = new CriteriaQuery(criteria);
        return (int)elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public int countByUserId(String userId, String itemId) {
        Criteria criteria = new Criteria();
        criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
        criteria.and("endTime").exists();
        if (StringUtils.isNotBlank(itemId)) {
            criteria.and("itemId").is(itemId);
        }
        criteria.and("allUserId").contains(userId);
        Query query = new CriteriaQuery(criteria);
        return (int)elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public long countDoingByItemId(String itemId) {
        Criteria criteria = new Criteria();
        criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
        criteria.and("endTime").empty();
        if (StringUtils.isNotBlank(itemId)) {
            criteria.and("itemId").is(itemId);
        }
        Query query = new CriteriaQuery(criteria);
        return elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public boolean deleteOfficeDoneInfo(String processInstanceId) {
        boolean b = false;
        try {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoRepository
                .findByProcessInstanceIdAndTenantId(processInstanceId, Y9LoginUserHolder.getTenantId());
            if (officeDoneInfo != null) {
                officeDoneInfoRepository.delete(officeDoneInfo);
            }
            LOGGER.info("*************************数据中心删除成功**************************************");
            b = true;
        } catch (Exception e) {
            LOGGER.warn("*************************数据中心删除失败**************************************", e);
        }
        return b;
    }

    @Override
    public OfficeDoneInfo findByProcessInstanceId(String processInstanceId) {
        return officeDoneInfoRepository.findByProcessInstanceIdAndTenantId(processInstanceId,
            Y9LoginUserHolder.getTenantId());
    }

    @Override
    public void saveOfficeDone(OfficeDoneInfo info) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = "";
        try {
            processInstanceId = info.getProcessInstanceId();
            OfficeDoneInfo doneInfo = null;
            try {
                doneInfo = officeDoneInfoRepository.findByProcessInstanceIdAndTenantId(processInstanceId,
                    Y9LoginUserHolder.getTenantId());
            } catch (Exception e) {
                LOGGER.warn("异常", e);
            }
            if (doneInfo == null) {
                officeDoneInfoRepository.save(info);
            } else {
                info.setId(doneInfo.getId());
                officeDoneInfoRepository.save(info);
            }
            LOGGER.info("***************************保存办结件信息成功*****************************");
        } catch (Exception e) {
            final Writer result = new StringWriter();
            LOGGER.warn("异常", e);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLog errorLogModel = new ErrorLog();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("ES流程信息数据保存失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogService.saveErrorLog(errorLogModel);
            } catch (Exception e1) {
                LOGGER.warn("异常", e1);
            }
            throw new Exception("OfficeDoneInfoManager saveOfficeDone error");
        }
    }

    @Override
    public Map<String, Object> searchAllByDeptId(String deptId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        dataMap.put(UtilConsts.SUCCESS, true);
        List<OfficeDoneInfoModel> list1 = new ArrayList<OfficeDoneInfoModel>();
        int totalPages = 1;
        long total = 0;
        try {
            if (page < 1) {
                page = 1;
            }
            Pageable pageable =
                PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Criteria criteria = new Criteria();
            criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
            criteria.and("deptId").contains(deptId);
            if (StringUtils.isNotBlank(itemId)) {
                criteria.and("itemId").is(itemId);
            }
            if (StringUtils.isNotBlank(title)) {
                criteria.and("title").contains(title).or(criteria.and("docNumber").contains(title));
            }
            if (StringUtils.isNotBlank(userName)) {
                criteria.and("creatUserName").contains(userName);
            }
            if (StringUtils.isNotBlank(year)) {
                criteria.and("startTime").contains(year);
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    criteria.and("endTime").empty();
                } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                    criteria.and("endTime").exists();
                }
            }
            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, INDEX);
            List<OfficeDoneInfo> list = searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());

            totalPages = (int)searchHits.getTotalHits() / rows;
            totalPages = searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1;

            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            total = searchHits.getTotalHits();
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            LOGGER.warn("异常", e);
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }

    @Override
    public Map<String, Object> searchAllByUserId(String userId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        dataMap.put(UtilConsts.SUCCESS, true);
        List<OfficeDoneInfoModel> list1 = new ArrayList<OfficeDoneInfoModel>();
        int totalPages = 1;
        long total = 0;
        try {
            if (page < 1) {
                page = 1;
            }
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Criteria criteria = new Criteria();
            criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
            criteria.and("allUserId").contains(userId);
            if (StringUtils.isNotBlank(itemId)) {
                criteria.and("itemId").is(itemId);
            }
            if (StringUtils.isNotBlank(title)) {
                criteria.and("title").contains(title).or(criteria.and("docNumber").contains(title));
            }
            if (StringUtils.isNotBlank(userName)) {
                criteria.and("creatUserName").contains(userName);
            }
            if (StringUtils.isNotBlank(year)) {
                criteria.and("startTime").contains(year);
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    criteria.and("endTime").empty();
                } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                    criteria.and("endTime").exists();
                }
            }
            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, INDEX);
            List<OfficeDoneInfo> list = searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());

            totalPages = (int)searchHits.getTotalHits() / rows;
            totalPages = searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1;

            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            total = searchHits.getTotalHits();
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            LOGGER.warn("异常", e);
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }

    @Override
    public Map<String, Object> searchAllList(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        dataMap.put(UtilConsts.SUCCESS, true);
        List<OfficeDoneInfoModel> list1 = new ArrayList<OfficeDoneInfoModel>();
        int totalPages = 1;
        long total = 0;
        try {
            if (page < 1) {
                page = 1;
            }
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Criteria criteria = new Criteria();
            criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
            if (StringUtils.isNotBlank(itemId)) {
                criteria.and("itemId").is(itemId);
            }
            if (StringUtils.isNotBlank(searchName)) {
                criteria.and("title").contains(searchName).or(criteria.and("docNumber").contains(searchName));
            }
            if (StringUtils.isNotBlank(userName)) {
                criteria.and("creatUserName").contains(userName);
            }
            if (StringUtils.isNotBlank(year)) {
                criteria.and("startTime").contains(year);
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    criteria.and("endTime").empty();
                } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                    criteria.and("endTime").exists();
                }
            }

            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, INDEX);
            List<OfficeDoneInfo> list = searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());

            totalPages = (int)searchHits.getTotalHits() / rows;
            totalPages = searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1;

            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            total = searchHits.getTotalHits();
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            LOGGER.warn("异常", e);
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }

    @Override
    public Map<String, Object> searchByItemId(String title, String itemId, String state, String startdate,
        String enddate, Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        dataMap.put(UtilConsts.SUCCESS, true);
        List<OfficeDoneInfoModel> list1 = new ArrayList<OfficeDoneInfoModel>();
        int totalPages = 1;
        long total = 0;
        try {
            if (page < 1) {
                page = 1;
            }
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Criteria criteria = new Criteria();
            criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
            if (StringUtils.isNotBlank(itemId)) {
                criteria.and("itemId").is(itemId);
            }
            if (StringUtils.isNotBlank(title)) {
                criteria.and("title").contains(title).or(criteria.and("docNumber").contains(title))
                    .or(criteria.and("creatUserName").contains(title));
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    criteria.and("endTime").empty();
                } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                    criteria.and("endTime").exists();
                }
            }
            if (StringUtils.isNotBlank(state) && state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "endTime"));
            }

            if (StringUtils.isNotBlank(startdate)) {
                startdate = startdate + " 00:00:00";
                criteria.and("startTime").greaterThanEqual(startdate);
            }
            if (StringUtils.isNotBlank(enddate)) {
                enddate = enddate + " 23:59:59";
                criteria.and("startTime").lessThanEqual(enddate);
            }

            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, INDEX);
            List<OfficeDoneInfo> list = searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());

            totalPages = (int)searchHits.getTotalHits() / rows;
            totalPages = searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1;

            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            total = searchHits.getTotalHits();
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            LOGGER.warn("异常", e);
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }

    @Override
    public Map<String, Object> searchByUserId(String userId, String title, String itemId, String startdate,
        String enddate, Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        dataMap.put(UtilConsts.SUCCESS, true);
        List<OfficeDoneInfoModel> list1 = new ArrayList<OfficeDoneInfoModel>();
        int totalPages = 1;
        long total = 0;
        try {
            if (page < 1) {
                page = 1;
            }
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "endTime"));
            Criteria criteria = new Criteria();
            criteria.and("tenantId").is(Y9LoginUserHolder.getTenantId());
            criteria.and("endTime").exists();
            if (StringUtils.isNotBlank(itemId)) {
                criteria.and("itemId").is(itemId);
            }
            if (StringUtils.isNotBlank(title)) {
                criteria.and("title").contains(title).or(criteria.and("docNumber").contains(title));
            }
            if (StringUtils.isNotBlank(startdate)) {
                startdate = startdate + " 00:00:00";
                criteria.and("startTime").greaterThanEqual(startdate);
            }
            if (StringUtils.isNotBlank(enddate)) {
                enddate = enddate + " 23:59:59";
                criteria.and("startTime").lessThanEqual(enddate);
            }

            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, INDEX);
            List<OfficeDoneInfo> list = searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());

            totalPages = (int)searchHits.getTotalHits() / rows;
            totalPages = searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1;

            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            total = searchHits.getTotalHits();
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            LOGGER.warn("异常", e);
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }
}
