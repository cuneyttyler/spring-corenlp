package com.gsu.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cnytync on 14/05/2017.
 */
public class Parser {

    public List<String> parse(String text) {
        List<String> outputLines = new ArrayList<>();

        String[] lines = text.split(System.getProperty("line.separator"));

        String regex = "//(.*) (.*)=\\{(.*):(.*),(.*):(.*)\\}";
        Pattern pattern = Pattern.compile(regex);
        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                System.out.println("1 : " + m.group(0));
                System.out.println("2 : " + m.group(1));
                System.out.println("3 : " + m.group(2));
                System.out.println("4 : " + m.group(3));
                System.out.println("5 : " + m.group(4));
                System.out.println("6 : " + m.group(5));
                System.out.println("7 : " + m.group(6));

                String line1 = m.group(1) + " " + m.group(2) + " = new " + m.group(1) + "();";
                String line2 = m.group(2) + ".set" + firstCaseUpper(m.group(3)) + "(" + m.group(4) + ");";
                String line3 = m.group(2) + ".set" + firstCaseUpper(m.group(5)) + "(" + m.group(6) + ");";

                outputLines.add(line1);
                outputLines.add(line2);
                outputLines.add(line3);
            } else {
                outputLines.add(line);
            }
        }

        return outputLines;
    }

    private String firstCaseUpper(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, 1).toUpperCase());
        sb.append(str.substring(1, str.length()));

        return sb.toString();
    }
}
