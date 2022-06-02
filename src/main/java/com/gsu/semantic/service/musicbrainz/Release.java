package com.gsu.semantic.service.musicbrainz;

/**
 * Created by cnytync on 11/09/2017.
 */
public class Release {

    private String id;
    private String title;
    private String disambiguation;
    private String firstReleaseData;
    private String primaryType;
    private String secondaryType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getFirstReleaseData() {
        return firstReleaseData;
    }

    public void setFirstReleaseData(String firstReleaseData) {
        this.firstReleaseData = firstReleaseData;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }
}
