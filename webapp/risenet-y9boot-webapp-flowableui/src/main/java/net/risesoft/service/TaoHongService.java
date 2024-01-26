package net.risesoft.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.springframework.stereotype.Service;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.xwpf.NiceXWPFDocument;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "taoHongService")
@Slf4j
public class TaoHongService {

    public void word2RedDocument(String content, String destDocx) {
        try {
            LOGGER.info("套红开始");
            // 模板文件地址
            String model = "C:\\Users\\10858\\Desktop\\开发资料\\深圳罗湖区.docx";
            // 模板文件 参数填写

            XWPFTemplate template = XWPFTemplate.compile(model);

            // 获取模板文件 公文
            NiceXWPFDocument main = template.getXWPFDocument();
            NiceXWPFDocument sub = new NiceXWPFDocument(new FileInputStream(content));
            // 合并两个文档
            NiceXWPFDocument newDoc = main.merge(sub);

            // 生成新文档
            FileOutputStream out = new FileOutputStream(destDocx);
            newDoc.write(out);
            newDoc.close();
            out.close();
            LOGGER.info("套红结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
