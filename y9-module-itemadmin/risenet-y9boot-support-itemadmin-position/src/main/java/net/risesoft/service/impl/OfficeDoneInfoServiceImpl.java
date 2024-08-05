package net.risesoft.service.impl;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ErrorLog;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.nosql.elastic.repository.OfficeDoneInfoRepository;
import net.risesoft.pojo.Y9Page;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeDoneInfoServiceImpl implements OfficeDoneInfoService {

    private static final IndexCoordinates INDEX = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);

    private final ErrorLogService errorLogService;

    private final OfficeDoneInfoRepository officeDoneInfoRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final OrgUnitApi orgUnitApi;

    @Override
    public void cancelMeeting(String processInstanceId) {
        OfficeDoneInfo info = officeDoneInfoRepository.findByProcessInstanceIdAndTenantId(processInstanceId,
            Y9LoginUserHolder.getTenantId());
        if (info != null) {
            info.setMeeting("0");
            info.setMeetingType("");
            officeDoneInfoRepository.save(info);
        }
    }

    @Override
    public int countByItemId(String itemId) {
        try {
            Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("endTime").exists();
            if (StringUtils.isNotBlank(itemId)) {
                criteria.subCriteria(new Criteria("itemId").is(itemId));
            }
            Query query = new CriteriaQuery(criteria);

            return (int)elasticsearchTemplate.count(query, INDEX);
        } catch (Exception e) {
            LOGGER.warn("异常", e);
        }
        return 0;
    }

    @Override
    public int countByUserId(String userId, String itemId) {
        try {
            Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("endTime").exists()
                .and("allUserId").contains(userId);
            if (StringUtils.isNotBlank(itemId)) {
                criteria.subCriteria(new Criteria("itemId").is(itemId));
            }
            Query query = new CriteriaQuery(criteria);
            long count = elasticsearchTemplate.count(query, INDEX);
            return (int)count;
        } catch (Exception e) {
            LOGGER.warn("异常", e);
        }
        return 0;
    }

    @Override
    public int countByUserIdAndSystemName(String orgUnitId, String systemName) {
        try {
            Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("endTime").exists()
                .and("allUserId").contains(orgUnitId);
            if (StringUtils.isNotBlank(systemName)) {
                criteria.subCriteria(new Criteria("systemName").is(systemName));
            }
            Query query = new CriteriaQuery(criteria);
            long count = elasticsearchTemplate.count(query, INDEX);
            return (int)count;
        } catch (Exception e) {
            LOGGER.warn("异常", e);
        }
        return 0;
    }

    @Override
    public long countDoingByItemId(String itemId) {
        try {
            Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("endTime").exists();
            if (StringUtils.isNotBlank(itemId)) {
                criteria.subCriteria(new Criteria("itemId").is(itemId));
            }
            Query query = new CriteriaQuery(criteria);
            long count = elasticsearchTemplate.count(query, INDEX);
            return (int)count;
        } catch (Exception e) {
            LOGGER.warn("异常", e);
        }
        return 0;
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
    public Y9Page<OfficeDoneInfoModel> pageMeetingList(String userName, String deptName, String title,
        String meetingType, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("meeting").is("1");
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(deptName)) {
            criteria.subCriteria(new Criteria("deptName").contains(deptName));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria("creatUserName").contains(userName));
        }
        if (StringUtils.isNotBlank(meetingType)) {
            criteria.subCriteria(new Criteria("meetingType").is(meetingType).and("meetingType").exists());
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
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
    public Y9Page<OfficeDoneInfoModel> searchAllByDeptId(String deptId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("deptId").contains(deptId);
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria("creatUserName").contains(userName));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria("startTime").startsWith(year));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByUserId(String userId, String title, String itemId, String userName,
        String state, String year, String startDate, String endDate, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");

        Criteria criteria =
            new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("allUserId").contains(userId);
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria("creatUserName").contains(userName));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria("startTime").startsWith(year));
        }
        if (StringUtils.isNotBlank(startDate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startDate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(endDate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(endDate + " 23:59:59"));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByUserIdAndSystemName(String orgUnitId, String title, String systemName,
        String itemId, String state, String year, String startDate, String endDate, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
        Criteria criteria =
            new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("allUserId").contains(orgUnitId);
        if (StringUtils.isNotBlank(systemName)) {
            criteria.subCriteria(new Criteria("systemName").is(systemName));
        }
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria("startTime").startsWith(year));
        }
        if (StringUtils.isNotBlank(startDate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startDate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(endDate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(endDate + " 23:59:59"));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByUserIdAndSystemName4xxx(String orgUnitId, String title,
        String systemName, String itemId, String target, String state, String year, String startDate, String endDate,
        Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
        Criteria criteria =
            new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("allUserId").contains(orgUnitId);
        if (StringUtils.isNotBlank(systemName)) {
            criteria.subCriteria(new Criteria("systemName").is(systemName));
        }
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(target)) {
            criteria.subCriteria(new Criteria("target").is(target));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria("startTime").startsWith(year));
        }
        if (StringUtils.isNotBlank(startDate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startDate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(endDate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(endDate + " 23:59:59"));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllList(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");

        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId());
        if (StringUtils.isNotBlank(searchName)) {
            criteria.subCriteria(new Criteria("title").contains(searchName).or("docNumber").contains(searchName));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria("creatUserName").contains(userName));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria("startTime").startsWith(year));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();
        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchByItemId(String title, String itemId, String state, String startdate,
        String enddate, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");

        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId());
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title)
                .or("creatUserName").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(startdate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startdate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(enddate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(enddate + " 23:59:59"));
        }
        if (StringUtils.isNotBlank(state)) {
            if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                criteria.subCriteria(new Criteria("endTime").not().exists());
            } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                criteria.subCriteria(new Criteria("endTime").exists());
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();

        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchByUserId(String userId, String title, String itemId, String startdate,
        String enddate, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "endTime");

        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("allUserId")
            .contains(userId).and("endTime").exists();
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria("itemId").is(itemId));
        }
        if (StringUtils.isNotBlank(startdate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startdate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(enddate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(enddate + " 23:59:59"));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();

        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public Y9Page<OfficeDoneInfoModel> searchByUserIdAndSystemName(String orgUnitId, String title, String systemName,
        String startdate, String enddate, Integer page, Integer rows) {
        List<OfficeDoneInfoModel> list1 = new ArrayList<>();
        int totalPages = 1;
        long total = 0;
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "endTime");

        Criteria criteria = new Criteria("tenantId").is(Y9LoginUserHolder.getTenantId()).and("allUserId")
            .contains(orgUnitId).and("endTime").exists();
        if (StringUtils.isNotBlank(title)) {
            criteria.subCriteria(new Criteria("title").contains(title).or("docNumber").contains(title));
        }
        if (StringUtils.isNotBlank(systemName)) {
            criteria.subCriteria(new Criteria("systemName").is(systemName));
        }

        if (StringUtils.isNotBlank(startdate)) {
            criteria.subCriteria(new Criteria("startTime").greaterThanEqual(startdate + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(enddate)) {
            criteria.subCriteria(new Criteria("startTime").lessThanEqual(enddate + " 23:59:59"));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
        SearchHits<OfficeDoneInfo> searchHits = elasticsearchTemplate.search(query, OfficeDoneInfo.class, index);
        List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<OfficeDoneInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        List<OfficeDoneInfo> list = pageList.getContent();
        OfficeDoneInfoModel officeDoneInfoModel;
        for (OfficeDoneInfo officeDoneInfo : list) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
            list1.add(officeDoneInfoModel);
        }
        totalPages = pageList.getTotalPages();
        total = pageList.getTotalElements();

        return Y9Page.success(page, totalPages, total, list1);
    }

    @Override
    public void setMeeting(String processInstanceId, String meetingType) {
        OfficeDoneInfo info = officeDoneInfoRepository.findByProcessInstanceIdAndTenantId(processInstanceId,
            Y9LoginUserHolder.getTenantId());
        if (info != null) {
            info.setMeeting("1");
            info.setMeetingType(meetingType);
            OrgUnit dept = orgUnitApi.getParent(Y9LoginUserHolder.getTenantId(), info.getCreatUserId()).getData();
            info.setDeptName(dept != null ? dept.getName() : "");
            officeDoneInfoRepository.save(info);
        }
    }

}
