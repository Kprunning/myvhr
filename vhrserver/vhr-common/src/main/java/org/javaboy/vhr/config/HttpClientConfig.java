package org.javaboy.vhr.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:httpclient.properties")
public class HttpClientConfig {
    // 属性文件会加载到Spring的Environment中,目前这个加载存在问题,environment为null
    private Environment environment;

    @Autowired
    public HttpClientConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * 定义httpClient连接池
     * <bean id="httpClientConnectionManager" class="org.apache.http.impl.conn.PoolingHttpClientConnectionManager" destroy-method="close">
     * 		<!-- 设置连接总数 -->
     * 		<property name="maxTotal" value="${http.pool.maxTotal}"></property>
     * 		<!-- 设置每个地址的并发数 -->
     * 		<property name="defaultMaxPerRoute" value="${http.pool.defaultMaxPerRoute}"></property>
     * 	</bean>
     * @return
     */
    @Bean(destroyMethod = "close")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 设置连接总数
        poolingHttpClientConnectionManager.setMaxTotal(environment.getProperty("http.pool.maxTotal",Integer.class));
        // 设置每个地址的并发数
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(environment.getProperty("http.pool.defaultMaxPerRoute",Integer.class));

        return poolingHttpClientConnectionManager;
    }

    /**
     * 定义HttpClient工厂,这里使用HttpClientBuilder的静态方法create构建
     * <bean id="httpClientBuilder" class="org.apache.http.impl.client.HttpClientBuilder" factory-method="create">
     * 		<property name="connectionManager" ref="httpClientConnectionManager"></property>
     * 	</bean>
     * @param poolingHttpClientConnectionManager 上面spring管理的,自动装配
     * @return
     */
    @Bean()
    public HttpClientBuilder httpClientBuilder(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        // 调用工厂方法,对应xml<bean>标签中的factory-method
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

        return httpClientBuilder;
    }

    /**
     * 得到httpClient实例
     * <bean id="httpClient" factory-bean="httpClientBuilder" factory-method="build"/>
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public HttpClient httpClient(HttpClientBuilder httpClientBuilder) {
        CloseableHttpClient httpClient = httpClientBuilder.build();
        return httpClient;
    }

    /**
     *  定期清理无效的连接
     * <!-- 定期清理无效的连接 -->
     * 	<bean class="com.jt.common.util.IdleConnectionEvictor" destroy-method="shutdown">
     * 		<constructor-arg index="0" ref="httpClientConnectionManager" />
     * 		<!-- 间隔一分钟清理一次 -->
     * 		<constructor-arg index="1" value="60000" />
     * 	</bean>
     */
    @Bean(destroyMethod = "shutdown")
    public IdleConnectionEvictor idleConnectionEvictor(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(poolingHttpClientConnectionManager, 60000, TimeUnit.MILLISECONDS);
        return idleConnectionEvictor;
    }

    /**
     * 定义requestConfig的工厂
     * <bean id="requestConfigBuilder" class="org.apache.http.client.config.RequestConfig.Builder">
     * 		<!-- 从连接池中获取到连接的最长时间 -->
     * 		<property name="connectionRequestTimeout" value="${http.request.connectionRequestTimeout}"/>
     * 		<!-- 创建连接的最长时间 -->
     * 		<property name="connectTimeout" value="${http.request.connectTimeout}"/>
     * 		<!-- 数据传输的最长时间 -->
     * 		<property name="socketTimeout" value="${http.request.socketTimeout}"/>
     * 		<!-- 提交请求前测试连接是否可用,此方法已弃用 -->
     * 		<property name="staleConnectionCheckEnabled" value="${http.request.staleConnectionCheckEnabled}"/>
     * 	</bean>
     * 	RequestConfig.Builder是一个静态内部类
     */
    @Bean
    public RequestConfig.Builder builder(){
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(environment.getProperty("http.request.connectionRequestTimeout",Integer.class));
        builder.setConnectTimeout(environment.getProperty("http.request.connectTimeout",Integer.class));
        builder.setSocketTimeout(environment.getProperty("http.request.socketTimeout",Integer.class));
        return builder;
    }

    /**
     * <!-- 得到requestConfig实例 -->
     * 	<bean id="requestConfig" factory-bean="requestConfigBuilder" factory-method="build" />
     */
    @Bean
    public RequestConfig requestConfig(RequestConfig.Builder builder){
        RequestConfig requestConfig = builder.build();
        return  requestConfig;
    }


}
