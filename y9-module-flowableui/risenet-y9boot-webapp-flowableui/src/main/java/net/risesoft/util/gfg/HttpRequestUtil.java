package net.risesoft.util.gfg;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequestUtil {

    // post方式请求，获取文件流
    public static byte[] postFile(String apiUrl, String jsonInputString) throws IOException {
        byte[] param = jsonInputString.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[0];

        URL url = new URL(apiUrl);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/octet-stream");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000); // 设置连接超时5秒
            connection.setReadTimeout(20000);   // 设置读取超时20秒

            // 发送 JSON 请求体
            try (OutputStream os = connection.getOutputStream()) {
                os.write(param, 0, param.length);
            }

            // 检查响应码
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                // 获取返回的文件流
                try (InputStream in = connection.getInputStream();
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    result = byteArrayOutputStream.toByteArray();

                    // 如果需要写入本地文件，可调用单独方法
//                    writeToFile(result, "output.ofd");
//                    System.out.println("文件保存成功！");
                }
            } else {
                String errorMessage = connection.getResponseMessage();
                System.err.println("请求失败，响应码: " + responseCode);
                System.err.println("错误信息: " + errorMessage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    // 将字节数组写入文件
    private static void writeToFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(data);
        }
    }
}

