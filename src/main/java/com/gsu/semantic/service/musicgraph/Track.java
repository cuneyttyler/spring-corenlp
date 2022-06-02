package com.gsu.semantic.service.musicgraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnytync on 12/09/2017.
 */
public class Track {

    private String track_index;

    private String original_release_year;

    private List<Lyricist> lyricists = new ArrayList<>();

    private String release_year;

    private String id;

    private List<Composer> composers = new ArrayList<>();

    private String duration;

    private String title;

    private String artist_name;

    private String track_album_id;

    private String track_artist_id;

    private String album_title;

    private String entity_type;

    private String track_youtube_id;

    private String popularity;

    public String getTrack_index() {
        return track_index;
    }

    public void setTrack_index(String track_index) {
        this.track_index = track_index;
    }

    public String getOriginal_release_year() {
        return original_release_year;
    }

    public void setOriginal_release_year(String original_release_year) {
        this.original_release_year = original_release_year;
    }

    public List<Lyricist> getLyricists() {
        return lyricists;
    }

    public void setLyricists(List<Lyricist> lyricists) {
        this.lyricists = lyricists;
    }

    public String getRelease_year() {
        return release_year;
    }

    public void setRelease_year(String release_year) {
        this.release_year = release_year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Composer> getComposers() {
        return composers;
    }

    public void setComposers(List<Composer> composers) {
        this.composers = composers;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getTrack_album_id() {
        return track_album_id;
    }

    public void setTrack_album_id(String track_album_id) {
        this.track_album_id = track_album_id;
    }

    public String getTrack_artist_id() {
        return track_artist_id;
    }

    public void setTrack_artist_id(String track_artist_id) {
        this.track_artist_id = track_artist_id;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getTrack_youtube_id() {
        return track_youtube_id;
    }

    public void setTrack_youtube_id(String track_youtube_id) {
        this.track_youtube_id = track_youtube_id;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void addComposer(Composer c ){
        composers.add(c);
    }

    public void addLyricist(Lyricist l) {
        lyricists.add(l);
    }
    @Override
    public String toString() {
        return "ClassPojo [track_index = " + track_index + ", original_release_year = " + original_release_year + ", lyricists = " + lyricists + ", release_year = " + release_year + ", id = " + id + ", composers = " + composers + ", duration = " + duration + ", title = " + title + ", artist_name = " + artist_name + ", track_album_id = " + track_album_id + ", track_artist_id = " + track_artist_id + ", album_title = " + album_title + ", entity_type = " + entity_type + ", track_youtube_id = " + track_youtube_id + ", popularity = " + popularity + "]";
    }

}
