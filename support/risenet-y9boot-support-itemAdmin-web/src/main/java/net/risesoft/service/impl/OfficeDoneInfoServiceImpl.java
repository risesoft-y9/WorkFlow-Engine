package net.risesoft.service.impl;

import java.io.PrintWriter;
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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * @date 2022/12/22
 */
@Service(value = "officeDoneInfoService")
@Transactional(readOnly = true)
@Slf4j
public class OfficeDoneInfoServiceImpl implements OfficeDoneInfoService {

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private OfficeDoneInfoRepository officeDoneInfoRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @Override
    public int countByItemId(String itemId) {
        try {
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            builder.must(QueryBuilders.existsQuery("endTime"));
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            SearchRequest searchRequest = new SearchRequest(Y9EsIndexConst.OFFICE_DONEINFO);
            searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(builder);
            searchRequest.source(searchSourceBuilder);
            long count = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits().length;
            return (int)count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countByUserId(String userId, String itemId) {
        try {
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("allUserId", "*" + userId + "*"));
            builder.must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            builder.must(QueryBuilders.existsQuery("endTime"));
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            SearchRequest searchRequest = new SearchRequest(Y9EsIndexConst.OFFICE_DONEINFO);
            searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(builder);
            searchRequest.source(searchSourceBuilder);
            long count = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits().length;
            return (int)count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countDoingByItemId(String itemId) {
        try {
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            builder.mustNot(QueryBuilders.existsQuery("endTime"));
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            SearchRequest searchRequest = new SearchRequest(Y9EsIndexConst.OFFICE_DONEINFO);
            searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(builder);
            searchRequest.source(searchSourceBuilder);
            long count = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits().length;
            return (int)count;
        } catch (Exception e) {
            e.printStackTrace();
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
            b = true;
        } catch (Exception e) {
            LOGGER.warn("*************************数据中心删除失败****************************", e);
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
                e.printStackTrace();
            }
            if (doneInfo == null) {
                officeDoneInfoRepository.save(info);
            } else {
                info.setId(doneInfo.getId());
                officeDoneInfoRepository.save(info);
            }
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
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
                e1.printStackTrace();
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
            Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("deptId", "*" + deptId + "*"));
            builder.must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            if (StringUtils.isNotBlank(title)) {
                BoolQueryBuilder builder2 =
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("title", "*" + title + "*"));
                builder2.should(QueryBuilders.wildcardQuery("docNumber", "*" + title + "*"));
                builder.must(builder2);
            }
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            if (StringUtils.isNotBlank(userName)) {
                builder.must(QueryBuilders.wildcardQuery("creatUserName", "*" + userName + "*"));
            }
            if (StringUtils.isNotBlank(year)) {
                builder.must(QueryBuilders.wildcardQuery("startTime", year + "*"));
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    builder.mustNot(QueryBuilders.existsQuery("endTime"));
                } else if (ItemBoxTypeEnum.DONE.getValue().equals(state)) {
                    builder.must(QueryBuilders.existsQuery("endTime"));
                }
            }
            IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(builder).withPageable(pageable).build();
            searchQuery.setTrackTotalHits(true);
            SearchHits<OfficeDoneInfo> searchHits =
                elasticsearchOperations.search(searchQuery, OfficeDoneInfo.class, index);
            List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            Page<OfficeDoneInfo> pageList = new PageImpl<OfficeDoneInfo>(list0, pageable, searchHits.getTotalHits());
            List<OfficeDoneInfo> list = pageList.getContent();
            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            totalPages = pageList != null ? pageList.getTotalPages() : 1;
            total = pageList != null ? pageList.getTotalElements() : 0;
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
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
            Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("allUserId", "*" + userId + "*"));
            builder.must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            if (StringUtils.isNotBlank(title)) {
                BoolQueryBuilder builder2 =
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("title", "*" + title + "*"));
                builder2.should(QueryBuilders.wildcardQuery("docNumber", "*" + title + "*"));
                builder.must(builder2);
            }
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            if (StringUtils.isNotBlank(userName)) {
                builder.must(QueryBuilders.wildcardQuery("creatUserName", "*" + userName + "*"));
            }
            if (StringUtils.isNotBlank(year)) {
                builder.must(QueryBuilders.wildcardQuery("startTime", year + "*"));
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    builder.mustNot(QueryBuilders.existsQuery("endTime"));
                } else if (ItemBoxTypeEnum.DONE.getValue().equals(state)) {
                    builder.must(QueryBuilders.existsQuery("endTime"));
                }
            }

            IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(builder).withPageable(pageable).build();
            searchQuery.setTrackTotalHits(true);
            SearchHits<OfficeDoneInfo> searchHits =
                elasticsearchOperations.search(searchQuery, OfficeDoneInfo.class, index);
            List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            Page<OfficeDoneInfo> pageList = new PageImpl<OfficeDoneInfo>(list0, pageable, searchHits.getTotalHits());

            // Page<OfficeDoneInfo> pageList = officeDoneInfoRepository.search(builder,
            // pageable);
            List<OfficeDoneInfo> list = pageList.getContent();
            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            totalPages = pageList != null ? pageList.getTotalPages() : 1;
            total = pageList != null ? pageList.getTotalElements() : 0;
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
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
            Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            if (StringUtils.isNotBlank(searchName)) {
                BoolQueryBuilder builder2 =
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("title", "*" + searchName + "*"));
                builder2.should(QueryBuilders.wildcardQuery("docNumber", "*" + searchName + "*"));
                builder.must(builder2);
            }
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            if (StringUtils.isNotBlank(userName)) {
                builder.must(QueryBuilders.wildcardQuery("creatUserName", "*" + userName + "*"));
            }
            if (StringUtils.isNotBlank(year)) {
                builder.must(QueryBuilders.wildcardQuery("startTime", year + "*"));
            }
            if (StringUtils.isNotBlank(state)) {
                if (ItemBoxTypeEnum.TODO.getValue().equals(state)) {
                    builder.mustNot(QueryBuilders.existsQuery("endTime"));
                } else if (ItemBoxTypeEnum.DONE.getValue().equals(state)) {
                    builder.must(QueryBuilders.existsQuery("endTime"));
                }
            }

            IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(builder).withPageable(pageable).build();
            searchQuery.setTrackTotalHits(true);
            SearchHits<OfficeDoneInfo> searchHits =
                elasticsearchOperations.search(searchQuery, OfficeDoneInfo.class, index);
            List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            Page<OfficeDoneInfo> pageList = new PageImpl<OfficeDoneInfo>(list0, pageable, searchHits.getTotalHits());

            List<OfficeDoneInfo> list = pageList.getContent();
            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            totalPages = pageList != null ? pageList.getTotalPages() : 1;
            total = pageList != null ? pageList.getTotalElements() : 0;
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
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
            Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "startTime");
            if (StringUtils.isNotBlank(state) && state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                pageable = PageRequest.of(page - 1, rows, Direction.DESC, "endTime");
            }
            BoolQueryBuilder builder = QueryBuilders.boolQuery()
                .must(QueryBuilders.wildcardQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            if (StringUtils.isNotBlank(state)) {
                if (state.equals(ItemBoxTypeEnum.DOING.getValue())) {
                    builder.mustNot(QueryBuilders.existsQuery("endTime"));
                } else if (state.equals(ItemBoxTypeEnum.DONE.getValue())) {
                    builder.must(QueryBuilders.existsQuery("endTime"));
                }
            }
            if (StringUtils.isNotBlank(title)) {
                BoolQueryBuilder builder2 =
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("title", "*" + title + "*"));
                builder2.should(QueryBuilders.wildcardQuery("docNumber", "*" + title + "*"));
                builder2.should(QueryBuilders.wildcardQuery("creatUserName", "*" + title + "*"));
                builder.must(builder2);
            }
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            if (StringUtils.isNotBlank(startdate)) {
                startdate = startdate + " 00:00:00";
                builder.must(QueryBuilders.rangeQuery("startTime").gte(startdate));
            }
            if (StringUtils.isNotBlank(enddate)) {
                enddate = enddate + " 23:59:59";
                builder.must(QueryBuilders.rangeQuery("startTime").lte(enddate));
            }

            IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(builder).withPageable(pageable).build();
            searchQuery.setTrackTotalHits(true);
            SearchHits<OfficeDoneInfo> searchHits =
                elasticsearchOperations.search(searchQuery, OfficeDoneInfo.class, index);
            List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            Page<OfficeDoneInfo> pageList = new PageImpl<OfficeDoneInfo>(list0, pageable, searchHits.getTotalHits());

            // Page<OfficeDoneInfo> pageList = officeDoneInfoRepository.search(builder,
            // pageable);
            List<OfficeDoneInfo> list = pageList.getContent();
            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            totalPages = pageList != null ? pageList.getTotalPages() : 1;
            total = pageList != null ? pageList.getTotalElements() : 0;
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
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
            Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, "endTime");
            BoolQueryBuilder builder =
                QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("allUserId", "*" + userId + "*"));
            builder.must(QueryBuilders.termsQuery("tenantId", Y9LoginUserHolder.getTenantId()));
            builder.must(QueryBuilders.existsQuery("endTime"));
            if (StringUtils.isNotBlank(title)) {
                BoolQueryBuilder builder2 =
                    QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("title", "*" + title + "*"));
                builder2.should(QueryBuilders.wildcardQuery("docNumber", "*" + title + "*"));
                builder.must(builder2);
            }
            if (StringUtils.isNotBlank(itemId)) {
                builder.must(QueryBuilders.termsQuery("itemId", itemId));
            }
            if (StringUtils.isNotBlank(startdate)) {
                startdate = startdate + " 00:00:00";
                builder.must(QueryBuilders.rangeQuery("startTime").gte(startdate));
            }
            if (StringUtils.isNotBlank(enddate)) {
                enddate = enddate + " 23:59:59";
                builder.must(QueryBuilders.rangeQuery("startTime").lte(enddate));
            }

            IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.OFFICE_DONEINFO);
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(builder).withPageable(pageable).build();
            searchQuery.setTrackTotalHits(true);
            SearchHits<OfficeDoneInfo> searchHits =
                elasticsearchOperations.search(searchQuery, OfficeDoneInfo.class, index);
            List<OfficeDoneInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
            Page<OfficeDoneInfo> pageList = new PageImpl<OfficeDoneInfo>(list0, pageable, searchHits.getTotalHits());

            // Page<OfficeDoneInfo> pageList = officeDoneInfoRepository.search(builder,
            // pageable);
            List<OfficeDoneInfo> list = pageList.getContent();
            OfficeDoneInfoModel officeDoneInfoModel = null;
            for (OfficeDoneInfo officeDoneInfo : list) {
                officeDoneInfoModel = new OfficeDoneInfoModel();
                Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
                list1.add(officeDoneInfoModel);
            }
            totalPages = pageList != null ? pageList.getTotalPages() : 1;
            total = pageList != null ? pageList.getTotalElements() : 0;
        } catch (Exception e) {
            dataMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        dataMap.put("currpage", page);
        dataMap.put("totalpages", totalPages);
        dataMap.put("total", total);
        dataMap.put("rows", list1);
        return dataMap;
    }

}
