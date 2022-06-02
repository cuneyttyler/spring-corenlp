package com.gsu.semantic.service.musicgraph;

import com.google.gson.JsonArray;
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
public class MusicGraph {

    public static String API_KEY = "c8303e90962e3a5ebd5a1f260a69b138";
    public static int LIMIT = 10;

    @Autowired
    public MyHttpClient myHttpClient;

    public List<LookupResponseItem> lookup(String prefix, String genre, String decade) throws Exception {
        String url = "http://api.musicgraph.com/api/v2/artist/suggest?" +
                "api_key=" + API_KEY +
                "&prefix=" + URLEncoder.encode(prefix) +
                "&limit=" + LIMIT;

        if (genre != null && genre.length() != 0) {
            url += "&genre=" + genre;
        }

        String response = myHttpClient.getAsString(url);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        List<LookupResponseItem> items = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                JsonObject o = data.get(i).getAsJsonObject();

                LookupResponseItem item = new LookupResponseItem();

                item.setAlternate_names(JsonUtils.getStringArrayAsList(o, "alternate_names"));
                item.setArtist_ref_id(JsonUtils.getPropertyAsString(o, "artist_ref_id"));
                item.setCountry_of_origin(JsonUtils.getPropertyAsString(o, "country_of_origin"));
                item.setDecade(JsonUtils.getPropertyAsString(o, "decade"));
                item.setEntity_type(JsonUtils.getPropertyAsString(o, "entity_type"));
                item.setGender(JsonUtils.getPropertyAsString(o, "gender"));
                item.setId(JsonUtils.getPropertyAsString(o, "id"));
                item.setMain_genre(JsonUtils.getPropertyAsString(o, "main_genre"));
                item.setMusicbrainz_id(JsonUtils.getPropertyAsString(o, "musicbrainz_image_url"));
                item.setMusicbrainz_image_url(JsonUtils.getPropertyAsString(o, "country_of_origin"));
                item.setName(JsonUtils.getPropertyAsString(o, "name"));
                item.setSort_name(JsonUtils.getPropertyAsString(o, "sort_name"));
                item.setSpotify_id(JsonUtils.getPropertyAsString(o, "spotify_id"));
                item.setYoutube_id(JsonUtils.getPropertyAsString(o, "youtube_id"));

                items.add(item);
            }
        }

        return items;
    }


    public void search(String prefix, String genre, String decade) {
        String url = "http://api.musicgraph.com/api/v2/artist/suggest?" +
                "api_key=" + API_KEY +
                "&prefix=" + prefix +
                "&limit=" + LIMIT;

        if (genre != null && genre.length() != 0) {
            url += "&genre=" + genre;
        }
    }

    public void artist() {

    }

    public List<Album> albums(String id) throws Exception {
        String url = "http://api.musicgraph.com/api/v2/artist/" + URLEncoder.encode(id) + "/albums" + "?api_key=" + API_KEY;

        String response = myHttpClient.getAsString(url);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        List<Album> albums = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                JsonObject o = data.get(i).getAsJsonObject();

                Album album = new Album();
                album.setMain_genre(JsonUtils.getPropertyAsString(o, "main_genre"));
                album.setAlbum_artist_id(JsonUtils.getPropertyAsString(o, "album_artist_id"));
                album.setAlbum_musicbrainz_id(JsonUtils.getPropertyAsString(o, "album_musicbrainz_id"));
                album.setAlbum_ref_id(JsonUtils.getPropertyAsString(o, "album_ref_id"));
                album.setArtist_name(JsonUtils.getPropertyAsString(o, "artist_name"));
                album.setDecade(JsonUtils.getPropertyAsString(o, "decade"));
                album.setEntity_type(JsonUtils.getPropertyAsString(o, "entity_type"));
                album.setId(JsonUtils.getPropertyAsString(o, "id"));
                album.setNumber_of_releases(JsonUtils.getPropertyAsString(o, "number_of_releases"));
                album.setNumber_of_tracks(JsonUtils.getPropertyAsString(o, "number_of_tracks"));
                album.setPopularity(JsonUtils.getPropertyAsString(o, "popularity"));
                album.setProduct_form(JsonUtils.getPropertyAsString(o, "product_form"));
                album.setRelease_date(JsonUtils.getPropertyAsString(o, "release_date"));
                album.setRelease_year(JsonUtils.getPropertyAsString(o, "release_year"));
                album.setTitle(JsonUtils.getPropertyAsString(o, "title"));

                albums.add(album);
            }
        }

        return albums;
    }

    public List<Track> tracks(String id) throws Exception {
        String url = "http://api.musicgraph.com/api/v2/album/" + id + "/tracks" + "?api_key=" + API_KEY;

        String response = myHttpClient.getAsString(url);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        List<Track> tracks = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                JsonObject o = data.get(i).getAsJsonObject();

                Track track = new Track();
                track.setTitle(JsonUtils.getPropertyAsString(o, "title"));
                track.setRelease_year(JsonUtils.getPropertyAsString(o, "release_year"));
                track.setPopularity(JsonUtils.getPropertyAsString(o, "popularity"));
                track.setAlbum_title(JsonUtils.getPropertyAsString(o, "album_title"));
                track.setArtist_name(JsonUtils.getPropertyAsString(o, "artist_name"));
                track.setDuration(JsonUtils.getPropertyAsString(o, "duration"));
                track.setEntity_type(JsonUtils.getPropertyAsString(o, "entity_type"));
                track.setId(JsonUtils.getPropertyAsString(o, "id"));
                track.setOriginal_release_year(JsonUtils.getPropertyAsString(o, "original_release_year"));
                track.setTrack_album_id(JsonUtils.getPropertyAsString(o, "track_album_id"));
                track.setTrack_artist_id(JsonUtils.getPropertyAsString(o, "track_artist_id"));
                track.setTrack_index(JsonUtils.getPropertyAsString(o, "track_index"));
                track.setTrack_youtube_id(JsonUtils.getPropertyAsString(o, "track_youtube_id"));

                JsonObject oo = o.getAsJsonObject("composer");
                Composer c = new Composer();
                c.setId(JsonUtils.getPropertyAsString(oo, "id"));
                c.setName(JsonUtils.getPropertyAsString(oo, "name"));

                track.addComposer(c);

                oo = o.getAsJsonObject("lyricist");
                Lyricist l = new Lyricist();
                l.setId(JsonUtils.getPropertyAsString(oo, "id"));
                l.setName(JsonUtils.getPropertyAsString(oo, "name"));

                track.addLyricist(l);
                tracks.add(track);
            }
        }

        return tracks;
    }

    public void similar() {

    }
}
