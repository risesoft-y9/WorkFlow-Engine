package net.risesoft.util;

import java.security.MessageDigest;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
public class Y9DzxhSendMsgUtil {

    private static final String SERVER_URL = "https://api.netease.im/sms/sendtemplate.action";
    private static final String APP_KEY = "ec7694c9e60ddb49b476621792a413c9";
    private static final String NONCE = "123456";
    private static final String APP_SECRET = "c21d1806c39a";
    private static final String MOULD_ID = "3912543";

    private static final char[] HEX_DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static void main(String[] args) throws Exception {
        String mobile = "15688451162";
        String userName = "admin1";
        String itemName = "地灾办件";
        String senderName = "admin";
        String title = "关于召开2020年度地灾协会年度任务的会议";
        boolean lo = Y9DzxhSendMsgUtil.sendMsgByphoneAndParams(mobile, userName, itemName, senderName, title);
        System.out.println(lo);
    }

    public static boolean sendMsgByphoneAndParams(String mobile, String receiverName, String itemName,
        String senderName, String title) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(SERVER_URL);

        String curTime = String.valueOf((System.currentTimeMillis() / 1000L));
        String checkSum = getCheckSum(APP_SECRET, NONCE, curTime);
        // 设置请求的header
        post.addHeader("AppKey", APP_KEY);
        post.addHeader("Nonce", NONCE);
        post.addHeader("CurTime", curTime);
        post.addHeader("CheckSum", checkSum);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求参数
        List<NameValuePair> nameValuePairs = Lists.newArrayList();
        List<String> mobilePhones = Lists.newArrayList();
        mobilePhones.add(mobile);
        List<String> params = Lists.newArrayList();
        params.add(receiverName);
        params.add(itemName);
        params.add(senderName);
        params.add(title);
        nameValuePairs.add(new BasicNameValuePair("templateid", MOULD_ID));
        nameValuePairs.add(new BasicNameValuePair("mobiles", JSONArray.toJSONString(mobilePhones)));
        nameValuePairs.add(new BasicNameValuePair("params", JSONArray.toJSONString(params)));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

        // 执行请求
        HttpResponse response = httpclient.execute(post);
        String responseEntity = EntityUtils.toString(response.getEntity(), "utf-8");

        // 判断是否发送成功，发送成功返回true
        String code = JSON.parseObject(responseEntity).getString("code");
        boolean b = "200".equals(code);
        if (b) {
            LOGGER.info("由{}发送至{}的短信发送成功", senderName, receiverName);
            return true;
        } else {
            LOGGER.warn("由{}发送至{}的短信发送失败, 错误信息:{}", senderName, receiverName, responseEntity);
        }
        return false;
    }

}
