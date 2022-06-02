package com.gsu.semantic.service.lastfm;

/**
 * Created by cnytync on 12/09/2017.
 */
public class Track {
    private String duration;
    private String name;
    private String url;
    private int rank;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
