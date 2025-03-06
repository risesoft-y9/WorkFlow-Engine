package net.risesoft.service.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.ExcelHandlerService;
import net.risesoft.y9.Y9LoginUserHolder;

import cn.idev.excel.EasyExcel;

/**
 * @author : qinman
 * @date : 2025-03-05
 * @since 9.6.8
 **/
@RequiredArgsConstructor
@Slf4j
@Service
public class ExcelHandlerServiceImpl implements ExcelHandlerService {

    @Override
    public void export(OutputStream outStream, List<Map<String, Object>> mapList, String[] columns) {
        List<List<String>> headName = new ArrayList<>();
        if (mapList.isEmpty()) {
            headName.add(Collections.singletonList("无查询"));
            EasyExcel.write(outStream).head(headName).sheet("Sheet1").doWrite(headName);
            return;
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<List<String>> headKey = new ArrayList<>();
        // 准备表头
        Arrays.stream(columns).forEach(column -> {
            String[] arr = column.split(":");
            headName.add(Collections.singletonList(arr[0]));
            headKey.add(Collections.singletonList(arr[1]));
        });
        // 准备数据
        List<List<String>> data = new ArrayList<>();
        mapList.forEach(map -> {
            List<String> list = new ArrayList<>();
            headKey.forEach(key -> {
                list.add(null == map.get(key.get(0)) ? "" : map.get(key.get(0)).toString());
            });
            data.add(list);
        });
        // 写入数据到 Excel 文件
        EasyExcel.write(outStream).head(headName).sheet("Sheet1").doWrite(data);
    }
}
