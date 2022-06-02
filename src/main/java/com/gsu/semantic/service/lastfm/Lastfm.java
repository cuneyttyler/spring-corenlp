package com.gsu.semantic.service.lastfm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gsu.common.util.JsonUtils;
import com.gsu.common.util.MyHttpClient;
import com.gsu.semantic.model.Node;
import com.gsu.semantic.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cnytync on 21/08/2017.
 */
@Component
public class Lastfm {

    @Autowired
    private MyHttpClient myHttpClient;

    private static String SIMILAR_ARTISTS_URL = "http://ws.audioscrobbler.com/2.0/?" +
            "method=artist.getsimilar&" +
            "api_key=ecc2e1fc920c9fcbcc19ba8790570691&" +
            "format=json&" +
            "artist=";

    private static String ARTIST_INFO_URL = "http://ws.audioscrobbler.com/2.0/?" +
            "method=artist.getInfo&" +
            "api_key=ecc2e1fc920c9fcbcc19ba8790570691&" +
            "format=json&" +
            "artist=";

    private static String ALBUM_INFO_URL = "http://ws.audioscrobbler.com/2.0/?" +
            "method=album.getinfo&" +
            "api_key=ecc2e1fc920c9fcbcc19ba8790570691&" +
            "format=json";

    private static String TOP_ALBUMS_URL = "http://ws.audioscrobbler.com/2.0/?" +
            "method=artist.gettopalbums&" +
            "api_key=ecc2e1fc920c9fcbcc19ba8790570691&" +
            "format=json&" +
            "artist=";

    private static String TOP_ARTISTS_BY_USER_URL = "http://ws.audioscrobbler.com/2.0/?" +
            "method=user.gettopartists" +
            "&api_key=ecc2e1fc920c9fcbcc19ba8790570691&" +
            "format=json" +
            "&user=";

    public List<Node> topArtistsByUser(String username) throws Exception {
        try {
            List<Node> nodes = new ArrayList<>();

            String response = myHttpClient.getAsString(TOP_ARTISTS_BY_USER_URL + URLEncoder.encode(username));

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            JsonObject item = json.getAsJsonObject("topartists");

            JsonArray arr = item.getAsJsonArray("artist");
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    Node node = new Node();

                    JsonObject o = arr.get(i).getAsJsonObject();
                    String mbid = JsonUtils.getPropertyAsString(o, "mbid");
                    String match = JsonUtils.getPropertyAsString(o, "match");
                    String artistName = JsonUtils.getPropertyAsString(o, "name");

                    node.setMbid(mbid);
                    node.setName(artistName);
                    node.setId(toId(node.getName()));

                    JsonArray images = o.getAsJsonArray("image");
                    if (images != null) {
                        for (JsonElement img : images) {
                            JsonObject jObj = img.getAsJsonObject();
                            String url = JsonUtils.getPropertyAsString(jObj, "#text");
                            String size = JsonUtils.getPropertyAsString(jObj, "size");

                            if (size.equals("small")) {
                                node.setSmallImg(url);
                            } else if (size.equals("medium")) {
                                node.setMediumImg(url);
                            } else if (size.equals("large")) {
                                node.setLargeImg(url);
                            }
                        }
                    }

                    nodes.add(node);
                }
            }

            return nodes;
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public List<Node> similarArtists(String name) throws Exception {
        try {
            List<Node> nodes = new ArrayList<>();

            String response = myHttpClient.getAsString(SIMILAR_ARTISTS_URL + URLEncoder.encode(name));

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            JsonObject item = json.getAsJsonObject("similarartists");

            JsonArray arr = item.getAsJsonArray("artist");
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    Node node = new Node();

                    JsonObject o = arr.get(i).getAsJsonObject();
                    String mbid = JsonUtils.getPropertyAsString(o, "mbid");
                    String match = JsonUtils.getPropertyAsString(o, "match");
                    String artistName = JsonUtils.getPropertyAsString(o, "name");

                    node.setMbid(mbid);
                    node.setName(artistName);
                    node.setId(toId(node.getName()));

                    JsonArray images = o.getAsJsonArray("image");
                    if (images != null) {
                        for (JsonElement img : images) {
                            JsonObject jObj = img.getAsJsonObject();
                            String url = JsonUtils.getPropertyAsString(jObj, "#text");
                            String size = JsonUtils.getPropertyAsString(jObj, "size");

                            if (size.equals("small")) {
                                node.setSmallImg(url);
                            } else if (size.equals("medium")) {
                                node.setMediumImg(url);
                            } else if (size.equals("large")) {
                                node.setLargeImg(url);
                            }
                        }
                    }

                    nodes.add(node);
                }
            }

