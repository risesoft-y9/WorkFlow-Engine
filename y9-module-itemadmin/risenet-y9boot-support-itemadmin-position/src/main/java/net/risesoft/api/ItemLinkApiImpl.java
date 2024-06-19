package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ItemLink4PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.ItemLinkRole;
import net.risesoft.entity.LinkInfo;
import net.risesoft.model.itemadmin.LinkInfoModel;
import net.risesoft.repository.jpa.ItemLinkBindRepository;
import net.risesoft.repository.jpa.ItemLinkRoleRepository;
import net.risesoft.repository.jpa.LinkInfoRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 事项链接接口
 *
 * @author zhangchongjie
 * @date 2024/05/14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemLink4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemLinkApiImpl implements ItemLink4PositionApi {

    private final PositionRoleApi positionRoleApi;

    private final ItemLinkBindRepository itemLinkBindRepository;

    private final LinkInfoRepository linkInfoRepository;

    private final ItemLinkRoleRepository itemLinkRoleRepository;

    /**
     * 获取事项链接列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return List<LinkInfoModel>
     */
    @Override
    public List<LinkInfoModel> getItemLinkList(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<LinkInfoModel> res_list = new ArrayList<>();
        List<ItemLinkBind> list = itemLinkBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
        for (ItemLinkBind bind : list) {
            List<ItemLinkRole> roleList = itemLinkRoleRepository.findByItemLinkId(bind.getId());
            if (roleList.isEmpty()) {// 未配置角色，没权限
                continue;
            }
            for (ItemLinkRole linkRole : roleList) {
                boolean b = positionRoleApi.hasRole(tenantId, linkRole.getRoleId(), positionId).getData();
                if (b) {
                    LinkInfo linkInfo = linkInfoRepository.findById(bind.getLinkId()).orElse(null);
                    LinkInfoModel model = new LinkInfoModel();
                    Y9BeanUtil.copyProperties(linkInfo, model);
                    res_list.add(model);
                    break;
                }
            }
        }
        return res_list;
    }

}
