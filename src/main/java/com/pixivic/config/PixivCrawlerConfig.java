package com.pixivic.config;

import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

@Configuration
public class PixivCrawlerConfig {

    @Bean
    public PooledGMService pooledGMService(@Value("${graphicsMagick.path}") String GMPath) {
        GMConnectionPoolConfig gmConnectionPoolConfig = new GMConnectionPoolConfig();
        gmConnectionPoolConfig.setGMPath(GMPath);
        return new PooledGMService(gmConnectionPoolConfig);
    }

    @Bean
    public HttpClient httpClient(@Value("${thread.num}") Integer nThread) throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCertificates = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }
        }};
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCertificates, new SecureRandom());
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(sslParams)
                .sslContext(sc)
                //.proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 8888)))
                .executor(Executors.newFixedThreadPool(nThread))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }
}
