package net.risesoft.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.BookMarkBindApi;
import net.risesoft.entity.BookMarkBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 书签绑定接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/bookMarkBind")
public class BookMarkBindApiImpl implements BookMarkBindApi {

    private final JdbcTemplate jdbcTemplate;
    private final BookMarkBindService bookMarkBindService;

    public BookMarkBindApiImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate,
        BookMarkBindService bookMarkBindService) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMarkBindService = bookMarkBindService;
    }

    /**
     * 根据模板和流程序列号查询模板的书签对应的值
     *
     * @param tenantId 租户id
     * @param wordTemplateId 模板id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map < String, Object>>} 通用请求返回对象 - data 是书签对应的值
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getBookMarkData(@RequestParam String tenantId,
        @RequestParam String wordTemplateId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        List<BookMarkBind> bookMarkBindList = bookMarkBindService.findByWordTemplateId(wordTemplateId);
        if (!bookMarkBindList.isEmpty()) {
            String tableName = bookMarkBindList.get(0).getTableName();
            String columnName = "";
            for (BookMarkBind boorMark : bookMarkBindList) {
                if (StringUtils.isBlank(columnName)) {
                    columnName = boorMark.getColumnName() + " AS " + boorMark.getBookMarkName();
                } else {
                    columnName += "," + boorMark.getColumnName() + " AS " + boorMark.getBookMarkName();
                }
            }
            String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE GUID='" + processSerialNumber + "'";
            map = jdbcTemplate.queryForMap(sql);
        }
        return Y9Result.success(map);
    }
}
