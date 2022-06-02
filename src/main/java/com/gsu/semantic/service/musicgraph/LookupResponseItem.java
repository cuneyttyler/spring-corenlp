package com.gsu.semantic.service.musicgraph;

import java.util.List;

/**
 * Created by cnytync on 12/09/2017.
 */
public class LookupResponseItem {
    private String country_of_origin;

    private String artist_ref_id;

    private String decade;

    private String youtube_id;

    private String sort_name;

    private String spotify_id;

    private String id;

    private String main_genre;

    private String musicbrainz_id;

    private String name;

    private String musicbrainz_image_url;

    private String gender;

    private String entity_type;

    private List<String> alternate_names;

    public String getCountry_of_origin() {
        return country_of_origin;
    }

    public void setCountry_of_origin(String country_of_origin) {
        this.country_of_origin = country_of_origin;
    }

    public String getArtist_ref_id() {
        return artist_ref_id;
    }

    public void setArtist_ref_id(String artist_ref_id) {
        this.artist_ref_id = artist_ref_id;
    }

    public String getDecade() {
        return decade;
    }

    public void setDecade(String decade) {
        this.decade = decade;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getSort_name() {
        return sort_name;
    }

    public void setSort_name(String sort_name) {
        this.sort_name = sort_name;
    }

    public String getSpotify_id() {
        return spotify_id;
    }

    public void setSpotify_id(String spotify_id) {
        this.spotify_id = spotify_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain_genre() {
        return main_genre;
    }

    public void setMain_genre(String main_genre) {
        this.main_genre = main_genre;
    }

    public String getMusicbrainz_id() {
        return musicbrainz_id;
    }

    public void setMusicbrainz_id(String musicbrainz_id) {
        this.musicbrainz_id = musicbrainz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusicbrainz_image_url() {
        return musicbrainz_image_url;
    }

    public void setMusicbrainz_image_url(String musicbrainz_image_url) {
        this.musicbrainz_image_url = musicbrainz_image_url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public List<String> getAlternate_names() {
        return alternate_names;
    }

    public void setAlternate_names(List<String> alternate_names) {
        this.alternate_names = alternate_names;
    }

    @Override
    public String toString() {
        return "ClassPojo [country_of_origin = " + country_of_origin + ", artist_ref_id = " + artist_ref_id + ", decade = " + decade + ", youtube_id = " + youtube_id + ", sort_name = " + sort_name + ", spotify_id = " + spotify_id + ", id = " + id + ", main_genre = " + main_genre + ", musicbrainz_id = " + musicbrainz_id + ", name = " + name + ", musicbrainz_image_url = " + musicbrainz_image_url + ", gender = " + gender + ", entity_type = " + entity_type + ", alternate_names = " + alternate_names + "]";
    }
}
