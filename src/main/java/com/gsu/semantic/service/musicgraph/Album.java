package com.gsu.semantic.service.musicgraph;

/**
 * Created by cnytync on 12/09/2017.
 */
public class Album {
    private String number_of_tracks;

    private String decade;

    private String release_year;

    private String number_of_releases;

    private String id;

    private String album_musicbrainz_id;

    private String title;

    private String product_form;

    private String main_genre;

    private String artist_name;

    private String release_date;

    private String album_ref_id;

    private String entity_type;

    private String album_artist_id;

    private String popularity;

    public String getNumber_of_tracks() {
        return number_of_tracks;
    }

    public void setNumber_of_tracks(String number_of_tracks) {
        this.number_of_tracks = number_of_tracks;
    }

    public String getDecade() {
        return decade;
    }

    public void setDecade(String decade) {
        this.decade = decade;
    }

    public String getRelease_year() {
        return release_year;
    }

    public void setRelease_year(String release_year) {
        this.release_year = release_year;
    }

    public String getNumber_of_releases() {
        return number_of_releases;
    }

    public void setNumber_of_releases(String number_of_releases) {
        this.number_of_releases = number_of_releases;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbum_musicbrainz_id() {
        return album_musicbrainz_id;
    }

    public void setAlbum_musicbrainz_id(String album_musicbrainz_id) {
        this.album_musicbrainz_id = album_musicbrainz_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProduct_form() {
        return product_form;
    }

    public void setProduct_form(String product_form) {
        this.product_form = product_form;
    }

    public String getMain_genre() {
        return main_genre;
    }

    public void setMain_genre(String main_genre) {
        this.main_genre = main_genre;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getAlbum_ref_id() {
        return album_ref_id;
    }

    public void setAlbum_ref_id(String album_ref_id) {
        this.album_ref_id = album_ref_id;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getAlbum_artist_id() {
        return album_artist_id;
    }

    public void setAlbum_artist_id(String album_artist_id) {
        this.album_artist_id = album_artist_id;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "ClassPojo [number_of_tracks = " + number_of_tracks + ", decade = " + decade + ", release_year = " + release_year + ", number_of_releases = " + number_of_releases + ", id = " + id + ", album_musicbrainz_id = " + album_musicbrainz_id + ", title = " + title + ", product_form = " + product_form + ", main_genre = " + main_genre + ", artist_name = " + artist_name + ", release_date = " + release_date + ", album_ref_id = " + album_ref_id + ", entity_type = " + entity_type + ", album_artist_id = " + album_artist_id + ", popularity = " + popularity + "]";
    }

}
