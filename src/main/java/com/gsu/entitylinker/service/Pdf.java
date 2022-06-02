package com.gsu.entitylinker.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by cnytync on 02/08/16.
 */
@Component
public class Pdf {

    public String getTextFromPdf(String path) throws IOException {


        if (!path.contains(".pdf")) {
            return "";
        }

        PDDocument document = PDDocument.load(new File(path));
//        PDPage doc = document.getPage(0);

        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(1);
        pdfStripper.setEndPage(document.getNumberOfPages());

        String text = pdfStripper.getText(document);

        return text;
    }

    public void blabla() throws IOException {
        PDDocument document = PDDocument.load(new File("/Users/cnytync/Documents/-/uni/paper (phd)/cmp blending/Concept Invention and Music.pdf"));
        PDPage doc = document.getPage(0);

        PDFMarkedContentExtractor p = new PDFMarkedContentExtractor();
        p.processPage(doc);
        p.getMarkedContents();

        return;
    }

    public static void main(String[] args) throws Exception {
        new Pdf().blabla();
    }

}
