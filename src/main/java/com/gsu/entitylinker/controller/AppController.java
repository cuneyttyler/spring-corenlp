//package com.gsu.entitylinker.controller;
//
//import com.gsu.entitylinker.model.*;
//import com.gsu.entitylinker.service.*;
//import com.gsu.common.util.Util;
////import edu.stanford.nlp.dcoref.CorefChain;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.net.URLDecoder;
//import java.util.*;
//
//@Controller("EntityLinkerAppController")
//@RequestMapping(value = "/api")
//public class AppController {
//
//    @Autowired
//    private CoreNlp coreNlp;
//
//    @Autowired
//    private CollocationFinder collocationFinder;
//
//    @Autowired
//    private ReVerb reverb;
//
//    @Autowired
//    private Pdf pdf;
//
//    @RequestMapping(value = "/extractRelationsFromPdf", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object extractRelationsFromPdf(@RequestBody Object body) throws Exception {
//        System.out.println("Extracting...");
//
////        List<String> paths = URLDecoder.decode((String) ((LinkedHashMap) body).get("text"), "UTF-8");
//        String text = pdf.getTextFromPdf("/Users/cnytync/Documents/-/lil/");
//
//        Collection<Triple> allTriples = reverb.getRelations(text);
//        Collection<Entity> allEntities = coreNlp.getNamedEntities(text);
//
//        return new Dto(allEntities, allTriples);
//    }
//
//    @RequestMapping(value = "/findCollocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object findCollocations() throws Exception {
//        System.out.println("Finding Collocations...");
//
//        String path = "/Users/cnytync/Documents/-/uni/paper (phd)";
//        File f = new File(path);
//
//        ArrayList<String> files = Util.listFilesForFolder(f);
//
//        files = new ArrayList<>();
////        files.add("/Users/cnytync/Downloads/The Silmarillion (Illustrated ebook).pdf");
////        files.add("/Users/cnytync/Documents/-/lang/français/yds-2013.pdf");
//        files.add("/Users/cnytync/Documents/-/IELTS/task1.pdf");
//
//        List<Collocation> collocations = collocationFinder.findCollocations(files);
//        Collections.sort(collocations);
//
//        return collocations;
//    }
//
//    @RequestMapping(value = "/findWordFrequencies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object findWordFrequencies() throws Exception {
//        System.out.println("Finding Frequent Words...");
//
//        String path = "/Users/cnytync/Documents/-/uni/paper (phd)";
//        File f = new File(path);
//
//        ArrayList<String> files = Util.listFilesForFolder(f);
//
//        files = new ArrayList<>();
////        files.add("/Users/cnytync/Downloads/The Silmarillion (Illustrated ebook).pdf");
////        files.add("/Users/cnytync/Documents/-/lang/français/yds-2013.pdf");
//        files.add("/Users/cnytync/Documents/-/IELTS/task1.pdf");
//
//        List<Word> words = collocationFinder.findWordFrequencies(files);
//        Collections.sort(words);
//
//        return words;
//    }
//
//    @RequestMapping(value = "/findRelatedDocs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object findRelatedDocs() throws Exception {
//        System.out.println("Finding Collocations...");
//
//        String path = "/Users/cnytync/Documents/-/uni/paper (phd)";
//        File f = new File(path);
//
//        ArrayList<String> files = Util.listFilesForFolder(f);
//
//        List<Collocation> collocations = collocationFinder.findCollocations(files);
//        Collections.sort(collocations);
//
//        return collocations;
//    }
//
//    @RequestMapping(value = "/analyseCorpus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object analyseCorpus() throws Exception {
//        System.out.println("Extracting...");
//
//        String path = "/Users/cnytync/Documents/-/uni/paper (phd)";
//        File f = new File(path);
//
//        ArrayList<String> files = Util.listFilesForFolder(f);
//
//        Map<Word, Integer> allWordFrequencies = new HashMap<>();
//        List<DocumentOutput> dos = new ArrayList<>();
//        for (String s : files) { //
//            String text = pdf.getTextFromPdf(s);
//
//            System.out.println("Extracting file " + s);
//
//            List<Map.Entry<Word, Integer>> freqs = coreNlp.getWordFrequencies(text);
//            dos.add(new DocumentOutput(s, freqs));
//
//            for (Map.Entry<Word, Integer> e : freqs) {
//                Integer v = e.getValue();
//
//                if (allWordFrequencies.get(e.getKey()) != null) {
//                    Integer vv = allWordFrequencies.get(e.getKey());
//                    allWordFrequencies.put(e.getKey(), vv + v);
//                } else {
//                    allWordFrequencies.put(e.getKey(), v);
//                }
//            }
//
//            break;
//
//        }
//
//        List<Map.Entry<Word, Integer>> sortedAwf = Util.sortMapByValues(allWordFrequencies);
//
//        CorpusOutput co = new CorpusOutput(sortedAwf, null); // dos
//        return co;
//    }
//
//    @RequestMapping(value = "/extractRelationsFromText", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object extractRelationsFromText(@RequestBody Object body) throws Exception {
//        System.out.println("Extracting...");
//
//        String text = URLDecoder.decode((String) ((LinkedHashMap) body).get("text"), "UTF-8");
//
//        Collection<Triple> allTriples = reverb.getRelations(text);
//        Collection<Entity> allEntities = coreNlp.getNamedEntities(text);
//
//        return new Dto(allEntities, allTriples);
//    }
//
//    @RequestMapping(value = "/extractEntitiesFromText", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object extractEntitiesFromText(@RequestBody Object body) throws Exception {
////        System.out.println("Extracting");
////
////        String text = URLDecoder.decode((String) ((LinkedHashMap) body).get("text"), "UTF-8");
////
////        Map<Integer, CorefChain> map = coreNlp.coreNLPTest(text);
////        Iterator it = map.entrySet().iterator();
////        while (it.hasNext()) {
////            Map.Entry pairs = (Map.Entry) it.next();
////            System.out.println(pairs.getKey() + " = " + pairs.getValue());
////        }
//
////        Collection<Triple> triples = coreNlp.openIE(text);
//
//
//        return null;
//    }
//
//    @RequestMapping(value = "/wiki/autocomplete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object wikiTest(@RequestParam String term) throws Exception {
//        String[] pages = new Wiki().listPages(term, null, Wiki.MAIN_NAMESPACE);
//
//        List<HashMap> maps = new ArrayList<>();
//        int i = 0;
//        for (String p : pages) {
//            HashMap<String, String> map = new LinkedHashMap<>();
//            map.put("id", p);
//            map.put("label", p);
//            map.put("value", p);
//
//            maps.add(map);
//
//            if (i++ == 10) break;
//        }
//
//        return maps;
//    }
//
//    @RequestMapping(value = "/wiki/page", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object getWikiText(@RequestParam String title) throws Exception {
//        Wiki wiki = new Wiki();
//
//        String page = wiki.getPageText(title);
//
//        String[] pages = null;
//        if (page.contains("#REDIRECT")) {
//            int beginIndex = page.indexOf("[[") + 2;
//            int endIndex = page.indexOf("]]");
//
//            String redirectTitle = page.substring(beginIndex, endIndex);
//            page = wiki.getPageText(redirectTitle);
//            pages = wiki.queryPage(redirectTitle);
//
////            page.replaceAll();
//            page = page.replaceAll("\\[\\[(.*)[\\|?](.*)\\]\\]", "$1");
//            page = page.replaceAll("\\{\\{.*\\}\\}", " ");
//            page = page.replaceAll("/n", " ");
//            page = page.replaceAll("\\n", " ");
//            page = page.replaceAll("<!--.*-->", " ");
//        }
//
//        System.out.println();
//
//        return page;
//    }
//}
