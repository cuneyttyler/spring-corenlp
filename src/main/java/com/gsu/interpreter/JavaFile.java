package com.gsu.interpreter;

/**
 * Created by cnytync on 14/05/2017.
 */
public class JavaFile {
    private String packageName;
    private String fileName;
    private String absolutePath;
    private String relativePath;

    public JavaFile() {

    }

    public JavaFile(String packageName, String fileName) {
        this.packageName = packageName;
        this.fileName = fileName;
    }

    public JavaFile(String packageName, String fileName, String absolutePath) {
        this.packageName = packageName;
        this.fileName = fileName;
        this.absolutePath = absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
