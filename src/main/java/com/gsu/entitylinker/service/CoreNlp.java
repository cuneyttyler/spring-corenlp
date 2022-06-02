//package com.gsu.entitylinker.service;
//
//import com.gsu.entitylinker.model.Entity;
//import com.gsu.entitylinker.model.Triple;
//import com.gsu.entitylinker.model.Word;
//import com.gsu.common.util.Util;
////import edu.stanford.nlp.dcoref.CorefChain;
////import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
////import edu.stanford.nlp.ie.crf.CRFClassifier;
////import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
////import edu.stanford.nlp.ie.util.RelationTriple;
////import edu.stanford.nlp.ling.CoreAnnotations;
////import edu.stanford.nlp.ling.CoreLabel;
////import edu.stanford.nlp.ling.IndexedWord;
////import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
////import edu.stanford.nlp.naturalli.OpenIE;
////import edu.stanford.nlp.pipeline.Annotation;
////import edu.stanford.nlp.pipeline.StanfordCoreNLP;
////import edu.stanford.nlp.semgraph.SemanticGraph;
////import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
////import edu.stanford.nlp.semgraph.SemanticGraphEdge;
////import edu.stanford.nlp.trees.*;
////import edu.stanford.nlp.util.CoreMap;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.*;
//
///**
// * Created by cnytync on 21/12/14.
// */
//@Component
//public class CoreNlp {
//
//    private static String MODEL = "/Users/cnytync/IdeaProjects/java/spring-corenlp/classifiers/english.muc.7class.distsim.crf.ser.gz";
//    private static String FRENCH_MODEL = "/Users/cnytync/Programming/libs/stanford-corenlp-full-2015-12-09/french/stanford-french-corenlp-2016-01-14-models.jar";
//    private static CRFClassifier<CoreLabel> classifier;
//    private static StanfordCoreNLP pipeline;
//    private static OpenIE openIE;
//    private Properties props = new Properties();
//
////    @PostConstruct
//    public void init() {
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma"); // tokenize,ssplit,pos,lemma,depparse,natlog,openie
////        props.setProperty("segment.model",FRENCH_MODEL);
////        props.setProperty("parse.model","edu/stanford/nlp/models/parser/nndep/UD_French.gz");
////        props.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/french/french.tagger");
//        classifier = CRFClassifier.getClassifierNoExceptions(MODEL);
//        pipeline = new StanfordCoreNLP(props);
//        openIE = new OpenIE(props);
//    }
//
//    public List<Map.Entry<Word, Integer>> getWordFrequencies(String text) {
//        List<Word> lemmas = lemmatize(text);
//
//        HashMap<Word, Integer> freqs = new HashMap<>();
//        for (Word s : lemmas) {
//            if (freqs.containsKey(s)) {
//                Integer f = freqs.get(s);
//                f++;
//                freqs.put(s, f);
//            } else {
//                freqs.put(s, 1);
//            }
//        }
//
//        freqs = excludeStopWords(freqs);
//        List<Map.Entry<Word, Integer>> sortedMap = Util.sortMapByValues(freqs);
//
//        return sortedMap;
//    }
//
//    public void findRelatedDocs(String[] words) {
//
//    }
//
//    public List<CoreMap> getSentences(String text) {
//        List<Word> lemmas = new LinkedList<Word>();
//
//        // create an empty Annotation just with the given text
//        Annotation document = new Annotation(text);
//
//        // run all Annotators on this text
//        this.pipeline.annotate(document);
//
//        // Iterate over all of the sentences found
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        return sentences;
//    }
//
//    public List<Word> lemmatize(String documentText) {
//        List<Word> lemmas = new LinkedList<Word>();
//
//        // create an empty Annotation just with the given text
//        Annotation document = new Annotation(documentText);
//
//        // run all Annotators on this text
//        this.pipeline.annotate(document);
//
//        // Iterate over all of the sentences found
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//
//            // Iterate over all tokens in a sentence
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//
//                if (!(pos.equals("CC") ||
//                        pos.equals("CD") ||
//                        pos.equals("DT") ||
//                        pos.equals("EX") ||
//                        pos.equals("FW") ||
//                        pos.equals("IN") ||
//                        pos.equals("LS") ||
//                        pos.equals("PDT") ||
//                        pos.equals("POS") ||
//                        pos.equals("PRP") ||
//                        pos.equals("PRP$") ||
//                        pos.equals("RB") ||
//                        pos.equals("RBR") ||
//                        pos.equals("RBS") ||
//                        pos.equals("RP") ||
//                        pos.equals("SYM") ||
//                        pos.equals("TO") ||
//                        pos.equals("UH") ||
//                        pos.equals("WDT") ||
//                        pos.equals("WP") ||
//                        pos.equals("WP$") ||
//                        pos.equals("WRB")
//                )) {
//                    // Retrieve and add the lemma for each word into the list of lemmas
//                    lemmas.add(new Word(token.get(CoreAnnotations.LemmaAnnotation.class), pos));
//                }
//            }
//        }
//
//        return lemmas;
//    }
//
//    public Collection<Triple> getRelations(String text) {
//        // Annotate text.
//        Annotation doc = new Annotation(text);
//        pipeline.annotate(doc);
//
//        Collection<Triple> allTriples = new ArrayList<Triple>();
//        // Loop over sentences in the document
//        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
//
//            // Get the OpenIE triples for the sentence
//            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
////            Collection<RelationTriple> triples = new ArrayList<>();
//
//            for (RelationTriple triple : triples) {
//                System.out.println(triple.confidence + "\t" +
//                        triple.subjectLemmaGloss() + " -> \t" +
//                        triple.relationLemmaGloss() + " -> \t" +
//                        triple.objectLemmaGloss());
//
//                Triple t = new Triple(triple.subjectLemmaGloss(), triple.relationLemmaGloss(), triple.objectLemmaGloss(), triple.confidence);
//
//                allTriples.add(t);
//            }
//
//            //  the clause splitter:
////            List<SentenceFragment> clauses = openIE.clausesInSentence(sentence);
////            for (SentenceFragment clause : clauses) {
////                System.out.println(clause.parseTree);
////            }
//        }
//
//        return allTriples;
//    }
//
//    public Collection<Entity> getNamedEntities(String text) {
//        List<List<CoreLabel>> classify = classifier.classify(text);
//        HashMap<String, Entity> entityMap = new HashMap();
//
//        StringBuilder prevEntity = new StringBuilder();
//        String prevCategory = "";
//
//        for (List<CoreLabel> coreLabels : classify) {
//            for (CoreLabel coreLabel : coreLabels) {
//                String word = coreLabel.word();
//                String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
//
//                if (!"O".equals(category)) {
//                    if (prevEntity.length() > 0) {
//                        prevEntity.append(" ");
//                    }
//
//                    prevEntity.append(word);
//                } else {
//                    if (prevEntity.length() > 0) {
//                        if (!entityMap.containsKey(prevEntity.toString()))
//                            entityMap.put(prevEntity.toString(), new Entity(prevEntity.toString(), prevCategory));
//                    }
//                    prevEntity = new StringBuilder();
//                }
//
//                prevCategory = category;
//            }
//        }
//
//        return entityMap.values();
//    }
//
//
//    public Map<Integer, CorefChain> coreNLPTest(String text) {
//        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
//        Properties props = new Properties();
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        // read some text in the text variable
////        String text = "Pink Floyd started to be boring."; // Add your text here!
//
//        // create an empty Annotation just with the given text
//        Annotation document = new Annotation(text);
//
//        // run all Annotators on this text
//        pipeline.annotate(document);
//
//        // these are all the sentences in this document
//        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for (CoreMap sentence : sentences) {
//            // traversing the words in the current sentence
//            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//
//                List rel = token.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
//                if (rel == null) rel = new ArrayList();
//
////                System.out.println("Word : " + word + ", pos : " + pos + ", ne : " + ne + ", rel size : " + rel.size());
//            }
//
//            // this is the parse tree of the current sentence
//            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//
//            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//
//            IndexedWord firstRoot = dependencies.getFirstRoot();
//            List<SemanticGraphEdge> incomingEdgesSorted =
//                    dependencies.getIncomingEdgesSorted(firstRoot);
//
//            System.out.println("In Edges");
//            for (SemanticGraphEdge edge : incomingEdgesSorted) {
//                System.out.println("-----------");
//                // Getting the target node with attached edges
//                IndexedWord dep = edge.getDependent();
//                System.out.println("Dependent=" + dep);
//                // Getting the source node with attached edges
//                IndexedWord gov = edge.getGovernor();
//                System.out.println("Governor=" + gov);
//                // Get the relation name between them
//                GrammaticalRelation relation = edge.getRelation();
//                System.out.println("Relation=" + relation);
//
//            }
//            System.out.println("\n-----------\n");
//
//            System.out.println("Out Edges");
//            // this section is same as above just we retrieve the OutEdges
//            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
//            for (SemanticGraphEdge edge : outEdgesSorted) {
//                System.out.println("-----------");
//                IndexedWord dep = edge.getDependent();
//                System.out.println("Dependent=" + dep);
//                IndexedWord gov = edge.getGovernor();
//                System.out.println("Governor=" + gov);
//                GrammaticalRelation relation = edge.getRelation();
//                System.out.println("Relation=" + relation);
//            }
//        }
//
//        // This is the coreference link graph
//        // Each chain stores a set of mentions that link to each other,
//        // along with a method for getting the most representative mention
//        // Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph =
//                document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
//
//        return graph;
//
//    }
//
//    public HashMap<Word, Integer> excludeStopWords(HashMap<Word, Integer> freqs) {
//        Set<Word> keys = freqs.keySet();
//        HashMap<Word, Integer> excluded = new HashMap<Word, Integer>();
//
//        for (Word w : keys) {
//            String k = w.getWord();
//
//            if (!(k.equals(".") ||
//                    k.equals(",") ||
//                    k.equals(";") ||
//                    k.equals("/") ||
//                    k.equals("ø") ||
//                    k.equals("%") ||
//                    k.equals("?") ||
//                    k.equals("|") ||
//                    k.equals("n") ||
//                    k.equals("ö") ||
//                    k.equals("o") ||
//                    k.equals("k") ||
//                    k.equals("s") ||
//                    k.equals("ü") ||
//                    k.equals("ú") ||
//                    k.equals("J") ||
//                    k.equals("^") ||
//                    k.equals("oa") ||
//                    k.equals("ø1") ||
//                    k.equals("å") ||
//                    k.equals("dl") ||
//                    k.equals("ac") ||
//                    k.equals("ã") ||
//                    k.equals("φ") ||
//                    k.equals("σ") ||
//                    k.equals("ös") ||
//                    k.equals("O.") ||
//                    k.equals("™") ||
//                    k.equals("→") ||
//                    k.equals("h̄") ||
//                    k.equals("øhö") ||
//                    k.equals("rü") ||
//                    k.equals("x1") ||
//                    k.equals("ë") ||
//                    k.equals("II") ||
//                    k.equals("W.") ||
//                    k.equals("¥") ||
//                    k.equals("ä") ||
//                    k.equals("î") ||
//                    k.equals("B.") ||
//                    k.equals("*") ||
//                    k.equals("v") ||
//                    k.equals("y") ||
//                    k.equals("the") ||
//                    k.equals("by") ||
//                    k.equals("of") ||
//                    k.equals("be") ||
//                    k.equals("and") ||
//                    k.equals("a") ||
//                    k.equals("in") ||
//                    k.equals("to") ||
//                    k.equals("-lrb-") ||
//                    k.equals("-rrb-") ||
//                    k.equals("that") ||
//                    k.equals("-lsb-") ||
//                    k.equals("-rcb-") ||
//                    k.equals("-rsb-") ||
//                    k.equals("as") ||
//                    k.equals("for") ||
//                    k.equals("we") ||
//                    k.equals("have") ||
//                    k.equals("this") ||
//                    k.equals("``") ||
//                    k.equals("''") ||
//                    k.equals(":") ||
//                    k.equals(":") ||
//                    k.equals("it") ||
//                    k.equals("on") ||
//                    k.equals("or") ||
//                    k.equals("can") ||
//                    k.equals("with") ||
//                    k.equals("they") ||
//                    k.equals("which") ||
//                    k.equals("not") ||
//                    k.equals("-") ||
//                    k.equals("--") ||
//                    k.equals("from") ||
//                    k.equals("use") ||
//                    k.equals("these") ||
//                    k.equals("'s") ||
//                    k.equals("such") ||
//                    k.equals("but") ||
//                    k.equals("1") ||
//                    k.equals("e.g.") ||
//                    k.equals("its") ||
//                    k.equals("there") ||
//                    k.equals("will") ||
//                    k.equals("do") ||
//                    k.equals("=") ||
//                    k.equals("x") ||
//                    k.equals("may") ||
//                    k.equals("one") ||
//                    k.equals("many") ||
//                    k.equals("way") ||
//                    k.equals("at") ||
//                    k.equals("within") ||
//                    k.equals("even") ||
//                    k.equals("other") ||
//                    k.equals("also") ||
//                    k.equals("he") ||
//                    k.equals("what") ||
//                    k.equals("how") ||
//                    k.equals("I") ||
//                    k.equals("2") ||
//                    k.equals("so") ||
//                    k.equals("where") ||
//                    k.equals("about")
//            )) {
//
//                excluded.put(w, freqs.get(w));
//            }
//        }
//
//        return excluded;
//    }
//}
