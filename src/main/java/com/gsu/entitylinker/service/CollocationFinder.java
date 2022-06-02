//package com.gsu.entitylinker.service;
//
//import com.gsu.entitylinker.model.Collocation;
//import com.gsu.entitylinker.model.Word;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.util.CoreMap;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class CollocationFinder {
//
//    @Autowired
//    private CoreNlp coreNlp;
//
//    @Autowired
//    private Pdf pdf;
//
//    /**
//     * @param corpus as list of filenames (absolute path)
//     */
//    public List<Collocation> findCollocations(List<String> corpus) throws Exception {
//        Map<Word, Integer> allWordFrequencies = new HashMap<>();
//        HashMap<String, Collocation> collocations = new HashMap<>();
//
//        HashMap<String, List<Collocation>> documentCollocationPairs = new HashMap<>();
//        for (String file : corpus) { //
//
//            System.out.println("Analysing file " + file);
//
//            String text = pdf.getTextFromPdf(file);
//            List<CoreMap> sentences = coreNlp.getSentences(text);
//
//            List<Collocation> colls = findSubsequentWords(sentences);
//            documentCollocationPairs.put(file, colls);
//
//            for (Collocation c : colls) {
//                String key = c.getCollocationString();
//
//                if (collocations.containsKey(key)) {
//                    collocations.get(key).addFrequency(c.getFrequency());
//                } else {
//                    collocations.put(key, c);
//                }
//            }
//
//        }
//
//        return new ArrayList(filter(collocations).values());
//    }
//
//    public List<Collocation> findSubsequentWords(List<CoreMap> sentences) {
//        boolean firstWord = true;
//        List<Collocation> collocations = new ArrayList<>();
//        Collocation collocation = new Collocation();
//
//        for (CoreMap sentence : sentences) {
//
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // Retrieve and add the lemma for each word into the list of lemmas
//                String wordString = token.get(CoreAnnotations.LemmaAnnotation.class);
//                Word word = new Word(wordString, pos);
//
//                if (firstWord) {
//                    collocation = new Collocation();
//                    collocation.getWords().add(word);
//
//                    firstWord = false;
//                } else {
//                    collocation.getWords().add(word);
//
//                    int index = collocations.indexOf(collocation);
//                    if (index == -1) {
//                        collocations.add(collocation);
//                    } else {
//                        Collocation c = collocations.get(index);
//                        c.incrementFrequency();
//                        collocations.set(index, c);
//                    }
//
//                    firstWord = true;
//                }
//            }
//        }
//
//        return collocations;
//    }
//
//    public List<Word> findWordFrequencies(List<String> corpus) throws Exception {
//        List<Word> words = new ArrayList<>();
//
//        for (String file : corpus) { //
//
//            System.out.println("Analysing file " + file);
//
//            String text = pdf.getTextFromPdf(file);
//            List<CoreMap> sentences = coreNlp.getSentences(text);
//
//            for (CoreMap sentence : sentences) {
//
//                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                    // this is the POS tag of the token
//                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                    // Retrieve and add the lemma for each word into the list of lemmas
//                    String wordString = token.get(CoreAnnotations.LemmaAnnotation.class);
//                    Word word = new Word(wordString, pos);
//
//                    int index = words.indexOf(word);
//                    if (index == -1) {
//                        words.add(word);
//                    } else {
//                        Word w = words.get(index);
//                        w.incrementFrequency();
//                        words.set(index, w);
//                    }
//
//                }
//            }
//        }
//
//        return words;
//    }
//
//    public HashMap<String, Collocation> filter(HashMap<String, Collocation> collocations) {
//        HashMap<String, Collocation> filtered = new HashMap<>();
//        Set<String> keys = collocations.keySet();
//
//        for (String key : keys) {
//            Collocation c = collocations.get(key);
//
//            boolean pass = true;
//            for (Word w : c.getWords()) {
//                if (!(w.getPos().equals("NN") ||
//                        w.getPos().equals("NNS") ||
//                        w.getPos().equals("NNP") ||
//                        w.getPos().equals("NNPS") ||
//                        w.getPos().equals("JJ") ||
//                        w.getPos().equals("JJR") ||
//                        w.getPos().equals("JJS"))) {
//
//                    pass = false;
//                }
//            }
//
//            if (pass) {
//                filtered.put(key, c);
//            }
//        }
//
//        return filtered;
//    }
//}
