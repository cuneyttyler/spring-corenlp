package com.gsu.semantic.model;

import com.gsu.semantic.service.lastfm.Lastfm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Created by cnytync on 21/08/2017.
 */
public class Node {
    private Integer dbId;
    private String id;
    private String name;
    private String escapedName;
    private String mbid;
    private String musicgraphId;
    private String url;
    private String smallImg;
    private String mediumImg;
    private String largeImg;
    private String bio;
    private String bioSummary;
    private Double weight = 1.0;
    private List<Tag> tags;
    private boolean saved = false;
    private Integer graphId;

    public Node() {
    }

    public Node(ResultSet rs, String prefix) throws SQLException {
        dbId = rs.getInt(prefix + ".id");
        name = rs.getString(prefix + ".name");
        escapedName = rs.getString(prefix + ".escaped_name");
        id = Lastfm.toId(name);
        mbid = rs.getString(prefix + ".mbid");
        url = rs.getString(prefix + ".url");
        smallImg = rs.getString(prefix + ".small_img");
        mediumImg = rs.getString(prefix + ".medium_img");
        largeImg = rs.getString(prefix + ".large_img");
        bio = rs.getString(prefix + ".bio");
        bioSummary = rs.getString(prefix + ".bio_summary");
        musicgraphId = rs.getString(prefix + ".musicgraph_id");
    }

    public Node(Integer dbId) {
        this.dbId = dbId;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }

    public String getMediumImg() {
        return mediumImg;
    }

    public void setMediumImg(String mediumImg) {
        this.mediumImg = mediumImg;
    }

    public String getLargeImg() {
        return largeImg;
    }

    public void setLargeImg(String largeImg) {
        this.largeImg = largeImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBioSummary() {
        return bioSummary;
    }

    public void setBioSummary(String bioSummary) {
        this.bioSummary = bioSummary;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getEscapedName() {
        return escapedName;
    }

    public void setEscapedName(String escapedName) {
        this.escapedName = escapedName;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getMusicgraphId() {
        return musicgraphId;
    }

    public void setMusicgraphId(String musicgraphId) {
        this.musicgraphId = musicgraphId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mbid);
    }

    @Override
    public boolean equals(Object o) {
        boolean retVal = false;

        if (o instanceof Node){
            Node node = (Node) o;
            retVal = node.id.equals(this.id);
        }

        return retVal;
    }
}
