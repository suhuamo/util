package com.suhuamo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.suhuamo.util.enums.CodeEnum;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * @author suhuamo
 * @slogan 想和喜欢的人睡在冬日的暖阳里
 * @date 2023/05/17
 * http请求处理工具
 */
public class HttpUtil {

    /**
     * 四种请求方式
     */
    private final static String GET_TEXT = "GET";
    private final static String POST_TEXT = "POST";
    private final static String PUT_TEXT = "PUT";
    private final static String DELETE_TEXT = "DELETE";
    /**
     * 两种链接策略
     */
    private final static String HTTPS_TEXT = "https://";
    private final static String HTTP_TEXT = "http://";
    /**
     * 请求中返回的固定的json参数，code-状态码
     */
    private final static String CODE_TEXT = "code";
    /**
     * 请求中返回的固定的json参数，data-响应数据
     */
    private final static String DATA_TEXT = "data";

    /**
     * 请求头中的 user-agent参数，每次随机选择一个防止ip被封
     */
    private final static List<String> MY_USER_AGENT = Arrays.asList(
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/537.75.14",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11",
            "Opera/9.25 (Windows NT 5.1; U; en)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Kubuntu)",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.12) Gecko/20070731 Ubuntu/dapper-security Firefox/1.5.0.12",
            "Lynx/2.8.5rel.1 libwww-FM/2.14 SSL-MM/1.4.1 GNUTLS/1.2.9",
            "Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.7 (KHTML, like Gecko) Ubuntu/11.04 Chromium/16.0.912.77 Chrome/16.0.912.77 Safari/535.7",
            "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 ");


    /**
     * 发送get请求并获得json格式的响应数据
     *
     * @param requestUrl 请求地址
     * @return JsonObject
     */
    public static JsonObject sendGet(String requestUrl) {
        return sendGet(requestUrl, null, null);
    }

    /**
     * 发送get请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param params         请求地址附带的参数-map格式，k-v对应名称-值
     * @return JsonObject
     */
    public static JsonObject sendGet(String requestUrl, Map<String, String> params) {
        return sendGet(requestUrl, params, null);
    }

    /**
     * 发送get请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param params         请求地址附带的参数-map格式，k-v对应名称-值
     * @param headProperties 请求头参数-map格式，k-v对应名称-值
     * @return JsonObject
     */
    public static JsonObject sendGet(String requestUrl, Map<String, String> params, Map<String, String> headProperties) {
        // 如果有附带数据，则进行拼接
        if(params != null) {
            StringBuilder stringBuilder = new StringBuilder(requestUrl);
            stringBuilder.append("?");
            params.forEach((k, v) -> {
                stringBuilder.append(k + "=" + v + "&");
            });
            requestUrl = stringBuilder.toString();
        }
        return sendHttp(requestUrl, headProperties, GET_TEXT, null);
    }

    /**
     * 发送post请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @return JsonObject
     */
    public static JsonObject sendPost(String requestUrl, Object data) {
        return sendPost(requestUrl, new Gson().toJson(data), null);
    }


    /**
     * 发送post请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @param headProperties 请求头参数
     * @return JsonObject
     */
    public static JsonObject sendPost(String requestUrl, Object data, Map<String, String> headProperties) {
        return sendHttp(requestUrl, headProperties, POST_TEXT, new Gson().toJson(data));
    }

    /**
     * 发送put请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @return JsonObject
     */
    public static JsonObject sendPut(String requestUrl, Object data) {
        return sendPut(requestUrl, new Gson().toJson(data), null);
    }

    /**
     * 发送put请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @param headProperties 请求头参数
     * @return JsonObject
     */
    public static JsonObject sendPut(String requestUrl, Object data, Map<String, String> headProperties) {
        return sendHttp(requestUrl, headProperties, PUT_TEXT, new Gson().toJson(data));
    }

    /**
     * 发送delete请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @return JsonObject
     */
    public static JsonObject sendDelete(String requestUrl, Object data) {
        return sendDelete(requestUrl, new Gson().toJson(data), null);
    }

    /**
     * 发送delete请求并获得json格式的响应数据
     *
     * @param requestUrl     请求地址
     * @param data           请求附带数据
     * @param headProperties 请求头参数
     * @return JsonObject
     */
    public static JsonObject sendDelete(String requestUrl, Object data, Map<String, String> headProperties) {
        return sendHttp(requestUrl, headProperties, DELETE_TEXT, new Gson().toJson(data));
    }

    /**
     * 发送请求并获取json格式的响应数据
     *
     * @param requestUrl     发送请求的地址
     * @param headProperties 请求头参数
     * @param requestMethod  请求方式
     * @param data           请求附带数据
     * @return JsonObject
     */
    private static JsonObject sendHttp(String requestUrl, Map<String, String> headProperties, String requestMethod, String data) {
        return getConnection(requestUrl, headProperties, requestMethod, data, isHttps(requestUrl));
    }

    /**
     * 判断该链接是否为https链接
     *
     * @param requestUrl
     * @return boolean 是否为https链接，如果是，则返回true
     */
    private static boolean isHttps(String requestUrl) {
        return requestUrl.startsWith(HTTPS_TEXT);
    }


    /**
     * 提供于Https请求，用于绕过SSL、TLS证书
     *
     * @param
     * @return SSLContext
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 发送请求并获取json格式的返回值
     *
     * @param requestUrl     发送请求的地址
     * @param headProperties 请求头中的参数
     * @param requestMethod  请求的方式
     * @param data           请求的参数内容
     * @return JsonObject
     */
    private static JsonObject getConnection(String requestUrl, Map<String, String> headProperties, String requestMethod, String data, boolean isHttps) {
        // 定义返回值对象
        JsonObject object = null;
        // 定义buffer对象，用来获取请求返回的值
        StringBuffer buffer = new StringBuffer();
        try {
            // 获取链接的url
            URL url = new URL(requestUrl);
            // 创建连接对象
            HttpURLConnection connection = null;
            if (isHttps) {
                // 其实使用 HttpURLConnection 也是一样的效果，但是由于 https 链接有证书，只能使用HttpsURLConnection才能设置忽略证书
                connection = (HttpsURLConnection) url.openConnection();
                //绕过证书
                SSLContext context = createIgnoreVerifySSL();
                ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            // 设置http请求的基本参数，如下：
            // 设置提交模式
            connection.setRequestMethod(requestMethod);
            // 设置连接超时 单位毫秒
            connection.setConnectTimeout(30000);
            // 设置读取超时 单位毫秒
            connection.setReadTimeout(30000);
            // 设置通用请求头的请求属性
            connection.setRequestProperty("accept", "*/*");
            // 设置维持长连接
            connection.setRequestProperty("connection", "Keep-Alive");
            // 设置文件字符集
            connection.setRequestProperty("Charset", "UTF-8");
            // 随机使用一个header
            connection.setRequestProperty("user-agent", MY_USER_AGENT.get(new Random().nextInt(MY_USER_AGENT.size())));
            // 设置用户输入的请求头中的参数,如果 headProperties 中有值的话，才开始遍历
            if (headProperties != null) {
                Set<String> keys = headProperties.keySet();
                // 遍历每一个参数
                for (String key : keys) {
                    // 向请求头中插入参数
                    connection.setRequestProperty(key, headProperties.get(key));
                }
            }
            // 如果不是get方法的话，需要额外配置的属性
            if (!requestMethod.equals(GET_TEXT)) {
                // 设置允许输出
                connection.setDoOutput(true);
                // 设置允许输入
                connection.setDoInput(true);
                // 设置不用缓存
                connection.setUseCaches(false);
                // 设置文件长度
                connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                // 设置文件类型:
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("accept", "application/json");
                OutputStream out = new DataOutputStream(connection.getOutputStream());
                // 写入请求的内容
                out.write(data.getBytes());
                out.flush();
                out.close();
            }
            // 获取返回值并将返回值封装为 InputStreamReader
            InputStreamReader isr;
            // 如果是200,那么获取正常地输入流
            if (connection.getResponseCode() == CodeEnum.SUCCESS.getCode()) {
                // StandardCharsets.UTF_8 就是字符串 ”utf-8“
                isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            }
            // 如果是非200,那么获取异常地输入流
            else {
                isr = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
            }
            // 再封装为 BufferedReader
            BufferedReader br = new BufferedReader(isr);
            // 用来接受 br 中的每一行内容，并且拼接到 buffer 里面中
            String str;
            while ((str = br.readLine()) != null) {
                buffer.append(str);
            }
            // 关闭各个流
            br.close();
            isr.close();
            // 将 buffer 对象转换为字符串再转换为 json 对象返回
            String res = buffer.toString();
            // 先判断返回结果是否为json对象，如果不是，那么人为创造该数据为json数据返回
            if (!JsonParser.parseString(res).isJsonObject()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(DATA_TEXT, JsonParser.parseString(res).getAsString());
                object = jsonObject;
            } else {
                object = JsonParser.parseString(res).getAsJsonObject();
            }
            // 如果http请求中返回值不包含code码，那么手动添加
            if (object.get(CODE_TEXT) == null) {
                object.addProperty(CODE_TEXT, connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，则返回异常结果数据。
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(DATA_TEXT, "搜索超时，请过段时间再搜索或重新搜索，不过小花建议您建议过段时间再搜索哦。");
            jsonObject.addProperty(CODE_TEXT, CodeEnum.TIMED_OUT.getCode());
            return jsonObject;
        }
        // 返回最终结果
        return object;
    }
}
