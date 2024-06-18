package net.risesoft.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.pool.DruidDataSource;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.db.DbUtil;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;

import y9.dbcomment.Y9CommentUtil;

@RestController
@RequestMapping("/admin/comment")
public class Y9CommentController {

    @Autowired
    @Qualifier("y9PublicDS")
    private DruidDataSource y9PublicDS;

    @Autowired
    @Qualifier("defaultDataSource")
    private DruidDataSource y9FlowableDS;

    @Autowired
    @Qualifier("y9TenantDataSource")
    private Y9TenantDataSource y9TenantDS;

    private JdbcTemplate jdbcTemplate4Public = null;

    private JdbcTemplate jdbcTemplate4FlowableDefault = null;

    private JdbcTemplate jdbcTemplate4Tenant = null;

    @PostConstruct
    public void init() {
        this.jdbcTemplate4Public = new JdbcTemplate(this.y9PublicDS);
        this.jdbcTemplate4FlowableDefault = new JdbcTemplate(this.y9FlowableDS);
        this.jdbcTemplate4Tenant = new JdbcTemplate(this.y9TenantDS);
    }

    @RequestMapping(value = "/refresh")
    public String refreshComment() {
        String dbType = DbUtil.getDbTypeString(y9FlowableDS);

        String y9publicPackageEntity = Y9Context.getProperty("y9.feature.comment.packagesToScanEntityPublic", "net.risesoft.y9public.entity");// 自定义公共表路径
        String packageEntity = Y9Context.getProperty("y9.app.itemAdmin.jpa.packagesToScanEntity", "net.risesoft.entity");// 自定义租户表路径
        /*
          默认库
         */
        if ("mysql".equals(dbType)) {
            Y9CommentUtil.scanner4Mysql(jdbcTemplate4FlowableDefault, y9publicPackageEntity.split(","));
        } else if ("oracle".equals(dbType)) {
            Y9CommentUtil.scanner4Oracle(jdbcTemplate4FlowableDefault, y9publicPackageEntity.split(","));
        }

        /*
          租户库
         */
        List<String> tenants = jdbcTemplate4Public.queryForList("select id from Y9_COMMON_TENANT", String.class);
        for (String tenantId : tenants) {
            Y9LoginUserHolder.setTenantId(tenantId);
            DataSource ds = y9TenantDS.determineTargetDataSource();
            dbType = DbUtil.getDbTypeString(ds);
            if (dbType != null && "mysql".equals(dbType)) {
                Y9CommentUtil.scanner4Mysql(jdbcTemplate4Tenant, packageEntity.split(","));
            } else if (dbType != null && "oracle".equals(dbType)) {
                Y9CommentUtil.scanner4Oracle(jdbcTemplate4Tenant, packageEntity.split(","));
            }
        }

        return "finished.";
    }

}
