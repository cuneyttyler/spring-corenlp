package com.gsu.entitylinker.service;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;

/**
 * Created by cnytync on 07/05/2017.
 */
public class BoilerplateRemover {
    public static void main(String args[]) throws Exception {
//        Document doc = Jsoup.connect("https://www.theguardian.com/film/2016/jan/14/alan-rickman-giant-of-british-film-and-theatre-dies-at-69").get();
//        String text = ArticleExtractor.INSTANCE.getText(doc.html());

        String html = IOUtils.toString(new URI("https://www.theguardian.com/film/2016/jan/14/alan-rickman-giant-of-british-film-and-theatre-dies-at-69"));
        String text = ArticleExtractor.INSTANCE.getText(html);

        System.out.println("text : \n" + text);

//        for (TextBlock block : getTextBlocks()) {
//            // block.isContent() tells you if it's likely to be content or not
//            // block.getText() gives you the block's text
//
//        }

        //Property p={id:propId,name:propName};

    }
}
