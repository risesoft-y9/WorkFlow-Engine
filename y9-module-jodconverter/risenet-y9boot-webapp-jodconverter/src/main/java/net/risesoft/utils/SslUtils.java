/*
package net.risesoft.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SslUtils {
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void main(String[] args) {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        System.out.println(dateStr);
    }

    */
/**
 * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
 *//*
   
   public static void ignoreSsl() throws Exception {
    HostnameVerifier hv = (urlHostName, session) -> true;
    trustAllHttpsCertificates();
    HttpsURLConnection.setDefaultHostnameVerifier(hv);
   }
   
   static class miTM implements TrustManager, X509TrustManager {
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
   
    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        // 空实现 - 用于创建信任所有服务器证书的TrustManager
        // 在开发/测试环境中绕过SSL证书验证时使用
        // 注意：在生产环境中不应使用此实现，存在安全风险
    }
   
    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        // 空实现 - 用于创建信任所有客户端证书的TrustManager
        // 在开发/测试环境中绕过SSL证书验证时使用
        // 注意：在生产环境中不应使用此实现，存在安全风险
    }
   }
   
   }
   */
