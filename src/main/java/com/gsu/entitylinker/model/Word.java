package com.gsu.entitylinker.model;

import java.util.Objects;

/**
 * Created by cnytync on 08/11/2016.
 */
public class Word implements Comparable {

    private String word;
    private String pos;
    private int frequency = 1;

    public Word(String word, String pos) {
        this.word = word;
        this.pos = pos;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Word)) {
            return false;
        }
        Word w = (Word) o;
        return
                Objects.equals(word, w.word) &&
                        Objects.equals(pos, w.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, pos);
    }

    @Override
    public int compareTo(Object o) {
        Word w = (Word) o;

        if (frequency > w.frequency) {
            return -1;
        } else if (frequency < w.frequency) {
            return 1;
        } else {
            return 0;
        }
    }
}
