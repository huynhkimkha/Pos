package com.antdigital.agency.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;

@Component
public class EzSoftwareServiceRequest {
    @Autowired
    private Environment env;

    public <T> T get(String uri, Class<T> clazz, HttpServletRequest request)
            throws IOException, ClientProtocolException, JAXBException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);

        String authHeader = request.getHeader("Authorization");
        get.setHeader("Authorization", authHeader);

        CloseableHttpResponse response = httpClient.execute(createHost(), get);

        return this.getContentResponse(response, clazz);
    }

    public <T> T delete(String uri, Class<T> clazz, HttpServletRequest request)
            throws IOException, ClientProtocolException, JAXBException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(uri);

        String authHeader = request.getHeader("Authorization");
        delete.setHeader("Authorization", authHeader);

        CloseableHttpResponse response = httpClient.execute(createHost(), delete);

        return this.getContentResponse(response, clazz);
    }

    public <T,K> T post(String uri, K body, Class<T> clazz, HttpServletRequest request)
            throws IOException, ClientProtocolException, JAXBException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);

        String authHeader = request.getHeader("Authorization");
        post.setHeader("Authorization", authHeader);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);
        HttpEntity entry = new StringEntity(json);
        post.setEntity(entry);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(createHost(), post);

        return getContentResponse(response, clazz);
    }

    public <T,K> T put(String uri, K body, Class<T> clazz, HttpServletRequest request)
            throws IOException, ClientProtocolException, JAXBException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut(uri);

        String authHeader = request.getHeader("Authorization");
        put.setHeader("Authorization", authHeader);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);
        HttpEntity entry = new StringEntity(json);
        put.setEntity(entry);
        put.setHeader("Accept", "application/json");
        put.setHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpClient.execute(createHost(), put);

        return getContentResponse(response, clazz);
    }

    private <T> T getContentResponse(CloseableHttpResponse response, Class<T> clazz)
            throws IOException, ParseException, JAXBException {
        HttpEntity httpEntity = response.getEntity();
        String apiOutput = EntityUtils.toString(httpEntity);
        ObjectMapper mapper = new ObjectMapper();
        T res = mapper.readValue(apiOutput, clazz);

        response.close();

        return res;
    }

    private HttpHost createHost() {
        String DOMAIN = env.getProperty("adi.services.ezsoftware.base-url");

        return HttpHost.create(DOMAIN);
    }
}
