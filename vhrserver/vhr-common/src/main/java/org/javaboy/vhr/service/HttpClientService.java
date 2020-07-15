package org.javaboy.vhr.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模拟发起http请求
 */
@Component
public class HttpClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);
    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;

    @Autowired(required = false)
    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    @Autowired(required = false)
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    /**
     * 实现HttpClientPost方法思考
     * 1.需要设定url参数.
     * 2.Map<String,String>,使用Map数据结构实现参数封装
     * 3.设定字符集编码utf-8
     * 难点:POST如何传递参数?
     * 答案:POST请求将参数转化为二进制字节流信息进行数据传输
     * 一般form表单提交使用POST提交
     */
    public String doPost(String url, Map<String,String> params, String charset){
        String result = null;
        // 1.判断字符集编码是否为null,如果参数为空则默认为utf-8
        if(StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        // 2.获取请求对象的实体
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        try {
            // 3.判断用户是否传递了参数
            if (params != null) {
                // 将用户传入的数据Map封装到List集合中
                List<NameValuePair> parameters = new ArrayList<>();
                for (Map.Entry<String,String> entry : params.entrySet()) {
                    BasicNameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    parameters.add(nameValuePair);
                }

                // 实现参数封装
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, charset);
                httpPost.setEntity(formEntity);
            }
            // 4.发起POST请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            // 5.判断请求结果是否正确,200代表正常相应
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 6.获取服务端回传的数据
                result = EntityUtils.toString(httpResponse.getEntity(),charset);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    // 重载方法
    public String doPost(String url) {
        return doPost(url, null, null);
    }
    public String doPost(String url, Map<String,String> params) {
        return doPost(url, params, null);
    }

    /**
     * 实现Get请求
     * 说明:get请求中的参数是经过拼接形成的,例如:8091/addUser?id=1&name=tom
     * @param url
     * @param params
     * @param charset
     * @return
     */
    public String doGet(String url, Map<String,String> params, String charset) {
        String result = null;
        // 1.判断字符集编码是否为空
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        try {
            // 2.判断是否有参数
            if (params != null) {
                // 通过工具类实现路径的自动拼接
                URIBuilder builder = new URIBuilder(url);
                for (Map.Entry<String,String> entry : params.entrySet()) {
                    builder.addParameter(entry.getKey(),entry.getValue());
                }
                // 将路径进行拼接 addUser?id=1&name=zw
                url = builder.build().toString();
            }
            // 定义请求类型
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            // 发起请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(),charset);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    // doGet方法重载
    public String doGet(String url, Map<String,String> params) {
        return doGet(url, params,null);
    }
    public String doGet(String url) {
        return doGet(url,null,null);
    }

}
