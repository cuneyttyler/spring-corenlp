package com.gsu.entitylinker.service;

import com.gsu.entitylinker.model.Dto;
import com.gsu.entitylinker.model.Triple;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cnytync on 14/08/16.
 */
@Component
public class ReVerb {

    public Collection<Triple> getRelations(String text) throws Exception {
        Collection<Triple> allTriples = new ArrayList<>();


        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(text);

        // Prints out the (token, tag, chunk-tag) for the sentence
        for (int i = 0; i < sent.getLength(); i++) {
            String token = sent.getToken(i);
            String posTag = sent.getPosTag(i);
            String chunkTag = sent.getChunkTag(i);
            System.out.println(token + " " + posTag + " " + chunkTag);
        }

        // Prints out extractions from the sentence.
        ReVerbExtractor reverb = new ReVerbExtractor();
        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            double conf = confFunc.getConf(extr);
            System.out.println("Arg1=" + extr.getArgument1());
            System.out.println("Rel=" + extr.getRelation());
            System.out.println("Arg2=" + extr.getArgument2());
            System.out.println("Conf=" + conf);

            Triple t = new Triple(extr.getArgument1().toString(), extr.getRelation().toString(), extr.getArgument2().toString(), conf);

            allTriples.add(t);
        }

        return allTriples;
    }


}
