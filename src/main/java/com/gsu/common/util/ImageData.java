package com.gsu.common.util;

import java.io.Serializable;

/**
 * Created by cnyt on 25.11.2014.
 */
public class ImageData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 911859843692712456L;

    private String filename;

    private Long size;

    private Integer width;

    private Integer height;

    private Integer ppi;

    private String type;

    public ImageData(String filename, Long size, Integer width, Integer height, Integer ppi, String type) {
        this.filename = filename;
        this.size = size;
        this.width = width;
        this.height = height;
        this.ppi = ppi;
        this.type = type;
    }

    public ImageData() {
        super();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getPpi() {
        return ppi;
    }

    public void setPpi(Integer ppi) {
        this.ppi = ppi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
