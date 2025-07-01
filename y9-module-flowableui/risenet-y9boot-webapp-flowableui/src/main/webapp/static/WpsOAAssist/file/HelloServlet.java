package com.kso.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@WebServlet({"/HelloServlet"})
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static String s_fileName = "";

    ByteArrayOutputStream output;

    /**
     * 下载文件
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String filename = request.getParameter("name");
        if (filename == null || filename.isEmpty()) {
            out.print("please set file name ");
        } else {
            // 假设文件都在服务的根目录下
            String realFileName = request.getServletContext().getRealPath("/") + filename;
            System.out.println(realFileName);
            // 实例化一个向客户端输出文件流
            OutputStream outputStream = response.getOutputStream();
            // 输出文件用的字节数组，每次向输出流发送600个字节
            byte b[] = new byte[600];
            // 要向客户端输出的文件
            File fileload = new File(realFileName);
            System.out.println(filename);
            String utf8filename = URLEncoder.encode(filename, "UTF-8");
            System.out.println(utf8filename);
            response.setHeader("Content-disposition", "attachment; filename=" + utf8filename);
            // 通知客户端：文件的MIME类型
            response.setContentType("application/msword");
            // 通知客户端：文件的长度
            long fileLength = fileload.length();
            String length = String.valueOf(fileLength);
            response.setHeader("Content-length", length);
            // 读取文件，并发送给客户端下载
            FileInputStream inputStream = new FileInputStream(fileload);
            int n = 0;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
            inputStream.close();
            outputStream.close();
        }
    }

    /**
     * 上传文件
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        System.out.println("添加任务");
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        String value = "";

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload((FileItemFactory)factory);
            upload.setHeaderEncoding("UTF-8");

            List items = upload.parseRequest(request);
            boolean isOk = false;
            Map<Object, Object> param = new HashMap<>(16);
            for (Object object : items) {
                FileItem fileItem = (FileItem)object;
                if (fileItem.isFormField()) {
                    System.out.println(
                        fileItem.getFieldName() + ":" + fileItem.getString("utf-8") + ", size:" + fileItem.getSize());
                    param.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
                    continue;
                }
                String fieldName = fileItem.getFieldName();
                // 必须要有文件名，需要客户端传参时注意
                String fileName = fileItem.getName();
                if (fileName.equals("blob")) {
                    if (param.containsKey("filename")) {
                        fileName = param.get("filename").toString();
                    } else if (param.containsKey("fileName")) {
                        fileName = param.get("fileName").toString();
                    }
                }
                String filePath = request.getSession().getServletContext().getRealPath("/") + fileName;
                System.out.println(fieldName + ":" + filePath);

                FileOutputStream fileOut = new FileOutputStream(filePath);
                InputStream in = fileItem.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) > 0) {
                    fileOut.write(buffer, 0, len);
                }
                in.close();
                fileOut.close();
                response.setHeader("Content-disposition", "attachment; filename*=UTF-8''" + fileName);
                response.getWriter().write(fileName.concat("上传成功"));

                return;
            }
        } catch (FileUploadException e) {

            e.printStackTrace();
        }

        response.sendError(404, "no ssison");
    }
}