package com.gsu.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://www.mkyong.com/java/apache-httpclient-examples/
 * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
 */
@Component
public class MyHttpClient {

    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    public Map<String, Object> get(String url) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        Map<String, Object> map = new ObjectMapper().readValue(result.toString(),
                new TypeReference<Map<String, String>>() {
                });

        return map;
    }

    public String getAsString(String url) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    public HashMap<String, Object> post(String uri, Map<String, Object> parameters, Map<String, String> headers) throws Exception {
        String url = "https://selfsolve.apple.com/wcResults.do";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        for (String key : headers.keySet()) {
            post.setHeader(key, headers.get(key));
        }

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        for (String key : parameters.keySet()) {
            urlParameters.add(new BasicNameValuePair(key, parameters.get(key) + ""));
        }

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
//        System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }


        HashMap<String, Object> responseMap =
                new ObjectMapper().readValue(result.toString(), HashMap.class);

        return responseMap;
    }
}


//Map<String, String> headers = new HashMap<>();
//headers.put("content-type", MediaType.APPLICATION_JSON_VALUE);
//
//        Map<String, Object> parameters = new ObjectMapper().convertValue(new Request(text), Map.class);
//
//        Map<String, Object> response = new MyHttpClient().post("http://spotlight.dbpedia.org/dev/rest/annotate/", parameters, headers);