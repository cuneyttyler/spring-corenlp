package com.gsu.entitylinker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cnytync on 07/11/2016.
 */
public class DocumentOutput {

    private String file;
    private List<Map.Entry<Word, Integer>> wordFrequencies;

    public DocumentOutput(String file, List<Map.Entry<Word, Integer>> wordFrequencies) {
        this.file = file;
        this.wordFrequencies = wordFrequencies;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<Map.Entry<Word, Integer>> getWordFrequencies() {
        return wordFrequencies;
    }

    public void setWordFrequencies(List<Map.Entry<Word, Integer>> wordFrequencies) {
        this.wordFrequencies = wordFrequencies;
    }
}
