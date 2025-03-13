package net.risesoft.api;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import net.risesoft.api.itemadmin.DocumentCopyApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.DocumentCopy;
import net.risesoft.entity.OpinionCopy;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocumentCopyModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentCopyService;
import net.risesoft.service.ItemPageService;
import net.risesoft.service.OpinionCopyService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
@Validated
@RestController
@RequestMapping(value = "/services/rest/documentCopy", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentCopyApiImpl implements DocumentCopyApi {

    private final DocumentCopyService documentCopyService;

    private final ProcessParamService processParamService;

    private final OpinionCopyService opinionCopyService;

    private final ItemPageService itemPageService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final JdbcTemplate jdbcTemplate;

    public DocumentCopyApiImpl(DocumentCopyService documentCopyService, ProcessParamService processParamService,
        OpinionCopyService opinionCopyService, ItemPageService itemPageService, PersonApi personApi,
        OrgUnitApi orgUnitApi, @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate) {
        this.documentCopyService = documentCopyService;
        this.processParamService = processParamService;
        this.opinionCopyService = opinionCopyService;
        this.itemPageService = itemPageService;
        this.personApi = personApi;
        this.orgUnitApi = orgUnitApi;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Y9Page<DocumentCopyModel> findByUserId(String tenantId, String userId, String orgUnitId,
        QueryParamModel queryParamModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int page = queryParamModel.getPage(), rows = queryParamModel.getRows();
        String systemNameSql = "";
        StringBuilder paramSql = new StringBuilder();
        Object object = queryParamModel;
        Class queryParamModelClazz = object.getClass();
        Field[] fields = queryParamModelClazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if ("serialVersionUID".equals(f.getName()) || "page".equals(f.getName()) || "rows".equals(f.getName())) {
                continue;
            }
            Object fieldValue;
            try {
                fieldValue = f.get(object);
                if (null != fieldValue) {
                    if ("systemName".equals(f.getName())) {
                        systemNameSql = StringUtils.isBlank(queryParamModel.getSystemName()) ? ""
                            : " AND P.SYSTEMNAME = '" + fieldValue + "' ";
                    } else {
                        paramSql.append(" AND INSTR(P.").append(f.getName().toUpperCase()).append(",'")
                            .append(fieldValue).append("') > 0 ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String processParamSql = "LEFT JOIN FF_PROCESS_PARAM P ON C.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER ";
        String bySql = "GROUP BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC";
        String allSql = "SELECT C.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER FROM FF_DOCUMENT_COPY C "
            + processParamSql + " WHERE C.STATUS < " + DocumentCopyStatusEnum.CANCEL.getValue() + paramSql
            + systemNameSql + " AND C.USERID = ? " + bySql;
        String countSql =
            "SELECT COUNT(C.ID) FROM FF_DOCUMENT_COPY C " + processParamSql + " WHERE C.USERID= ? AND C.STATUS < "
                + DocumentCopyStatusEnum.CANCEL.getValue() + paramSql + systemNameSql;
        Object[] args = new Object[1];
        args[0] = orgUnitId;
        ItemPage<DocumentCopyModel> ardModelPage = itemPageService.page(allSql, args,
            new BeanPropertyRowMapper<>(DocumentCopyModel.class), countSql, args, page, rows);
        return Y9Page.success(page, ardModelPage.getTotalpages(), ardModelPage.getTotal(), ardModelPage.getRows());
    }

    @Override
    public Y9Result<List<DocumentCopyModel>> findByProcessSerialNumbers(String tenantId, String userId,
        String orgUnitId, String[] processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        StringBuilder sb = new StringBuilder();
        Arrays.stream(processSerialNumbers).forEach(processSerialNumber -> {
            if (StringUtils.isBlank(sb.toString())) {
                sb.append("'").append(processSerialNumber).append("'");
            } else {
                sb.append(",'").append(processSerialNumber).append("'");
            }
        });
        String sql =
            "SELECT C.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER FROM FF_DOCUMENT_COPY C LEFT JOIN FF_PROCESS_PARAM P ON C.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER WHERE C.PROCESSSERIALNUMBER IN ( "
                + sb + ") GROUP BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC";
        List<DocumentCopyModel> content = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DocumentCopyModel.class));
        return Y9Result.success(content);
    }

    @Override
    public Y9Result<List<DocumentCopyModel>> findListByUserId(String tenantId, String userId, String orgUnitId,
        @RequestBody(required = false) QueryParamModel queryParamModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String systemNameSql = "";
        StringBuilder paramSql = new StringBuilder();
        if (null != queryParamModel) {
            Object object = queryParamModel;
            Class queryParamModelClazz = object.getClass();
            Field[] fields = queryParamModelClazz.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if ("serialVersionUID".equals(f.getName()) || "page".equals(f.getName())
                    || "rows".equals(f.getName())) {
                    continue;
                }
                Object fieldValue;
                try {
                    fieldValue = f.get(object);
                    if (null != fieldValue) {
                        if ("systemName".equals(f.getName())) {
                            systemNameSql = StringUtils.isBlank(queryParamModel.getSystemName()) ? ""
                                : " AND P.SYSTEMNAME = '" + fieldValue + "' ";
                        } else {
                            paramSql.append(" AND INSTR(P.").append(f.getName().toUpperCase()).append(",'")
                                .append(fieldValue).append("') > 0 ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String processParamSql = "LEFT JOIN FF_PROCESS_PARAM P ON C.PROCESSSERIALNUMBER = P.PROCESSSERIALNUMBER ";
        String bySql = "GROUP BY C.PROCESSSERIALNUMBER ORDER BY P.CREATETIME DESC";
        String allSql = "SELECT C.*,P.SYSTEMCNNAME,P.TITLE,P.HOSTDEPTNAME,P.CUSTOMNUMBER FROM FF_DOCUMENT_COPY C "
            + processParamSql + " WHERE C.STATUS < " + DocumentCopyStatusEnum.CANCEL.getValue() + paramSql
            + systemNameSql + " AND C.USERID = ? " + bySql;
        Object[] args = {orgUnitId};
        List<DocumentCopyModel> content =
            jdbcTemplate.query(allSql, args, new BeanPropertyRowMapper<>(DocumentCopyModel.class));
        return Y9Result.success(content);
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                documentCopy.setStatus(DocumentCopyStatusEnum.TODO_SIGN.getValue());
                documentCopy.setSystemName(processParam.getSystemName());
                documentCopy.setCreateTime(sdf.format(new Date()));
                documentCopy.setUpdateTime(sdf.format(new Date()));
                list.add(documentCopy);
            });
            documentCopyService.save(list);
            return Y9Result.success();
        } else {
            return Y9Result.failure("保存意见失败");
        }
    }

    @Override
    public Y9Result<Object> setStatus(String tenantId, String userId, String orgUnitId, String id, Integer status) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Optional<DocumentCopy> optional = documentCopyService.findById(id);
        if (optional.isPresent()) {
            DocumentCopy documentCopy = optional.get();
            documentCopy.setStatus(status);
            documentCopy.setUpdateTime(sdf.format(new Date()));
            documentCopyService.save(documentCopy);
            return Y9Result.success();
        }
        return Y9Result.failure("传签件不存在");
    }

    @Override
    public Y9Result<Object> deleteByProcessSerialNumber(String tenantId, String userId, String orgUnitId,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DocumentCopy> dcList =
            documentCopyService.findByProcessSerialNumberAndUserId(processSerialNumber, orgUnitId);
        dcList.stream().filter(documentCopy -> documentCopy.getStatus() < DocumentCopyStatusEnum.CANCEL.getValue())
            .forEach(documentCopy -> {
                documentCopy.setStatus(DocumentCopyStatusEnum.DELETE.getValue());
                documentCopy.setUpdateTime(sdf.format(new Date()));
                documentCopyService.save(documentCopy);
            });
        return Y9Result.success();
    }
}
