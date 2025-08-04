package net.risesoft.service.template;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.entity.template.PrintTemplate;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface PrintTemplateService {

    /**
     * 复制打印模板绑定信息
     *
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId, String newItemId);

    /**
     * 删除打印模板绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 删除绑定的打印模板
     *
     * @param id
     * @return
     */
    Y9Result<String> deleteBindPrintTemplate(String id);

    /**
     * 删除打印模板
     *
     * @param id
     * @return
     */
    Y9Result<String> deletePrintTemplate(String id);

    /**
     * 下载模板
     *
     * @param id
     * @param response
     * @param request
     */
    void download(String id, HttpServletResponse response, HttpServletRequest request);

    /**
     * Description:
     *
     * @return
     */
    List<PrintTemplate> listAll();

    /**
     * 根据模版名称模糊搜索打印模板的列表
     *
     * @param fileName 文档名称
     * @return
     */
    List<PrintTemplate> listByFileNameLike(String fileName);

    /**
     * 查询绑定打印模板的列表
     *
     * @param itemId
     * @return
     */
    List<ItemPrintTemplateBind> listTemplateBindByItemId(String itemId);

    /**
     * Description: 保存绑定的模板
     *
     * @param itemId
     * @param templateId
     * @param templateName
     * @param templateUrl
     * @param templateType
     * @return
     */
    Y9Result<String> saveBindTemplate(String itemId, String templateId, String templateName, String templateUrl,
        String templateType);

    /**
     * Description:
     *
     * @param printTemplate
     */
    void saveOrUpdate(PrintTemplate printTemplate);

    /**
     * 上传打印模板
     *
     * @param file
     * @return
     */
    Y9Result<String> uploadTemplate(MultipartFile file);
}