            return nodes;
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public Node artistInfo(String name) throws Exception {
        try {
            Node node = new Node();

            String response = myHttpClient.getAsString(ARTIST_INFO_URL + URLEncoder.encode(name));

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            JsonObject item = json.getAsJsonObject("artist");

            // error handling missing ** //

            node.setMbid(JsonUtils.getPropertyAsString(item, "mbid"));
            node.setName(JsonUtils.getPropertyAsString(item, "name"));
            node.setUrl(JsonUtils.getPropertyAsString(item, "url"));
            node.setId(toId(node.getName()));

            JsonObject bio = item.getAsJsonObject("bio");
            node.setBio(JsonUtils.getPropertyAsString(bio, "content"));
            node.setBioSummary(JsonUtils.getPropertyAsString(bio, "summary"));

            JsonArray images = item.getAsJsonArray("image");
            if (images != null) {
                for (JsonElement img : images) {
                    JsonObject jObj = img.getAsJsonObject();
                    String url = JsonUtils.getPropertyAsString(jObj, "#text");
                    String size = JsonUtils.getPropertyAsString(jObj, "size");

                    if (size.equals("small")) {
                        node.setSmallImg(url);
                    } else if (size.equals("medium")) {
                        node.setMediumImg(url);
                    } else if (size.equals("large")) {
                        node.setLargeImg(url);
                    }
                }
            }

            JsonArray tagArray = item.getAsJsonObject("tags").getAsJsonArray("tag");
            if (tagArray != null) {
                List<Tag> tags = new ArrayList<>();

                for (JsonElement tagElement : tagArray) {
                    JsonObject jObj = tagElement.getAsJsonObject();
                    String tagName = JsonUtils.getPropertyAsString(jObj, "name");
                    String url = JsonUtils.getPropertyAsString(jObj, "url");

                    Tag tag = new Tag(tagName, url);
                    tags.add(tag);
                }

                node.setTags(tags);
            }

            return node;
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public List<Album> topAlbums(String artist) throws Exception {
        try {
            List<Album> albums = new ArrayList<>();

            String response = myHttpClient.getAsString(TOP_ALBUMS_URL + URLEncoder.encode(artist));

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            JsonObject topalbums = json.getAsJsonObject("topalbums");

            JsonArray albumArray = topalbums.getAsJsonArray("album");
            for (JsonElement el : albumArray) {
                JsonObject item = el.getAsJsonObject();

                Album album = new Album();

                album.setMbid(JsonUtils.getPropertyAsString(item, "mbid"));
                album.setName(JsonUtils.getPropertyAsString(item, "name"));
                album.setUrl(JsonUtils.getPropertyAsString(item, "url"));
                album.setListeners(JsonUtils.getPropertyAsString(item, "listeners"));

                JsonObject bio = item.getAsJsonObject("wiki");
                album.setContent(JsonUtils.getPropertyAsString(bio, "content"));
                album.setContentSummary(JsonUtils.getPropertyAsString(bio, "summary"));

                JsonArray images = item.getAsJsonArray("image");
                if (images != null) {
                    for (JsonElement img : images) {
                        JsonObject jObj = img.getAsJsonObject();
                        String url = JsonUtils.getPropertyAsString(jObj, "#text");
                        String size = JsonUtils.getPropertyAsString(jObj, "size");

                        if (size.equals("small")) {
                            album.setSmallImg(url);
                        } else if (size.equals("medium")) {
                            album.setMediumImg(url);
                        } else if (size.equals("large")) {
                            album.setLargeImg(url);
                        }
                    }
                }

                albums.add(album);
            }

            return albums;
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public Album albumInfo(String artist, String albumStr) throws Exception {
        try {
            String response = myHttpClient.getAsString(ALBUM_INFO_URL + "&artist=" + URLEncoder.encode(artist) + "&album=" + URLEncoder.encode(albumStr));

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            JsonObject item = json.getAsJsonObject("album");

            Album album = new Album();

            album.setMbid(JsonUtils.getPropertyAsString(item, "mbid"));
            album.setName(JsonUtils.getPropertyAsString(item, "name"));
            album.setUrl(JsonUtils.getPropertyAsString(item, "url"));
            album.setListeners(JsonUtils.getPropertyAsString(item, "listeners"));

            JsonObject bio = item.getAsJsonObject("wiki");
            album.setContent(JsonUtils.getPropertyAsString(bio, "content"));
            album.setContentSummary(JsonUtils.getPropertyAsString(bio, "summary"));

            JsonArray images = item.getAsJsonArray("image");
            if (images != null) {
                for (JsonElement img : images) {
                    JsonObject jObj = img.getAsJsonObject();
                    String url = JsonUtils.getPropertyAsString(jObj, "#text");
                    String size = JsonUtils.getPropertyAsString(jObj, "size");

                    if (size.equals("small")) {
                        album.setSmallImg(url);
                    } else if (size.equals("medium")) {
                        album.setMediumImg(url);
                    } else if (size.equals("large")) {
                        album.setLargeImg(url);
                    }
                }
            }

            JsonArray tagArray = item.getAsJsonObject("tags").getAsJsonArray("tag");
            if (tagArray != null) {
                List<Tag> tags = new ArrayList<>();

                for (JsonElement tagElement : tagArray) {
                    JsonObject jObj = tagElement.getAsJsonObject();
                    String tagName = JsonUtils.getPropertyAsString(jObj, "name");
                    String url = JsonUtils.getPropertyAsString(jObj, "url");

                    Tag tag = new Tag(tagName, url);
                    tags.add(tag);
                }

                album.setTags(tags);
            }

            JsonArray trackArray = item.getAsJsonObject("tracks").getAsJsonArray("track");
            for (JsonElement el : trackArray) {
                JsonObject trackObj = el.getAsJsonObject();

                Track track = new Track();
                track.setName(JsonUtils.getPropertyAsString(trackObj, "name"));
                track.setDuration(JsonUtils.getPropertyAsString(trackObj, "duration"));
                track.setUrl(JsonUtils.getPropertyAsString(trackObj, "url"));
                track.setRank(Integer.parseInt(JsonUtils.getPropertyAsString(trackObj.getAsJsonObject("@attr"), "rank")));

                album.addTrack(track);
            }

            return album;
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public static String toId(String str) {
        return URLEncoder.encode(str.replace(" ", "_")).replace("%", "_").replace(".", "_").replace(":", "_");
    }
}