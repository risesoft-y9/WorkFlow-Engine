package net.risesoft.service.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.ExcelHandlerService;

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
    public void export(OutputStream outStream, String[] processSerialNumbers) {
        // 定义表头
        List<List<String>> head = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        List<String> head1 = new ArrayList<>();
        head0.add("姓名");
        head1.add("年龄");
        head.add(head0);
        head.add(head1);
        // 准备数据
        List<List<String>> data = new ArrayList<>();
        Arrays.stream(processSerialNumbers).forEach(processSerialNumber -> {
            List<String> list = new ArrayList<>();
            list.add("秦漫" + processSerialNumber);
            list.add(processSerialNumber);
            data.add(list);
        });
        // 写入数据到 Excel 文件
        EasyExcel.write(outStream).head(head).sheet("Sheet1").doWrite(data);
        // EasyExcel.write(outStream).head(head).sheet("Sheet1").doWrite(data);
    }
}
