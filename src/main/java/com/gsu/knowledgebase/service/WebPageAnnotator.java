package com.gsu.knowledgebase.service;

import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;
import com.gsu.common.util.MyHttpClient;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
import de.l3s.boilerpipe.sax.ImageExtractor;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Component
public class WebPageAnnotator {

    @Autowired
    MyHttpClient myHttpClient;

    public static void main(String args[]) throws Exception {
//        String html = IOUtils.toString(new URI("http://www.medievalists.net/2017/08/living-books-renaissance-ferrara/"));
        String html = new MyHttpClient().getAsString("http://www.medievalists.net/2017/08/living-books-renaissance-ferrara/");
        String text = ArticleExtractor.INSTANCE.getText(html);

        Map response = new RestTemplate().getForObject(
                "http://model.dbpedia-spotlight.org/en/annotate?text=" +
                        text +
                        "&confidence=0.5&support=0&spotter=Default&disambiguator=Default&policy=whitelist&types=&sparql="
                , Map.class);

        response.get("");

    }

    public String getWebPageText(String url) throws Exception {
        try {
            String html = myHttpClient.getAsString(url);
            String text = ArticleExtractor.INSTANCE.getText(html);

            return text;
        } catch (Exception e) {
            return "";
        }
    }

    public String getWebPageTextAsHtml(String url) {
        try {
            String requestUrl = "https://boilerpipe-web.appspot.com/extract?" +
                    "url=" + URLEncoder.encode(url) +
                    "&extractor=ArticleExtractor&output=htmlFragment&extractImages=&token=";

            String html = myHttpClient.getAsString(requestUrl);

            return html;
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> annotateWebPage(String url) throws Exception {
        String html = myHttpClient.getAsString(url);
        String text = ArticleExtractor.INSTANCE.getText(html);

        Goose goose = new Goose(new Configuration());
//        Article article = goose.extractContent(url);

        String cut = text.length() < 5000 ? text : text.substring(0, 5000);

        Map response = new RestTemplate().getForObject(
                "http://model.dbpedia-spotlight.org/en/annotate?text="
                        + cut +
                        "&confidence=0.5&support=0&spotter=Default&disambiguator=Default&policy=whitelist&types=&sparql="
                , Map.class);
//        if (response.get("Resources") == null) {
//            return null;
//        }

        List<String> entities = new ArrayList<>();
        for (Map r : (List<Map>) response.get("Resources")) {
            String uri = (String) r.get("@URI");

            if (!entities.contains(uri)) {
                entities.add(uri);
            }
        }

        return entities;
    }

    public String getWebsiteImage2(String url) throws Exception {

        List<Image> images = ImageExtractor.INSTANCE.process(new URL(url), LargestContentExtractor.INSTANCE);

        List<String> strs = new ArrayList<>();
        for (Image img : images) {
            strs.add(img.getSrc());
        }

        if (strs.size() > 0) {
            return strs.get(0);
        } else {
            return "";
        }
    }

    public String getWebsiteImage(String url) throws Exception {
        String html = myHttpClient.getAsString("https://boilerpipe-web.appspot.com/extract?url=" + URLEncoder.encode(url)
                + "&extractor=LargestContentExtractor&output=img&extractImages=4&token=");

        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img");

        List<String> images = new ArrayList<>();
        for (Element element : elements) {
            images.add(element.attr("src"));
        }

        if (images.size() > 0) {
            return images.get(0);
        } else {
            return "";
        }
    }


    public String getWebPageTitle(String uri) throws Exception {
        Document doc = Jsoup.connect(uri).get();

        if (doc == null || doc.select("title") == null) {
            return null;
        }

        return doc.select("title").html();
    }

    public class Request {
        private String text;
        private double confidence = 0.5;
        private double support = 0;
        private String spotter = "Default";
        private String policy = "whitelist";
        private String sparql = "";

        public Request() {
        }

        public Request(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public double getSupport() {
            return support;
        }

        public void setSupport(double support) {
            this.support = support;
        }

        public String getSpotter() {
            return spotter;
        }

        public void setSpotter(String spotter) {
            this.spotter = spotter;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }

        public String getSparql() {
            return sparql;
        }

        public void setSparql(String sparql) {
            this.sparql = sparql;
        }
    }
}

//Map response = new RestTemplate().getForObject(
//        "http://model.dbpedia-spotlight.org/en/annotate?text=" +
//                text +
//                "&confidence=0.5&support=0&spotter=Default&disambiguator=Default&policy=whitelist&types=&sparql="
//        , Map.class);

//HttpHeaders headers = new HttpHeaders();
//headers.setAccept(Arrays.asList(MediaType.ALL));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Request> request = new HttpEntity<Request>(new Request(text), headers);
//
//        Object response = new RestTemplate().postForObject(
//        "http://spotlight.dbpedia.org/dev/rest/annotate/", request, Object.class);