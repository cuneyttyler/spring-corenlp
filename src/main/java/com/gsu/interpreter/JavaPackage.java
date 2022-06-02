package com.gsu.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnytync on 14/05/2017.
 */
public class JavaPackage {
    public String name;
    public List<JavaFile> javaFiles = new ArrayList<>();
    public List<JavaPackage> javaPackages = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JavaFile> getJavaFiles() {
        return javaFiles;
    }

    public void setJavaFiles(List<JavaFile> javaFiles) {
        this.javaFiles = javaFiles;
    }

    public List<JavaPackage> getJavaPackages() {
        return javaPackages;
    }

    public void setJavaPackages(List<JavaPackage> javaPackages) {
        this.javaPackages = javaPackages;
    }
}
