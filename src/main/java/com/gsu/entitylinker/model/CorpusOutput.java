package com.gsu.entitylinker.model;

import java.util.List;
import java.util.Map;

/**
 * Created by cnytync on 07/11/2016.
 */
public class CorpusOutput {
    private List<Map.Entry<Word, Integer>> allWordFrequencies;
    private List<DocumentOutput> documentOutputs;

    public CorpusOutput(List<Map.Entry<Word, Integer>> allWordFrequencies, List<DocumentOutput> documentOutputs) {
        this.allWordFrequencies = allWordFrequencies;
        this.documentOutputs = documentOutputs;
    }

    public List<Map.Entry<Word, Integer>> getAllWordFrequencies() {
        return allWordFrequencies;
    }

    public void setAllWordFrequencies(List<Map.Entry<Word, Integer>> allWordFrequencies) {
        this.allWordFrequencies = allWordFrequencies;
    }

    public List<DocumentOutput> getDocumentOutputs() {
        return documentOutputs;
    }

    public void setDocumentOutputs(List<DocumentOutput> documentOutputs) {
        this.documentOutputs = documentOutputs;
    }
}
