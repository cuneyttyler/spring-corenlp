package com.gsu.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnyt on 10.12.2014.
 */
public class JsonUtils {

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(object);
    }

    public static String getPropertyAsString(JsonObject item, String name) {
        return item == null ||
                item.isJsonNull() ||
                item.getAsJsonPrimitive(name) == null ||
                item.getAsJsonPrimitive(name).isJsonNull() ? null : item.getAsJsonPrimitive(name).getAsString();
    }

    public static List<String> getStringArrayAsList(JsonObject o, String name) {
        JsonArray arr = o.getAsJsonArray(name);

        if (arr == null || arr.isJsonNull()) {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (int j = 0; j < arr.size(); j++) {
            list.add(arr.get(j).getAsString());
        }

        return list;
    }
}
