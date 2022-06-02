package com.gsu.semantic.service.musicbrainz;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gsu.common.util.JsonUtils;
import com.gsu.common.util.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class Musicbrainz {

    @Autowired
    private MyHttpClient myHttpClient;

    public List<Release> releases(String mbid) throws Exception {
        String url = "http://musicbrainz.org/ws/2/artist/" + mbid + "?inc=release-groups&fmt=json";

        String response = myHttpClient.getAsString(url);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        JsonArray releaseGroups = json.getAsJsonArray("release-groups");

        List<Release> releases = new ArrayList<>();
        if (releaseGroups != null) {
            for (int i = 0; i < releaseGroups.size(); i++) {
                JsonObject o = releaseGroups.get(i).getAsJsonObject();

                Release release = new Release();

                release.setDisambiguation(JsonUtils.getPropertyAsString(o, "disambiguation"));
                release.setFirstReleaseData(JsonUtils.getPropertyAsString(o, "first-release-date"));
                release.setId(JsonUtils.getPropertyAsString(o, "id"));
                release.setPrimaryType(o.get("primary-type").isJsonNull() ? "" : JsonUtils.getPropertyAsString(o, "primary-type"));
                release.setTitle(JsonUtils.getPropertyAsString(o, "title"));

                JsonArray secondaryTypes = json.getAsJsonArray("secondary-types");
                if (secondaryTypes != null && secondaryTypes.size() > 0) {
                    release.setSecondaryType(secondaryTypes.get(0).getAsString());
                }

                releases.add(release);
            }
        }

        return releases;
    }
}