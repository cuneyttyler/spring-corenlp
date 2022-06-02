package com.gsu.knowledgebase.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gsu.common.util.MyHttpClient;
import com.gsu.knowledgebase.model.Entity;
import com.gsu.knowledgebase.model.Property;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Component
public class DBPedia {

    @Autowired
    MyHttpClient myHttpClient;

    public static void main(String args[]) throws Exception {
        new DBPedia().getEntityByUri("http://dbpedia.org/resource/Bayesian_network");
    }

    public Entity getEntityByUri2(String uri) {
        String req = "http://live.dbpedia.org/sparql";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(req)
                .queryParam("query",
                        uri)
                .queryParam("format", "application/json-ld");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        HttpEntity<Entity> response =
                new RestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.GET, httpEntity, Entity.class);

        return response.getBody();
    }

    public Entity getEntityByUri(String uri) throws Exception {
        Entity entity = new Entity();

        String req = "http://live.dbpedia.org/sparql?default-graph-uri=" +
                "http%3A%2F%2Fdbpedia.org&query=DESCRIBE%20" +
                "%3C" + uri + "%3E" +
                "&" +
                "format=application%2Fjson-ld";


//        String str = IOUtils.toString(new URI(req));
        String str = myHttpClient.getAsString(req);


        JsonObject json = new JsonParser().parse(str).getAsJsonObject();

        JsonObject item = json.getAsJsonObject(uri);

        JsonArray arr = item.getAsJsonArray("http://dbpedia.org/ontology/abstract");
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                if (getAsString(o, "lang").equals("en")) {
                    entity.setDescription(getAsString(o, "value"));
                }
            }
        }

        arr = item.getAsJsonArray("http://www.w3.org/2000/01/rdf-schema#label");
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                if (getAsString(o, "lang").equals("en")) {
                    entity.setName(getAsString(o, "value"));
                }
            }
        }

        List<Property> properties = new ArrayList<>();
        for (Map.Entry<String, JsonElement> propertyElement : item.entrySet()) {
            arr = propertyElement.getValue().getAsJsonArray();

            Iterator<JsonElement> it = arr.iterator();
            while (it.hasNext()) {
                JsonObject property = it.next().getAsJsonObject();

                Property p = new Property();
                p.setSource("dbpedia");
                p.setName(getTypeNameFromUri(propertyElement.getKey()));
                p.setUri(propertyElement.getKey());
                p.setLang(getAsString(property, "en"));
                p.setDatatype(getAsString(property, "datatype"));
                p.setType(getAsString(property, "type"));
                p.setValue(getAsString(property, "value"));
                p.setValueLabel(getAsString(property, "value").split("/")[getAsString(property, "value").split("/").length - 1]);

                properties.add(p);
            }
        }

        entity.setProperties(properties);
        entity.setEntityType("web-page-annotation");
        entity.setDbpediaUri(uri);

        return entity;
    }

    private String getTypeNameFromUri(String uri) {
        if (uri.contains("w3.org")) {
            return uri.split("#")[1];
        } else {
            String[] arr = uri.split("/");
            return arr[arr.length - 1];
        }
    }

    private String getAsString(JsonObject o, String propertyName) {
        if (o.getAsJsonPrimitive(propertyName) == null) {
            return null;
        } else {
            return o.getAsJsonPrimitive(propertyName).getAsString();
        }
    }
}