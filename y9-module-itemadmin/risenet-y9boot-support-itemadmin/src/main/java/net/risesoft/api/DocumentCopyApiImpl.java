package net.risesoft.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentCopyApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.user.UserApi;
import net.risesoft.entity.DocumentCopy;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.opinion.OpinionCopy;
import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocumentCopyModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentCopyService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.opinion.OpinionCopyService;
import net.risesoft.service.util.ItemPageService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
@Validated
@RestController
@Slf4j
@RequestMapping(value = "/services/rest/documentCopy", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentCopyApiImpl implements DocumentCopyApi {

    private final DocumentCopyService documentCopyService;

    private final ProcessParamService processParamService;

    private final OpinionCopyService opinionCopyService;

    private final ItemPageService itemPageService;

    private final UserApi userApi;

    private final OrgUnitApi orgUnitApi;

    private final JdbcTemplate jdbcTemplate;

    public DocumentCopyApiImpl(
        DocumentCopyService documentCopyService,
        ProcessParamService processParamService,
        OpinionCopyService opinionCopyService,
        ItemPageService itemPageService,
        UserApi userApi,
        OrgUnitApi orgUnitApi,
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate) {
        this.documentCopyService = documentCopyService;
        this.processParamService = processParamService;
        this.opinionCopyService = opinionCopyService;
        this.itemPageService = itemPageService;
        this.userApi = userApi;
        this.orgUnitApi = orgUnitApi;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Y9Page<DocumentCopyModel> findByUserId(String tenantId, String userId, String orgUnitId,
        QueryParamModel queryParamModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int page = queryParamModel.getPage(), rows = queryParamModel.getRows();
        StringBuilder whereSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(orgUnitId);
        buildQueryConditions(queryParamModel, whereSql, params);
        String leftJoinSql = "LEFT JOIN FF_PROCESS_PARAM P ON C.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER ";
        String bySql = "ORDER BY P.CREATETIME DESC ) A WHERE A.RS_NUM = 1";
        // 使用参数化SQL
        String allSql =
            "SELECT A.* FROM (SELECT C.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER,ROW_NUMBER() OVER (PARTITION BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC) AS RS_NUM FROM FF_DOCUMENT_COPY C "
                + leftJoinSql + " WHERE C.STATUS < ? AND USERID = ? " + whereSql + bySql;
        String countSql =
            "SELECT COUNT(*) FROM ( SELECT ROW_NUMBER() OVER (PARTITION BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC) AS RS_NUM  FROM FF_DOCUMENT_COPY C "
                + leftJoinSql + " WHERE C.STATUS < ? AND USERID = ? " + whereSql + ") ALIAS WHERE RS_NUM = 1";
        List<Object> countParams = new ArrayList<>();
        countParams.add(DocumentCopyStatusEnum.CANCEL.getValue());
        countParams.add(orgUnitId);
        countParams.addAll(params.subList(1, params.size()));
        Object[] args = params.toArray();
        ItemPage<DocumentCopyModel> ardModelPage = itemPageService.page(allSql, args,
            new BeanPropertyRowMapper<>(DocumentCopyModel.class), countSql, countParams.toArray(), page, rows);
        return Y9Page.success(page, ardModelPage.getTotalpages(), ardModelPage.getTotal(), ardModelPage.getRows());
    }

    /**
     * 构建查询条件
     */
    private void buildQueryConditions(QueryParamModel queryParamModel, StringBuilder whereSql, List<Object> params) {
        Class<?> queryParamModelClazz = queryParamModel.getClass();
        Field[] fields = queryParamModelClazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (shouldSkipField(f.getName())) {
                continue;
            }
            try {
                Object fieldValue = f.get(queryParamModel);
                if (fieldValue != null) {
                    appendCondition(whereSql, params, f.getName(), fieldValue);
                }
            } catch (Exception e) {
                LOGGER.error("构建查询条件异常", e);
            }
        }
    }

    /**
     * 判断是否应该跳过该字段
     */
    private boolean shouldSkipField(String fieldName) {
        return "serialVersionUID".equals(fieldName) || "page".equals(fieldName) || "rows".equals(fieldName);
    }

    /**
     * 根据字段名添加相应的查询条件
     */
    private void appendCondition(StringBuilder whereSql, List<Object> params, String fieldName, Object fieldValue) {
        if ("systemName".equals(fieldName)) {
            whereSql.append(" AND P.SYSTEMNAME = ? ");
            params.add(fieldValue);
        } else if ("bureauIds".equals(fieldName)) {
            whereSql.append(" AND P.HOSTDEPTID = ? ");
            params.add(fieldValue);
        } else {
            // 使用白名单验证字段名，防止SQL注入
            String safeFieldName = validateAndSanitizeFieldName(fieldName);
            if (safeFieldName != null) {
                whereSql.append(" AND INSTR(P.").append(safeFieldName).append(",?) > 0 ");
                params.add(fieldValue);
            }
        }
    }

    /**
     * 验证并清理字段名，防止SQL注入
     */
    private String validateAndSanitizeFieldName(String fieldName) {
        // 白名单字段校验
        List<String> allowedFields = Arrays.asList("TITLE", "HOSTDEPTNAME", "CUSTOMNUMBER");
        String upperFieldName = fieldName.toUpperCase();
        if (allowedFields.contains(upperFieldName)) {
            return upperFieldName;
        }
        LOGGER.warn("非法字段名: {}", fieldName);
        return null;
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Y9Result<List<DocumentCopyModel>> findByProcessSerialNumbers(String tenantId, String userId,
        String orgUnitId, String[] processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < processSerialNumbers.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("'").append(processSerialNumbers[i]).append("'");
        }
        String sql =
            "SELECT A.* FROM (SELECT C.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER,ROW_NUMBER() OVER (PARTITION BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC) AS RS_NUM FROM FF_DOCUMENT_COPY C LEFT JOIN FF_PROCESS_PARAM P ON C.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER WHERE C.PROCESSSERIALNUMBER IN ( "
                + sb + ") ORDER BY P.CREATETIME DESC ) A WHERE A.RS_NUM = 1";
        List<DocumentCopyModel> content = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DocumentCopyModel.class));
        return Y9Result.success(content);
    }

    @Override
    public Y9Result<List<DocumentCopyModel>> findListByUserId(String tenantId, String userId, String orgUnitId,
        @RequestBody(required = false) QueryParamModel queryParamModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        StringBuilder whereSql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        params.add(orgUnitId);
        if (null != queryParamModel) {
            buildQueryConditions(queryParamModel, whereSql, params);
        }
        String allSql = buildAllSql(whereSql.toString());
        List<Object> finalParams = new ArrayList<>();
        finalParams.add(DocumentCopyStatusEnum.CANCEL.getValue());
        finalParams.addAll(params);
        List<DocumentCopyModel> content =
            jdbcTemplate.query(allSql, new BeanPropertyRowMapper<>(DocumentCopyModel.class), finalParams.toArray());
        return Y9Result.success(content);
    }

    private String buildAllSql(String whereSql) {
        String bySql = " ORDER BY P.CREATETIME DESC) A WHERE A.RS_NUM = 1";
        return "SELECT A.* FROM (SELECT D.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER,"
            + "ROW_NUMBER() OVER (PARTITION BY D.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC) AS RS_NUM "
            + "FROM FF_DOCUMENT_COPY D LEFT JOIN FF_PROCESS_PARAM P ON D.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER WHERE D.STATUS < ? AND D.USERID = ? "
            + whereSql + bySql;
    }

    @Override
    public Y9Result<List<DocumentCopyModel>> findByProcessSerialNumberAndSenderId(String tenantId, String userId,
        String orgUnitId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<DocumentCopy> dcList =
            documentCopyService.findByProcessSerialNumberAndSenderId(processSerialNumber, orgUnitId);
        List<DocumentCopyModel> modelList = new ArrayList<>();
        dcList.forEach(documentCopy -> {
            DocumentCopyModel model = new DocumentCopyModel();
            Y9BeanUtil.copyProperties(documentCopy, model);
            modelList.add(model);
        });
        return Y9Result.success(modelList);
    }

    @Override
    public Y9Result<Object> save(String tenantId, String userId, String orgUnitId, String processSerialNumber,
        String users, String opinion) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
        OpinionCopy newOc = new OpinionCopy();
        newOc.setId(Y9IdGenerator.genId());
        newOc.setProcessSerialNumber(processSerialNumber);
        newOc.setContent(StringUtils.isBlank(opinion) ? "无" : opinion);
        newOc.setSend(true);
        Optional<OpinionCopy> optional = opinionCopyService.saveOrUpdate(newOc);
        if (optional.isPresent()) {
            String opinionCopyId = optional.get().getId();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            List<DocumentCopy> list = new ArrayList<>();
            Arrays.stream(users.split(";")).forEach(user -> {
                String[] nameAndId = user.split(":");
                DocumentCopy documentCopy = new DocumentCopy();
                documentCopy.setId(Y9IdGenerator.genId());
                documentCopy.setOpinionCopyId(opinionCopyId);
                documentCopy.setProcessSerialNumber(processSerialNumber);
                documentCopy.setProcessInstanceId(processParam.getProcessInstanceId());
                documentCopy.setUserName(nameAndId[0]);
                documentCopy.setUserId(nameAndId[1]);
                documentCopy.setSenderId(orgUnitId);
                documentCopy.setSenderName(orgUnit.getName());
                documentCopy.setStatus(DocumentCopyStatusEnum.TODO_SIGN);
                documentCopy.setSystemName(processParam.getSystemName());
                list.add(documentCopy);
            });
            documentCopyService.save(list);
            return Y9Result.success();
        } else {
            return Y9Result.failure("保存意见失败");
        }
    }

    @Override
    public Y9Result<Object> setStatus(String tenantId, String userId, String orgUnitId, String id,
        DocumentCopyStatusEnum status) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Optional<DocumentCopy> optional = documentCopyService.findById(id);
        if (optional.isPresent()) {
            DocumentCopy documentCopy = optional.get();
            documentCopy.setStatus(status);
            documentCopyService.save(documentCopy);
            return Y9Result.success();
        }
        return Y9Result.failure("传签件不存在");
    }

    @Override
    public Y9Result<Object> deleteByProcessSerialNumber(String tenantId, String userId, String orgUnitId,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<DocumentCopy> dcList =
            documentCopyService.findByProcessSerialNumberAndUserId(processSerialNumber, orgUnitId);
        dcList.stream()
            .filter(documentCopy -> documentCopy.getStatus().getValue() < DocumentCopyStatusEnum.CANCEL.getValue())
            .forEach(documentCopy -> {
                documentCopy.setStatus(DocumentCopyStatusEnum.DELETE);
                documentCopyService.save(documentCopy);
            });
        return Y9Result.success();
    }
}
