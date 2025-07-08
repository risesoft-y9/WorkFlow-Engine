package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemInterfaceBind;
import net.risesoft.entity.interfaceinfo.InterfaceInfo;
import net.risesoft.entity.interfaceinfo.InterfaceRequestParams;
import net.risesoft.entity.interfaceinfo.InterfaceResponseParams;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.InterfaceService;

/**
 * 接口信息
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/interface", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterfaceRestController {

    private final InterfaceService interfaceService;

    /**
     * 获取接口绑定事项列表
     *
     * @param id 接口id
     * @return
     */
    @GetMapping(value = "/findByInterfaceId")
    public Y9Result<List<ItemInterfaceBind>> findByInterfaceId(@RequestParam String id) {
        List<ItemInterfaceBind> list = interfaceService.listInterfaceById(id);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取接口列表
     *
     * @param name 接口名称
     * @param type 接口类型
     * @param address 接口地址
     * @return
     */
    @GetMapping(value = "/findInterfaceList")
    public Y9Result<List<InterfaceInfo>> findInterfaceList(@RequestParam(required = false) String name,
        @RequestParam(required = false) String type, @RequestParam(required = false) String address) {
        List<InterfaceInfo> list = interfaceService.listInterfaces(name, type, address);
        return Y9Result.success(list, "获取列表成功");
    }

    /**
     * 获取接口请求参数列表
     *
     * @param name 参数名称
     * @param type 参数类型
     * @param id 接口id
     * @return
     */
    @GetMapping(value = "/findRequestParamsList")
    public Y9Result<List<InterfaceRequestParams>> findRequestParamsList(@RequestParam(required = false) String name,
        @RequestParam(required = false) String type, @RequestParam String id) {
        List<InterfaceRequestParams> list = interfaceService.listRequestParams(name, type, id);
        return Y9Result.success(list, "获取列表成功");
    }

    /**
     * 获取接口响应参数列表
     *
     * @param name 参数名称
     * @param id 接口id
     * @return
     */
    @GetMapping(value = "/findResponseParamsList")
    public Y9Result<List<InterfaceResponseParams>> findResponseParamsList(@RequestParam(required = false) String name,
        @RequestParam String id) {
        List<InterfaceResponseParams> list = interfaceService.listResponseParamsByNameAndId(name, id);
        return Y9Result.success(list, "获取列表成功");
    }

    /**
     * 移除接口信息
     *
     * @param id 接口id
     * @return
     */
    @PostMapping(value = "/removeInterface")
    public Y9Result<String> removeInterface(@RequestParam String id) {
        interfaceService.removeInterface(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 移除接口请求参数
     *
     * @param ids 参数ids
     * @return
     */
    @PostMapping(value = "/removeRequestParams")
    public Y9Result<String> removeRequestParams(@RequestParam String[] ids) {
        interfaceService.removeReqParams(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 移除接口响应参数
     *
     * @param ids 参数ids
     * @return
     */
    @PostMapping(value = "/removeResponseParams")
    public Y9Result<String> removeResponseParams(@RequestParam String[] ids) {
        interfaceService.removeResParams(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 一键保存响应参数
     *
     * @param jsonData 参数json
     * @return
     */
    @PostMapping(value = "/saveAllResponseParams")
    public Y9Result<String> saveAllResponseParams(String interfaceId, String jsonData) {
        interfaceService.saveAllResponseParams(interfaceId, jsonData);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存接口信息
     *
     * @param info 接口信息
     * @return
     */
    @PostMapping(value = "/saveInterface")
    public Y9Result<String> saveInterface(InterfaceInfo info) {
        interfaceService.saveInterfaceInfo(info);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存接口请求参数
     *
     * @param info 参数信息
     * @return
     */
    @PostMapping(value = "/saveRequestParams")
    public Y9Result<String> saveRequestParams(InterfaceRequestParams info) {
        interfaceService.saveRequestParams(info);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存接口响应参数
     *
     * @param info 参数信息
     * @return
     */
    @PostMapping(value = "/saveResponseParams")
    public Y9Result<String> saveResponseParams(InterfaceResponseParams info) {
        interfaceService.saveResponseParams(info);
        return Y9Result.successMsg("保存成功");
    }
}
