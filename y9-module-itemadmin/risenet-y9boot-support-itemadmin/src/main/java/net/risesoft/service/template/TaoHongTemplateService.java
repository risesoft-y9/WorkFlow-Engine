package net.risesoft.service.template;

import java.util.List;

import net.risesoft.entity.template.TaoHongTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TaoHongTemplateService {

    /**
     * 根据id查找套红模板
     *
     * @param id
     * @return
     */
    TaoHongTemplate getById(String id);

    /**
     * 根据委办局Id查找套红模板（委办局Id是唯一的，所以这里就不在加上租户Id这个条件）
     *
     * @param bureauGuid
     * @return
     */
    List<TaoHongTemplate> listByBureauGuid(String bureauGuid);

    /**
     * Description:查找当前租户的套红模板
     *
     * @param tenantId
     * @param name
     * @return
     */
    List<TaoHongTemplate> listByTenantId(String tenantId, String name);

    /**
     * 根据Id删除模板
     *
     * @param id
     */
    void removeTaoHongTemplate(String id);

    /**
     * 根据传进来的模板Id数组来删除模板
     *
     * @param templateGuids
     */
    void removeTaoHongTemplate(String[] templateGuids);

    /**
     * 新增或者修改套红模板
     *
     * @param taoHongTemplate
     * @return
     */
    TaoHongTemplate saveOrUpdate(TaoHongTemplate taoHongTemplate);
}
