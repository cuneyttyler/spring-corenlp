package com.gsu.entitylinker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by cnytync on 10/11/2016.
 */
public class Collocation implements Comparable {
    private List<Word> words = new ArrayList<>();
    private List<String> documents = new ArrayList<>();
    private int frequency = 1;

    public Collocation() {
    }

    public Collocation(List<Word> words, List<String> documents, int frequency) {
        this.words = words;
        this.documents = documents;
        this.frequency = frequency;
    }

    public Collocation(List<Word> words, int frequency) {
        this.words = words;
        this.frequency = frequency;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        frequency++;
    }

    public void addFrequency(int frequency) {
        this.frequency += frequency;
    }

    public String getCollocationString() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for (Word w : words) {
            stringBuilder.append(w.getWord());
            if (i++ < words.size() - 1) stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Collocation) || ((Collocation) o).words.size() != words.size()) {
            return false;
        }
        Collocation c = (Collocation) o;

        boolean equals = true;
        for (int i = 0; i < words.size(); i++) {
            if (!((Collocation) o).words.get(i).equals(words.get(i))) {
                equals = false;
            }
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(words);
    }

    @Override
    public int compareTo(Object o) {
        Collocation c = (Collocation) o;

        if (frequency > c.frequency) {
            return -1;
        } else if (frequency < c.frequency) {
            return 1;
        } else {
            return 0;
        }
    }
}
