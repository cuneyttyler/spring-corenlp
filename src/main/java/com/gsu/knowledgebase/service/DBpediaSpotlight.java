package com.gsu.knowledgebase.service;

import com.gsu.knowledgebase.model.AnnotationItem;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class DBpediaSpotlight {

    public Collection<AnnotationItem> annotateText(String text) {
        Map<String, AnnotationItem> items = new HashMap<>();

        int count = text.length() / 5000 + 1;
        for (int i = 0; i < count; i++) {
            int to = text.length() < i * 5000 + 4999 ? text.length() : i * 5000 + 4999;
            String cut = text.length() < 5000 ? text : text.substring(i * 5000, to);

            cut.replaceAll("\\{", "[");
            cut = cut.replaceAll("\\}", "]");

            Map response = new RestTemplate().getForObject(
                    "http://api.dbpedia-spotlight.org/en/annotate?text=" +
                            cut +
                            "&confidence=0.5&support=0&spotter=Default&disambiguator=Default&policy=whitelist&types=&sparql="
                    , Map.class);

            if (response != null && response.get("Resources") != null) {
                for (Map r : (List<Map>) response.get("Resources")) {
                    AnnotationItem item = new AnnotationItem();
                    item.setOffset(Integer.parseInt((String) r.get("@offset")) + i * 5000);
                    item.setUri((String) r.get("@URI"));
                    item.setSurfaceForm((String) r.get("@surfaceForm"));
                    item.setTypes((String) r.get("@types"));

                    if (!items.containsKey(item.getUri())) {
                        items.put(item.getUri(), item);
                    }
                }
            }
        }

        return items.values();
    }
}
