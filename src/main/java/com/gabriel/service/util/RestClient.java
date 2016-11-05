package com.gabriel.service.util;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Created by liuyufei on 30/10/16.
 */
@Component
public class RestClient {
//    private String server = "http://localhost:3000";
    private RestTemplate rest;
    private HttpHeaders headers;
    private HttpStatus status;

//    public RestClient() throws Exception {
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(new FileInputStream(new File("/Users/liuyufei/Desktop/seek.jks")), "123456".toCharArray());
//
//        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
//            new SSLContextBuilder()
//                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
//                .loadKeyMaterial(keyStore, "123456".toCharArray())
//                .build(),
//            NoopHostnameVerifier.INSTANCE);
//
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
//
//        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        this.rest = new RestTemplate(requestFactory);
//
////        this.rest = new RestTemplate(requestFactory);
////        this.rest = new RestTemplate();
//        this.headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json; charset=utf-16");
//        headers.add("Accept", "*/*");
//
//    }

    public String get(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    public String post(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
        ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

//    public void put(String uri, String json) {
//        HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
//        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.PUT, requestEntity, null);
//        this.setStatus(responseEntity.getStatusCode());
//    }
//
//    public void delete(String uri) {
//        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
//        ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.DELETE, requestEntity, null);
//        this.setStatus(responseEntity.getStatusCode());
//    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
