package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.InterfaceRequestParams;
import net.risesoft.entity.InterfaceResponseParams;
import net.risesoft.entity.ItemInterfaceBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface InterfaceService {

    /**
     * 根据id获取接口信息
     *
     * @param id
     * @return
     */
    InterfaceInfo findById(String id);

    /**
     * 根据接口名称获取接口信息
     *
     * @param interfaceName
     * @return
     */
    List<InterfaceInfo> findByInterfaceName(String interfaceName);

    /**
     * 获取所有接口信息
     *
     * @return
     */
    List<InterfaceInfo> findAll();

    /**
     * 获取接口绑定事项列表
     *
     * @param id
     * @return
     */
    List<ItemInterfaceBind> listInterfaceById(String id);

    /**
     * 获取接口列表
     *
     * @param name
     * @param type
     * @param address
     * @return
     */
    List<InterfaceInfo> listInterfaces(String name, String type, String address);

    /**
     * 获取接口请求参数列表
     *
     * @param name
     * @param type
     * @param id
     * @return
     */
    List<InterfaceRequestParams> listRequestParams(String name, String type, String id);

    /**
     * 获取接口响应参数列表
     *
     * @param name
     * @param id
     * @return
     */
    List<InterfaceResponseParams> listResponseParamsByNameAndId(String name, String id);

    /**
     * 移除接口信息
     *
     * @param id
     */
    void removeInterface(String id);

    /**
     * 移除接口请求参数
     *
     * @param ids
     */
    void removeReqParams(String[] ids);

    /**
     * 移除接口响应参数
     *
     * @param ids
     */
    void removeResParams(String[] ids);

    /**
     * 一键保存响应参数
     *
     * @param interfaceId
     * @param jsonData
     */
    void saveAllResponseParams(String interfaceId, String jsonData);

    /**
     * 保存接口信息
     *
     * @param info
     */
    InterfaceInfo saveInterfaceInfo(InterfaceInfo info);

    /**
     * 保存接口请求参数
     *
     * @param info
     */
    void saveRequestParams(InterfaceRequestParams info);

    /**
     * 保存接口响应参数
     *
     * @param info
     */
    void saveResponseParams(InterfaceResponseParams info);

}
