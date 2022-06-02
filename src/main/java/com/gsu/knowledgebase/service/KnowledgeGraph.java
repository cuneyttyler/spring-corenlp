package com.gsu.knowledgebase.service;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Example of Java client calling Knowledge Graph Search API
 */
public class KnowledgeGraph {

    public static void main(String[] args) {
        try {


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void get() {

    }

    public static void search() throws Exception {
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        JSONParser parser = new JSONParser();

        GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
        url.put("query", "Hagia Sophia");
        url.put("limit", "10");
        url.put("indent", "true");
        url.put("key", "AIzaSyBDbwt1D2Y3vh9NtEhUgTp4sg10KtIRfec");

        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();

        JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
        JSONArray elements = (JSONArray) response.get("itemListElement");

        for (Object element : elements) {
            System.out.println(JsonPath.read(element, "$.result.name").toString());
        }
    }
}
